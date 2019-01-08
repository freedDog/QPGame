package com.game.core.player.event;

/**
 *  事件监听接口<br>
 * IEventListener.java
 * @author JiangBangMing
 * 2019年1月8日下午1:14:39
 */
public interface IEventListener {
	/** 触发锁定 **/
	void onEventLock();

	/** 事件触发 **/
	void onEvent(Event event);

	/** 触发解锁 **/
	void onEventUnlock();
}
