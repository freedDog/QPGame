package com.game.base.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.DBConfig;
import com.game.framework.component.log.Log;

/**
 * DB路由表<br>
 * 用于管理更新路由表内容<br>
 * 
 * DBConfigMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午5:49:20
 */
public class DBConfigMgr {

	protected Map<Integer, DBConfig> configMap = new HashMap<>();
	protected List<DBConfig> configs = new ArrayList<>();

	/** 获取区服对应DB配置 **/
	public DBConfig getConfig(int gameZoneId) {
		return configMap.get(gameZoneId);
	}

	/** 获取DB配置 **/
	public List<DBConfig> getConfigs() {
		return configs;
	}

	/** 检测整理配置列表, 把列表配置整理成Map, 检测区服冲突. **/
	public boolean initAndCheckConfigs(List<DBConfig> configs, Map<Integer, DBConfig> configMap) {
		int csize = (configs != null) ? configs.size() : 0;
		for (int i = 0; i < csize; i++) {
			DBConfig config = configs.get(i);
			// 检测gameZoneId冲突
			int[] gameZoneIds = config.getGameZoneIds();
			for (int gameZoneId : gameZoneIds) {
				DBConfig old = configMap.put(gameZoneId, config);
				if (old != null) {
					Log.error("存在相同的gameZoneId! gameZoneId=" + gameZoneId + " config=" + config);
					return false;
				}
			}
		}
		return true;
	}

	/************************ 静态句柄 *****************************/

	protected static DBConfigMgr instance = new DBConfigMgr(); // 默认

	@SuppressWarnings("unchecked")
	public static <T extends DBConfigMgr> T getInstance() {
		return (T) instance;
	}
}

