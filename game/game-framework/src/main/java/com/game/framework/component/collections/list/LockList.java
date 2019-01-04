package com.game.framework.component.collections.list;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

import com.game.framework.utils.collections.ListUtils.IAction;
import com.game.framework.utils.collections.ListUtils.IFilter;


/**
 * 带读写锁的列表<br>
 * 对外开放, 直接使用.
 * LockList.java
 * @author JiangBangMing
 * 2019年1月3日下午1:09:27
 */
public class LockList<T> extends LockList0<T> implements ILockList<T>
{

	@Override
	public boolean addIfAbsent(T obj)
	{
		return super.addIfAbsent(obj);
	}

	@Override
	public boolean add(T obj)
	{
		return super.add(obj);
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		return super.addAll(c);
	}

	@Override
	public T get(int index)
	{
		return super.get(index);
	}

	@Override
	public T remove(int index)
	{
		return super.remove(index);
	}

	@Override
	public boolean remove(T obj)
	{
		return super.remove(obj);
	}

	@Override
	public void clear()
	{
		super.clear();
	}

	@Override
	public int action(IAction<? super T> action, int maxCount)
	{
		return super.action(action, maxCount);
	}

	@Override
	public T find(IFilter<? super T> filter)
	{
		return super.find(filter);
	}

	@Override
	public List<T> findAll(IFilter<? super T> filter, int maxCount)
	{
		return super.findAll(filter, maxCount);
	}

	@Override
	public List<T> getList()
	{
		return super.getList();
	}

	@Override
	public List<T> getList(int start, int size)
	{
		return super.getList(start, size);
	}

	@Override
	public int size()
	{
		return super.size();
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty();
	}

	@Override
	public ReadWriteLock getLock()
	{
		return super.getLock();
	}

	@Override
	public boolean contains(T obj)
	{
		return super.contains(obj);
	}

}
