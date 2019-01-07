package com.game.base.server.gamemgr;

import com.game.base.service.rpc.handler.IServerMgrService;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.impl.ProxyClient;

/**
 * GameMgr客户端<br>
 * GameMgrClient.java
 * @author JiangBangMing
 * 2019年1月7日下午5:47:39
 */
public class GameMgrClient extends ProxyClient {
	private static GameMgrClient instance;

	public GameMgrClient(ProxyService device) {
		super(device);
		instance = this;
	}

	public IServerMgrService getService() {
		return this.getChannel().createImpl(IServerMgrService.class);
	}

	public static GameMgrClient getInstance() {
		return instance;
	}
}

