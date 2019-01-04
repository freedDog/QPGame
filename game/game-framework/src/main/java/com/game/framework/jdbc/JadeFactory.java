package com.game.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import com.game.framework.jdbc.executer.JdbcExecuter;
import com.game.framework.jdbc.executer.JdbcQuery;
import com.game.framework.jdbc.executer.JdbcUpdate;
import com.game.framework.jdbc.executer.mapper.JdbcRowMapper;



/**
 * jdbc工厂<br>
 * jdbc : Java数据库连接(Java DataBase Connectivity)
 * JadeFactory.java
 * @author JiangBangMing
 * 2019年1月3日下午4:27:32
 */
public class JadeFactory extends JadeBaseFactory {
	protected final DataSource dataSource;
	protected final ConcurrentMap<Class<?>, Object> daos;

	public JadeFactory(DataSource dataSource) {
		this.dataSource = dataSource;
		daos = new ConcurrentHashMap<>();
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@SuppressWarnings("unchecked")
	public <T> T getDAO(Class<T> clazz) {
		Object obj = daos.get(clazz);
		if (obj != null) {
			return (T) obj;
		}

		// 创建DAO
		Object dao = null;
		try {
			dao = createDAO(clazz);
			if (dao == null) {
				return null; // 创建失败
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// 插入数据
		Object old = daos.putIfAbsent(clazz, dao);
		dao = (old != null) ? old : dao;
		return (T) dao;
	}

	/** 查询返回单个结果 **/
	public static <T> T jdbcQueryOnce(Connection conn, Class<T> retType, String sql, Object... args) throws Exception {
		List<T> list = jdbcQuery(conn, retType, sql, args);
		int lsize = (list != null) ? list.size() : 0;
		return (lsize > 0) ? list.get(0) : null;
	}

	/** 查询 **/
	@SuppressWarnings("unchecked")
	public static <T> List<T> jdbcQuery(Connection conn, Class<T> retType, String sql, Object... args) throws Exception {
		PreparedStatement ps = null;

		try {
			// 获取处理器
			JdbcRowMapper<?> mapper = JdbcRowMapper.createRowMapper(retType);
			// 遍历处理
			ps = JdbcQuery.createPreparedStatement(conn, sql, args);
			List<?> list = JdbcQuery.doInPreparedStatement(ps, mapper);
			// 返回数据
			return (List<T>) list;
		} catch (Exception e) {
			System.err.println("查询执行错误!? " + sql + " " + Arrays.toString(args));
			throw e;
		} finally {
			JdbcExecuter.close(ps);
			JdbcExecuter.close(conn);
		}
	}

	/**
	 * 更新方法<br>
	 * 
	 * @param generatedKeys
	 *            是否返回主键
	 * @param totalResult
	 *            是否统一结果(成功数量)
	 * **/
	public static <T> List<T> jdbcUpdate(Connection conn, Class<T> retType, boolean generatedKeys, boolean totalResult, String sql, List<Object[]> batchArgs) throws Exception {
		if (conn == null) {
			throw new Exception("无法获取数据库连接!");
		}
		PreparedStatement ps = null;
		try {
			// 遍历处理
			ps = JdbcUpdate.createPreparedStatement(conn, sql, batchArgs, generatedKeys);
			List<T> list = JdbcUpdate.doInPreparedStatement(ps, retType, generatedKeys, totalResult);
			// 返回数据
			return list;
		} catch (Exception e) {
			System.err.println("查询执行错误!? " + sql + " " + batchArgs);
			throw e;
		} finally {
			JdbcExecuter.close(ps);
			JdbcExecuter.close(conn);
		}
	}

	/** 更新方法 **/
	public static <T> List<T> jdbcUpdate(Connection conn, Class<T> retType, boolean generatedKeys, boolean totalResult, String sql, Object[] args) throws Exception {
		List<Object[]> batchArgs = new ArrayList<>();
		batchArgs.add(args);
		return jdbcUpdate(conn, retType, generatedKeys, totalResult, sql, batchArgs);
	}

	/** 更新方法 **/
	public static <T> List<T> jdbcUpdate(Connection conn, Class<T> retType, String sql, Object[] args) throws Exception {
		List<Object[]> batchArgs = new ArrayList<>();
		batchArgs.add(args);
		return jdbcUpdate(conn, retType, false, true, sql, batchArgs);
	}

}
