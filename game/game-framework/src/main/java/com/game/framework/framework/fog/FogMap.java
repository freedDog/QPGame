package com.game.framework.framework.fog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.component.collections.list.LockList;



/**
 * 地图迷雾表<br>
 * 记录那些是打开的迷雾点<br>
 * 线程同步<br>
 * 创建1个IFogPoint类, 然后调用add方法添加解锁的节点即可,<br>
 * onAdd方法为解锁后用于同步消息, getOpens用于获取当前所有解锁点<br>
 * FogMap.java
 * @author JiangBangMing
 * 2019年1月3日下午2:57:08
 */
public class FogMap<P extends IFogPoint>
{
	protected final ConcurrentMap<Integer, ConcurrentMap<Integer, P>> points;
	protected final LockList<P> opens;

	public FogMap()
	{
		points = new ConcurrentHashMap<>();
		opens = new LockList<>();
	}

	/** 是否有记录 **/
	public boolean isOpen(int x, int y)
	{
		ConcurrentMap<Integer, P> map = points.get(x);
		return (map != null) ? map.containsKey(y) : false;
	}

	/** 新增事件 **/
	protected void onAdd(List<P> list, Object... args)
	{
	}

	/** 记录迷雾打开点 **/
	public void add(List<P> list, Object... args)
	{
		List<P> news = new ArrayList<>();
		// 遍历插入
		int psize = (list != null) ? list.size() : 0;
		for (int i = 0; i < psize; i++)
		{
			P point = list.get(i);
			int x = point.getX();
			int y = point.getY();
			// 检测是否存在点
			ConcurrentMap<Integer, P> map = points.get(x);
			if (map == null)
			{
				map = new ConcurrentHashMap<>();
				ConcurrentMap<Integer, P> old = points.putIfAbsent(x, map);
				map = (old != null) ? old : map;
			}

			// 插入记录
			P old = map.putIfAbsent(y, point);
			if (old != null)
			{
				continue; // 已经记录过这个点
			}
			// 记录新开点
			news.add(point);
			opens.add(point);
		}

		// 触发事件
		int nsize = (news != null) ? news.size() : 0;
		if (nsize > 0)
		{
			onAdd(news, args);
		}
	}

	/** 获取所有打开的点 **/
	public List<P> getOpens()
	{
		return opens.getList();
	}

}
