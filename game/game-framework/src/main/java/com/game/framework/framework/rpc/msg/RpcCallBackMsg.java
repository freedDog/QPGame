package com.game.framework.framework.rpc.msg;

/**
 * rpc回调消息 RpcCallBackMsg.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:20:40
 */
public class RpcCallBackMsg {
	/** 常规回馈函数(参数的回调) **/
	public static final short CALLBACKTYPE_NORMAL = 0;
	/** 阻塞回调调用 **/
	public static final short CALLBACKTYPE_SYNC = 1;

	// 调用参数
	protected Class<?>[] paramTypes; // 函数参数类型
	protected byte[] data;
	// 回调Id
	protected long callbackId;
	// 识别类型.
	protected short type; // 回调类型, 0:常规, 1:阻塞回调

	public long getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(long callbackId) {
		this.callbackId = callbackId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}

}
