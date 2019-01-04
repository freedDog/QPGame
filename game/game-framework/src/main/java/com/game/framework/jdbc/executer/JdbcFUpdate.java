package com.game.framework.jdbc.executer;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import com.game.framework.jdbc.JadeBaseFactory;


/**
 * 基于JadeFactory的更新对象<br>
 * JdbcFUpdate.java
 * 
 * @author JiangBangMing 2019年1月3日下午5:04:14
 */
public class JdbcFUpdate<T> extends JdbcUpdate<T> {
	protected JadeBaseFactory factory;
	protected Method method; // 函数

	public JdbcFUpdate(JadeBaseFactory factory, Method method, String sql) throws Exception {
		super(sql, method.getParameterTypes(), method.getGenericReturnType(), isGeneratedKeys(method),
				isTotalResult(method));
		this.factory = factory;
		this.method = method;
	}

	@Override
	protected DataSource getDataSource() {
		return factory.getDataSource();
	}

}
