package com.game.base.service.mgr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.module.ModuleMgr;
import com.game.base.utils.RandomUtils;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyChannelMgr;

/**
 * 服务器连接管理器<br>
 * 用于提供各个模块获取连接.<br>
 * type类型分网关(ServerConfig.TYPE_GATE)和逻辑服(ServerConfig.TYPE_GAME)<br>
 * GameChannelMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午3:48:51
 */
public class GameChannelMgr {
	protected static ConcurrentMap<Integer, ProxyChannelMgr<ServerConfig>> mgrs = new ConcurrentHashMap<>();

	/**
	 * 获取指定玩家所在模块的服务<br>
	 * 如果玩家不在内存, 返回null;
	 * 
	 * @params nullRandom 如果没有进程返回随机, 否则返回null
	 * **/
	public static <T, M extends Enum<?>> T getChannelServiceByPlayerId(long playerId, M moduleName, Class<T> serviceClass, boolean nullRandom) {
		// 处理playerId
		if (playerId <= 0) {
			Log.error("playerId error! " + playerId, true);
			return null;
		}
		// 获取玩家所在地址
		ServerConfig config = MailBox.get(playerId, moduleName);
		if (config == null) {
			// 没有在线, 随机一个进程处理.
			if (nullRandom) {
				return GameChannelMgr.getRandomServiceByModule(moduleName, serviceClass);
			}
			return null;
		}

		// 获取连接
		T service = getChannelService(config, serviceClass);
		if (service == null) {
			// 这个进程不存在了
			MailBox.remove(playerId, moduleName);
			Log.warn("找不到玩家所在进程连接, 清除连接! playerId=" + playerId + " moduleName=" + moduleName + " config=" + config);
			// 清除连接, 随机一个处理
			if (nullRandom) {
				return GameChannelMgr.getRandomServiceByModule(moduleName, serviceClass);
			}
			return null;
		}
		return service;
	}

	/**
	 * 获取指定玩家所在模块的服务<br>
	 * 如果玩家不在内存, 返回随机进程
	 * **/
	public static <T, M extends Enum<?>> T getChannelServiceByPlayerId(long playerId, M moduleName, Class<T> serviceClass) {
		return getChannelServiceByPlayerId(playerId, moduleName, serviceClass, true);
	}

	/** 获取所有连接 **/
	public static List<ProxyChannel> getChannels() {
		List<ProxyChannel> channels = new ArrayList<>();
		for (ProxyChannelMgr<ServerConfig> mgr : mgrs.values()) {
			channels.addAll(mgr.getChannels());
		}
		return channels;
	}

	/** 获取指定模块的服务 **/
	public static <T, M extends Enum<?>> List<T> getChannelServicesByModule(M moduleName, Class<T> serviceClass) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(ServerConfig.TYPE_GAME);
		if (mgr == null) {
			return null;
		}
		// 获取配置
		List<ServerConfig> configs = ModuleMgr.getServerConfigs(moduleName);
		List<ProxyChannel> channels = mgr.getChannelsByConfigs(configs);
		int csize = (channels != null) ? channels.size() : 0;
		if (csize <= 0) {
			return null;
		}
		// 遍历获取服务
		List<T> services = new ArrayList<>();
		for (int i = 0; i < csize; i++) {
			services.add(channels.get(i).createImpl(serviceClass));
		}
		return services;
	}

	/** 获取指定模块的服务 **/
	public static <T> T getChannelService(ServerConfig config, Class<T> serviceClass) {
		ProxyChannel proxyChannel = getChannel(config);
		return (proxyChannel != null) ? proxyChannel.createImpl(serviceClass) : null;
	}

	/** 获取连接(全部中找) **/
	public static ProxyChannel getChannel(ServerConfig config) {
		for (ProxyChannelMgr<ServerConfig> mgr : mgrs.values()) {
			ProxyChannel channel = mgr.getChannel(config);
			if (channel != null) {
				return channel;
			}
		}
		return null;
	}

	/** 根据配置获取连接 **/
	public static <T extends Enum<?>> List<ProxyChannel> getChannelsByModule(T moduleName) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(ServerConfig.TYPE_GAME);
		if (mgr == null) {
			return null;
		}
		// 获取配置
		List<ServerConfig> configs = ModuleMgr.getServerConfigs(moduleName);
		return mgr.getChannelsByConfigs(configs);
	}

	/** 随机一个模块连接 **/
	protected static <T extends Enum<?>> ProxyChannel getRandomChannelByModule(T moduleName) {
		List<ProxyChannel> proxyChannels = getChannelsByModule(moduleName);
		int psize = (proxyChannels != null) ? proxyChannels.size() : 0;
		if (psize <= 0) {
			return null;
		}
		// 随机一个
//		int r = (int) (Math.random() * psize);
		int r = RandomUtils.randomInt(psize);
		return proxyChannels.get(r);
	}
	
	/** 随机一个模块服务,独立模块就是唯一的. **/
	public static <E extends Enum<?>, T> T getRandomServiceByModule(E moduleName, Class<T> serviceClass) {
		ProxyChannel proxyChannel = getRandomChannelByModule(moduleName);
		return (proxyChannel != null) ? proxyChannel.createImpl(serviceClass) : null;
	}

	public static List<ProxyChannel> getChannels(int type) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getChannels() : null;
	}

	public static List<ServerConfig> getConfigs(int type) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getAllConfigs() : null;
	}

	public static ServerConfig getConfig(int type, int appId) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getConfig(appId) : null;
	}

	public static ProxyChannel getChannel(int type, int appId) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getChannel(appId) : null;
	}

	public static ProxyChannel getChannel(int type, ServerConfig config) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getChannel(config) : null;
	}

	// /** 获取连接列表 **/
	// protected static SGChannelMgr getChannelMgr(int type)
	// {
	// return mgrs.get(type);
	// }

	/** 获取连接列表 **/
	@SuppressWarnings("unchecked")
	public static <T extends ProxyChannelMgr<ServerConfig>> T getChannelMgr(int type) {
		return (T) mgrs.get(type);
	}

	/** 获取随机连接 **/
	public static ProxyChannel getRandomChannel(int type) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getRandomChannel() : null;
	}

	/** 获取随机连接 **/
	public static ServerConfig getRandomConfig(int type) {
		ProxyChannelMgr<ServerConfig> mgr = getChannelMgr(type);
		return (mgr != null) ? mgr.getRandomConfig() : null;
	}
}
