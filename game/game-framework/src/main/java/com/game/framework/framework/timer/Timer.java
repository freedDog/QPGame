package com.game.framework.framework.timer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *间隔定时器<br>
 * 阻塞定时器线程.
 * Timer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:15:31
 */
public abstract class Timer extends com.game.framework.component.timer.Timer
{
	protected static AtomicInteger id = new AtomicInteger();
	protected int runCount = 0; // 执行次数

	/**
	 * @param name
	 *            定时器名称
	 * @param intervalTime
	 *            间隔时间(ms)
	 **/
	public Timer(String name, int intervalTime)
	{
		this(name, 0, intervalTime);
	}

	/**
	 * @param name
	 *            定时器名称
	 * @param delayTime
	 *            第一次执行延迟时间
	 * @param intervalTime
	 *            间隔时间(ms)
	 **/
	public Timer(String name, int delayTime, int intervalTime)
	{
		this(name, delayTime, intervalTime, 0);
	}

	/**
	 * @param name
	 *            定时器名称
	 * @param delayTime
	 *            第一次执行延迟时间
	 * @param intervalTime
	 *            间隔时间(ms)
	 * @param resetCount
	 *            重复次数(0为永久)
	 */
	public Timer(String name, int delayTime, int intervalTime, int resetCount)
	{
		this(name, System.currentTimeMillis() + delayTime, intervalTime, resetCount);
	}

	/**
	 * @param name
	 *            定时器名称
	 * @param startTime
	 *            第一次起始时间(ms)
	 * @param intervalTime
	 *            间隔时间(ms)
	 * @param resetCount
	 *            重复次数(0为永久)
	 */
	public Timer(String name, long startTime, int intervalTime, int resetCount)
	{
		super(id.incrementAndGet(), name, startTime, intervalTime, resetCount);
	}

	/** 执行函数 **/
	public void run(long prevTime, long nowTime, int count)
	{
		execute(prevTime, nowTime, runCount++);
	}

	/**
	 * 执行函数
	 * 
	 * @param runCount
	 *            执行次数
	 **/
	protected abstract void execute(long prevTime, long nowTime, int runCount);
}