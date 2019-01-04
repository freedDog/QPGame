package com.game.framework.jdbc.executer;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.game.framework.jdbc.annotation.IndependentReturn;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.executer.mapper.JdbcRowMapper;
import com.game.framework.jdbc.executer.param.JdbcParam;



/**
 * Jdbc执行器
 * JdbcExecuter.java
 * @author JiangBangMing
 * 2019年1月3日下午4:43:13
 */
public abstract class JdbcExecuter<T> {
	protected String baseSql; // 基础sql
	protected Class<?> retType; // 返回值(如果是List, 代表是子类型)

	protected String sql;
	protected List<JdbcParam> params; // 参数
	protected JdbcRowMapper<T> mapper; // 反射对象

	@SuppressWarnings("unchecked")
	public JdbcExecuter(String sql, Class<?>[] parameterTypes, Type retType) throws Exception {
		this.baseSql = sql;

		// 转换sql基础语句
		this.params = JdbcParam.createParams(sql, parameterTypes); // 解析sql语句中的参数
		this.sql = JdbcParam.resetSql(sql, params); // 整理出能执行的语句

		// 如果有返回值, 创建返回对象解析器.
		this.retType = getMethodReturnType(retType);
		if (!this.isEmptyReturn()) {
			mapper = JdbcRowMapper.createRowMapper((Class<T>) getReturnType());
			// 创建反射对象
			if (mapper == null) {
				throw new Exception("找不到符合的反射对象! " + getReturnType());
			}
		}

	}

	/** 获取dataSource **/
	protected abstract DataSource getDataSource();

	/** 根据参数生成执行过程 **/
	public abstract Process process(Object... args) throws Exception;

	/** 创建PreparedStatement **/
	protected abstract PreparedStatement createPreparedStatement(Connection con, Process process) throws Exception;

	/** 处理结果回馈 **/
	protected abstract List<T> doInPreparedStatement(PreparedStatement ps, Process process) throws Exception;

	/** 执行过程 **/
	public List<T> execute(Process process) throws Exception {
		// 获取连接
		DataSource dataSource = getDataSource();
		Connection conn = dataSource.getConnection();
		if (conn == null) {
			throw new Exception("无法获取数据库连接! dataSource=" + dataSource);
		}
		// 执行过程
		PreparedStatement ps = null;
		try {
			// 创建过程
			ps = createPreparedStatement(conn, process);
			// 处理执行
			return doInPreparedStatement(ps, process);
		} catch (Exception e) {
			System.err.println("执行SQL错误! " + sql + " " + e.toString() + " By " + process);
			throw e;
		} finally {
			close(ps);
			close(conn);
		}
	}

	/** 单对象返回 **/
	public final T executeOnce(Process process) throws Exception {
		// 执行
		List<T> list = this.execute(process);
		if (list != null && list.size() > 0) {
			T obj = list.get(0); // 正常返回
			return obj;
		}
		// 空值返回
		return createEmptyValue();
	}

	/** 空值返回 **/
	@SuppressWarnings("unchecked")
	public T createEmptyValue() {
		return (T) createEmptyValue(getReturnType());
	}

	/** 返回值 **/
	public Class<?> getReturnType() {
		return retType;
	}

	/** 是否空返回 **/
	public boolean isEmptyReturn() {
		return isEmptyReturn(getReturnType());
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/** 是否空返回 **/
	public static boolean isEmptyReturn(Class<?> retType) {
		if (retType == null || retType == Void.class || retType == void.class) {
			return true;
		}
		return false;
	}

	/** 空值返回 **/
	public static Object createEmptyValue(Class<?> retType) {
		if (retType == int.class) {
			return Integer.valueOf(0);
		} else if (retType == long.class) {
			return Long.valueOf(0);
		} else if (retType == short.class) {
			return Short.valueOf("0");
		} else if (retType == float.class) {
			return Float.valueOf(0.0f);
		} else if (retType == double.class) {
			return Double.valueOf(0.0);
		} else if (retType == boolean.class) {
			return Boolean.FALSE;
		}
		return null;
	}

	/** 关闭数据库连接 **/
	public static void close(Connection conn) {
		if (conn == null) {
			return;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 关闭PreparedStatement **/
	public static void close(PreparedStatement pstmt) {
		if (pstmt == null) {
			return;
		}
		try {
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 关闭ResultSet **/
	public static void close(ResultSet rs) {
		if (rs == null) {
			return;
		}

		try {
			rs.close();
		} catch (Throwable ex) {
			System.err.println("关闭jdbc ResultSet错误! ");
			ex.printStackTrace();
		}
	}

	/** 更新操作是否要返回主键 **/
	public static boolean isGeneratedKeys(Method method) {
		ReturnGeneratedKeys rk = method.getAnnotation(ReturnGeneratedKeys.class);
		return (rk != null);
	}

	/** 检测更新操作是否独立返回汇总结果 **/
	public static boolean isTotalResult(Method method) {
		IndependentReturn ir = method.getAnnotation(IndependentReturn.class);
		return (ir == null);
	}

	/** 获取函数返回值具体类型, 如果是List, 会获取List内的类型 **/
	protected static Class<?> getMethodReturnType(Type retType) {
		// 识别返回类型
		if (retType instanceof ParameterizedType) {
			// 泛型返回值
			ParameterizedType pt = (ParameterizedType) retType;
			if (pt.getRawType() == List.class) {
				Type type = pt.getActualTypeArguments()[0];
				// 判断泛型的泛型
				if (type instanceof ParameterizedType) {
					// 获取基础类型
					return (Class<?>) ((ParameterizedType) type).getRawType();
				}
				// 列表中的是基础类型
				return (Class<?>) type;
			} else {
				// 其他泛型对象, 用基础类作为返回类.
				return (Class<?>) pt.getRawType();
			}
		}
		// 直接返回
		return (Class<?>) retType;
	}

	/** 过程, 就是一次数据库操作. **/
	public static interface Process {
	}
}
