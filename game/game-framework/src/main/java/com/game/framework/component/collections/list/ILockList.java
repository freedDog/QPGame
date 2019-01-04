package com.game.framework.component.collections.list;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

import com.game.framework.utils.collections.ListUtils.IAction;
import com.game.framework.utils.collections.ListUtils.IFilter;


/**
 *  带读写锁的列表<br>
 * ILockList.java
 * @author JiangBangMing
 * 2019年1月3日下午1:08:11
 */
public interface ILockList<T> {
	
	boolean addIfAbsent(T obj);

	boolean add(T obj);

	boolean addAll(Collection<? extends T> c);

	T get(int index);

	T remove(int index);

	boolean remove(T obj);

	void clear();

	int action(IAction<? super T> action, int maxCount);

	T find(IFilter<? super T> filter);

	List<T> findAll(IFilter<? super T> filter, int maxCount);

	List<T> getList();

	List<T> getList(int start, int size);

	int size();

	boolean isEmpty();

	ReadWriteLock getLock();

	boolean contains(T obj);
}
