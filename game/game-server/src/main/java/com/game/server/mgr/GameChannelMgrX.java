package com.game.server.mgr;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.rpc.GameProxyServiceService;
import com.game.framework.framework.rpc.impl.ProxyConnectMgr;
import com.game.server.GameServer;
import com.game.server.rpc.GameServiceHandler;

/**
 * 逻辑服连接管理器<br>
 * 用于提供各个模块获取连接.
 * 
 */
public class GameChannelMgrX extends GameChannelMgr {
	protected static GameProxyServiceService proxyService = null;

	protected static boolean init(GameServer server) {
		// 创建ProxyService
		proxyService = new GameProxyServiceService(new GameServiceHandler(server));
		proxyService.start();

		// 创建连接管理器
		mgrs.put(ServerConfig.TYPE_GATE, new ProxyConnectMgr<ServerConfig>());
		mgrs.put(ServerConfig.TYPE_GAME, new GameClientMgr()); // 其他逻辑服的连接
		return true;
	}

	protected static void destroy() {
		if (proxyService != null) {
			proxyService.stop();
			proxyService = null;
		}
	}

	public static ProxyConnectMgr<ServerConfig> getGateClientMgr() {
		return getChannelMgr(ServerConfig.TYPE_GATE);
	}

	public static GameClientMgr getGameClientMgr() {
		return getChannelMgr(ServerConfig.TYPE_GAME);
	}

}

