package com.game.framework.framework.aoi;

import java.util.List;

import com.game.framework.component.collections.list.LockList;


/**
 * AOI世界中的一个空间范围(节点空间)<br>
 * AOINode.java
 * @author JiangBangMing
 * 2019年1月3日下午2:01:49
 */
public class AOINode<E extends AOIEntity<?>>
{
	protected final LockList<E> entitys;
	protected final AOIMgr<E, ?> mgr;
	protected final int cellX;
	protected final int cellY;

	public AOINode(AOIMgr<E, ?> mgr, int cellX, int cellY)
	{
		this.mgr = mgr;
		entitys = new LockList<>();
		this.cellX = cellX;
		this.cellY = cellY;
	}

	/** 添加对象 **/
	public boolean add(E entity)
	{
		if (!entitys.add(entity))
		{
			return false;
		}
		entity.setNode(this);
		return true;
	}

	/** 移除对象 **/
	public boolean remove(E entity)
	{
		if (!entitys.remove(entity))
		{
			return false;
		}
		if (entity.getNode() != this)
		{
			return false;
		}
		entity.setNode(null);
		return true;
	}

	/** 获取当前节点中所有对象 **/
	public List<E> getList()
	{
		return entitys.getList();
	}

	public int size()
	{
		return entitys.size();
	}

	@Override
	public String toString()
	{
		return entitys.toString();
	}

	public int getCellX()
	{
		return cellX;
	}

	public int getCellY()
	{
		return cellY;
	}
}
