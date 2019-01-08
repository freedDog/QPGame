package com.game.server.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.DBConfig;
import com.game.base.service.db.LogDbConfigMgr;

/**
 * DB路由表<br>
 * 用于管理更新路由表内容<br>
 * LogGameDBConfigMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:44:40
 */
public class LogGameDBConfigMgr extends LogDbConfigMgr {
	/** 重载配置 **/
	public boolean reload(List<DBConfig> configList) {
		// 这里能同步的话, 都是manager上检测过了.
		Map<Integer, DBConfig> tempMap = new HashMap<>();
		if (!super.initAndCheckConfigs(configList, tempMap)) {
			return false;
		}
		configs = configList;
		configMap = tempMap;
		return true;
	}

	static {
		LogDbConfigMgr.instance = new LogGameDBConfigMgr();
	}

	public static LogGameDBConfigMgr getLogInstance() {
		return (LogGameDBConfigMgr) instance;
	}

}