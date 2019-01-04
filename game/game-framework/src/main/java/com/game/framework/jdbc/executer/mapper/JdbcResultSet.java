package com.game.framework.jdbc.executer.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *  结果集合
 * JdbcResultSet.java
 * @author JiangBangMing
 * 2019年1月3日下午5:00:16
 */
public class JdbcResultSet implements IResultSet
{
	protected ResultSet resultSet;

	public JdbcResultSet(ResultSet resultSet)
	{
		this.resultSet = resultSet;
	}

	@Override
	public Object getObject(int row) throws Exception
	{
		return resultSet.getObject(row);
	}

	@Override
	public int getColumnCount() throws Exception
	{
		return resultSet.getMetaData().getColumnCount();
	}

	@Override
	public String getColumnName(int row) throws Exception
	{
		return getColumnName(resultSet.getMetaData(), row);
	}

	/** 获取索引key名称 **/
	protected static String getColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException
	{
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1)
		{
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}
}
