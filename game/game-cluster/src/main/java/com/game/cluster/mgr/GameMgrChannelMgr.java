package com.game.cluster.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.GlobalConfig;
import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.constant.ProxyParamType;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.rpc.handler.IServerMgrClient;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyChannelMgr;
import com.game.framework.framework.rpc.impl.ProxyConnectMgr;
import com.game.framework.utils.collections.ArrayUtils;

/**
 * 游戏管理器, 连接管理.
 * 
 */
public class GameMgrChannelMgr extends GameChannelMgr {
	public static boolean init() {
		// 创建这3中类型的连接管理
		mgrs.put(ServerConfig.TYPE_GAME, new ProxyConnectMgr<ServerConfig>());
		mgrs.put(ServerConfig.TYPE_GATE, new ProxyConnectMgr<ServerConfig>());
		return true;
	}

	/** 获取对应模块的配置 **/
	protected static ModuleConfig findModuleInfo(String moduleName, int[] gameZoneIds) {
		int gsize = (gameZoneIds != null) ? gameZoneIds.length : 0;

		// 遍历所有连接
		ProxyChannelMgr<ServerConfig> games = getChannelMgr(ServerConfig.TYPE_GAME);
		for (ProxyChannel channel : games.getChannels()) {
			// 获取模块信息
			List<ModuleConfig> configs = channel.getParam(ProxyParamType.PARAM_MODULES);
			int csize = (configs != null) ? configs.size() : 0;
			for (int i = 0; i < csize; i++) {
				ModuleConfig config = configs.get(i);
				if (!moduleName.equals(config.getName())) {
					continue;
				}
				// 检测是否有区服限制
				if (gsize <= 0) {
					return config; // 没有限制, 只要是这个名字都可以
				}

				// 判断区服
				int[] checkIds = config.getGameZoneIds();
				for (int j = 0; j < gsize; j++) {
					int gameZoneId = gameZoneIds[j];
					// 检测是否包含这个gameZoneId
					if (ArrayUtils.contains(checkIds, gameZoneId)) {
						return config; // 包含这个gameZoneId了
					}
				}
			}
		}
		return null;
	}

	/** 检测模块是否冲突 **/
	public static boolean checkModuleInfos(List<ModuleConfig> moduleConfigs) {
		// 检测模块
		int csize = (moduleConfigs != null) ? moduleConfigs.size() : 0;
		if (csize <= 0) {
			return true;
		}

		// 遍历检测模块配置
		for (ModuleConfig moduleConfig : moduleConfigs) {
			// 判断是否全局没有区服限制
			int[] gameZoneIds = moduleConfig.getGameZoneIds();
			int gsize = (gameZoneIds != null) ? gameZoneIds.length : 0;
			if (gsize > 0) {
				// 检测当前所有游戏连接的模块是否冲突
				ModuleConfig conflict = findModuleInfo(moduleConfig.getName(), gameZoneIds);
				if (conflict != null) {
					Log.warn("模块区服存在冲突!? " + conflict + " -> " + moduleConfig);
					return false;
				}
			}

			// 检测是否是独占模块
			int mode = moduleConfig.getMode();
			if (mode == ModuleConfig.MODE_UNIQUE) {
				// 检测当前所有游戏连接的模块是否冲突
				ModuleConfig conflict = findModuleInfo(moduleConfig.getName(), null);
				if (conflict != null) {
					Log.warn("独占模块存在冲突!? " + conflict + " -> " + moduleConfig);
					return false;
				}

			}
		}
		return true;
	}

	/** 创建模块信息列表 **/
	protected static Map<String, List<ModuleConfig>> createModuleInfos() {
		Map<String, List<ModuleConfig>> moduleInfos = new HashMap<>();

		// 同步所有服务器模块信息
		ProxyConnectMgr<ServerConfig> games = getChannelMgr(ServerConfig.TYPE_GAME);
		for (ProxyChannel channel : games.getChannels()) {
			// 获取模块信息
			List<ModuleConfig> moduleConfigs = channel.getParam(ProxyParamType.PARAM_MODULES);
			int msize = (moduleConfigs != null) ? moduleConfigs.size() : 0;
			// 遍历整理模块列表
			for (int i = 0; i < msize; i++) {
				ModuleConfig moduleConfig = moduleConfigs.get(i);
				String moduleName = moduleConfig.getName();
				List<ModuleConfig> configs = moduleInfos.get(moduleName);
				if (configs == null) {
					configs = new ArrayList<>();
					moduleInfos.put(moduleName, configs);
				}
				configs.add(moduleConfig);
			}
		}
		return moduleInfos;
	}

	/** 同步单个服务器的全局配置 **/
	public static void syncGlobalConfigs(ProxyChannel channel) {
		GlobalConfig config = GlobalConfigMgr.getConfig();
		IServerMgrClient client = channel.createImpl(IServerMgrClient.class);
		client.syncGlobalConfig(channel, config);
	}

