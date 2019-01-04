package com.game.framework.framework.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.game.framework.component.log.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * 单个数据库连接池<br>
 * DBPool.java
 * @author JiangBangMing
 * 2019年1月3日下午2:50:50
 */
public class DBPool {
	private static final String URL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false";

	// 目前采用的是HikariCP2.4.9的版本（因为目前支持JDK7的最新版本），如果后续升级到JDK8版本可以考虑升级对应的HikariCP的最新版本。
	private HikariDataSource dbPool;
	private String dbUrl;
	private int minConnCount;
	private int maxConnCount;

	public DBPool(String host, int port, String dbName, String userName, String password, int minConn, int maxConn) {
		this(String.format(URL, host, port, dbName), userName, password, minConn, maxConn);
	}

	public DBPool(String dbUrl, String userName, String password, int minConn, int maxConn) {
		init(dbUrl, userName, password, minConn, maxConn);
	}

	/** 不初始化构造 **/
	public DBPool() {
	}

	/** 初始化 **/
	protected boolean init(String host, int port, String dbName, String userName, String password, int minConn, int maxConn) {
		return init(String.format(URL, host, port, dbName), userName, password, minConn, maxConn);
	}

	/** 初始化 **/
	protected boolean init(String dbUrl, String userName, String password, int minConn, int maxConn) {
		// Log.debug("数据库连接: " + dbUrl);
		this.dbUrl = dbUrl;
		this.minConnCount = minConn;
		this.maxConnCount = maxConn;
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setJdbcUrl(dbUrl);
		config.setUsername(userName);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", true);
		config.addDataSourceProperty("prepStmtCacheSize", 250);
		config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
		config.setMinimumIdle(minConn);
		config.setMaximumPoolSize(maxConn);
		dbPool = new HikariDataSource(config);
		return true;
	}

	/**
	 * 获得一个数据库连接
	 */
	public Connection getConnection() {
		try {
			return (dbPool != null) ? dbPool.getConnection() : null;
		} catch (SQLException e) {
			Log.error("获取数据库连接失败", e);
			return null;
		}
	}

	/**
	 * 关闭数据库连接池
	 */
	public void shutdown() {
		if (dbPool != null) {
			dbPool.close();
		}
	}

	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("DBPool[");
		strBdr.append("dbUrl=").append(dbUrl);
		strBdr.append(" minConnCount=").append(minConnCount);
		strBdr.append(" maxConnCount=").append(maxConnCount);
		strBdr.append("]");
		return strBdr.toString();
	}
}