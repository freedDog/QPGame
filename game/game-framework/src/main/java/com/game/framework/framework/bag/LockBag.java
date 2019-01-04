package com.game.framework.framework.bag;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.framework.utils.struct.result.Result;



/**
 * 线程安全的背包<br>
 * 使用读写锁处理
 * LockBag.java
 * @author JiangBangMing
 * 2019年1月3日下午2:39:30
 */
public abstract class LockBag<T extends Item<D>, D extends IItemTempInfo> extends Bag<T, D>
{
	protected final ReadWriteLock lock;

	public LockBag()
	{
		lock = new ReentrantReadWriteLock();
	}

	public ReadWriteLock getLock()
	{
		return lock;
	}

	@Override
	public boolean init(List<T> items)
	{
		try
		{
			lock.writeLock().lock();
			return super.init(items);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public void tidy()
	{
		try
		{
			lock.writeLock().lock();
			super.tidy();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public Result check(int itemTempId, long change, boolean stack)
	{
		try
		{
			lock.readLock().lock();
			return super.check(itemTempId, change, stack);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	protected Result add(D tempInfo, int count, short source, boolean stack, Object... args)
	{
		try
		{
			lock.writeLock().lock();
			return super.add(tempInfo, count, source, stack, args);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	protected Result remove(D tempInfo, int count, short source, Object... args)
	{
		try
		{
			lock.writeLock().lock();
			return super.remove(tempInfo, count, source, args);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public int getCount(int itemTempId)
	{
		try
		{
			lock.readLock().lock();
			return super.getCount(itemTempId);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public List<T> getItems()
	{
		try
		{
			lock.readLock().lock();
			return super.getItems();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public int getEmptySlot()
	{
		try
		{
			lock.readLock().lock();
			return super.getEmptySlot();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public int getCellCount()
	{
		try
		{
			lock.readLock().lock();
			return super.getCellCount();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	protected void flushCache()
	{
		try
		{
			lock.writeLock().lock();
			super.flushCache();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	protected Result remove(long itemId, int count, short source, Object... args)
	{
		try
		{
			lock.writeLock().lock();
			return super.remove(itemId, count, source, args);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public T get(long id)
	{
		try
		{
			lock.readLock().lock();
			return super.get(id);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean resetSlot(T item, int slot)
	{
		try
		{
			lock.writeLock().lock();
			return super.resetSlot(item, slot);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

}
