package com.game.base.service.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.utils.RandomUtils;
import com.game.framework.component.log.Log;

/**
 * 模块管理器<br>
 * 管理当前服务器模块的信息
 * ModuleMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午4:02:00
 */
public class ModuleMgr {
	protected static Map<String, List<ModuleConfig>> moduleInfos = new HashMap<>(); // 模块列表
	protected static Map<String, List<ServerConfig>> serverInfos = new HashMap<>(); // 模块对应服务器列表

	/** 同步模块信息 **/
	public static boolean syncModuleInfos(Map<String, List<ModuleConfig>> moduleInfos) {
		// 整理模板
		Map<String, List<ServerConfig>> serverInfos = new HashMap<>();
		for (Map.Entry<String, List<ModuleConfig>> entry : moduleInfos.entrySet()) {
			// 创建列表
			String moduleName = entry.getKey();

			// 获取这个模块对应的服务器列表
			List<ServerConfig> serverConfigs0 = serverInfos.get(moduleName);
			if (serverConfigs0 == null) {
				serverConfigs0 = new ArrayList<>();
				serverInfos.put(moduleName, serverConfigs0);
			}

			// 遍历记录
			List<ModuleConfig> moduleConfigs0 = entry.getValue();
			for (ModuleConfig moduleConfig : moduleConfigs0) {
				serverConfigs0.add(moduleConfig.getServerConfig());
			}
		}
		ModuleMgr.moduleInfos = moduleInfos;
		ModuleMgr.serverInfos = serverInfos;

		Log.info(showModuleInfos("模块信息", moduleInfos));
		return true;
	}

	/** 获取模块列表 **/
	public static <T extends Enum<?>> List<ModuleConfig> getModuleConfigs(T moduleName) {
		return getModuleConfigs(moduleName.name());
	}

	/** 获取模块列表 **/
	public static List<ModuleConfig> getModuleConfigs(String moduleName) {
		return (moduleInfos != null) ? moduleInfos.get(moduleName) : null;
	}

	/** 获取对应模块名的服务器 **/
	public static List<ServerConfig> getServerConfigs(String moduleName) {
		return (serverInfos != null) ? serverInfos.get(moduleName) : null;
	}

	/** 获取模块名对应的服务器 **/
	public static <T extends Enum<?>> List<ServerConfig> getServerConfigs(T moduleName) {
		return getServerConfigs(moduleName.name());
	}

	/** 获取模块对应区服 **/
	public static ServerConfig getServerConfig(ModuleName moduleName) {
		return getServerConfig(moduleName, 0);
	}

	/** 获取模块对应区服 **/
	public static ServerConfig getServerConfig(ModuleName moduleName, int gameZoneId) {
		return getServerConfig(moduleName.name(), gameZoneId);
	}

	/** 根据模块获取对应区服(第一个) **/
	public static ServerConfig getServerConfig(String moduleName, int gameZoneId) {
		// 检测是否有这个模块
		List<ModuleConfig> configs = getModuleConfigs(moduleName);
		int csize = (configs != null) ? configs.size() : 0;
		if (csize <= 0) {
			return null;
		}

		// gameZoneId为0代表随机1个.
		if (gameZoneId <= 0) {
			int r = RandomUtils.randomInt(configs.size());
//			int r = (int) (Math.random() * csize);
			return configs.get(r).getServerConfig();
		}

		// 遍历查找符合的区服
		for (int i = 0; i < csize; i++) {
			ModuleConfig config = configs.get(i);
			if (config != null && config.checkGameZoneId(gameZoneId)) {
				return config.getServerConfig();
			}
		}
		return null;
	}

	/** 输出模块信息 **/
	public static String showModuleInfos(String text, Map<String, List<ModuleConfig>> moduleConfigs) {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(text);
		strBdr.append(":{\n");
		for (Map.Entry<String, List<ModuleConfig>> entry : moduleConfigs.entrySet()) {
			List<ModuleConfig> mclist = entry.getValue();
			String moduleName = entry.getKey();
			// 写入信息
			strBdr.append(moduleName);
			strBdr.append(": \n\t[\n");
			for (ModuleConfig moduleConfig : mclist) {
				strBdr.append("\t");
				strBdr.append(moduleConfig);
				strBdr.append("\n");
			}
			strBdr.append("\t]\n");
		}
		strBdr.append("}");
		return strBdr.toString();

	}
}
