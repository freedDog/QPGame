package com.game.framework.framework.rpc.msg;

import java.util.Map;

/**
 * rpc远程调用消息
 * RpcInfoMsg.java
 * @author JiangBangMing
 * 2019年1月3日下午3:16:29
 */
public class RpcInfoMsg {
	protected Map<String, Class<?>[]> methods;

	public Map<String, Class<?>[]> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, Class<?>[]> methods) {
		this.methods = methods;
	}
}
