package com.game.framework.framework.timer.action;

import com.game.framework.component.action.ActionQueue;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.MinuteTimer;

/**
 * 每分钟定时器<br>
 * 增加执行队列, 防止阻塞定时器线程.
 * ActionMinuteTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:21:49
 */
public abstract class ActionMinuteTimer extends MinuteTimer
{
	protected final ActionQueue queue; // 执行队列

	public ActionMinuteTimer(String name)
	{
		super(name);
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