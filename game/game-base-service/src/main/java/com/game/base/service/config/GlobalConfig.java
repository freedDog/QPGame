package com.game.base.service.config;

import java.util.List;

import com.game.framework.framework.redis.RedisPool.RedisConfig;

/**
 * 全局配置<br>
 * 指从GameManager进程同步过来的配置
 * GlobalConfig.java
 * @author JiangBangMing
 * 2019年1月4日下午5:45:58
 */
public class GlobalConfig {
	protected List<DBConfig> dbConfigs;
	protected List<RedisConfig> redisConfigs;
	protected List<DBConfig> logDbConfigs;

	public List<DBConfig> getDbConfigs() {
		return dbConfigs;
	}

	public void setDbconfigs(List<DBConfig> dbConfigs) {
		this.dbConfigs = dbConfigs;
	}
	
	public List<DBConfig> getLogDbConfigs() {
		return logDbConfigs;
	}

	public void setLogDbconfigs(List<DBConfig> logDbConfigs) {
		this.logDbConfigs = logDbConfigs;
	}

	public List<RedisConfig> getRedisConfigs() {
		return redisConfigs;
	}

	public void setRedisConfigs(List<RedisConfig> redisConfigs) {
		this.redisConfigs = redisConfigs;
	}
}
