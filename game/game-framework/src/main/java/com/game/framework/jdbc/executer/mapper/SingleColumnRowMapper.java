package com.game.framework.jdbc.executer.mapper;

import com.game.framework.jdbc.utils.JdbcUtils;

/**
 * 简单的反射对象(单个数据处理)<br>
 * SingleColumnRowMapper.java
 * @author JiangBangMing
 * 2019年1月3日下午5:00:38
 */
public class SingleColumnRowMapper<T> extends JdbcRowMapper<T>
{
	public SingleColumnRowMapper(Class<T> retType)
	{
		super(retType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T mapRow(IResultSet rs, int rowNum) throws Exception
	{
		// 获取数据(第一个)
		Object obj = rs.getObject(1);
		obj = JdbcUtils.toJavaObject(obj, retType);
		return (T) obj;
	}
}
