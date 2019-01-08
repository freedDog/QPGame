package com.game.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.server.gamemgr.GameApp;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DBPoolMgr;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.db.LogDaoMgr;
import com.game.base.service.db.LogDbPoolMgr;
import com.game.base.service.mgr.KeyGenerateMgr;
import com.game.base.service.mgr.RouteMgr;
import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleName;
import com.game.base.service.tempmgr.ConfigurationMgr;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.handler.IProxyHandler;
import com.game.framework.framework.rpc.impl.ProxyServer;
import com.game.framework.framework.timer.TimeMgr;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.CheckUtils;
import com.game.framework.utils.ReflectUtils;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.ThreadUtils;
import com.game.proto.protocol.Protocol;
import com.game.server.db.DaoImplMgr;
import com.game.server.db.LogDaoImplMgr;
import com.game.server.mgr.GameChannelMgrX;
import com.game.server.mgr.GameRouteMgr;
import com.game.server.rpc.ServerMgrClientHandler;

/**
 * 逻辑服
 * GameServer.java
 * @author JiangBangMing
 * 2019年1月8日上午9:59:19
 */
public class GameServer extends GameApp {
	private ProxyServer server;
	private Map<String, Module> modules;
	private List<String> moduleNames;

	protected GameServer() {
		modules = new HashMap<>();
		moduleNames = new ArrayList<>();
	}

	@Override
	protected boolean init(String[] args) throws Exception {
		if (!super.init(args)) {
			return false;
		}

		// 检测配置是否正确
		XmlNode config = ConfigMgr.getConfig();
		String configName = config.getName();
		if (configName == null || !configName.equals("Game")) {
			Log.error("服务器配置不是Game!");
			return false;
		}

		// 语言key检测
		if (!CheckUtils.checkUniqueType(TextTempId.class)) {
			return false;
		}
		if (!CheckUtils.checkUniqueType(Protocol.class)) {
			return false;
		}

		// 初始化模块
		if (!init(DBPoolMgr.class, DBPoolMgr.getInstance())) {
			return false;
		}
		if (!init(LogDbPoolMgr.class, LogDbPoolMgr.getInstance())) {
			return false;
		}
		if (!init(DaoMgr.class, new DaoImplMgr())) {
			return false;
		}
		if (!init(LogDaoMgr.class, new LogDaoImplMgr())) {
			return false;
		}

		if (!init(RouteMgr.class, new GameRouteMgr())) {
			return false;
		}

		if (!initStatic(GameChannelMgrX.class, this)) {
			return false;
		}

		if (!initStatic(ConfigurationMgr.class)) {
			return false;
		}
		// 处理游戏模块
		List<XmlNode> moduleNodes = config.getElems("Module");
		int msize = (moduleNodes != null) ? moduleNodes.size() : 0;
		if (msize <= 0) {
			Log.error("逻辑服没找到任何需要加载模块, 没必要启动!?");
			return false;
		}
		// 遍历创建模块
		// Map<Module, int[][]> mGameZoneIds = new HashMap<>();
		for (int i = 0; i < msize; i++) {
			XmlNode moduleNode = moduleNodes.get(i);
			try {
				// 检测模块名
				String moduleName = moduleNode.getAttr("class", "");
				if (StringUtils.isEmpty(moduleName)) {
					Log.error("模块没有类名! " + moduleNode);
					return false;
				}

				// 处理区服
				// String gameZondIdStr = moduleNode.getAttr("gameZoneIds", "");
				// int[][] gameZoneIds = !StringUtils.isEmpty(gameZondIdStr) ? DataUtils.splitToInt2(gameZondIdStr, "\\|", ",") : null;
				// int ggsize = (gameZoneIds != null) ? gameZoneIds.length : 0;
				// if (ggsize > 0) {
				// mGameZoneIds.put(module, gameZoneIds);
				// }

				// 创建对象
				@SuppressWarnings("unchecked")
				Class<? extends Module> moduleClass = (Class<? extends Module>) Class.forName(moduleName);
				Module module = ReflectUtils.createInstance(moduleClass, true, moduleNode);
				if (module == null) {
					Log.error("无法创建模块对象! node=" + moduleNode);
					return false;
				}

				// 检测是否与其他模块重名
				ModuleName mn = module.getModuleName();
				Module old = modules.get(mn.name());
				if (old != null) {
					Log.error("存在一样的模块! " + mn + " module=" + moduleClass + " -> " + old);
					return false;
				}
				// 初始化
				modules.put(mn.name(), module);
				moduleNames.add(module.getModuleName().name());

			} catch (Exception e) {
				Log.error("创建组件失败! ", e);
				return false;
			}
		}
		// 初始化区服(必须在APP创建完模块列表执行)
		// if (!GameZoneMgrX.init(mGameZoneIds)) {
		// Log.error("GameZoneMgr初始化失败!");
		// return false;
		// }
		// 初始化mailbox管理器
		// if (!PartMailBoxMgr.init(moduleNames.toArray(new String[0]))) {
		// Log.error("MailBox初始化失败!");
		// return false;
		// }

		// 连接rpc管理器
		if (!initGameMgrClient()) {
			return false;
		}

		// 最多等待N秒, 等连接上DB代理进行读取数据.
		long startTime = System.currentTimeMillis();
		while (true) {
			// 检测数据库连接表是否同步了
			if (!DBPoolMgr.getInstance().getConfigs().isEmpty()) {
				break; // 有数据库配置信息了
			}
			// 检测超时
			long waitTime = System.currentTimeMillis() - startTime;
			if (waitTime >= 10 * 1000) {
				Log.error("等待连接DB更新失败, 请检查GameMananger是否启动! waitTime=" + waitTime);
				return false;
			}
			// 等待100ms
			ThreadUtils.sleep(100);
		}

		// 初始化各个模块
		for (Module module : modules.values()) {
			try {
				// 初始化组件
				if (!module.init()) {
					Log.error("模块初始化失败! module=" + module);
					return false;
				}
			} catch (Exception e) {
				Log.error("初始化组件失败! ", e);
				return false;
			}
		}

		// 启动rpc服务器(这个还是最后才启动, 如果冲突则关闭, 否则网关提前连接过来但是数据却没有初始化)
		server = new ProxyServer(ProxyService.getInstance());
		int port = config.getAttr("port", 9001);
		if (!server.startSync(port, 10 * 1000)) {
			Log.error("启动rpc服务失败! port=" + port);
			return false;
		}
		
		// 最后再 初始化key 不然redis 还没有连接上
		KeyGenerateMgr.init(getConfig());
		
		// 测试代码
//		if (ConfigMgr.isDebug()) {
//			ServiceMgr.enqueue(new Action(1000) {
//				@Override
//				public void execute() throws Exception {
//					LocalGameTest test = new LocalGameTest();
//					test.run();
//				}
//			});
//		}

		return true;
	}

