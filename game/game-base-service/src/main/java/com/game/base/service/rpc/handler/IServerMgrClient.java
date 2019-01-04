package com.game.base.service.rpc.handler;

import java.util.List;
import java.util.Map;

import com.game.base.service.config.GlobalConfig;
import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.framework.framework.rpc.ProxyChannel;

/**
 * 游戏服子服务器同步接口
 * 
 */
public interface IServerMgrClient {
	/** 同步数据库信息 **/
	void syncGlobalConfig(ProxyChannel channel, GlobalConfig globalConfig);

	/** 同步更新模块信息 **/
	void syncModuleInfos(ProxyChannel channel, Map<String, List<ModuleConfig>> moduleInfos);

	/** 同步所有服务器列表 **/
	void sycnServerInfos(ProxyChannel channel, Map<Integer, List<ServerConfig>> serverInfos);

}