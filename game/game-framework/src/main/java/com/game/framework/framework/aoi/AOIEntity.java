package com.game.framework.framework.aoi;

import java.util.List;

import com.game.framework.component.collections.list.ILockList;
import com.game.framework.component.collections.list.LockList;
import com.game.framework.component.log.Log;


/**
 * AOI对象<br>
 * 视野对象是保证双方都看到相同的人
 * AOIEntity.java
 * @author JiangBangMing
 * 2019年1月3日下午2:01:15
 */
public class AOIEntity<E extends AOIEntity<?>>
{
	protected AOINode<?> node;
	protected ILockList<E> entitys; // 视野内的玩家

	protected float x;
	protected float y;
	protected int viewWidth; // 视野大小
	protected int viewHeight;

	public AOIEntity(int viewWidth, int viewHeight)
	{
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		entitys = new LockList<>();
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public boolean setPos(float x, float y)
	{
		if (this.x == x && this.y == y)
		{
			return false;
		}
		this.x = x;
		this.y = y;
		return true;
	}

	@SuppressWarnings("unchecked")
	public <N extends AOINode<?>> N getNode()
	{
		return (N) node;
	}

	protected void setNode(AOINode<?> node)
	{
		this.node = node;
	}

	/** 是否在视野中 **/
	public boolean inView(AOIEntity<?> entity)
	{
		return inView(entity.getX(), entity.getY());
	}

	/** 是否在视野中 **/
	public boolean inView(float cx, float cy)
	{
		float startX = x - viewWidth / 2;
		float endX = x + viewWidth / 2;
		float startY = y - viewHeight / 2;
		float endY = y + viewHeight / 2;
		// Log.debug(startY + " <" + cy + "< " + endY);
		return (startX <= cx && cx <= endX) && (startY <= cy && cy <= endY);
	}

	/** 检测筛选, 如果返回false为不让对方进入视野 **/
	protected boolean checkFilter(E entity)
	{
		return true;
	}

	/** 检测是否进入视野(处理双方视野) **/
	public boolean checkEnter(E entity)
	{
		// 过滤自己
		if (entity.equals(this))
		{
			return false;
		}
		// 检测是否在视野中
		@SuppressWarnings("unchecked")
		AOIEntity<AOIEntity<?>> entity0 = (AOIEntity<AOIEntity<?>>) entity;
		if (entitys.contains(entity) || entity0.entitys.contains(this))
		{
			return false; // 已经在视野中了.
		}

		// 判断是否进入视野(双方一方看到即可, 实际上就是按照视野大的来)
		if (!this.inView(entity) && !entity0.inView(this))
		{
			return false; // 不在视野
		}
		if (!checkFilter(entity) || !entity0.checkFilter(this))
		{
			return false; // 筛选掉了
		}

		// 尝试加入
		if (!entitys.addIfAbsent(entity))
		{
			return false; // 已经在视野内
		}
		if (!entity0.entitys.addIfAbsent(this))
		{
			entitys.remove(entity); // 把之前加的取消掉
			return false;
		}

		onEntityEnter(entity);
		entity0.onEntityEnter(this);
		return true;
	}

	/** 检测对方是否离开视野(处理双方视野) **/
	public boolean checkLeave(E entity)
	{
		// 过滤自己
		if (entity.equals(this))
		{
			return false;
		}
		// 检测是否在视野中
		@SuppressWarnings("unchecked")
		AOIEntity<AOIEntity<?>> entity0 = (AOIEntity<AOIEntity<?>>) entity;
		if (!entitys.contains(entity) || !entity0.entitys.contains(this))
		{
			return false; // 不在视野内, 不判断离开.
		}

		// 判断是否在视野(1方还看到就算还在, 以大的为准)
		if (this.inView(entity) || entity0.inView(this))
		{
			return false; // 还在视野内
		}
		return removeEntity(entity);
	}

	/** 直接移除视野内的玩家(处理双方) **/
	protected boolean removeEntity(E entity)
	{
		@SuppressWarnings("unchecked")
		AOIEntity<AOIEntity<?>> entity0 = (AOIEntity<AOIEntity<?>>) entity;
		// 双方都移除, 要2边移除成功才算.
		boolean result = entitys.remove(entity); // 移除
		result = result && entity0.entitys.remove(this);
		if (!result)
		{
			return false; // 已经不在了
		}

		// 移除成功
		onEntityLeave(entity);
		entity0.onEntityLeave(this);
		return true;
	}

	/** 对方在你视野内移动 **/
	protected void onEntityMove(E entity, float x, float y)
	{
		Log.debug(entity + "发现" + this + " 在移动 ");
	}

	/** 对方在进入你视野 **/
	protected void onEntityEnter(E entity)
	{
		Log.debug(entity + "发现" + this + "进入视野 ");
	}

	/** 对方在离开你视野 **/
	protected void onEntityLeave(E entity)
	{
		Log.debug(entity + "发现" + this + " 离开视野  ");
	}

	@Override
	public String toString()
	{
		return "[x=" + x + ", y=" + y + "]";
	}

	public int getViewWidth()
	{
		return viewWidth;
	}

	public void setViewWidth(int viewWidth)
	{
		this.viewWidth = viewWidth;
	}

	public int getViewHeight()
	{
		return viewHeight;
	}

	public void setViewHeight(int viewHeight)
	{
		this.viewHeight = viewHeight;
	}

	/** 获取视野内玩家 (新建列表) **/
	public List<E> getEntitys()
	{
		return entitys.getList();
	}

}
