package com.game.framework.component.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.framework.utils.collections.ListUtils;

/**
 * 带读写锁的列表<br>
 * 成员函数不对外开完, 用于继承.
 * LockList0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:10:06
 */
public class LockList0<T>
{
	protected final ReadWriteLock lock;
	protected final List<T> list;

	protected LockList0()
	{
		this(0);
	}

	protected LockList0(int size)
	{
		lock = new ReentrantReadWriteLock();
		list = new ArrayList<T>(size);
	}

	/**
	 * 新增数据
	 * 
	 * @param obj
	 * @return
	 */
	protected boolean add(T obj)
	{
		try
		{
			lock.writeLock().lock();
			return list.add(obj);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/** 如果已经在里面了, 不插入. **/
	protected boolean addIfAbsent(T obj)
	{
		try
		{
			lock.writeLock().lock();
			return (!list.contains(obj)) ? list.add(obj) : false;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 批量添加
	 * 
	 * @param c
	 * @return
	 */
	protected boolean addAll(Collection<? extends T> c)
	{
		try
		{
			lock.writeLock().lock();
			return list.addAll(c);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param index
	 * @return
	 */
	protected T get(int index)
	{
		lock.readLock().lock();
		T obj = list.get(index);
		lock.readLock().unlock();
		return obj;
	}

	/**
	 * 删除对应索引的对象
	 * 
	 * @param index
	 * @return
	 */
	protected T remove(int index)
	{
		lock.writeLock().lock();
		T obj = list.remove(index);
		lock.writeLock().unlock();
		return obj;
	}

	/**
	 * 删除对象
	 * 
	 * @param obj
	 * @return
	 */
	protected boolean remove(T obj)
	{
		try
		{
			lock.writeLock().lock();
			return list.remove(obj);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 清除数据
	 */
	protected void clear()
	{
		try
		{
			lock.writeLock().lock();
			list.clear();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 * @param maxCount
	 * @return
	 */
	protected int action(ListUtils.IAction<? super T> action, int maxCount)
	{
		// 检测是否空
		boolean isEmpty = this.isEmpty();
		if (isEmpty)
		{
			return 0;
		}
		try
		{
			lock.writeLock().lock();
			return ListUtils.action(list, action, maxCount);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * 查找对象
	 * 
	 * @param filter
	 * @return
	 */
	protected T find(ListUtils.IFilter<? super T> filter)
	{
		// 检测是否空
		boolean isEmpty = this.isEmpty();
		if (isEmpty)
		{
			return null;
		}

		lock.readLock().lock();
		T obj = ListUtils.find(list, filter);
		lock.readLock().unlock();
		return obj;
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param list
	 *            列表
	 * @param filter
	 *            过滤器
	 * @param maxCount
	 *            最大数量, 0为全部
	 * @return 符合条件的数据数组
	 */
	protected List<T> findAll(ListUtils.IFilter<? super T> filter, int maxCount)
	{
		// 检测是否空
		boolean isEmpty = this.isEmpty();
		if (isEmpty)
		{
			return null;
		}

		lock.readLock().lock();
		List<T> ret = ListUtils.findAll(list, filter, maxCount);
		lock.readLock().unlock();

		return ret;
	}

	/**
	 * 获取数据列表(新建列表)
	 * 
	 * @return
	 */
	protected List<T> getList()
	{
		try
		{
			lock.readLock().lock();
			return new ArrayList<T>(list);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * 获取某个段的数据
	 * 
	 * @param start
	 * @param size
	 * @return
	 */
	protected List<T> getList(int start, int size)
	{
		try
		{
			lock.readLock().lock();
			return list.subList(start, start + size);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	protected int size()
	{
		try
		{
			lock.readLock().lock();
			return list.size();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	protected boolean isEmpty()
	{
		try
		{
			lock.readLock().lock();
			return list.isEmpty();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	protected ReadWriteLock getLock()
	{
		return lock;
	}

	protected boolean contains(T obj)
	{
		try
		{
			lock.readLock().lock();
			return list.contains(obj);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString()
	{
		lock.readLock().lock();
		String str = list.toString();
		lock.readLock().unlock();
		return str;
	}
}
