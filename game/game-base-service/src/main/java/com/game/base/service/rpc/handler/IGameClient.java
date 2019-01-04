package com.game.base.service.rpc.handler;

import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.proto.msg.Message;

/**
 * 逻辑服服务客户端接口
 * IGameClient.java
 * @author JiangBangMing
 * 2019年1月4日下午5:00:35
 */
public interface IGameClient {
	/** 客户端连接操作-无 **/
	int CLIENTACTION_NULL = 0;
	/** 客户端连接操作-绑定Id **/
	int CLIENTACTION_BIND = 1;
	/** 客户端连接操作-关闭连接 **/
	int CLIENTACTION_CLOSE = -1;

	/** 发消息到客户端, 根据玩家Id **/
	boolean sendMessageToClient(ProxyChannel channel, Message packet, int action);
	
//	/** 发客户端行为消息到客户端, 根据玩家Id 仅供登录使用 **/
//	boolean sendClientActionMessageToClient(ProxyChannel channel, Message packet, int action);

	/**
	 * 根据连接Id发消息到客户端<br>
	 * 
	 * @param action
	 *            0: 无, 1:绑定玩家Id(根据消息中的连接Id和playerId), -1:断开连接s
	 **/
	void sendMessageToClientByConnectId(ProxyChannel channel, Message packet, int action);

	/** 发消息到客户端, 根据区服群发, gameZoneId为0代表全发. **/
	void sendMessageToAll(ProxyChannel channel, int[] gameZoneIds, Message packet);

	/** 绑定玩家Id **/
	void bindPlayerId(long connectId, long playerId);

	/** 断开玩家连接 **/
	void closeConnectByPlayerId(long playerId);

	/** 断开玩家连接 **/
	void closeConnectByConnectId(long connectId);

	/** 测试调用 **/
	int testCall(ProxyChannel channel, int a, RpcCallback callback);

}
