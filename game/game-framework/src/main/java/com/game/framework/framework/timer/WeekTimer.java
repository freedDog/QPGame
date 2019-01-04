package com.game.framework.framework.timer;

import com.game.framework.utils.TimeUtils;

/**
 *  每周循环定时器
 * WeekTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:19:06
 */
public abstract class WeekTimer extends Timer
{
	/**
	 * @param name
	 * @param day
	 *            本周几, 周日为1, 周一为2
	 * @param hour
	 * @param min
	 */
	public WeekTimer(String name, int day, int hour, int min)
	{
		super(name, getStartTime(day, hour, min), (int) TimeUtils.oneWeekTimeL, 0);
	}

	/** 获取起始时间(上周触发的时间点) **/
	protected static long getStartTime(int day, int hour, int min)
	{
		long dayTime = System.currentTimeMillis() - TimeUtils.oneWeekTimeL;
		long weekTime = TimeUtils.getWeekTime(dayTime, day, hour, min, 0);
		return weekTime;
	}
}
