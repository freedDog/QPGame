package com.game.framework.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * map集合工具 MapUtils.java
 * 
 * @author JiangBangMing 2019年1月2日下午6:26:04
 */
public class MapUtils {
	public static <K, V> V get(Map<K, V> map, K key) {
		return (V) get(map, key, null);
	}

	public static <K, V> V get(Map<K, V> map, K key, V default0) {
		if (map == null) {
			return default0;
		}
		V v = map.get(key);
		if (v == null) {
			return default0;
		}
		return v;
	}

	public static <K1, K2, V> Map<K1, Map<K2, V>> createUnmodifiableMaps(Map<K1, Map<K2, V>> maps) {
		Map<K1, Map<K2, V>> tempMaps = new HashMap<>();
		for (Map.Entry<K1, Map<K2, V>> entry : maps.entrySet()) {
			tempMaps.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
		}
		tempMaps = Collections.unmodifiableMap(tempMaps);
		return tempMaps;
	}

	public static <K, V> Map<K, List<V>> createUnmodifiableListMap(Map<K, List<V>> maps) {
		Map<K, List<V>> tempMaps = new HashMap<>();
		for (Map.Entry<K, List<V>> entry : maps.entrySet()) {
			tempMaps.put(entry.getKey(), Collections.unmodifiableList( entry.getValue()));
		}
		tempMaps = Collections.unmodifiableMap(tempMaps);
		return tempMaps;
	}

	public static <K, V> boolean remove(Map<K, V> map, IFilter<K, ? super V> filter) {
		int count = removeAll(map, filter, 1);
		return count > 0;
	}

	public static <K, V> int removeAll(Map<K, V> map, IFilter<K, ? super V> filter, final int maxCount) {
		IAction<K, V> action = new IAction<K,V>() {
			protected int actionCount = 0;

			public boolean action(K key, V value, Iterator<?> iter) {
				if ((filter == null) || (filter.check(key, value))) {
					iter.remove();
					this.actionCount += 1;
				}
				return this.actionCount < maxCount;
			}
		};
		return action(map, action, 0);
	}

	public static <K, V> V find(Map<K, V> map, IFilter<K, ? super V> filter) {
		List<V> findList = findAll(map, filter, 1);
		int count = findList != null ? findList.size() : 0;
		if (count > 0) {
			return (V) findList.get(0);
		}
		return null;
	}

	public static <K, V> List<V> findAll(Map<K, V> map, IFilter<K, ? super V> filter, final int maxCount) {
		final List<V> findList = new ArrayList<>();
		IAction<K, V> action = new IAction<K,V>() {
			protected int actionCount = 0;

			public boolean action(K key, V value, Iterator<?> iter) {
				if ((filter == null) || filter.check(key, value)) {
					findList.add(value);
					this.actionCount += 1;
				}
				return this.actionCount < maxCount;
			}
		};
		action(map, action, 0);
		return findList;
	}

	public static <K, V> int action(Map<K, V> map, IAction<? super K, ? super V> action, int maxCount) {
		if ((map.isEmpty()) || (action == null)) {
			return 0;
		}
		int actionCount = 0;
		Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<K, V> entry = iter.next();
			K key = entry.getKey();
			V value = entry.getValue();
			if ((key != null) && (value != null)) {
				if (!action.action(key, value, iter)) {
					return actionCount;
				}
				actionCount++;
				if ((maxCount > 0) && (actionCount >= maxCount)) {
					break;
				}
			}
		}
		return actionCount;
	}

	public static <K, V> int action0(Map<K, V> map, IAction<? super K, ? super V> action, int maxCount) {
		if ((map.isEmpty()) || (action == null)) {
			return 0;
		}
		int actionCount = 0;
		Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<K, V> entry = iter.next();
			K key = entry.getKey();
			V value = entry.getValue();
			if ((key != null) && (value != null)) {
				if (!action.action(key, value, null)) {
					return actionCount;
				}
				actionCount++;
				if ((maxCount > 0) && (actionCount >= maxCount)) {
					break;
				}
			}
		}
		return actionCount;
	}

	public static <K, V> Map<K, V> toMap(Collection<V> list, IKeyer<? extends K, V> keyer) {
		Map<K, V> map = new HashMap<>();
		int lsize = list != null ? list.size() : 0;
		if (lsize <= 0) {
			return map;
		}
		for (V v : list) {
			K key = keyer.key(v);
			if (key != null) {
				map.put(key, v);
			}
		}
		return map;
	}

	public static abstract class Foreach<K, V> implements MapUtils.IAction<K, V> {
		public abstract void action(K paramK, V paramV);

		public boolean action(K key, V value, Iterator<?> iter) {
			action(key, value);
			return true;
		}
	}

	public static abstract interface IAction<K, V> {
		public abstract boolean action(K paramK, V paramV, Iterator<?> paramIterator);
	}

	public static abstract interface IFilter<K, V> {
		public abstract boolean check(K paramK, V paramV);
	}

	public static abstract interface IKeyer<K, V> {
		public abstract K key(V paramV);
	}
}
