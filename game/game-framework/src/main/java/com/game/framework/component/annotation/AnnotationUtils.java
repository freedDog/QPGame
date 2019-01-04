package com.game.framework.component.annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.MapUtils;


/**
 * 注解工具
 * AnnotationUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:00:48
 */
public class AnnotationUtils {
	/** 获取优先级属性 **/
	public static int getPriority(Class<?> clazz) {
		Priority priority = (clazz != null) ? clazz.getAnnotation(Priority.class) : null;
		return (priority != null) ? priority.value() : 0;
	}

	/** 获取优先级属性 **/
	public static int getPriority(Object obj) {
		return getPriority((obj != null) ? obj.getClass() : null);
	}

	/** 根据优先级排序 **/
	public static List<Class<?>> sortByClassPriority(List<Class<?>> clazzs) {
		Collections.sort(clazzs, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return -Integer.compare(getPriority(o1), getPriority(o2));
			}
		});
		return clazzs;
	}

	/** 根据优先级排序 **/
	public static <T extends Object> List<T> sortByPriority(List<T> list) {
		Collections.sort(list, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return -Integer.compare(getPriority(o1), getPriority(o2));
			}
		});
		return list;
	}

	/** 根据注解分类 **/
	public static <K, T> Map<K, Class<?>> createKeyByAnnotation(Collection<Class<?>> classes, MapUtils.IKeyer<K, Class<?>> keyer) {
		// 检查数量
		int csize = (classes != null) ? classes.size() : 0;
		if (csize <= 0) {
			return new HashMap<>(0);
		}

		// 遍历处理
		Map<K, Class<?>> map = new HashMap<>(csize);
		for (Class<?> clazz : classes) {
			// 过滤注解跳过
			if (clazz.getAnnotation(Ignore.class) != null) {
				continue;
			}
			// 处理key
			K key = keyer.key(clazz);
			if (key == null) {
				continue;
			}
			// 插入并检测是否存在重复
			Class<?> old = map.put(key, clazz);
			if (old != null) {
				Log.error("存在重复的key! " + clazz + " -> " + old);
				continue;
			}
		}
		return map;
	}
}
