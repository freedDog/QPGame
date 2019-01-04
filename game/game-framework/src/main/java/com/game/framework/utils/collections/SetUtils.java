package com.game.framework.utils.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * set处理工具
 * SetUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:27:54
 */
public class SetUtils
{

	/**
	 * 根据条件删除
	 * 
	 * @param set
	 * @param filter
	 * @return
	 */
	public static <T> boolean remove(Set<T> set, IFilter<? super T> filter)
	{
		int count = removeAll(set, filter, 1);
		return count > 0;
	}

	/**
	 * 根据条件删除
	 * 
	 * @param set
	 * @param filter
	 * @param maxCount
	 * @return
	 */
	public static <T> int removeAll(Set<T> set, final IFilter<? super T> filter, final int maxCount)
	{
		// 遍历读取
		IAction<T> action = new IAction<T>()
		{
			protected int actionCount = 0;

			@Override
			public boolean action(T data, Iterator<?> iter)
			{
				// 判断过滤
				if (filter == null || filter.check(data))
				{
					iter.remove(); // 执行删除
					actionCount++;
				}
				return (maxCount > 0) ? actionCount < maxCount : true; // 数量限制
			}
		};
		return action(set, action, 0); // 顺序,越早得数据在前面
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param filter
	 *            过滤器
	 * @return 符合条件的数据
	 */
	public static <T> T find(Set<T> set, IFilter<? super T> filter)
	{
		List<T> findList = findAll(set, filter, 1);
		int count = (findList != null) ? findList.size() : 0;
		if (count > 0)
		{
			return findList.get(0);
		}
		return null; // 没有
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param set
	 *            列表
	 * @param filter
	 *            过滤器
	 * @param maxCount
	 *            最大数量, 0为全部
	 * @return 符合条件的数据数组
	 */
	public static <T> List<T> findAll(Set<T> set, final IFilter<? super T> filter, final int maxCount)
	{
		// 遍历读取
		final List<T> findList = new ArrayList<T>();
		IAction<T> action = new IAction<T>()
		{
			protected int actionCount = 0;

			@Override
			public boolean action(T data, Iterator<?> iter)
			{
				// 判断过滤
				if (filter == null || filter.check(data))
				{
					findList.add(data); // 加入列表
					actionCount++;
				}
				return (maxCount > 0) ? actionCount < maxCount : true; // 数量限制
			}
		};
		action(set, action, 0); // 顺序,越早得数据在前面
		return findList;
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 *            执行器
	 * @param maxCount
	 *            最多支持执行数量
	 * @return 执行个数
	 */
	public static <T> int action(Set<T> set, IAction<? super T> action, int maxCount)
	{
		if (set.isEmpty() || action == null)
		{
			return 0; // 空的,无视
		}

		// 遍历运行
		int actionCount = 0;
		Iterator<T> iter = set.iterator();
		while (iter.hasNext())
		{
			// 提取消息
			T data = iter.next();
			if (data == null)
			{
				continue; // 跳过
			}

			// 执行处理
			if (!action.action(data, iter))
			{
				return actionCount; // 执行失败,终止
			}
			actionCount++; // 执行成功
			// 检查执行数限制
			if (maxCount > 0 && actionCount >= maxCount)
			{
				break;
			}
		}
		return actionCount;
	}

	// 过滤接口
	public interface IFilter<T>
	{
		public boolean check(T d);
	}

	// 处理接口
	public interface IAction<T>
	{
		public boolean action(T data, Iterator<?> iter);
	}
}
