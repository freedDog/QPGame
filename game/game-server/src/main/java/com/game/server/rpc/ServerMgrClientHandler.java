package com.game.server.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.DBConfig;
import com.game.base.service.config.GlobalConfig;
import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.db.DBPoolMgr;
import com.game.base.service.db.LogDbPoolMgr;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleMgr;
import com.game.base.service.rpc.callback.ServerRegisterCallBack;
import com.game.base.service.rpc.handler.IServerMgrClient;
import com.game.base.service.rpc.handler.IServerMgrService;
import com.game.framework.component.log.Log;
import com.game.framework.framework.redis.RedisPool.RedisConfig;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.handler.ProxyHandler;
import com.game.framework.utils.collections.ListUtils;
import com.game.server.GameServer;
import com.game.server.db.GameDBConfigMgr;
import com.game.server.db.LogGameDBConfigMgr;
import com.game.server.mgr.GameChannelMgrX;

/**
 * 游戏服连接管理器连接处理句柄
 * 
 */
@Rpc
public class ServerMgrClientHandler extends ProxyHandler implements IServerMgrClient {
	protected final GameServer server;

	public ServerMgrClientHandler(GameServer server) {
		this.server = server;
	}

	@Override
	public boolean onConnect(final ProxyChannel channel) {
		Log.debug("与管理服连接建立!");

		// 整理模块列表
		List<Module> modules = server.getModules();
		int msize = (modules != null) ? modules.size() : 0;

		// 创建模块配置类表
		List<ModuleConfig> moduleConfigs = new ArrayList<>();
		for (int i = 0; i < msize; i++) {
			Module module = modules.get(i);
			ModuleConfig moduleConfig = module.getConfig();
			moduleConfigs.add(moduleConfig);
		}

		// 注册服务器
		IServerMgrService gameMgrService = channel.createImpl(IServerMgrService.class);
		gameMgrService.registerGame(channel, moduleConfigs, server.getConfig(), new ServerRegisterCallBack(channel));
		return true;
	}

	@Override
	public boolean onClose(ProxyChannel channel) {
		Log.debug("逻辑服连接断开!");
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void syncModuleInfos(ProxyChannel channel, Map<String, List<ModuleConfig>> moduleInfos) {
		if (!ModuleMgr.syncModuleInfos(moduleInfos)) {
			Log.error("模块信息同步错误!?" + moduleInfos);
			return;
		}
		GameChannelMgrX.getGameClientMgr().syncModuleInfos(moduleInfos);
	}

	@Rpc.RpcFunc
	@Override
	public void sycnServerInfos(ProxyChannel channel, Map<Integer, List<ServerConfig>> serverInfos) {
		// 这里的服务器列表会通过模块列表推算出来
	}

	@Rpc.RpcFunc
	@Override
	public void syncGlobalConfig(ProxyChannel channel, GlobalConfig globalConfig) {
		syncRedisConfigs(globalConfig);
		syncDBConfigs(globalConfig);
		syncLogDBConfigs(globalConfig);

	}

	/** 同步日志数据库配置 **/
	private void syncLogDBConfigs(GlobalConfig globalConfig) {
		LogGameDBConfigMgr configMgr = (LogGameDBConfigMgr) LogGameDBConfigMgr.getLogInstance();

		// 加载数据库配置
		List<DBConfig> dbConfigs = globalConfig.getLogDbConfigs();
		if (!configMgr.reload(dbConfigs)) {
			Log.error("同步DB列表错误!?" + dbConfigs);
			return;
		}
		// 重连DB
		if (!LogDbPoolMgr.getInstance().reload(configMgr.getConfigs())) {
			Log.error("重连数据库失败!");
			return;
		}
		Log.info("更新数据库列表成功! : " + ListUtils.toString(dbConfigs));
	}

	/** 同步数据库配置 **/
	private void syncDBConfigs(GlobalConfig globalConfig) {
		GameDBConfigMgr configMgr = GameDBConfigMgr.getInstance();

		// 加载数据库配置
		List<DBConfig> dbConfigs = globalConfig.getDbConfigs();
		if (!configMgr.reload(dbConfigs)) {
			Log.error("同步DB列表错误!?" + dbConfigs);
			return;
		}
		// 重连DB
		if (!DBPoolMgr.getInstance().reload(configMgr.getConfigs())) {
			Log.error("重连数据库失败!");
			return;
		}
		Log.info("更新数据库列表成功! : " + ListUtils.toString(dbConfigs));
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
