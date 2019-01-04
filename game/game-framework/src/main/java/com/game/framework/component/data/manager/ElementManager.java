package com.game.framework.component.data.manager;

import com.game.framework.utils.collections.MapUtils;

/**
 * 对象管理器<br>
 * ElementManager.java
 * @author JiangBangMing
 * 2019年1月3日下午1:41:18
 */
public abstract class ElementManager<K, T> extends ElementManager0<K, T>
{
	@Override
	public boolean isExist(K key)
	{
		return super.isExist(key);
	}

	@Override
	public T getFromCache(K key)
	{
		return super.getFromCache(key);
	}

	@Override
	public T get(K key)
	{
		return super.get(key);
	}

	@Override
	public T remove(K key)
	{
		return super.remove(key);
	}

	@Override
	public boolean remove(K key, T entity)
	{
		return super.remove(key, entity);
	}

	@Override
	public void forech(MapUtils.Foreach<K, T> foreach)
	{
		super.forech(foreach);
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
	}

	@Override
	public int size()
	{
		return super.size();
	}
}
