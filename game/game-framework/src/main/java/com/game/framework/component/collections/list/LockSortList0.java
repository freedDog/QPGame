package com.game.framework.component.collections.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 排序列表
 * LockSortList0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:12:05
 */
public class LockSortList0<T> extends LockList0<T>
{
	protected final Comparator<? super T> comparator; // 比较器

	protected LockSortList0(Comparator<? super T> comparator)
	{
		this(comparator, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param comparator
	 *            比较器
	 * @param list
	 *            初始化列表
	 */
	protected LockSortList0(Comparator<? super T> comparator, List<T> list)
	{
		super((list != null) ? list.size() : 0);
		this.comparator = comparator;
		int lsize = (list != null) ? list.size() : 0;
		if (lsize > 0)
		{
			Collections.sort(list, comparator);
			lock.writeLock().lock();
			this.list.addAll(list);
			lock.writeLock().unlock();
		}
	}

	/** 顺序插入 **/
	public static <T> boolean addSort(List<T> list, T obj, Comparator<? super T> comparator)
	{
		// 插入到列表最后
		boolean result = (list != null) ? list.add(obj) : false;
		if (!result)
		{
			return false;
		}

		// 排序处理
		int size = (list != null) ? list.size() : 0;
		if (size >= 2)
		{
			for (int i = size - 2; i >= 0; i--)
			{
				T v = list.get(i);
				int c = comparator.compare(obj, v);
				if (c <= 0)
				{
					break; // 到点了, 不排序.
				}
				// 更换位置.
				list.set(i + 1, v);
				list.set(i, obj);
			}
		}
		return result;
	}

	@Override
	protected boolean add(T obj)
	{
		lock.writeLock().lock();
		boolean result = addSort(list, obj, comparator);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	protected boolean addIfAbsent(T obj)
	{
		boolean result = false;
		lock.writeLock().lock();
		if (!list.contains(obj))
		{
			result = addSort(list, obj, comparator);
		}
		lock.writeLock().unlock();
		return result;
	}

	@Override
	protected boolean addAll(Collection<? extends T> c)
	{
		boolean result = false;
		lock.writeLock().lock();
		for (T t : c)
		{
			result = (result) ? addSort(list, t, comparator) : result;
		}
		lock.writeLock().unlock();
		return result;
	}

	/** 获取比较器 **/
	protected Comparator<? super T> getComparator()
	{
		return comparator;
	}

}
