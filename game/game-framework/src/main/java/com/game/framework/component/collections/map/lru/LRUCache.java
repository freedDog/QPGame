package com.game.framework.component.collections.map.lru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.utils.collections.MapUtils;


/**
 * 自动清除HashMap（least recently used）
 * LRUCache.java
 * @author JiangBangMing
 * 2019年1月3日下午1:25:29
 */
public class LRUCache<K, V> extends LRUCache0<K, V> {

	public LRUCache(int cacheSize) {
		super(cacheSize);
	}

	/**
	 * 设置移除监听器
	 * 
	 * @param listener
	 */
	public void setListener(LRURemoveListener<K, V> listener) {
		super.setListener0(listener);
	}

	/**
	 * 获取移除监听
	 * 
	 * @return
	 */
	public LRURemoveListener<K, V> getListener() {
		return super.getListener0();
	}

	/**
	 * 设置缓存数量最大值
	 * 
	 * @param cacheSize
	 */
	public void setCacheSize(int cacheSize) {
		super.setCacheSize0(cacheSize);
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see HashMap#put(Object, Object)
	 */
	public V put(K k, V v) {
		return super.put0(k, v);
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see ConcurrentMap#putIfAbsent(Object, Object)
	 */
	public V putIfAbsent(K k, V v) {
		return super.putIfAbsent0(k, v);
	}

	/**
	 * @param k
	 * @return V
	 * @see LinkedHashMap#get(Object)
	 */
	public V get(K k) {
		return super.get0(k);
	}

	/**
	 * @see List
	 * @see ArrayList
	 * @return List<V>
	 */
	public List<V> getAll() {
		return super.getAll0();
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 * @param maxCount
	 * @return
	 */
	public int action(MapUtils.IAction<? super K, ? super V> action, int maxCount) {
		return super.action0(action, maxCount);
	}

	/**
	 * @param k
	 * @return V
	 * @see HashMap#remove(Object)
	 */
	public V remove(K k) {
		return super.remove0(k);
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {
		super.clear0();
	}

}
