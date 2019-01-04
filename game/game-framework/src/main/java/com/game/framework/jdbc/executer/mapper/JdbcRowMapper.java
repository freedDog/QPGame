package com.game.framework.jdbc.executer.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * jdbc反射处理
 * JdbcRowMapper.java
 * @author JiangBangMing
 * 2019年1月3日下午4:47:20
 */
public abstract class JdbcRowMapper<T>
{
	protected Class<T> retType;

	@SuppressWarnings("unchecked")
	public JdbcRowMapper(Class<?> retType)
	{
		this.retType = (Class<T>) retType;
	}

	/** 处理当前行信息 **/
	public abstract T mapRow(IResultSet rs, int rowNum) throws Exception;

	/***************** 静态处理 *****************/

	/** 基础类列表 **/
	protected static final Class<?>[] singleClasses = new Class<?>[] { boolean.class, Boolean.class, //
			byte.class, Byte.class, //
			short.class, Short.class, //
			int.class, Integer.class, //
			long.class, Long.class, //
			float.class, Float.class, //
			double.class, Double.class,//
			Number.class,//
			BigDecimal.class,//
			BigInteger.class,//
			String.class //
	};

	/** 创建对应反射对象 **/
	@SuppressWarnings("unchecked")
	public static <T> JdbcRowMapper<T> createRowMapper(Class<T> clazz) throws Exception
	{
		// 遍历检测是否属于基础类型, 是则返回SingleColumnRowMapper
		for (int i = 0; i < singleClasses.length; i++)
		{
			Class<?> check = singleClasses[i];
			if (check.isAssignableFrom(clazz))
			{
				return new SingleColumnRowMapper<T>(clazz);
			}
		}
		// Map类处理
		if (Map.class.isAssignableFrom(clazz))
		{
			return (JdbcRowMapper<T>) new ColumnMapRowMapper();
		}
		// 对象处理
		return new BeanPropertyRowMapper<>(clazz);
		// return (JdbcRowMapper<T>) new ColumnMapRowMapper();
	}

}
