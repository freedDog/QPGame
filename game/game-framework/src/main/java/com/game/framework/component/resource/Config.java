package com.game.framework.component.resource;

import com.game.framework.utils.ObjectUtils;

/**
 * 配置对象
 * Config.java
 * @author JiangBangMing
 * 2019年1月3日下午1:47:34
 */
public abstract class Config {

	/** 获取字符串参数 **/
	protected abstract String get(Object key);

	/**
	 * 查找匹配的值,并将值转换为指定的类型T
	 * 
	 * @param key
	 * @param clazz
	 * @return 与key匹配的值, 如果没有找到匹配, 那么返回null.
	 */
	public <T> T get(Object key, Class<T> clazz) {
		String value = get(key);
		if (value == null) {
			return ObjectUtils.defualtValue(clazz); // 默认值
		}
		return ObjectUtils.toValue(value, clazz); // 转换值
	}

	/** 不带默认值的获取返回 **/
	protected <T> T get0(Object key, Class<T> clazz) {
		String value = get(key);
		if (value == null) {
			return null; // 返回空
		}
		return ObjectUtils.stringToValue(value, clazz); // 转换解析, 如果格式不对, 返回null.
	}

	/**
	 * 获取参数, 如果不存在参数,则返回默认值
	 * 
	 * @param key
	 *            参数名
	 * @param defualt
	 *            默认值
	 * @return 值
	 */
	public <T> T get(Object key, T defualt) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) defualt.getClass();
		T value = this.get0(key, clazz); // 不带默认值, 格式不会就返回null
		if (value == null) {
			return defualt;
		}
		return value;
	}

	/**
	 * 查找匹配的值,并将值转换为short类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public short shortValue(Object key) {
		return get(key, short.class);
	}

	/**
	 * 查找匹配的值,并将值转换为int类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public int intValue(Object key) {
		return get(key, int.class);
	}

	/**
	 * 查找匹配的值,并将值转换为long类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public long longValue(Object key) {
		return get(key, long.class);
	}

	/**
	 * 查找匹配的值,并将值转换为float类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public float floatValue(Object key) {
		return get(key, float.class);
	}

	/**
	 * 查找匹配的值,并将值转换为double类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public double doubleValue(Object key) {
		return get(key, double.class);
	}

	/**
	 * 查找匹配的值,并将值转换为boolean类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public boolean booleanValue(Object key) {
		return get(key, boolean.class);
	}

	/**
	 * 查找匹配的值,并将值转换为String类型
	 * 
	 * @param key
	 * @return 匹配的值
	 */
	public String stringValue(Object key) {
		return get(key);
	}

}
