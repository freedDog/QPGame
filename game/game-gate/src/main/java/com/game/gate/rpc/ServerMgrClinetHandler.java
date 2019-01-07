package com.game.gate.rpc;

import java.util.List;
import java.util.Map;

import com.game.base.service.config.GlobalConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.module.ModuleMgr;
import com.game.base.service.rpc.callback.ServerRegisterCallBack;
import com.game.base.service.rpc.handler.IServerMgrClient;
import com.game.base.service.rpc.handler.IServerMgrService;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.redis.RedisPool.RedisConfig;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyChannelMgr;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.handler.ProxyHandler;
import com.game.framework.utils.collections.ListUtils;
import com.game.gate.GateServer;
import com.game.gate.mgr.GateChannelMgr;
import com.game.base.service.config.ModuleConfig;

/**
 * 游戏服管理器连接处理句柄
 * 
 */
@Rpc
public class ServerMgrClinetHandler extends ProxyHandler implements IServerMgrClient {
	protected final GateServer server;

	public ServerMgrClinetHandler(GateServer server) {
		this.server = server;
	}

	@Rpc.RpcFunc
	@Override
	public void syncModuleInfos(ProxyChannel channel, Map<String, List<ModuleConfig>> moduleInfos) {
		ModuleMgr.syncModuleInfos(moduleInfos);
	}

	@Override
	public boolean onConnect(final ProxyChannel channel) {
		Log.debug("与管理服连接建立!");
		// 注册服务器
		final ServerConfig serverConfig = App.getInstance().getConfig();
		final IServerMgrService gameMgrService = channel.createImpl(IServerMgrService.class);
		gameMgrService.registerGate(channel, serverConfig, new ServerRegisterCallBack(channel));
		return true;
	}

	@Override
	public boolean onClose(ProxyChannel channel) {
		Log.info("与管理服断开!");
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void sycnServerInfos(ProxyChannel channel, Map<Integer, List<ServerConfig>> serverInfos) {
		// 获取数据代理列表
		List<ServerConfig> serverConfigs = serverInfos.get(ServerConfig.TYPE_GAME);
		// 输出日志
		Log.info(ProxyChannelMgr.showServerInfos("更新逻辑服列表", serverConfigs));
		// 更新列表
		GateChannelMgr.getGameClientMgr().syncServerList(serverConfigs);
	}

	@Rpc.RpcFunc
	@Override
	public void syncGlobalConfig(ProxyChannel channel, GlobalConfig globalConfig) {
		syncRedisConfigs(globalConfig);
	}

	/** 同步Redis配置 **/
	private void syncRedisConfigs(GlobalConfig globalConfig) {
		List<RedisConfig> redisConfigs = globalConfig.getRedisConfigs();
		if (!RedisMgr.reload(redisConfigs)) {
			Log.error("同步Redis列表错误!?" + redisConfigs);
			return;
		}
		Log.info("更新Redis列表成功! : " + ListUtils.toString(redisConfigs));
	}
}

