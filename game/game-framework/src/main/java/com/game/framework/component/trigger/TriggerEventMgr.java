package com.game.framework.component.trigger;

import java.util.Iterator;
import java.util.List;

import com.game.framework.component.collections.list.LockList;
import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.ListUtils;


/**
 * 触发事件管理器<br>
 * 按照时间计算触发事件
 * TriggerEventMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午1:54:58
 */
public class TriggerEventMgr<T, E extends ITriggerEvent<T>>
{
	protected final LockList<E> events;

	public TriggerEventMgr()
	{
		// 事件排序, 越早创建的越前
		// events = new LockSortList<>(new Comparator<E>()
		// {
		// @Override
		// public int compare(E arg0, E arg1)
		// {
		// return -Utils.CompareUtils.compare(arg0.getExcTime(0, 0),
		// arg1.getExcTime(0, 0));
		// }
		// });

		events = new LockList<>();
	}

	/** 初始化事件 **/
	public boolean init(List<E> events)
	{
		this.events.addAll(events);
		return true;
	}

	/** 添加事件, 事件开始时间最好是当前时间之后, 否则更新过的玩家不会再更新这个事件 **/
	public boolean add(E event)
	{
		// long nowTime = System.currentTimeMillis();
		// if (event.getStartTime() < nowTime)
		// {
		// Log.error("只能添加当前时间之后的事件!" + event, true);
		// return false;
		// }
		// 判断时间
		events.add(event);
		return true;
	}

	public void updata(T obj, long prevTime, long nowTime)
	{
		// 遍历事件
		List<E> list = events.getList();
		for (E event : list)
		{
			// 判断是否存活, 用prevTime检测, 避免漏掉.
			if (!event.isAlive(prevTime))
			{
				continue;
			}

			// 判断是否能执行
			try
			{
				event.execute(obj, prevTime, nowTime);
			}
			catch (Exception e)
			{
				Log.error("处理事件错误! ", e);
			}
		}

	}

	/** 清除过期事件 **/
	public void clean(final long nowTime)
	{
		// 遍历事件, 清除事件
		events.action(new ListUtils.IAction<E>()
		{
			@Override
			public boolean action(E event, Iterator<?> iter)
			{
				// 判断事件是否结束
				if (!event.isAlive(nowTime))
				{
					iter.remove(); // 删除
				}
				return true;
			}
		}, 0);
	}

	/** 检测是否能执行 **/
	public static boolean checkExec(long prevTime, long nowTime, long execTime)
	{
		// 判断事件是否执行过.
		if (prevTime >= execTime)
		{
			return false; // 这个事件已经开始过了.
		}
		if (nowTime < execTime)
		{
			return false; // 尚未开始
		}
		// 可触发
		return true;
	}

	/**
	 * 检测循环事件(精确计算出触发区间)
	 * 
	 * @param startTime
	 *            事件开始时间
	 * @param intervalTime
	 *            事件间隔
	 * @param resetCount
	 *            事件重复次数, 0为永久
	 * @return 触发区间[N, N+1], null为无效过滤, 时间可通过startTime+N*intervalTime
	 */
	public static int[] checkLoopExec(long prevTime, long nowTime, long startTime, int intervalTime, int resetCount)
	{
		// 检测是否开始
		if (nowTime < startTime)
		{
			return null; // 尚未开始
		}
		// 控制开始时间, 避免过多执行.
		prevTime = Math.max(prevTime, startTime);

		// 检测最长时间限制
		if (resetCount != 0)
		{
			long endTime = startTime + ((long) resetCount * intervalTime);
			if (prevTime > endTime)
			{
				return null; // 上次计算的时间已经超过最大时间, 无需计算了
			}
			// 控制检测时间(避免操作了还继续)
			nowTime = Math.min(nowTime, endTime);
		}
		intervalTime = Math.max(intervalTime, 1);

		// 计算各自对应的间隔时间
		int startIndex = (int) Math.ceil((prevTime - startTime) / (double) intervalTime);
		int endIndex = (int) Math.ceil((nowTime - startTime) / (double) intervalTime);
		int count = endIndex - startIndex;
		// Log.debug(startIndex + " -> " + endIndex + " = " + count);
		if (count <= 0)
		{
			return null; // 无除触发
		}

		return new int[] { startIndex, endIndex };
	}

	/** 间隔任务中, 获取与当前最接近的执行时间 **/
	public static long getNowExecTime(long checkTime, long startTime, int intervalTime, int resetCount)
	{
		// 有循环次数, 计算这个时候是第几次循环中
		long dt = checkTime - startTime;
		if (dt <= 0)
		{
			return startTime; // 还没到开始时间
		}

		// 计算第几次循环中
		intervalTime = Math.max(intervalTime, 1);
		int count = (int) (dt / intervalTime);
		count = (resetCount != 0) ? Math.min(count, resetCount - 1) : count; // 循环次数最大不能超过设置次数

		// 计算出实际时间
		long msgTime = startTime + count * intervalTime;
		return msgTime;
	}
}
