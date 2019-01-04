package com.game.base.service.server;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.log.LogConfig;
import com.game.framework.component.log.Log;
import com.game.framework.component.log.Log.LogType;
import com.game.framework.component.service.record.TimeRecordManager;
import com.game.framework.framework.mgr.NettyServiceMgr;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.TimeMgr;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.SystemUtils;

/**
 * 服务应用对象.
 * App.java
 * @author JiangBangMing
 * 2019年1月4日下午3:12:32
 */
public abstract class App extends com.game.framework.framework.app.App{
	private ServerConfig config; // 服务器信息配置

	protected App() {
		instance = this;
	}

	/** 获取服务器配置 **/
	public ServerConfig getConfig() {
		return config;
	}

	@Override
	protected boolean init(String[] args) throws Exception {
		// 检测参数
		if (!checkArgs(args)) {
			return false;
		}

		// 初始化配置
		String configPath = args[0];
		String serverName = args[1];
		if (!ConfigMgr.init(configPath, serverName)) {
			Log.error("日志配置错误");
			return false;
		}

		// 服务器通用组件初始化
		int appId = ConfigMgr.getAttr("id", 0);
		String appName = ConfigMgr.getAttr("name", "");
		if (appId <= 0 || StringUtils.isEmpty(appName)) {
			Log.error("appId或appName为空! appId=" + appId + " appName=" + appName);
			return false;
		}
		String host = ConfigMgr.getAttr("host", "");
		int port = ConfigMgr.getAttr("port", 9001);
		this.config = new ServerConfig(appId, appName, host, port);

		// 初始化配置路径
		XmlNode node = ConfigMgr.getElem("Log");
		String logPath = XmlNode.getAttr(node, "path", "./logs");
		String prefix = XmlNode.getAttr(node, "prefix", "");
		// 如果没有前缀, 按照服务器名字命名.
		prefix = (!StringUtils.isEmpty(prefix)) ? prefix : appName + "_";
		LogType level = ConfigMgr.isDebug() ? LogType.DEBUG : LogType.INFO; // 日志等级
		// LogType level = LogType.DEBUG;
		
		// 房间日志级别
		XmlNode roomLogNode = ConfigMgr.getElem("RoomLog");
		int roomLogLvl = Integer.parseInt(XmlNode.getAttr(roomLogNode, "level", "0"));
		boolean console = XmlNode.getAttr(roomLogNode, "console", true);
		
		LogConfig.init(logPath, prefix, level, roomLogLvl, console);

		// 信息输出
		Log.info("system: " + SystemUtils.system);
		Log.info("ip: " + SystemUtils.getLocalIp());
		Log.info("mac: " + SystemUtils.getMAC());

		// 线程池初始化
		if (!initStatic(ServiceMgr.class)) {
			return false;
		}
		if (!initStatic(NettyServiceMgr.class)) {
			return false;
		}
		if (!initStatic(TimeMgr.class)) {
			return false;
		}
		if (!initStatic(RedisMgr.class)) {
			return false;
		}
		return true;
	}

	/** 服务器销毁 **/
	protected void destroy() throws Exception {
		// 组件关闭
		super.destroy();

		// 保存信息
		saveRunInfo();
	}

	/** 保存运行信息 **/
	public void saveRunInfo() {
		String appName = getAppName();
		if (StringUtils.isEmpty(appName)) {
			return;
		}

		// 保存运行时间
		String logPath = LogConfig.getLogPath();
		// String savePath = String.format("%s/%s_%s_%s.log", logPath,
		// getAppName(), "runInfos", TimeUtils.toString(new Date(),
		// "yyyyMMddHH"));
		String savePath = String.format("%s/%s_%s.log", logPath, appName,
				"runInfos");
		TimeRecordManager.getInstance().saveFile(savePath, true);
	}

	/********** 静态 *********/
	protected static App instance;

	@SuppressWarnings("unchecked")
	public static <T extends App> T getInstance() {
		if (instance == null) {
			return null;
		}
		return (T) instance;
	}

	@SuppressWarnings("unchecked")
	public static <T extends App> T getInstance(Class<T> clazz) {
		if (instance == null) {
			Log.error("服务器应用尚未创建! ", true);
			return null;
		}
		return (T) instance;
	}

	/** 检测服务器参数 **/
	protected static boolean checkArgs(String[] args) {
		if (args == null || args.length < 2) {
			Log.error("启动参数错误! 参数1: 配置路径, 参数2: 配置名称.");
			System.exit(0);
			return false;
		}
		// 参数检测
		String configPath = args[0];
		if (StringUtils.isEmpty(configPath)) {
			Log.error("配置路径不能为空!");
			return false;
		}
		String serverName = args[1];
		if (StringUtils.isEmpty(serverName)) {
			Log.error("服务器名称不能为空!");
			return false;
		}
		return true;
	}

	@Override
	public String getAppName() {
		return config.getName();
	}

	@Override
	public int getAppId() {
		return config.getId();
	}

}
