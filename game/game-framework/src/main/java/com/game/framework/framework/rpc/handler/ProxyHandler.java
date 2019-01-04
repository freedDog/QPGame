package com.game.framework.framework.rpc.handler;

import com.game.framework.framework.rpc.ProxyService;

/**
 * rpc服务处理句柄
 * ProxyHandler.java
 * @author JiangBangMing
 * 2019年1月3日下午3:49:11
 */
public abstract class ProxyHandler implements IProxyHandler {
	protected ProxyService service;

	public void setService(ProxyService service) {
		this.service = service;
	}

	/** 获取设备 **/
	@SuppressWarnings("unchecked")
	public <D extends ProxyService> D getService() {
		return (D) service;
	}
}
