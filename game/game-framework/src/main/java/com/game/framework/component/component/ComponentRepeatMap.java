package com.game.framework.component.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.annotation.Ignore;
import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.MapUtils;



/**
 * 带Key验证的组件集合
 * ComponentRepeatMap.java
 * @author JiangBangMing
 * 2019年1月3日下午1:38:17
 */
public abstract class ComponentRepeatMap<K, T> {
	protected final List<T> datas;
	protected final Map<K, T> dataMap;

	public ComponentRepeatMap() {
		datas = new ArrayList<>();
		dataMap = new HashMap<>();
		load();
	}

	/** 通过key获取对应组件 **/
	public T get(K key) {
		return dataMap.get(key);
	}

	/** 加载数据 **/
	protected abstract boolean load();

	/** 获取组件的keys **/
	protected abstract K[] getKeys(T data);

	/** 初始化列表 **/
	protected boolean init(List<Class<?>> classes) {
		int csize = (classes != null) ? classes.size() : 0;
		for (int i = 0; i < csize; i++) {
			Class<?> clazz0 = classes.get(i);
			// 检测忽略
			if (clazz0.getAnnotation(Ignore.class) != null) {
				continue;
			}
			// 创建对象
			try {
				@SuppressWarnings("unchecked")
				T obj = (T) clazz0.newInstance();
				// 获取key
				K[] keys = this.getKeys(obj);
				int ksize = (keys != null) ? keys.length : 0;
				if (ksize <= 0) {
					Log.error("获取不到对象的key!" + obj);
					return false;
				}

				// 遍历插入key
				for (int j = 0; j < ksize; j++) {
					// 插入
					K key = keys[j];
					T old = dataMap.put(key, obj);
					if (old != null) {
						Log.error("存在相同key的对象! key=" + key + " old=" + old + " now=" + obj);
						return false;
					}
				}
				datas.add(obj); // 插入数据
			} catch (Exception e) {
				Log.error("创建组件失败! class=" + clazz0, e);
			}
		}
		return true;
	}

	/** 遍历处理 **/
	public void forech(MapUtils.Foreach<K, T> foreach) {
		MapUtils.action(dataMap, foreach, 0);
	}

	/**
	 * 查找对象
	 * 
	 * @param filter
	 * @return
	 */
	public T find(MapUtils.IFilter<K, ? super T> filter) {
		return MapUtils.find(dataMap, filter);
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param list
	 *            列表
	 * @param filter
	 *            过滤器
	 * @param maxCount
	 *            最大数量, 0为全部
	 * @return 符合条件的数据数组
	 */
	public List<T> findAll(MapUtils.IFilter<K, ? super T> filter, int maxCount) {
		return MapUtils.findAll(dataMap, filter, maxCount);
	}

	/** 获取全部数据 **/
	public List<T> getAll() {
		return datas;
	}

	public int size() {
		return datas.size();
	}

	public boolean isEmpty() {
		return datas.isEmpty();
	}
}
