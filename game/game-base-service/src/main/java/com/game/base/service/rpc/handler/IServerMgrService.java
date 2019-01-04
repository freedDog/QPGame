package com.game.base.service.rpc.handler;

import java.util.List;

import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;

/**
 * 游戏管理远程函数定义 IServerMgrService.java
 * 
 * @author JiangBangMing 2019年1月4日下午5:57:25
 */
public interface IServerMgrService extends IBaseService {
	/** 注册逻辑服 **/
	void registerGame(ProxyChannel channel, List<ModuleConfig> moduleConfigs, ServerConfig serverConfig,
			RpcCallback callback);

	/** 注册网关服 **/
	void registerGate(ProxyChannel channel, ServerConfig serverConfig, RpcCallback callback);

	/** 测试函数 **/
	int getGameZondId(long playerId);
}
