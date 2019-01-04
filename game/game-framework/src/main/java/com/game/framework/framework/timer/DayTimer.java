package com.game.framework.framework.timer;

import com.game.framework.utils.TimeUtils;

/**
 * 每日循环定时器
 * DayTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:15:00
 */
public abstract class DayTimer extends Timer {
	public DayTimer(String name, int hour, int min) {
		super(name, getStartTime(hour, min), (int) TimeUtils.oneDayTimeL, 0);
	}

	/** 获取起始时间(昨天触发的时间点) **/
	protected static long getStartTime(int hour, int min) {
		long dayTime = System.currentTimeMillis() - TimeUtils.oneDayTimeL;
		return TimeUtils.getDayTime(dayTime, hour, min, 0);
	}
}
