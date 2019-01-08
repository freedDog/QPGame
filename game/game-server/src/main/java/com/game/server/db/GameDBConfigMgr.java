package com.game.server.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.DBConfig;
import com.game.base.service.db.DBConfigMgr;

/**
 * DB路由表<br>
 * 用于管理更新路由表内容<br>
 * GameDBConfigMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:47:46
 */
public class GameDBConfigMgr extends DBConfigMgr {
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
		DBConfigMgr.instance = new GameDBConfigMgr();
	}

	public static GameDBConfigMgr getInstance() {
		return (GameDBConfigMgr) instance;
	}

}