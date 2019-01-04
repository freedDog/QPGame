package com.game.framework.component.collections.map.lru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.utils.collections.MapUtils;



/**
 * 自动清除HashMap（least recently used）
 * LRUCache0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:24:14
 */
public class LRUCache0<K, V> {
	protected final LinkedHashMap<K, V> map; // hashMap
	protected volatile int cacheSize; // 缓存数量
	protected volatile transient LRURemoveListener<K, V> listener; // 删除监听(volatile) 可以不加

	public LRUCache0(int cacheSize) {
		// 创建一个LinkedHashMap 派生类
		this.map = new LinkedHashMap<K, V>(cacheSize, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
				int bufSize = size();
				boolean needRemove = bufSize > LRUCache0.this.cacheSize;
				if (needRemove) {
					if (listener != null) {
						listener.handle(entry.getKey(), entry.getValue(), LRURemoveListener.removeType_auto);
					}
				}
				return needRemove; // 确定是否删除
			}
		};
		this.cacheSize = cacheSize;
	}

	/**
	 * @param k
	 * @return V
	 * @see HashMap#remove(Object)
	 */
	protected V remove0(K k) {
		V old = map.remove(k);
		// 执行删除处理
		if (old != null && this.listener != null) {
			listener.handle(k, old, LRURemoveListener.removeType_remove);
		}
		return old;
	}

	/**
	 * 清除所有数据
	 */
	protected void clear0() {
		if (map.isEmpty()) {
			return; // 本来就空的
		}
		// 读取当前数据
		Set<Map.Entry<K, V>> all = null;
		if (this.listener != null) {
			all = new HashSet<Map.Entry<K, V>>(map.entrySet());
		}
		// 清除
		map.clear();
		// 激活删除操作
		if (this.listener != null) {
			Iterator<Map.Entry<K, V>> iter = all.iterator();
			while (iter.hasNext()) {
				Map.Entry<K, V> entry = iter.next();
				K k = entry.getKey();
				V v = entry.getValue();
				if (k == null || v == null) {
					continue;
				}
				listener.handle(k, v, LRURemoveListener.removeType_clear);
			}
		}
	}

	/**
	 * 是否空
	 * 
	 * @return
	 */
	protected boolean isEmpty0() {
		return map.isEmpty();
	}

	/**
	 * 设置移除监听器
	 * 
	 * @param listener
	 */
	protected void setListener0(LRURemoveListener<K, V> listener) {
		this.listener = listener;
	}

	/**
	 * 获取移除监听
	 * 
	 * @return
	 */
	protected LRURemoveListener<K, V> getListener0() {
		LRURemoveListener<K, V> ret = this.listener;
		return ret;
	}

	/**
	 * 设置缓存数量最大值
	 * 
	 * @param cacheSize
	 */
	protected void setCacheSize0(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see HashMap#put(Object, Object)
	 */
	protected V put0(K k, V v) {
		if (v == null) {
			return null;
		}
		// 插入会产生删除
		V old = map.put(k, v);
		return old;
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see ConcurrentMap#putIfAbsent(Object, Object)
	 */
	protected V putIfAbsent0(K k, V v) {
		V old = null;
		if (!map.containsKey(k)) {
			map.put(k, v);
			old = v;
		} else {
			old = map.get(k);
		}
		return old;
	}

	/**
	 * @param k
	 * @return
	 * @see HashMap#containsKey(Object k)
	 */
	protected boolean containsKey0(K k) {
		return map.containsKey(k);
	}

	/**
	 * @param k
	 * @return V
	 * @see LinkedHashMap#get(Object)
	 */
	protected V get0(K k) {
		V v = map.get(k);
		return v;
	}

	/**
	 * @see List
	 * @see ArrayList
	 * @return List<V>
	 */
	protected List<V> getAll0() {
		List<V> list = new ArrayList<V>(map.values());
		return list;
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 * @param maxCount
	 * @return
	 */
	protected int action0(MapUtils.IAction<? super K, ? super V> action, int maxCount) {
		return MapUtils.action(map, action, maxCount);
	}

}
