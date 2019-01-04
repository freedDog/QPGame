package com.game.framework.framework.aoi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.ListUtils;


/**
 * AOI管理器<br>
 * 传统RPG游戏管理空间算法(2维)<br>
 * 实际上就是个碰撞算法.<br>
 * 视野会保证双方看到能够互相看到(前提是双方视野范围相同, 如果一方大, 那么按照大的来.)
 * AOIMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午2:02:43
 */
public abstract class AOIMgr<E extends AOIEntity<?>, N extends AOINode<E>>
{
	protected int cellWidth; // 一个区间宽度
	protected int cellHeight; // 一个区间高度
	protected final ConcurrentMap<Integer, ConcurrentMap<Integer, N>> nodes;
	protected final ReadWriteLock lock; // 锁定, 主要用于执行清除的

	public AOIMgr(int cellWidth, int cellHeight)
	{
		this.cellHeight = cellHeight;
		this.cellWidth = cellWidth;
		lock = new ReentrantReadWriteLock();
		nodes = new ConcurrentHashMap<>();
	}

	/** 根据位置转化索引 **/
	protected static int getIndex(float v, int s)
	{
		return (int) (((v < 0) ? v - s + 1 : v) + (s / 2)) / s;
	}

	/** 获取或者创建节点, 整个过程锁定, 防止中途被清除掉. **/
	protected N getAndCreateByIndex(int x, int y)
	{
		try
		{
			lock.readLock().lock();
			// 获取所在位置.
			ConcurrentMap<Integer, N> nodes0 = nodes.get(x);
			if (nodes0 == null)
			{
				// 创建节点
				nodes0 = new ConcurrentHashMap<>();
				ConcurrentMap<Integer, N> old = nodes.putIfAbsent(x, nodes0);
				nodes0 = (old != null) ? old : nodes0;
			}
			// 创建
			N node = nodes0.get(y);
			if (node != null)
			{
				return node; // 已经创建了
			}
			// 创建节点
			node = create(x, y);
			N old = nodes0.putIfAbsent(y, node);
			node = (old != null) ? old : node;
			return node;
		}
		finally
		{
			lock.readLock().unlock();
		}

	}

	/** 创建AOI节点 **/
	protected abstract N create(int cellX, int cellY);

	/** 根据索引获取对应区间 **/
	protected N getByIndex(int x, int y, boolean create)
	{
		// 获取所在位置.
		ConcurrentMap<Integer, N> nodes0 = nodes.get(x);
		if (nodes0 == null)
		{
			if (!create)
			{
				return null;
			}
			return getAndCreateByIndex(x, y);
		}
		// 获取节点
		N node = nodes0.get(y);
		if (node == null)
		{
			if (!create)
			{
				return null;
			}
			return getAndCreateByIndex(x, y);
		}
		return node;

	}

	/** 通过位置获取对应的位置. **/
	protected N getByPos(float x, float y, boolean create)
	{
		int cellX = getIndex(x, cellWidth);
		int cellY = getIndex(y, cellHeight);
		// Log.info("" + x + " " + y + " -> " + cellX + " " + cellY);
		return getByIndex(cellX, cellY, create);
	}

