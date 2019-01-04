package com.game.framework.framework.rpc;

import java.lang.reflect.Method;

/**
 * rpc回调函数 RpcCallback.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:22:18
 */
public class RpcCallback {
	protected long callbackId;

	public boolean callBack(Object... args) {
		throw new RuntimeException("调用错误, rpc回调只能在远程服务器上调用.");
	}

	/** 回调超时 **/
	protected void onTimeOut() {
	}

	/** 回调超时 **/
	protected void onTimeOut(long timeout) {
		onTimeOut();
	}

	/** 获取解析参数类, 返回null则自动用回调函数的参数代替. **/
	public Class<?>[] getParamTypes() {
		return null;
	}

	/** 超时时间 **/
	public int getTimeOut() {
		return 10 * 1000;
	}

	/** 回调函数名称 **/
	protected final static String CALLBACK_FUNCNAME = "onCallBack";

	/** 获取回调函数 **/
	public static Method getCallbackMethod(RpcCallback callback) {
		return RpcUtils.getMethodByName(callback.getClass(), RpcCallback.CALLBACK_FUNCNAME);
	}

	@Override
	public String toString() {
		return "RpcCallback [callbackId=" + callbackId + "]";
	}
}
