package com.game.base.service.rpc.handler;

import java.util.Map;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.module.ModuleName;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.proto.msg.Message;

/**
 * 逻辑服服务接口
 * IGameService.java
 * @author JiangBangMing
 * 2019年1月7日下午6:07:32
 */
public interface IGameService extends IBaseService {
	/**
	 * 注册网关信息
	 * 
	 * @param channel
	 * @param gateInfo
	 *            网关信息
	 * @param callback
	 */
	@Rpc.RpcFunc
	void registerGate(ProxyChannel channel, ServerConfig gateInfo, RpcCallback callback);

	/**
	 * 模块消息处理
	 * 
	 * @param playerId
	 * @param moduleName
	 * @param code
	 * @param args
	 */
	@Rpc.RpcFunc
	void onModuleMessage(Message packet, ProxyChannel channel);

	/**
	 * http请求
	 */
	@Rpc.RpcFunc
	void onHttpRequest(String url, Map<String, String> params, ProxyChannel channel, RpcCallback callback);

	/** 测试函数 **/
	int testCall(ProxyChannel channel, int a, RpcCallback callback);

	/** 玩家连接通过认证 */
	void playerConnectionVerified(ProxyChannel channel, long connectId, long playerId, ServerConfig gate);

	/** 玩家掉线处理 **/
	void playerLostConnection(ProxyChannel channel, long connectId, long playerId);

	/** 模块指令 **/
	void onModuleCommon(ModuleName moduleName, String cmd, Map<String, String> params);
}
