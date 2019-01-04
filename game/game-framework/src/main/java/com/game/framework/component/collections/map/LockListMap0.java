package com.game.framework.component.collections.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全列表Map<br>
 * 1个key对应1个数组<br>
 * LockListMap0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:16:01
 */
public class LockListMap0<K, T>
{
	protected final ReadWriteLock lock;
	protected final Map<K, List<T>> map;

	protected LockListMap0()
	{
		lock = new ReentrantReadWriteLock();
		map = new HashMap<>();
	}

	/** 删除key对应数组 **/
	protected boolean remove(K key)
	{
		lock.writeLock().lock();
		try
		{
			List<T> old = map.remove(key);
			return (old != null) ? old.size() > 0 : false;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/** 移除数据, 返回是否移除成功. **/
	protected boolean remove(K key, T v)
	{
		lock.writeLock().lock();
		try
		{
			// 获取列表
			List<T> list = map.get(key);
			if (list == null)
			{
				return false; // 不存在数据
			}
			// 删除数据
			return list.remove((Object) v);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/** 添加数据, 返回当前key中存在的数量. **/
	protected int add(K key, T v)
	{
		lock.writeLock().lock();
		// 获取列表
		List<T> list = map.get(key);
		if (list == null)
		{
			list = new ArrayList<>();
			map.put(key, list);
		}
		// 添加数据
		list.add(v);
		int size = list.size();
		lock.writeLock().unlock();
		return size;
	}

	/** 添加数据, 如果已经在列表中存在相同的, 不插入, 返回当前key中存在的数量. **/
	protected boolean addIfAbsent(K key, T v)
	{
		try
		{
			lock.writeLock().lock();
			// 获取列表
			List<T> list = map.get(key);
			if (list == null)
			{
				list = new ArrayList<>();
				map.put(key, list);
			}
			// 检测是否在列表中
			if (list.contains(v))
			{
				return false;
			}
			// 添加数据
			return list.add(v);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 获取数组<br>
	 * 没有数据返回null
	 * **/
	protected List<T> get(K key)
	{
		lock.readLock().lock();
		try
		{
			// 获取数据
			List<T> list = map.get(key);
			if (list == null || list.size() <= 0)
			{
				return null;
			}
			// 复制一份出去
			return new ArrayList<>(list);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/** 获取所有数据 **/
	protected Map<K, List<T>> getAll()
	{
		Map<K, List<T>> out = new HashMap<>();
		lock.readLock().lock();
		try
		{
			Iterator<Map.Entry<K, List<T>>> iter = map.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry<K, List<T>> entry = iter.next();
				K key = entry.getKey();
				List<T> list = entry.getValue();
				if (key == null || list == null || list.size() <= 0)
				{
					continue;
				}
				// 复制数组
				out.put(key, new ArrayList<>(list));
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		return out;
	}
}
