package com.game.framework.component.timer;

/**
 * 定时器
 * ITimer.java
 * @author JiangBangMing
 * 2019年1月3日下午1:53:03
 */
public interface ITimer
{
	/** 检测定时器在时间内触发多少次, 大于0为触发. **/
	int check(long prevTimeL, long nowTimeL);

	/** 是否有效 **/
	boolean isAlive();
}
