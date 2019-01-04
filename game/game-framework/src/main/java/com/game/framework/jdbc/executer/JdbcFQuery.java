package com.game.framework.jdbc.executer;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import com.game.framework.jdbc.JadeBaseFactory;



/**
 * 基于JadeFactory的查询对象<br>
 * JdbcFQuery.java
 * @author JiangBangMing
 * 2019年1月3日下午5:01:08
 */
public class JdbcFQuery<T> extends JdbcQuery<T>
{
	protected JadeBaseFactory factory;
	protected Method method; // 函数

	public JdbcFQuery(JadeBaseFactory factory, Method method, String sql) throws Exception
	{
		super(sql, method.getParameterTypes(), method.getGenericReturnType());
		this.factory = factory;
		this.method = method;
	}

	@Override
	protected DataSource getDataSource()
	{
		return factory.getDataSource();
	}

}
