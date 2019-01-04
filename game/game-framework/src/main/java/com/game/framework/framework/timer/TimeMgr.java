package com.game.framework.framework.timer;

import com.game.framework.component.timer.TimerManager;
import com.game.framework.utils.ThreadUtils;
import com.game.framework.component.timer.TimerManager.ITrigger;
import com.game.framework.framework.timer.action.ActionTimer;

/**
 *  定时器
 * TimeMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午4:18:14
 */
public class TimeMgr
{

	private static volatile boolean running = false;
	protected static TimerManager<Timer> tmgr;

	public static boolean init()
	{
		tmgr = new TimerManager<>();

		// 定时器触发执行器
		final ITrigger<Timer> trigger = new ITrigger<Timer>()
		{
			@Override
			public void handle(Timer timer, int count, long prevTimeL, long nowTimeL)
			{
				timer.run(prevTimeL, nowTimeL, count);
			}
		};

		// 创建线程处理
		running = true;
		ThreadUtils.run("TimeMgr", new Runnable()
		{
			@Override
			public void run()
			{
				long preTime = System.currentTimeMillis();
				while (running)
				{
					ThreadUtils.sleep(100);
					// 定时器更新
					long preTime0 = preTime;
					long nowTime = System.currentTimeMillis();
					preTime = nowTime;

					// 定时器更新
					tmgr.checkTimer(preTime0, nowTime, trigger);
				}
			}
		});

		return true;
	}

	public static void stop()
	{
		running = false;
	}

	/** 注册定时器 **/
	public static boolean register(Timer timer)
	{
		return tmgr.add(timer);
	}

	/** 延时执行 **/
	public static void delayExecute(int delay, final Runnable task)
	{
		tmgr.add(new ActionTimer("delay-call", delay, 0, 1)
		{
			@Override
			protected void execute(long prevTime, long nowTime, int runCount)
			{
				task.run();
			}
		});
	}
}
