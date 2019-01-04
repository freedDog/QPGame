package com.game.framework.framework.rpc.msg;

/**
 * rpc远程调用消息 RpcCallMsg.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:17:04
 */
public class RpcCallMsg {
	// 远程调用参数
	protected Class<?>[] paramTypes; // 函数参数类型
	protected byte[] data; // 参数数据
	// 调用函数名
	protected String method; // 函数名
	// 函数阻塞回馈(只能是1个回调结果)
	protected Class<?> retType; // 回调类型
	protected long callbackId; // 回调Id

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public long getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(long callbackId) {
		this.callbackId = callbackId;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public Class<?> getRetType() {
		return retType;
	}

	public void setRetType(Class<?> retType) {
		this.retType = retType;
	}
}
