package com.game.framework.framework.rpc.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyChannelMgr;
import com.game.framework.framework.rpc.ProxyConfig;
import com.game.framework.framework.rpc.ProxyService;


/**
 * rpc客户端管理器(客户端用于管理连接多个服务器)<br>
 * 用于创建客户端连接到各个服务器.
 * ProxyClientMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:58:14
 */
public class ProxyClientMgr<C extends ProxyConfig> extends ProxyChannelMgr<C> {
	protected Map<C, ProxyClient> map;
	protected ProxyService service;

	public ProxyClientMgr(ProxyService service) {
		map = new HashMap<>();
		this.service = service;
	}

	/** 创建客户端 **/
	protected ProxyClient createClient(ProxyService service, C config) {
		return new ProxyClient(service);
	}

	/** 同步服务器列表, 检测出哪些要断开, 哪些要连接. **/
	public synchronized boolean syncServerList(Collection<C> configs) {
		// 更新链接
		Map<C, ProxyClient> oldMap = new HashMap<>(map);
		Map<C, ProxyClient> newMap = new HashMap<>();
		int ssize = (configs != null) ? configs.size() : 0;
		if (ssize > 0) {
			for (C config : configs) {
				// 先从当前链接中拿出存在链接
				ProxyClient client = oldMap.remove(config);
				if (client == null) {
					// 新建链接
					client = createClient(service, config);
					client.start(config.getHost(), config.getPort());
					Log.debug("建立新连接: " + config);
				}
				newMap.put(config, client);
			}
		}
		// 绑定新连接
		map = newMap;

		// 关闭旧连接
		for (ProxyClient client : oldMap.values()) {
			client.stop();
			Log.debug("停止连接: " + client);
		}
		oldMap.clear();
		return true;
	}

	/** 获取连接 **/
	public ProxyClient get(C config) {
		return map.get(config);
	}

	/** 获取连接 **/
	public List<ProxyClient> getAll() {
		return new ArrayList<>(map.values());
	}

	@Override
	public ProxyChannel getChannel(C config) {
		ProxyClient client = map.get(config);
		return (client != null) ? client.getChannel() : null;
	}

	@Override
	public List<ProxyChannel> getChannels() {
		List<ProxyChannel> channels = new ArrayList<>();
		for (ProxyClient client : map.values()) {
			channels.add(client.getChannel());
		}
		return channels;
	}

	@Override
	public int getChannelCount() {
		return map.size();
	}

	@Override
	public List<C> getAllConfigs() {
		return new ArrayList<>(map.keySet());
	}

	public synchronized void stopAll() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("停止所有连接: \n");
		for (ProxyClient client : map.values()) {
			client.stop();
			strBdr.append("停止连接: " + client + "\n");
		}
		map.clear();
		Log.debug(strBdr.toString());
	}

	@Override
	protected int getAppId(C config) {
		return config.getId();
	}

}
