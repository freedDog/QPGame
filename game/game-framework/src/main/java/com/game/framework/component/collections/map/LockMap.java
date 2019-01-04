package com.game.framework.component.collections.map;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import com.game.framework.utils.collections.MapUtils.IAction;
import com.game.framework.utils.collections.MapUtils.IFilter;



/**
 * 带读写锁的哈希表
 * LockMap.java
 * @author JiangBangMing
 * 2019年1月3日下午1:17:48
 */
public class LockMap<K, V> extends LockMap0<K, V> implements Map<K, V> {
	public LockMap(int size) {
		super(size);
	}

	public LockMap() {
		this(0);
	}

	@Override
	public V put(K key, V value) {
		return super.put(key, value);
	}

	@Override
	public V putIfAbsent(K k, V v) {
		return super.putIfAbsent(k, v);
	}

	@Override
	public boolean containsKey(Object k) {
		return super.containsKey(k);
	}

	@Override
	public V get(Object key) {
		return super.get(key);
	}

	@Override
	public V remove(Object key) {
		return super.remove(key);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public List<V> values() {
		return super.values();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public int size() {
		return super.size();
	}

	@Override
	public int action(IAction<K, ? super V> action, int maxCount) {
		return super.action(action, maxCount);
	}

	@Override
	public List<V> findAll(IFilter<K, ? super V> filter, int maxCount) {
		return super.findAll(filter, maxCount);
	}

	@Override
	public V find(IFilter<K, ? super V> filter) {
		return super.find(filter);
	}

	@Override
	public ReadWriteLock getLock() {
		return super.getLock();
	}

	@Override
	public boolean containsValue(Object value) {
		return super.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
	}

	@Override
	public Set<K> keySet() {
		return super.keySet();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return super.entrySet();
	}

}
