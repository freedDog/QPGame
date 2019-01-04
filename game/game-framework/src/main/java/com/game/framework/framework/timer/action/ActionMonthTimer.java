package com.game.framework.framework.timer.action;

import com.game.framework.component.action.ActionQueue;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.MonthTimer;

/**
 * 每月定时器<br>
 * 增加执行队列, 防止阻塞定时器线程.
 * **/
public abstract class ActionMonthTimer extends MonthTimer
{
	protected final ActionQueue queue; // 执行队列

	/**
	 * @param name
	 * @param day
	 *            本周几, 周日为1, 周一为2
	 * @param hour
	 * @param min
	 */
	public ActionMonthTimer(String name, int day, int hour, int min)
	{
		super(name, day, hour, min);
		queue = new ActionQueue(ServiceMgr.getExecutor());
	}

	@Override
	public void run(final long prevTime, final long nowTime, final int count)
	{
		queue.enqueue(new Runnable()
		{
			@Override
			public void run()
			{
				execute(prevTime, nowTime, runCount++);
			}
		});
	}
}