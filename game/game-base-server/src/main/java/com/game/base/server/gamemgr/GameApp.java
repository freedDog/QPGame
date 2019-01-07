package com.game.base.server.gamemgr;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.handler.IProxyHandler;
import com.game.framework.framework.xml.XmlNode;

/**
 * GameManager客户端<br>
 * GameApp.java
 * @author JiangBangMing
 * 2019年1月7日下午5:45:32
 */
public abstract class GameApp extends App {
	protected GameMgrClient gameMgrClient;

	@Override
	protected boolean init(String[] args) throws Exception {
		if (!super.init(args)) {
			return false;
		}

		// 初始化组件
		// if (!PartMailBoxMgr.init(getKeys())) {
		// Log.error("初始化MailBox失败!");
		// return false;
		// }
		return true;
	}

	@Override
	protected void destroy() throws Exception {
		// 管理管理器连接
		if (gameMgrClient != null) {
			gameMgrClient.stop();
			gameMgrClient = null;
			Log.info("关闭GameManager连接成功!");
		}
		// 关闭其他组件
		super.destroy();
	}

	/** 初始化GameMgr连接器 **/
	protected boolean initGameMgrClient() {
		// 连接管理器
		XmlNode managerConfig = ConfigMgr.getManagerConfig();
		String mhost = managerConfig.getAttr("host", "127.0.0.1");
		int mport = managerConfig.getAttr("port", 9001);
		ProxyService rpcService = new ProxyService(createServerMgrClinetHandler());

		// 初始化对象
		if (!init(GameMgrClient.class, new GameMgrClient(rpcService))) {
			return false;
		}
		// 启动
		if (!GameMgrClient.getInstance().start(mhost, mport)) {
			Log.error("启动rpc服务失败!");
			return false;
		}
		return true;
	}

	protected abstract IProxyHandler createServerMgrClinetHandler();
}

