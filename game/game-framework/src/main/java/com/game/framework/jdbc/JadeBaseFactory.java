package com.game.framework.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.SQL;
import com.game.framework.jdbc.executer.JdbcExecuter;
import com.game.framework.jdbc.executer.JdbcFQuery;
import com.game.framework.jdbc.executer.JdbcFUpdate;
import com.game.framework.jdbc.executer.JdbcQuery;
import com.game.framework.jdbc.executer.JdbcUpdate;
import com.game.framework.jdbc.utils.JdbcUtils;


/**
 * jdbc基类工厂 JadeBaseFactory.java
 * 
 * @author JiangBangMing 2019年1月3日下午4:28:17
 */
public abstract class JadeBaseFactory {
	/** DAO函数对应表 **/
	protected final ConcurrentMap<Method, JdbcExecuter<?>> map = new ConcurrentHashMap<Method, JdbcExecuter<?>>();

	/** 动态类反射接口 **/
	protected final InvocationHandler invocationHandler = new InvocationHandler() {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return DAOInvoke(proxy, method, args);
		}
	};

	/** 获取数据源 **/
	public abstract DataSource getDataSource();

	/** 检测函数是否对应类型 **/
	protected boolean checkMethod(Method method, String methodName, Class<?>[] paramTypes) {
		// 检测函数名
		if (!method.getName().equals(methodName)) {
			return false;
		}
		// 检测参数
		int psize = (paramTypes != null) ? paramTypes.length : 0;
		Class<?>[] paramTypes0 = method.getParameterTypes();
		int mpsize = (paramTypes0 != null) ? paramTypes0.length : 0;
		if (psize != mpsize) {
			return false;
		}
		// 检测参数类型
		for (int i = 0; i < psize; i++) {
			// 检测是否都相同
			if (!paramTypes0[i].equals(paramTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 基础函数处理<br>
	 * toString<br>
	 * hashCode<br>
	 * equals<br>
	 */
	protected Object[] baseInvoke(Object proxy, Method method, Object[] args) throws Exception {
		// 如果是toString返回类名
		if (checkMethod(method, "toString", null)) {
			return new Object[] { true, proxy.getClass().getName() };
		} else if (checkMethod(method, "hashCode", null)) {
			// dao的hashcode用当前的工厂+类hashCode做
			int hashCode = this.hashCode() + proxy.getClass().hashCode();
			return new Object[] { true, hashCode };
		} else if (checkMethod(method, "equals", new Class<?>[] { Object.class })) {
			boolean result = (proxy == args[0]);
			return new Object[] { true, result };
		}
		return null;
	}

	/** DAO调用处理 */
	protected Object DAOInvoke(Object proxy, Method method, Object[] args) throws Exception {
		// 基础函数处理
		Object[] rets = baseInvoke(proxy, method, args);
		if (rets != null && ((Boolean) rets[0]) == true) {
			return rets[1];
		}

		// 获取函数对应的执行器
		JdbcExecuter<?> executer = map.get(method);
		if (executer == null) {
			throw new Exception("没找到对应执行对象:" + method);
		}

		// 根据参数生成执行过程
		JdbcExecuter.Process process = executer.process(args);
		// 判断是否返回数组列表
		Class<?> clazz = method.getReturnType();
		if (clazz == List.class) {
			return executer.execute(process); // 数组返回
		}
		return executer.executeOnce(process); // 列表返回
	}

	/** 创建DAO **/
	protected Object createDAO(Class<?> clazz) throws Exception {
		// 检测是否是接口
		if (!clazz.isInterface()) {
			throw new RuntimeException("clazz is interface" + clazz);
		}
		// 检测是否绑定有dao类
		DAO dao = clazz.getAnnotation(DAO.class);
		if (dao == null) {
			throw new RuntimeException("接口不继承 DAO注解!" + clazz);
		}

		// 遍历查询所有函数
		for (Method method : clazz.getDeclaredMethods()) {
			SQL sql = method.getAnnotation(SQL.class);
			if (sql == null) {
				continue;
			}
			try {
				// 创建处理器
				JdbcExecuter<?> executer = createExecuter(method, dao, sql);
				map.putIfAbsent(method, executer);
			} catch (Exception e) {
				throw new RuntimeException("解析SQL函数错误! " + method, e);
			}
		}

		// 生成动态类
		ClassLoader classLoader = this.getClass().getClassLoader();
		return Proxy.newProxyInstance(classLoader, new Class<?>[] { clazz }, invocationHandler);
	}

	/** 创建处理器(一个SQL对应一个) **/
	protected JdbcExecuter<?> createExecuter(Method method, DAO dao, SQL sql) throws Exception {
		String sqlStr = sql.value();

		// 创建执行器
		JdbcExecuter<?> executer = null;
		int sqlType = JdbcUtils.getSQLType(sqlStr);
		if (sqlType == JdbcUtils.SQLTYPE_READ) {
			executer = createQuery(method, sqlStr, dao, sql);
		} else if (sqlType == JdbcUtils.SQLTYPE_WRITE) {
			// 创建修改处理
			executer = createUpdate(method, sqlStr, dao, sql);
		} else {
			throw new Exception("未知SQL语句! " + sqlStr);
		}

		return executer;
	}

	/** 创建查找器 **/
	protected <T> JdbcQuery<T> createQuery(Method method, String sql, DAO dao, SQL sql0) throws Exception {
		return new JdbcFQuery<T>(this, method, sql);
	}

	/** 创建修改器 **/
	protected <T> JdbcUpdate<T> createUpdate(Method method, String sql, DAO dao, SQL sql0) throws Exception {
		// 创建更新执行器
		return new JdbcFUpdate<T>(this, method, sql);
	}

	/***************** 静态处理 *****************/

}
