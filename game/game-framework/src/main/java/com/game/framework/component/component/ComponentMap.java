package com.game.framework.component.component;

import java.lang.reflect.Array;

/**
 * 带Key验证的组件集合(Key是可重复的)
 * ComponentMap.java
 * @author JiangBangMing
 * 2019年1月3日下午1:37:40
 */
public abstract class ComponentMap<K, T> extends ComponentRepeatMap<K, T> {
	/** 获取组件的keys **/
	protected K[] getKeys(T data) {
		K key = getKey(data);
		if (key == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		K[] array = (K[]) Array.newInstance(key.getClass(), 1);
		array[0] = key;
		return array;
	}

	/** 获取组件的keys **/
	protected abstract K getKey(T data);
}