	/** 获取视野范围内所有涉及的位置 **/
	protected List<N> getViewNode(float x, float y, int viewX, int viewY)
	{
		List<N> nodes = new ArrayList<>();
		// 根据视野范围, 计算出涉及的区间
		int startX = getIndex(x - viewX / 2, cellWidth);
		int endX = getIndex(x + viewX / 2, cellWidth);
		int startY = getIndex(y - viewY / 2, cellHeight);
		int endY = getIndex(y + viewY / 2, cellHeight);
		for (int i = startX; i <= endX; i++)
		{
			for (int j = startY; j <= endY; j++)
			{
				N node = this.getByIndex(i, j, false);
				if (node != null)
				{
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	/** 获取视野范围内所有涉及的位置 **/
	protected List<N> getViewNode(AOIEntity<?> entity, float x, float y)
	{
		// 整理视野区间
		int viewX = entity.getViewWidth();
		int viewY = entity.getViewHeight();
		return getViewNode(x, y, viewX, viewY);
	}

	/** 进入 **/
	@SuppressWarnings("unchecked")
	public boolean enter(final E entity)
	{
		final float x = entity.getX();
		final float y = entity.getY();
		try
		{
			lock.readLock().lock();
			// 锁定, 保证这个过程中节点不被清除.
			N node = getByPos(x, y, true);
			if (!node.add(entity))
			{
				Log.error("添加失败! 没道理", true);
				return false;
			}
		}
		finally
		{
			lock.readLock().unlock();
		}

		// 获取视野所在玩家
		List<N> nodes = getViewNode(entity, x, y);
		List<E> news = getEntitys(nodes);

		// 检测新增用户
		for (E view : news)
		{
			((AOIEntity<AOIEntity<?>>) entity).checkEnter(view);
		}
		return true;
	}

	/** 原地更新(如果有筛选情况的话, 离开了保证在屏幕内的人能正常显示) **/
	@SuppressWarnings("unchecked")
	public void recheck(final E entity)
	{
		// 清除更新当前视野内的玩家
		List<E> nows = (List<E>) entity.getEntitys();
		for (E view : nows)
		{
			((AOIEntity<AOIEntity<?>>) entity).checkLeave(view);
		}
		float x = entity.getX();
		float y = entity.getY();
		// 计算获取新视野范围和视野人物
		List<N> nnodes = getViewNode(entity, x, y);
		List<E> news = getEntitys(nnodes);

		// 检测新增用户
		for (E view : news)
		{
			// 过滤自己
			if (view == null || view.equals(entity))
			{
				continue;
			}
			((AOIEntity<AOIEntity<?>>) view).checkEnter(entity);
			((AOIEntity<AOIEntity<?>>) entity).checkEnter(view);
		}
	}

	/** 移动 **/
	@SuppressWarnings("unchecked")
	public void move(final E entity, final float x, final float y)
	{
		final float px = entity.getX();
		final float py = entity.getY();
		if (px == x && py == y)
		{
			return; // 没动, 玩个球.
		}

		// 处理模块移动
		N pnode = entity.getNode(); // 源区域
		N nnode = getByPos(x, y, true); // 移动后的新区域
		try
		{
			lock.readLock().lock();
			// 保证这个过程中不被清除
			if (pnode == null || !pnode.equals(nnode))
			{
				if (pnode != null)
				{
					pnode.remove(entity);
				}
				if (!nnode.add(entity))
				{
					Log.error("添加失败! 没道理", true);
					return;
				}
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		entity.setPos(x, y);
		// Log.debug(entity + " " + px + "," + py + " -> " + x + "," + y);

		// 清除更新当前视野内的玩家
		List<E> nows = (List<E>) entity.getEntitys();
		for (E view : nows)
		{
			((AOIEntity<AOIEntity<?>>) entity).checkLeave(view);
		}

		// 计算获取新视野范围和视野人物
		List<N> nnodes = getViewNode(entity, x, y);
		List<E> news = getEntitys(nnodes);

		// 检测新增用户
		for (E view : news)
		{
			// 过滤自己
			if (view == null || view.equals(entity))
			{
				continue;
			}
			((AOIEntity<AOIEntity<?>>) entity).checkEnter(view);
		}

		// 更新当前视野内玩家自己的移动
		nows = (List<E>) entity.getEntitys();
		for (E view : nows)
		{
			((AOIEntity<AOIEntity<?>>) view).onEntityMove(entity, x, y);
		}

	}

	/** 离开 **/
	@SuppressWarnings("unchecked")
	public boolean leave(final E entity)
	{
		N node = entity.getNode();
		if (node == null || !node.remove(entity))
		{
			return false; // 移除失败或者没有, 不管
		}

		// 清除更新当前视野内的玩家
		List<E> nows = (List<E>) entity.getEntitys();
		for (E view : nows)
		{
			((AOIEntity<AOIEntity<?>>) view).removeEntity(entity);
			((AOIEntity<AOIEntity<?>>) entity).removeEntity(view);
		}
		return true;
	}

	/** 清除没有人的区间 **/
	public void clean()
	{
		// 遍历所有节点
		Map<Integer, ConcurrentMap<Integer, N>> all = new HashMap<>(nodes);
		Iterator<ConcurrentMap.Entry<Integer, ConcurrentMap<Integer, N>>> iter = all.entrySet().iterator();
		while (iter.hasNext())
		{
			ConcurrentMap.Entry<Integer, ConcurrentMap<Integer, N>> entry = iter.next();
			ConcurrentMap<Integer, N> map = entry.getValue();
			List<N> removes = new ArrayList<>(map.values());

			try
			{
				lock.writeLock().lock();

				// 遍历清除节点
				for (N node : removes)
				{
					// 再判断一次数量进行移除.
					int nsize = (node != null) ? node.size() : 0;
					if (nsize <= 0)
					{
						// 移除
						map.remove(node.getCellY(), node);
					}
				}

				// 判断数量, 清除cell.
				if (map.size() <= 0)
				{
					Integer key = entry.getKey();
					nodes.remove(key, map);
				}

			}
			finally
			{
				lock.writeLock().unlock();
			}
		}

	}

	public List<E> getEntitys()
	{
		List<E> retList = new ArrayList<>();
		if (nodes == null)
		{
			return retList;
		}
		// 遍历所有节点
		for (ConcurrentMap<Integer, N> map : nodes.values())
		{
			if (map == null)
			{
				continue;
			}
			// 统计map
			for (N node : map.values())
			{
				if (node == null)
				{
					continue;
				}
				retList.addAll(node.getList());
			}
		}
		return retList;
	}

	public int getEntitySize()
	{
		if (nodes == null)
		{
			return 0;
		}
		// 遍历所有节点
		int esize = 0;
		for (ConcurrentMap<Integer, N> map : nodes.values())
		{
			if (map == null)
			{
				continue;
			}
			// 统计map
			for (N node : map.values())
			{
				esize += (node != null) ? node.size() : 0;
			}
		}
		return esize;
	}

	/** 获取节点数 **/
	public int getNodeSize()
	{
		if (nodes == null)
		{
			return 0;
		}
		int nsize = 0;
		for (ConcurrentMap<Integer, N> map : nodes.values())
		{
			nsize = (map != null) ? map.size() : 0;
		}
		return nsize;
	}

	/** 获取出各个节点中所有的对象 **/
	protected static <E extends AOIEntity<?>, N extends AOINode<E>> List<E> getEntitys(List<N> nodes)
	{
		// 遍历节点
		List<E> entitys = new ArrayList<>();
		int nsize = (nodes != null) ? nodes.size() : 0;
		for (int i = 0; i < nsize; i++)
		{
			N cnode = nodes.get(i);
			if (cnode == null)
			{
				continue;
			}
			// 遍历对象
			entitys.addAll(cnode.getList());
		}
		return entitys;
	}

	/** 遍历各个节点中的玩家 **/
	protected static <E extends AOIEntity<?>, N extends AOINode<E>> boolean action(List<N> nodes, ListUtils.IAction<E> action)
	{
		// 遍历节点
		int nsize = (nodes != null) ? nodes.size() : 0;
		for (int i = 0; i < nsize; i++)
		{
			N cnode = nodes.get(i);
			if (cnode == null)
			{
				continue;
			}
			// 遍历对象
			List<E> elist = cnode.getList();
			int esize = elist != null ? elist.size() : 0;
			for (int j = 0; j < esize; j++)
			{
				E entity = elist.get(j);
				if (entity == null)
				{
					continue;
				}

				if (!action.action(entity, null))
				{
					return false;
				}
			}
		}
		return true;
	}
}
