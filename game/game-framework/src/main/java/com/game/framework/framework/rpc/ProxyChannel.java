package com.game.framework.framework.rpc;

import com.game.framework.framework.rpc.msg.RpcMsg;

/**
 * 代理连接(一个连接代理代表一个进程)<br>
 * ProxyChannel.java
 * @author JiangBangMing
 * 2019年1月3日下午3:49:59
 */
public abstract class ProxyChannel extends RpcChannel {
	protected ProxyConfig config;

	protected ProxyChannel() {
	}

	/** 获取配置 **/
	@SuppressWarnings("unchecked")
	public <C extends ProxyConfig> C getConfig() {
		return (C) config;
	}

	public void setConfig(ProxyConfig config) {
		this.config = config;
	}

	@Override
	public void close() {
	}

	@Override
	public abstract void write(RpcMsg msg);

	@Override
	public String toString() {
		return this.getClass() + "[" + config + "]";
	}
}
