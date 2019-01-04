package com.game.framework.framework.timer;

import com.game.framework.utils.TimeUtils;

/**
 * 每小时执行一次的定时器
 * HourTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:16:38
 */
public abstract class HourTimer extends Timer
{
	public HourTimer(String name)
	{
		super(name, TimeUtils.getNextHourTime(), (int) TimeUtils.oneHourTimeL, 0);
	}

}