	@Override
	protected void destroy() throws Exception {
		// 关闭通用
		TimeMgr.stop();
		Log.info("关闭定时器成功!");
		//保存Key
		KeyGenerateMgr.save();
		// 关闭各个模块
		for (Module module : modules.values()) {
			try {
				Log.info("开始关闭模块[" + module.getModuleName() + "]...");
				module.destroy();
				Log.info("关闭模块[" + module.getModuleName() + "]成功!");
			} catch (Exception e) {
				Log.error("关闭模块[" + module.getModuleName() + "]失败!", e);
			}
		}
		modules.clear();
		Log.info("所有模块关闭成功");

		// 关闭rpc连接
		if (server != null) {
			server.stop();
			server = null;
			Log.info("关闭RPC服务器");
		}

		// 组件关闭
		super.destroy();
	}

	/** 获取各个模块 **/
	public List<Module> getModules() {
		return new ArrayList<>(modules.values());
	}

	public Module getModule(ModuleName mn) {
		return modules.get(mn.name());
	}

	public Module getModule(String moduleName) {
		return modules.get(moduleName);
	}

	@Override
	protected IProxyHandler createServerMgrClinetHandler() {
		return new ServerMgrClientHandler(this);
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length <= 0) {
			args = new String[] { "config_loacl/servers.xml", "game1" };
		}

		// 启动
		GameServer server = new GameServer();
		if (!server.start(args)) {
			Log.error("服务器启动错误! " + Arrays.toString(args));
			System.exit(0);
		}
	}
}