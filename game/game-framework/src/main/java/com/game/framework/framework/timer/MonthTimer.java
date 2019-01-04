package com.game.framework.framework.timer;

import com.game.framework.utils.TimeUtils;

/**
 * 每月循环定时器<br>
 * MonthTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:17:31
 */
public abstract class MonthTimer extends Timer
{
	protected int day;
	protected int hour;
	protected int min;

	public MonthTimer(String name, int day, int hour, int min)
	{
		super(name, 0, 0, 0);
		this.day = day;
		this.hour = hour;
		this.min = min;
	}

	@Override
	public int check(long prevTimeL, long nowTimeL)
	{
		// 这里只能从检测下手, 比起每日每周, 每月的间隔时间不是不固定的.
		long monthTime = TimeUtils.getMonthTime(nowTimeL, day, hour, min, 0);
		if (prevTimeL < monthTime && monthTime <= nowTimeL)
		{
			return 1;
		}
		return 0;
	}

}
