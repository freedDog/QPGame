package com.game.framework.jdbc.executer.mapper;

import com.game.framework.jdbc.utils.JdbcUtils;
import com.game.framework.utils.ReflectUtils;
import com.game.framework.utils.ReflectUtils.ClassField;

/**
 *  * 对象反射<br>
 * 属性get和set优化
 * BeanPropertyRowMapper.java
 * @author JiangBangMing
 * 2019年1月3日下午4:58:54
 */
public class BeanPropertyRowMapper<T> extends JdbcRowMapper<T> {
	public BeanPropertyRowMapper(Class<T> retType) throws Exception {
		super(retType);
	}

	@Override
	public T mapRow(IResultSet rs, int rowNum) throws Exception {
		// 创建对象
		T retObj = retType.newInstance();
		// 创建对象并获取数据
		int columnCount = rs.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			// 获取参数名称
			String key = rs.getColumnName(i);
			ClassField<T> cf = ReflectUtils.ClassField.getClassField(retType, key);
			if (cf == null) {
				throw new Exception("数据不能设置! " + key + " ->" + retType);
			}
			Class<?> ptype = cf.getDeclaringClass();

			// 获取数据
			Object src = rs.getObject(i);
			Object obj = (src != null) ? JdbcUtils.toJavaObject(src, JdbcUtils.toJavaClass(ptype)) : null;
			// obj = JdbcUtils.toJavaObject(obj);
			if (obj == null) {
				continue;
			}

			try {
				// 设置数据
				cf.set(retObj, obj);
			} catch (Exception e) {
				throw new Exception("参数设置错误! " + cf + " set " + obj);
			}
		}

		return retObj;
	}
}
