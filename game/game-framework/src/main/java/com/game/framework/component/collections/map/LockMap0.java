package com.game.framework.component.collections.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.framework.utils.collections.MapUtils;



/**
 * 带读写锁的哈希表
 * LockMap0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:18:24
 */
public class LockMap0<K, V> {
	protected final ReadWriteLock lock;
	protected final Map<K, V> map;

	public LockMap0(int size) {
		lock = new ReentrantReadWriteLock();
		map = new HashMap<K, V>(size);
	}

	/**
	 * 插入值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	protected V put(K key, V value) {
		lock.writeLock().lock();
		V old = map.put(key, value);
		lock.writeLock().unlock();
		return old;
	}

	/**
	 * 空插入, 如果存在值,则返回表中的值.
	 * 
	 * @param k
	 * @param v
	 * @return 最终在表中的自
	 */
	protected V putIfAbsent(K k, V v) {
		V ret = null;
		lock.writeLock().lock();
		// 判断是否存在数据
		if (!map.containsKey(k)) {
			// 不存在插入
			map.put(k, v);
			ret = v;
		} else {
			// 存在, 返回
			ret = map.get(k);
		}
		lock.writeLock().unlock();
		return ret;
	}

	/**
	 * 是否存在这个Key
	 * 
	 * @param k
	 * @return
	 */
	protected boolean containsKey(Object k) {
		try {
			lock.readLock().lock();
			return map.containsKey(k);
		} finally {
			lock.readLock().unlock();
		}
	}

	/** 是否存在这个Value */
	protected boolean containsValue(Object value) {
		try {
			lock.readLock().lock();
			return map.containsValue(value);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	protected V get(Object key) {
		lock.readLock().lock();
		V value = map.get(key);
		lock.readLock().unlock();
		return value;
	}

	/**
	 * 删除数据
	 * 
	 * @param key
	 * @return
	 */
	protected V remove(Object key) {
		lock.writeLock().lock();
		V old = map.remove(key);
		lock.writeLock().unlock();
		return old;
	}

	/**
	 * 清除数据
	 * 
	 */
	protected void clear() {
		boolean isEmpty = this.isEmpty();
		if (isEmpty) {
			return;
		}
		try {
			lock.writeLock().lock();
			map.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/** 获取数据 */
	protected List<V> values() {
		try {
			lock.readLock().lock();
			return new ArrayList<V>(map.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 是否为空
	 * 
	 * @return
	 */
	protected boolean isEmpty() {
		return size() <= 0;
	}

	protected int size() {
		try {
			lock.readLock().lock();
			return map.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 * @param maxCount
	 * @return
	 */
	protected int action(MapUtils.IAction<K, ? super V> action, int maxCount) {
		boolean isEmpty = this.isEmpty();
		if (isEmpty) {
			return 0;
		}
		try {
			lock.writeLock().lock();
			return MapUtils.action(map, action, maxCount);
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected List<V> findAll(MapUtils.IFilter<K, ? super V> filter, int maxCount) {
		boolean isEmpty = this.isEmpty();
		if (isEmpty) {
			return null;
		}

		lock.readLock().lock();
		List<V> list = MapUtils.findAll(map, filter, maxCount);
		lock.readLock().unlock();
		return list;
	}

	protected V find(MapUtils.IFilter<K, ? super V> filter) {
		boolean isEmpty = this.isEmpty();
		if (isEmpty) {
			return null;
		}

		lock.readLock().lock();
		V obj = MapUtils.find(map, filter);
		lock.readLock().unlock();
		return obj;
	}

	protected ReadWriteLock getLock() {
		return lock;
	}

	protected void putAll(Map<? extends K, ? extends V> m) {
		try {
			lock.writeLock().lock();
			map.putAll(m);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/** 读取key值 **/
	protected Set<K> keySet() {
		try {
			lock.readLock().lock();
			return new HashSet<K>(map.keySet());
		} finally {
			lock.readLock().unlock();
		}
	}

	/** 复制 **/
	protected Set<Map.Entry<K, V>> entrySet() {
		try {
			lock.readLock().lock();
			return new HashSet<Map.Entry<K, V>>(map.entrySet());
		} finally {
			lock.readLock().unlock();
		}
	}
}
