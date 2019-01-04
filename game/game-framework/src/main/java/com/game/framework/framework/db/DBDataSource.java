package com.game.framework.framework.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * DBPool 数据源<br>
 * 把DBPool转成DataSource
 * DBDataSource.java
 * @author JiangBangMing
 * 2019年1月3日下午2:53:16
 */
public class DBDataSource implements DataSource {
	private DBPool dbPool;

	public DBDataSource(DBPool dbPool) {
		this.dbPool = dbPool;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dbPool.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
}
