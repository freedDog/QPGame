package com.game.gate;

import java.util.Arrays;

import com.game.base.server.gamemgr.GameApp;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.impl.ProxyClientMgr;
import com.game.framework.framework.xml.XmlNode;
import com.game.gate.client.ClientServer;
import com.game.gate.http.HttpServer;
import com.game.gate.mgr.GateChannelMgr;
import com.game.gate.rpc.ServerMgrClinetHandler;

/**
 * 网关服
 * 
 */
public class GateServer extends GameApp {
	private ClientServer clientServer;
	private HttpServer httpServer;

	@Override
	protected boolean init(String[] args) throws Exception {
		if (!super.init(args)) {
			return false;
		}

		// 检测配置是否正确
		XmlNode config = ConfigMgr.getConfig();
		String configName = config.getName();
		if (configName == null || !configName.equals("Gate")) {
			Log.error("服务器配置不是Gate!");
			return false;
		}

		// 初始化组件
		if (!initStatic(GateChannelMgr.class, this)) {
			return false;
		}

		if (!initGameMgrClient()) {
			return false;
		}

		// 启动客户端连接socket
		clientServer = new ClientServer();
		int port = config.getAttr("port", 9001);
		if (!clientServer.startSync(port)) {
			Log.error("启动客户端服务失败! port=" + port);
			return false;
		}

		// 启动http服务器
		httpServer = new HttpServer();
		if (!httpServer.startSync(config)) {
			Log.error("启动http服务失败!");
			return false;
		}

		return true;
	}

	@Override
	protected void destroy() throws Exception {
		// 关闭http
		if (httpServer != null) {
			httpServer.stop();
			httpServer = null;
		}
		// 关闭socket服务器
		if (clientServer != null) {
			clientServer.stop();
			clientServer = null;
		}
		// 关闭连接
		ProxyClientMgr<ServerConfig> gameClientMgr = GateChannelMgr.getGameClientMgr();
		if (gameClientMgr != null) {
			gameClientMgr.stopAll();
		}

		// 释放
		super.destroy();
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length <= 0) {
			args = new String[] { "config_loacl/servers.xml", "gate1" };
		}

		// 启动
		GateServer server = new GateServer();
		if (!server.start(args)) {
			Log.error("服务器启动错误! " + Arrays.toString(args));
			System.exit(0);
		}
	}

	@Override
	protected ServerMgrClinetHandler createServerMgrClinetHandler() {
		return new ServerMgrClinetHandler(this);
	}
}