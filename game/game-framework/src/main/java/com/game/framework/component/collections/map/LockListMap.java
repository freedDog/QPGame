package com.game.framework.component.collections.map;

import java.util.List;
import java.util.Map;

/**
 * 线程安全列表Map<br>
 * 1个key对应1个数组<br>
 * LockListMap.java
 * @author JiangBangMing
 * 2019年1月3日下午1:17:08
 */
public class LockListMap<K, T> extends LockListMap0<K, T>
{

	@Override
	public boolean remove(K key)
	{
		return super.remove(key);
	}

	@Override
	public boolean remove(K key, T v)
	{
		return super.remove(key, v);
	}

	@Override
	public int add(K key, T v)
	{
		return super.add(key, v);
	}

	@Override
	public boolean addIfAbsent(K key, T v)
	{
		return super.addIfAbsent(key, v);
	}

	@Override
	public List<T> get(K key)
	{
		return super.get(key);
	}

	@Override
	public Map<K, List<T>> getAll()
	{
		return super.getAll();
	}

}
