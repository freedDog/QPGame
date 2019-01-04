package com.game.framework.framework.timer;

import com.game.framework.utils.TimeUtils;

/**
 * 每分钟执行一次的定时器
 * MinuteTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:17:02
 */
public abstract class MinuteTimer extends Timer
{
	public MinuteTimer(String name)
	{
		super(name, TimeUtils.getNextMinTime(), (int) TimeUtils.oneMinuteTimeL, 0);
	}

}
