package com.game.framework.jdbc.executer.mapper;

/**
 *  结果集合
 * IResultSet.java
 * @author JiangBangMing
 * 2019年1月3日下午4:48:01
 */
public interface IResultSet
{
	/** 按照索引获取数据 **/
	Object getObject(int row) throws Exception;

	/** 获取一列总数 **/
	int getColumnCount() throws Exception;

	/** 获取索引key名称 **/
	String getColumnName(int row) throws Exception;
}
