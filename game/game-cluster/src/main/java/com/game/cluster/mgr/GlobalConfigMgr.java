package com.game.cluster.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.DBConfig;
import com.game.base.service.config.GlobalConfig;
import com.game.base.service.db.DBConfigMgr;
import com.game.base.utils.DataUtils;
import com.game.framework.component.log.Log;
import com.game.framework.framework.redis.RedisPool.RedisConfig;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.StringUtils;

/**
 * GameManager全局配置管理器
 * 
 */
public class GlobalConfigMgr {
	protected static GlobalConfig config;

	public static boolean init() {
		return reload();
	}

	public static GlobalConfig getConfig() {
		return config;
	}

	/** 重新加载 **/
	public static boolean reload() {
		// 重载日志数据库配置
		List<DBConfig> logDbConfigs = reloadLogDBConfigs();
		if (logDbConfigs == null) {
			return false;
		}

		// 重载数据库配置
		List<DBConfig> dbConfigs = reloadDBConfigs();
		if (dbConfigs == null) {
			return false;
		}

		// 重载redis数据库
		List<RedisConfig> redisConfigs = reloadRedisConfigs();
		if (redisConfigs == null) {
			return false;
		}

		// 创建配置
		GlobalConfig temp = new GlobalConfig();
		temp.setDbconfigs(dbConfigs);
		temp.setRedisConfigs(redisConfigs);
		temp.setLogDbconfigs(logDbConfigs);

		// 替换配置
		config = temp;

		// 触发同步
		GameMgrChannelMgr.syncGlobalConfigsToAll();

		return true;
	}

	/** 重载Redis配置 **/
	private static List<RedisConfig> reloadRedisConfigs() {
		// 处理数据库连接
		XmlNode rootNode = ConfigMgr.getConfig();
		List<XmlNode> nodes = XmlNode.getElems(rootNode, "Redis");
		int nsize = (nodes != null) ? nodes.size() : 0;
		if (nsize <= 0) {
			Log.error("没有Redis配置! DBA Node");
			return null;
		}

		// 遍历处理
		List<RedisConfig> configList = new ArrayList<>();
		for (int i = 0; i < nsize; i++) {
			XmlNode node = nodes.get(i);
			// 检测连接
			String host = node.getAttr("host", "");
			int port = node.getAttr("port", 0);
			if (StringUtils.isEmpty(host) || port <= 0) {
				Log.error("redis配置错误! redis=" + node);
				return null;
			}
			// 其他信息
			String auth = node.getAttr("auth", "");

			// 创建配置
			RedisConfig config = new RedisConfig(host, port, auth);
			configList.add(config);
		}
		return configList;
	}

	/** 重载数据库配置 **/
	private static List<DBConfig> reloadDBConfigs() {
		// 处理数据库连接
		XmlNode config = ConfigMgr.getConfig();
		List<XmlNode> nodes = XmlNode.getElems(config, "DBA");
		int nsize = (nodes != null) ? nodes.size() : 0;
		if (nsize <= 0) {
			Log.error("没有数据库配置! DBA Node");
			return null;
		}

		// 遍历处理
		List<DBConfig> configList = new ArrayList<>();
		for (int i = 0; i < nsize; i++) {
			XmlNode node = nodes.get(i);
			// 检测连接
			String host = node.getAttr("host", "");
			int port = node.getAttr("port", 0);
			String dbName = node.getAttr("dbName", "");
			if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(host) || port <= 0) {
				Log.error("数据库配置错误! dba=" + node);
				return null;
			}
			// 其他信息
			String username = node.getAttr("username", "");
			String password = node.getAttr("password", "");
			short minConnCount = node.getAttr("minConnCount", (short) 15);
			short maxConnCount = node.getAttr("maxConnCount", (short) 30);

			// 创建配置
			String gameZondIdStrs = node.getAttr("gameZoneIds", "0");
			int[] gameZoneIds = DataUtils.splitToInt(gameZondIdStrs, ",");
			DBConfig dbconfig = new DBConfig(gameZoneIds, dbName, host, port, username, password, minConnCount, maxConnCount);
			configList.add(dbconfig);
		}

		// 整理检测数据库
		Map<Integer, DBConfig> configMap = new HashMap<>();
		if (!DBConfigMgr.getInstance().initAndCheckConfigs(configList, configMap)) {
			return null;
		}
		return configList;
	}

	/** 重载数据库配置 **/
	private static List<DBConfig> reloadLogDBConfigs() {
		// 处理数据库连接
		XmlNode config = ConfigMgr.getConfig();
		List<XmlNode> nodes = XmlNode.getElems(config, "LogDBA");
		int nsize = (nodes != null) ? nodes.size() : 0;
		if (nsize <= 0) {
			Log.error("没有数据库配置! LogDBA Node");
			return null;
		}

		// 遍历处理
		List<DBConfig> configList = new ArrayList<>();
		for (int i = 0; i < nsize; i++) {
			XmlNode node = nodes.get(i);
			// 检测连接
			String host = node.getAttr("host", "");
			int port = node.getAttr("port", 0);
			String dbName = node.getAttr("dbName", "");
			if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(host) || port <= 0) {
				Log.error("数据库配置错误! dba=" + node);
				return null;
			}
			// 其他信息
			String username = node.getAttr("username", "");
			String password = node.getAttr("password", "");
			short minConnCount = node.getAttr("minConnCount", (short) 15);
			short maxConnCount = node.getAttr("maxConnCount", (short) 30);

			// 创建配置
			String gameZondIdStrs = node.getAttr("gameZoneIds", "0");
			int[] gameZoneIds = DataUtils.splitToInt(gameZondIdStrs, ",");
			DBConfig dbconfig = new DBConfig(gameZoneIds, dbName, host, port, username, password, minConnCount, maxConnCount);
			configList.add(dbconfig);
		}

		// 整理检测数据库
		Map<Integer, DBConfig> configMap = new HashMap<>();
		if (!DBConfigMgr.getInstance().initAndCheckConfigs(configList, configMap)) {
			return null;
		}
		return configList;
	}
}
