package com.game.framework.framework.rpc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyChannelMgr;
import com.game.framework.framework.rpc.ProxyConfig;
import com.game.framework.framework.rpc.RpcChannel;



/**
 * rpc客户端连接管理器(服务器用于管理客户端连接)<br>
 * 用于管理从各个客户端连接过来的连接
 * ProxyConnectMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:59:14
 */
public class ProxyConnectMgr<C extends ProxyConfig> extends ProxyChannelMgr<C> {
	protected final ConcurrentMap<C, ProxyChannel> connects;

	public ProxyConnectMgr() {
		connects = new ConcurrentHashMap<>();
	}

	/** 注册客户端, 如果存在相同的不能注册. **/
	public boolean register(ProxyChannel channel, C config) {
		// 绑定信息
		channel.setConfig(config);

		// 尝试添加
		RpcChannel old = connects.putIfAbsent(config, channel);
		if (old != null) {
			Log.error("重复注册连接! old=" + old + " new=" + channel);
			return false;
		}
		return true;
	}

	/** 注销连接, 如果已被移除, 返回false **/
	public boolean unregister(ProxyChannel channel) {
		// 获取配置
		C config = channel.getConfig();
		if (config == null) {
			return false;
		}
		// 执行移除.
		if (!connects.remove(config, channel)) {
			return false; // 已经移除了
		}
		return true;
	}

	@Override
	public List<C> getAllConfigs() {
		return new ArrayList<>(connects.keySet());
	}

	@Override
	public List<ProxyChannel> getChannels() {
		return new ArrayList<>(connects.values());
	}

	@Override
	public ProxyChannel getChannel(C config) {
		return connects.get(config);
	}

	@Override
	public int getChannelCount() {
		return connects.size();
	}

	@Override
	protected int getAppId(C config) {
		return config.getId();
	}
}
