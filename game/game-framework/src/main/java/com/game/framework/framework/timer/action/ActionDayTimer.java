package com.game.framework.framework.timer.action;

import com.game.framework.component.action.ActionQueue;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.DayTimer;

/**
 * 每日定时器<br>
 * 增加执行队列, 防止阻塞定时器线程.
 * ActionDayTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:19:48
 */
public abstract class ActionDayTimer extends DayTimer
{
	protected final ActionQueue queue; // 执行队列

	public ActionDayTimer(String name, int hour, int min)
	{
		super(name, hour, min);
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