package com.game.framework.framework.rpc;

import java.util.ArrayList;
import java.util.List;

import com.game.framework.utils.collections.ListUtils;



/**
 * 连接管理器<br>
 * ProxyChannelMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:52:31
 */
public abstract class ProxyChannelMgr<C> {
	/** 获取连接数量 **/
	public abstract int getChannelCount();

	/** 获取所有连接 **/
	public abstract List<ProxyChannel> getChannels();

	/** 获取当前绑定的所有配置 **/
	public abstract List<C> getAllConfigs();

	/** 获取连接 **/
	public abstract ProxyChannel getChannel(C config);

	/** 获取随机连接 **/
	public ProxyChannel getRandomChannel() {
		List<ProxyChannel> channels = getChannels();
		int csize = (channels != null) ? channels.size() : 0;
		if (csize <= 0) {
			return null;
		}
		// 随机找出一个可用的
		int r = (int) (Math.random() * csize);
		for (int i = 0; i < csize; i++) {
			ProxyChannel channel = channels.get(r + i);
			if (channel != null && channel.isConnect()) {
				return channel; // 可用
			}
		}
		return channels.get(r);
	}

	/** 获取随机连接 **/
	public C getRandomConfig() {
		List<C> configs = getAllConfigs();
		int csize = (configs != null) ? configs.size() : 0;
		if (csize <= 0) {
			return null;
		}
		int r = (int) (Math.random() * csize);
		return configs.get(r);
	}

	/** 根据AppId获取配置 **/
	public C getConfig(int appId) {
		List<C> configs = getAllConfigs();
		int csize = (configs != null) ? configs.size() : 0;
		if (csize <= 0) {
			return null;
		}
		// 遍历AppId
		for (C config : configs) {
			// 检测配置Id
			if (appId == getAppId(config)) {
				return config;
			}
		}
		return null;
	}

	/** 获取配置AppId **/
	protected abstract int getAppId(C config);

	/** 获取AppId连接 **/
	public ProxyChannel getChannel(int appId) {
		C config = getConfig(appId);
		if (config == null) {
			return null; // 找不到配置
		}
		// 获取连接
		return getChannel(config);
	}

	/** 获取当前绑定的所有配置 **/
	public List<ProxyChannel> getChannelsByConfigs(List<C> configs) {
		int csize = (configs != null) ? configs.size() : 0;
		if (csize <= 0) {
			return null;
		}
		// 获取连接
		List<ProxyChannel> out = new ArrayList<>();
		for (int i = 0; i < csize; i++) {
			C config = configs.get(i);
			ProxyChannel channel = this.getChannel(config);
			if (channel != null) {
				out.add(channel);
			}
		}
		return out;

	}

	/** 输出服务器列表信息 **/
	public static <T> String showServerInfos(String text, List<T> serverConfigs) {
		return text + ": " + ListUtils.toString(serverConfigs);
	}
}
