package com.game.cluster.rpc;

import java.util.List;

import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.constant.ProxyParamType;
import com.game.base.service.player.Player;
import com.game.base.service.rpc.handler.IServerMgrService;
import com.game.base.service.server.App;
import com.game.cluster.GameMgrServer;
import com.game.cluster.mgr.GameMgrChannelMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.rpc.handler.ProxyHandler;

/**
 * 游戏服管理器服务
 * 
 */
@Rpc
public class ServerMgrServiceHandler extends ProxyHandler implements IServerMgrService {
	protected final GameMgrServer server;

	public ServerMgrServiceHandler(GameMgrServer server) {
		this.server = server;
	}

	@Rpc.RpcFunc
	@Override
	public void registerGate(ProxyChannel channel, ServerConfig info, RpcCallback callback) {
		// 检测这个服务器是否占用了.
		if (!GameMgrChannelMgr.register(channel, info, ServerConfig.TYPE_GATE)) {
			callback.callBack(0, "注册失败!", null);
			return;
		}

		// 回调成功
		callback.callBack(1, null, server.getConfig());

		// 同步全局配置
		GameMgrChannelMgr.syncGlobalConfigs(channel);
		// 同步单服模块信息
		GameMgrChannelMgr.syncModuleInfos(channel);
		// 同步所有服务器信息
		GameMgrChannelMgr.sycnServerInfosToAll();
	}

	@Rpc.RpcFunc
	@Override
	public synchronized void registerGame(ProxyChannel channel, List<ModuleConfig> moduleConfigs, ServerConfig info, RpcCallback callback) {
		// 检测模块
		if (!GameMgrChannelMgr.checkModuleInfos(moduleConfigs)) {
			callback.callBack(0, "模块信息冲突!", null);
			return;
		}

		// 检测这个服务器是否占用了.
		if (!GameMgrChannelMgr.register(channel, info, ServerConfig.TYPE_GAME)) {
			callback.callBack(0, "注册失败!", null);
			return;
		}

		// 绑定模块
		channel.setParam(ProxyParamType.PARAM_MODULES, moduleConfigs);

		// 回调成功
		callback.callBack(1, null, server.getConfig());

		// 同步全局配置
		GameMgrChannelMgr.syncGlobalConfigs(channel);
		// 同步模块
		GameMgrChannelMgr.syncModuleInfoToAll();
		// 同步所有服务器信息
		GameMgrChannelMgr.sycnServerInfosToAll();
	}

	@Override
	public synchronized boolean onConnect(ProxyChannel channel) {
		return true;
	}

	@Override
	public synchronized boolean onClose(ProxyChannel channel) {
		// 断开处理
		if (!GameMgrChannelMgr.unregister(channel)) {
			return false;
		}
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public String shutdown(String key, int waitTime) {
		App.getInstance().stop(waitTime);
		return "ok";
	}

	@Rpc.RpcFunc
	@Override
	public int getGameZondId(long playerId) {
		return Player.getGameZoneId(playerId);
	}

}
