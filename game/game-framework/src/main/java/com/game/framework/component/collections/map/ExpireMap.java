package com.game.framework.component.collections.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 销毁map<br>
 * 调用expire()执行超时清除<br>
 * 线程安全<br>
 * 超过时间没有获取就自动清除值<br>
 * ExpireMap.java
 * @author JiangBangMing
 * 2019年1月3日下午1:14:48
 */
public class ExpireMap<K, V> {
	protected ConcurrentMap<K, Element> map;
	protected int expireTime;

	public ExpireMap(int expireTime) {
		map = new ConcurrentHashMap<>();
		this.expireTime = expireTime;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public V get(Object key) {
		Element e = map.get(key);
		if (e == null) {
			return null;
		}
		// 更新
		e.update();
		return e.value;
	}

	public V put(K key, V value, int expire) {
		Element e = new Element(value, expire);
		// 插入数据
		Element old = map.put(key, e);
		return (old != null && !old.expire()) ? old.value : null;
	}

	public V put(K key, V value) {
		return put(key, value, expireTime);
	}

	public V remove(Object key) {
		Element e = map.remove(key);
		return (e != null) ? e.value : null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			put(key, value);
		}
	}

	public void clear() {
		map.clear();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<V> values() {
		return create(map.values());
	}

	private Collection<V> create(Collection<Element> values) {
		List<V> list = new ArrayList<>(values.size());
		for (Element element : values) {
			if (element.expire()) {
				continue;
			}
			list.add(element.value);
		}
		return list;
	}

	/** 超时清除 **/
	public void expire() {
		// 遍历计算
		Iterator<Map.Entry<K, Element>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<K, Element> entry = iter.next();
			Element element = entry.getValue();
			if (element.expire()) {
				iter.remove(); // 清除
			}
		}
	}

	/** 代替值 **/
	private class Element {
		public final V value;
		private long time; // 更新时间
		public final int expire;

		public Element(V value, int expire) {
			this.value = value;
			this.expire = expire;
			update();
		}

		public void update() {
			this.time = System.currentTimeMillis();
		}

		/** 是否过期 **/
		public boolean expire() {
			return System.currentTimeMillis() > (time + expire);
		}

		@Override
		public String toString() {
			return (value != null) ? value.toString() : null;
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