	/** 同步全局配置到所有服务器 **/
	public static void syncGlobalConfigsToAll() {
		// 同步所有服务器模块信息
		GlobalConfig config = GlobalConfigMgr.getConfig();

		// 遍历发送到所有服
		for (ProxyChannelMgr<ServerConfig> mgr : mgrs.values()) {
			for (ProxyChannel channel : mgr.getChannels()) {
				IServerMgrClient client = channel.createImpl(IServerMgrClient.class);
				client.syncGlobalConfig(channel, config);
			}
		}
	}

	/** 同步模块信息到所有服务器 **/
	public static void syncModuleInfoToAll() {
		// 同步所有服务器模块信息
		Map<String, List<ModuleConfig>> moduleInfos = createModuleInfos();

		// 遍历发送到所有服
		for (ProxyChannelMgr<ServerConfig> mgr : mgrs.values()) {
			for (ProxyChannel channel : mgr.getChannels()) {
				try {
					IServerMgrClient client = channel.createImpl(IServerMgrClient.class);
					client.syncModuleInfos(channel, moduleInfos);
				} catch (Exception e) {
					Log.warn("同步模块信息失败! channel=" + channel);
				}
			}
		}
	}

	/** 同步单个服务器的模块信息 **/
	public static void syncModuleInfos(ProxyChannel channel) {
		Map<String, List<ModuleConfig>> moduleInfos = createModuleInfos();
		IServerMgrClient client = channel.createImpl(IServerMgrClient.class);
		client.syncModuleInfos(channel, moduleInfos);
	}

	/** 同步服务器信息到所有服务器 **/
	public static void sycnServerInfosToAll() {
		// 创建服务器列表
		Map<Integer, List<ServerConfig>> serverInfos = new HashMap<>();
		for (Map.Entry<Integer, ProxyChannelMgr<ServerConfig>> entry : mgrs.entrySet()) {
			Integer type = entry.getKey();
			ProxyChannelMgr<ServerConfig> mgr = entry.getValue();
			// 插入服务器列表
			serverInfos.put(type, mgr.getAllConfigs());

			// 输出日志
			Log.info(ProxyChannelMgr.showServerInfos("更新" + ServerConfig.getServerTypeStr(type) + "列表", mgr.getAllConfigs()));
		}

		// 遍历发送到所有服
		for (ProxyChannelMgr<ServerConfig> mgr : mgrs.values()) {
			for (ProxyChannel channel : mgr.getChannels()) {
				try {
					IServerMgrClient client = channel.createImpl(IServerMgrClient.class);
					client.sycnServerInfos(channel, serverInfos);
				} catch (Exception e) {
					Log.warn("同步服务器信息失败! channel=" + channel);
				}
			}
		}

	}

	/** 注册游戏服 **/
	public static synchronized boolean register(ProxyChannel channel, ServerConfig config, int type) {
		// 判断是否与自身的配置相同
		ServerConfig selfConfig = App.getInstance().getConfig();
		if (selfConfig.equals(config)) {
			Log.error("配置与自身冲突! config=" + config + " selfConfig=" + selfConfig);
			return false;
		}

		// 全局获取是否存在连接
		ProxyChannel old = getChannel(config);
		if (old != null) {
			Log.error("存在相同配置的连接! config=" + config + " old=" + old);
			return false;
		}

		// 获取类型的管理器
		ProxyConnectMgr<ServerConfig> mgr = (ProxyConnectMgr<ServerConfig>) getChannelMgr(type);
		if (mgr == null) {
			Log.error("未知服务器类型! type=" + type, true);
			return false;
		}

		// 绑定信息, 在注册前绑定, 否则可能会断开时
		channel.setConfig(config);
		channel.setParam(ProxyParamType.PARAM_TYPE, type);

		// 插入 检测这个服务器是否占用了.
		if (!mgr.register(channel, config)) {
			return false;
		}

		// 服务器连接成功
		Log.info(ServerConfig.getServerTypeStr(type) + "成功注册: info=" + config);
		return true;
	}

	/** 注册游戏服 **/
	public static synchronized boolean unregister(ProxyChannel channel) {
		// 关闭信息
		ServerConfig info = channel.getConfig();
		if (info == null) {
			return true;
		}
		// 处理连接
		int type = channel.getParam(ProxyParamType.PARAM_TYPE);
		ProxyConnectMgr<ServerConfig> mgr = (ProxyConnectMgr<ServerConfig>) getChannelMgr(type);
		if (mgr == null) {
			Log.error("未知服务器类型! type=" + type, true);
			return false;
		}
		// 尝试移除
		if (!mgr.unregister(channel)) {
			return false; // 被其他线程移除了.
		}

		// 同步服务器信息
		sycnServerInfosToAll();
		// 如果是游戏服断开, 那么更新模块内容(网关服没有模块)
		syncModuleInfoToAll();
		Log.info(ServerConfig.getServerTypeStr(type) + "断开连接: info=" + info);
		return true;
	}
}