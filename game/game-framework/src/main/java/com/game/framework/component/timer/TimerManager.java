package com.game.framework.component.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.game.framework.component.collections.list.LockList;
import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.ListUtils;


/**
 * 定时器管理器(线程安全)
 * TimerManager.java
 * @author JiangBangMing
 * 2019年1月3日下午1:56:56
 */
public class TimerManager<T extends ITimer>
{
	protected final LockList<T> list;

	public TimerManager()
	{
		list = new LockList<T>();
	}

	public boolean add(T timer)
	{
		return list.add(timer);
	}

	protected void clear()
	{
		list.clear();
	}

	protected List<T> getTimers()
	{
		return new ArrayList<>(list.getList());
	}

	/**
	 * 遍历触发各个定时器<br>
	 * 同步, 避免同时执行.
	 * 
	 * @param prevTimeL
	 * @param nowTimeL
	 * @param trigger
	 * @return
	 */
	public synchronized int checkTimer(final long prevTimeL, final long nowTimeL, final ITrigger<T> trigger)
	{
		// 遍历执行
		ListUtils.IAction<T> action = new ListUtils.IAction<T>()
		{
			@Override
			public boolean action(T timer, Iterator<?> iter)
			{
				// 检测时间
				int count = timer.check(prevTimeL, nowTimeL);
				if (count <= 0)
				{
					return true; // 没产生触发, 跳过
				}

				// 触发并返回
				try
				{
					trigger.handle(timer, count, prevTimeL, nowTimeL);
				}
				catch (Exception e)
				{
					Log.error("定时器触发执行错误! ", e);
				}

				// 判断定时器是否过期
				if (!timer.isAlive())
				{
					iter.remove(); // 移除
				}
				return true;
			}
		};
		return list.action(action, 0);
	}

	/** 处理接口 **/
	public interface ITrigger<T>
	{
		/** 定时器触发, @param count 触发次数(如果是很频繁的时间可能同时触发多次) **/
		public void handle(T timer, int count, long prevTime, long nowTime);
	}
}
