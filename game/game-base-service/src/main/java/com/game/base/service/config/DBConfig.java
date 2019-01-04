package com.game.base.service.config;

import java.util.Arrays;

/**
 * DB路由配置
 * DBConfig.java
 * @author JiangBangMing
 * 2019年1月4日下午5:46:31
 */
public class DBConfig {
	protected String dbName; // db名称
	protected String host; // 数据库地址
	protected int port; // 数据库端口
	protected String username; // 数据库名
	protected String password; // 数据库密码
	protected short minConnCount; // 初始连接数
	protected short maxConnCount; // 最大连接数
	protected int[] gameZoneIds; // 对应区服

	public DBConfig(int[] gameZoneIds, String dbName, String host, int port, String username, String password, short minConnCount, short maxConnCount) {
		super();
		this.gameZoneIds = gameZoneIds;
		this.dbName = dbName;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.minConnCount = minConnCount;
		this.maxConnCount = maxConnCount;
	}

	protected DBConfig() {
	}

	/** 检测是否包含gameZone **/
	public boolean checkGameZoneId(int gameZoneId) {
		int gsize = (gameZoneIds != null) ? gameZoneIds.length : 0;
		for (int i = 0; i < gsize; i++) {
			int gameZoneId0 = gameZoneIds[i];
			if (gameZoneId0 == 0 || gameZoneId0 == gameZoneId) {
				return true;
			}
		}
		return false;
	}

	public int[] getGameZoneIds() {
		return gameZoneIds;
	}

	public String getDbName() {
		return dbName;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public short getMinConnCount() {
		return minConnCount;
	}

	public short getMaxConnCount() {
		return maxConnCount;
	}

	@Override
	public String toString() {
		return "DBConfig [gameZoneIds=" + Arrays.toString(gameZoneIds) + ", dbName=" + dbName + ", host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
				+ ", minConnCount=" + minConnCount + ", maxConnCount=" + maxConnCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBConfig other = (DBConfig) obj;
		if (dbName == null) {
			if (other.dbName != null)
				return false;
		} else if (!dbName.equals(other.dbName))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

}

