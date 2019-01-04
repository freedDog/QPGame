package com.game.framework.component.trigger;

/**
 * 事件<br>
 * ITriggerEvent.java
 * @author JiangBangMing
 * 2019年1月3日下午1:54:23
 */
public interface ITriggerEvent<T>
{
	/** 检测是否存活 **/
	boolean isAlive(long checkTime);

	/** 检测执行事件(这么检测, 怎么执行自己定) **/
	void execute(T obj, long prevTime, long nowTime);
}
