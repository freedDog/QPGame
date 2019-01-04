package com.game.framework.component.service.record;

/**
 * <ul>
 * 时间测量器
 * <li>通过start和end计算出所使用的时间</li>
 * <li>通常用于记录某个操作花费的时间</li>
 * </ul>
 * TimeMeter.java
 * @author JiangBangMing
 * 2019年1月3日下午1:49:43
 */
public class TimeMeter {

	private long startTimeL = 0L;
	private long endTimeL = 0L;

	public TimeMeter() {
		super();
	}

	public void start() {
		startTimeL = System.currentTimeMillis();
	}

	public int end() {
		endTimeL = System.currentTimeMillis();
		int useTime = (int) (endTimeL - startTimeL);
		return useTime;
	}

}
