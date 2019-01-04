package com.game.framework.framework.aoi;

import java.util.Comparator;
import java.util.List;

import com.game.framework.component.collections.list.LockSortList;



/**
 * AOI筛选对象<br>
 * 能按优先级和数量过滤对象
 * AOIFilterEntity.java
 * @author JiangBangMing
 * 2019年1月3日下午2:03:44
 */
public abstract class AOIFilterEntity<E extends AOIEntity<?>> extends AOIEntity<E>
{
	protected int maxViewCount = 0;

	public AOIFilterEntity(int maxViewCount, int viewWidth, int viewHeight)
	{
		super(viewWidth, viewHeight);
		this.maxViewCount = maxViewCount;
		// 换成排序列表, 优先级高的在后
		this.entitys = new LockSortList<E>(new Comparator<E>()
		{
			@Override
			public int compare(E o1, E o2)
			{
				int priority1 = getPriority(o1);
				int priority2 = getPriority(o2);
				return Integer.compare(priority1, priority2);
			}
		});
	}

	/** 获取优先级, 如果是0则是最低优先级, 直接过滤了. **/
	protected abstract int getPriority(E entity);

	@Override
	protected boolean checkFilter(E entity)
	{
		// 判断显示人数
		int esize = entitys.size();
		if (esize < maxViewCount)
		{
			return true;
		}

		// 超过上限, 按照优先级处理筛选
		int priority = getPriority(entity);
		if (priority <= 0)
		{
			return false; // 不优先, 不处理.
		}

		// 找不比这个低级的人, 列表是顺序的, 优先级高在上
		List<E> list = entitys.getList();
		esize = (list!=null)? list.size(): 0;
		for (int i = 0; i < esize; i++)
		{
			E e = list.get(i);
			int p = getPriority(e);
			if (p >= priority)
			{
				continue; // 优先级比这个高
			}
			// 可以替换掉这个
			if (!this.removeEntity(e))
			{
				continue; // 移除失败, 继续下一个.
			}
			// 剔除成功
			return true;
		}

		return false;
	}

	public void setMaxViewCount(int maxViewCount)
	{
		this.maxViewCount = maxViewCount;
	}

	public int getMaxViewCount()
	{
		return maxViewCount;
	}

}
