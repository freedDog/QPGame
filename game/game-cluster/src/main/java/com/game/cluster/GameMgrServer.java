package com.game.cluster;

import java.util.Arrays;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.rpc.GameProxyServiceService;
import com.game.base.service.server.App;
import com.game.cluster.mgr.GameMgrChannelMgr;
import com.game.cluster.mgr.GlobalConfigMgr;
import com.game.cluster.rpc.ServerMgrServiceHandler;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.impl.ProxyServer;
import com.game.framework.framework.xml.XmlNode;

/**
 *  集群管理类
 * GameMgrServer.java
 * @author JiangBangMing
 * 2019年1月4日下午3:04:19
 */
public class GameMgrServer extends App {
	private ProxyServer server;
	private GameProxyServiceService rpcService;

	@Override
	protected boolean init(String[] args) throws Exception {
		if (!super.init(args)) {
			return false;
		}

		// 检测配置是否正确
		XmlNode config = ConfigMgr.getConfig();
		String configName = config.getName();
		if (configName == null || !configName.equals("GameManager")) {
			Log.error("服务器配置不是GameManager!");
			return false;
		}

		// 初始化管理器
		if (!initStatic(GameMgrChannelMgr.class)) {
			Log.error("GameChannelMgr初始化失败");
			return false;
		}
		if (!initStatic(GlobalConfigMgr.class)) {
			Log.error("GlobalConfigMgr初始化失败");
			return false;
		}

		// 创建服务
		rpcService = new GameProxyServiceService(new ServerMgrServiceHandler(this));
		rpcService.start();

		// 启动rpc服务器
		int port = config.getAttr("port", 9001);
		server = new ProxyServer(rpcService);
		if (!server.startSync(port, 10 * 1000)) {
			Log.error("启动rpc服务失败! port=" + port);
			return false;
		}

		return true;
	}

	@Override
	protected void destroy() throws Exception {
		// 服务器关闭
		if (server != null) {
			server.stop();
			server = null;
		}

		// 服务关闭
		if (rpcService != null) {
			rpcService.stop();
			rpcService = null;
		}

		// 销毁
		super.destroy();
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length <= 0) {
			args = new String[] { "config_local/servers.xml", "gamemgr" };
		}
		// 启动
		GameMgrServer server = new GameMgrServer();
		if (!server.start(args)) {
			Log.error("服务器启动错误! " + Arrays.toString(args));
			System.exit(0);
		}
	}
}
