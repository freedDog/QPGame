package com.game.framework.framework.timer.action;

import com.game.framework.component.action.ActionQueue;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.timer.Timer;

/**
 * 间隔定时器<br>
 * 增加执行队列, 防止阻塞定时器线程.
 * ActionTimer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:23:09
 */
public abstract class ActionTimer extends Timer {
	protected final ActionQueue queue; // 执行队列

	/**
	 * @param name
	 *            定时器名称
	 * @param intervalTime
	 *            间隔时间(ms)
	 **/
	public ActionTimer(String name, int intervalTime) {
		super(name, intervalTime);
		queue = new ActionQueue(ServiceMgr.getExecutor());
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
	 **/
	public ActionTimer(String name, int delay, int intervalTime, int resetCount) {
		super(name, delay, intervalTime, resetCount);
		queue = new ActionQueue(ServiceMgr.getExecutor());
	}

	/** 执行函数 **/
	@Override
	public void run(final long prevTime, final long nowTime, final int count) {
		queue.enqueue(new Runnable() {
			@Override
			public void run() {
				execute(prevTime, nowTime, runCount++);
			}

			@Override
			public String toString() {
				return ActionTimer.this.toString() + "-Runnable";
			}
		});
	}

	@Override
	public String toString() {
		return "ActionTimer [" + this.getClass() + "]";
	}
}