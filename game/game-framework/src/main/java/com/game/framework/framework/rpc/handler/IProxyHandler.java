package com.game.framework.framework.rpc.handler;

import com.game.framework.framework.rpc.ProxyChannel;

/**
 * rpc服务处理句柄
 * IProxyHandler.java
 * @author JiangBangMing
 * 2019年1月3日下午3:48:51
 */
public interface IProxyHandler {
	/** rpc连接时 **/
	boolean onConnect(ProxyChannel channel);

	/** rpc断开时 **/
	boolean onClose(ProxyChannel channel);

}
