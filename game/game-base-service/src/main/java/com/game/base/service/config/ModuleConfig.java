package com.game.base.service.config;

import java.util.Arrays;

/**
 * 模块配置<br>
 * 逻辑服加载模块配置
 * ModuleConfig.java
 * @author JiangBangMing
 * 2019年1月4日下午3:56:23
 */
public class ModuleConfig {
	/** 唯一模块 **/
	public static final int MODE_UNIQUE = 1;

	protected String name; // 模块名
	protected int[] gameZoneIds; // 模板对应区服
	protected int mode; // 配置模式
	protected ServerConfig serverConfig; // 模块所在服务器配置

	protected ModuleConfig() {
	}

	public ModuleConfig(String name, ServerConfig serverConfig, int[] gameZoneIds, int mode) {
		super();
		this.name = name;
		this.gameZoneIds = gameZoneIds;
		this.serverConfig = serverConfig;
		this.mode = mode;
	}

	/** 获取模块名 **/
	public String getName() {
		return name;
	}

	/** 获取模块对应区服Id **/
	public int[] getGameZoneIds() {
		return gameZoneIds;
	}

	/** 获取对应服务器配置 **/
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	/** 检测是否包含gameZone **/
	public boolean checkGameZoneId(int gameZoneId) {
		// 检测是否有区服限制
		int gsize = (gameZoneIds != null) ? gameZoneIds.length : 0;
		if (gsize <= 0) {
			return true;
		}
		// 遍历检测是否符合
		for (int i = 0; i < gsize; i++) {
			int gameZoneId0 = gameZoneIds[i];
			if (gameZoneId0 == 0 || gameZoneId0 == gameZoneId) {
				return true; // 区服匹配或者存在0号区服代表全部通过
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String gzStr = (gameZoneIds != null) ? ", gameZoneIds=" + Arrays.toString(gameZoneIds) : "";
		return "ModuleConfig [name=" + name + gzStr + ", " + serverConfig + "]";
	}

	public int getMode() {
		return mode;
	}

}
