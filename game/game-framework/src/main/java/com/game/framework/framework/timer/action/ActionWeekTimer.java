package com.game.framework.framework.timer.action;

import com.game.framework.component.action.ActionQueue;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.WeekTimer;

/**
 * 每周定时器<br>
 * 增加执行队列, 防止阻塞定时器线程.
 * ActionWeekTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:24:02
 */
public abstract class ActionWeekTimer extends WeekTimer
{
	protected final ActionQueue queue; // 执行队列

	/**
	 * @param name
	 * @param day
	 *            本周几, 周日为1, 周一为2
	 * @param hour
	 * @param min
	 */
	public ActionWeekTimer(String name, int day, int hour, int min)
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