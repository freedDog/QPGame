package com.game.framework.jdbc.executer.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import com.game.framework.jdbc.utils.JdbcUtils;


/**
 *  Map表反射反射对象<br>
 * 只支持String/Object类型
 * ColumnMapRowMapper.java
 * @author JiangBangMing
 * 2019年1月3日下午4:59:34
 */
public class ColumnMapRowMapper extends JdbcRowMapper<Map<String, Object>>
{
	public ColumnMapRowMapper()
	{
		super(Map.class);
	}

	@Override
	public Map<String, Object> mapRow(IResultSet rs, int rowNum) throws Exception
	{
		// 检测数据
		int columnCount = rs.getColumnCount();

		// 创建对象并获取数据
		Map<String, Object> map = new LinkedHashMap<>(columnCount);
		for (int i = 1; i <= columnCount; i++)
		{
			// 获取数据
			Object obj = rs.getObject(i);
			obj = JdbcUtils.toJavaObject(obj);
			if (obj == null)
			{
				continue; // 过滤空
			}

			// 插入数据
			String key = rs.getColumnName(i);
			map.put(key, obj);
		}
		return map;
	}

}
