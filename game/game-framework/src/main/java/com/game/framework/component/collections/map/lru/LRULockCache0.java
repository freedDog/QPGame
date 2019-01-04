package com.game.framework.component.collections.map.lru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.framework.utils.collections.MapUtils;
import com.game.framework.utils.collections.SetUtils;



/**
 * 自动清除LinkedHashMap(带线程同步)
 * 
 * <p/>
 * LinkedHashMap是根据读取次数改变的排序, 因此读取或者删除都会产生数据改变, 不能用读写锁
 * LRULockCache0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:26:47
 */
public class LRULockCache0<K, V> extends LRUCache0<K, V>
{
	protected final ReadWriteLock lock; // 线程锁

	public LRULockCache0(int cacheSize)
	{
		super(cacheSize);
		this.lock = new ReentrantReadWriteLock();
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see HashMap#put(Object, Object)
	 */
	@Override
	protected V put0(K k, V v)
	{
		lock.writeLock().lock();
		V old = super.put0(k, v);
		lock.writeLock().unlock();
		return old;
	}

	/**
	 * @param k
	 * @param v
	 * @return V
	 * @see ConcurrentMap#putIfAbsent(Object, Object)
	 */
	@Override
	protected V putIfAbsent0(K k, V v)
	{
		lock.writeLock().lock();
		V old = super.putIfAbsent0(k, v);
		lock.writeLock().unlock();
		return old;
	}

	/**
	 * @param k
	 * @return V
	 * @see LinkedHashMap#get(Object)
	 */
	@Override
	protected V get0(K k)
	{
		// lock.readLock().lock();
		// V v = super.get0(k);
		// lock.readLock().unlock();

		// 这个读取包含修改, 属于写操作.
		lock.writeLock().lock();
		V v = super.get0(k);
		lock.writeLock().unlock();
		return v;
	}

	@Override
	protected boolean containsKey0(K k)
	{
		try
		{
			lock.readLock().lock();
			return super.containsKey0(k);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * @see List
	 * @see ArrayList
	 * @return List<V>
	 */
	@Override
	protected List<V> getAll0()
	{
		lock.readLock().lock();
		List<V> list = super.getAll0();
		lock.readLock().unlock();
		return list;
	}

	/**
	 * @param k
	 * @return V
	 * @see HashMap#remove(Object)
	 */
	@Override
	protected V remove0(K k)
	{
		lock.writeLock().lock();
		V old = super.remove0(k);
		lock.writeLock().unlock();
		return old;
	}

	@Override
	protected int action0(MapUtils.IAction<? super K, ? super V> action, int maxCount)
	{
		// 检测是否空
		boolean isEmpty = this.isEmpty0();
		if (isEmpty)
		{
			return 0;
		}
		try
		{
			lock.writeLock().lock();
			return super.action0(action, maxCount);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	protected boolean isEmpty0()
	{
		try
		{
			lock.readLock().lock();
			return super.isEmpty0();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * 遍历执行(只读)
	 * 
	 * @param action
	 * @param maxCount
	 * @return
	 */
	protected int action1(final MapUtils.IAction<? super K, ? super V> action, int maxCount)
	{
		// lock.readLock().lock();
		// int count = MapUtils.action0(super.map, action, maxCount);
		// lock.readLock().unlock();

		// 检测是否空
		boolean isEmpty = this.isEmpty0();
		if (isEmpty)
		{
			return 0;
		}
		// 复制一份
		Set<Map.Entry<K, V>> all = null;
		try
		{
			lock.readLock().lock();
			all = new HashSet<Map.Entry<K, V>>(super.map.entrySet());
		}
		finally
		{
			lock.readLock().unlock();
		}

		// 创建遍历器
		SetUtils.IAction<Map.Entry<K, V>> action0 = new SetUtils.IAction<Map.Entry<K, V>>()
		{
			@Override
			public boolean action(Entry<K, V> data, Iterator<?> iter)
			{
				K key = data.getKey();
				V value = data.getValue();
				return action.action(key, value, null);
			}
		};
		// 遍历执行
		int count = SetUtils.action(all, action0, maxCount);
		return count;
	}

	@Override
	protected void clear0()
	{
		try
		{
			lock.writeLock().lock();
			super.clear0();
		}
		finally
		{
			lock.writeLock().unlock();
		}

		// // 检测是否空
		// boolean isEmpty = this.isEmpty0();
		// if (isEmpty) {
		// return;
		// }
		//
		// // lock.writeLock().lock();
		// // super.clear0();
		// // lock.writeLock().unlock();
		//
		// // 复制一份
		// Set<Map.Entry<K, V>> all = null;
		// if (this.listener != null) {
		// lock.readLock().lock();
		// all = new HashSet<Map.Entry<K, V>>(super.map.entrySet());
		// lock.readLock().unlock();
		// }
		//
		// // 清除
		// lock.writeLock().lock();
		// super.map.clear();
		// lock.writeLock().unlock();
		//
		// // 激活删除操作
		// if (this.listener != null) {
		// if (all.isEmpty()) {
		// return;
		// }
		// // 遍历数据触发接口
		// Iterator<Map.Entry<K, V>> iter = all.iterator();
		// while (iter.hasNext()) {
		// Map.Entry<K, V> entry = iter.next();
		// K k = entry.getKey();
		// V v = entry.getValue();
		// if (k == null || v == null) {
		// continue;
		// }
		// listener.handle(k, v, LRURemoveListener.removeType_clear);
		// }
		// }

	}

}
