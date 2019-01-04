package com.game.framework.framework.bag;

import com.game.framework.utils.TimeUtils;

/**
 * 玩家物品
 * Item.java
 * @author JiangBangMing
 * 2019年1月3日下午2:36:51
 */
public abstract class Item<T extends IItemTempInfo>
{
	protected Bag<?, ?> bag;
	protected T tempInfo;

	public Item(Bag<?, ?> bag, T tempInfo)
	{
		this.bag = bag;
		this.tempInfo = tempInfo;
	}

	protected abstract void setCount(int count);

	/** 获取物品Id, 数据库唯一Id **/
	public abstract long getItemId();

	/** 获取物品数量 **/
	public abstract int getCount();

	/** 获取物品模板Id(类型Id) **/
	public int getTemplateId()
	{
		return tempInfo.getTemplateId();
	}

	/** 获取物品堆叠上限数量 **/
	public int getMaxCount()
	{
		return tempInfo.getMaxCount();
	}

	/** 获取物品有效时间(转成秒单位), 等于或者小于0为永久有效. **/
	public int getActiveTime()
	{
		return 0;
	}

	/** 获取在背包中的位置 **/
	public abstract int getSlot();

	/** 设置在背包中的位置(不能直接调用, 要通过背包分配) **/
	protected abstract void setSlot(int slot);

	/** 是否显示 **/
	public boolean isVisible()
	{
		return getSlot() >= 0;
	}

	/** 是否有效, 检测是否存在(删除无效), 检测是否过时(有有效时间时检测). **/
	public boolean isActive()
	{
		if (!isExist())
		{
			return false;
		}
		// 判断时效
		int activeTime = getActiveTime();
		if (activeTime > 0)
		{
			return TimeUtils.getCurrentTime() <= getActiveTime();
		}
		return true;
	}

	/** 是否存在(是否被删除) **/
	public abstract boolean isExist();

	/** 物品发生更新 **/
	@SuppressWarnings("unchecked")
	protected void update()
	{
		if (this.bag == null)
		{
			return;
		}
		((Bag<Item<?>, ?>) bag).updateItem(this);
	}

	/** 获取对应背包 **/
	@SuppressWarnings("unchecked")
	public <B extends Bag<?, ?>> B getBag()
	{
		return (B) bag;
	}

	public T getTempInfo()
	{
		return tempInfo;
	}

	@Override
	public String toString()
	{
		return "Item [id=" + this.getItemId() + " count=" + this.getCount() + " tempId=" + this.getTemplateId() + " slot=" + getSlot() + "]";
	}

}
