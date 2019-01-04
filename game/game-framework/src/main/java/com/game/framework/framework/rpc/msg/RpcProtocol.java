package com.game.framework.framework.rpc.msg;

/**
 * rpc通讯协议 RpcProtocol.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:23:52
 */
public interface RpcProtocol {
	/** 心跳 **/
	public static final short RPC_KEEP = 0;
	/** 信息 **/
	public static final short RPC_INFO = 1;
	/** 远程调用 **/
	public static final short RPC_CALL = 2;
	/** 回馈 **/
	public static final short RPC_CALLBACK = 3;
	/** 远程转发 **/
	public static final short RPC_RELAY = 4;

}
