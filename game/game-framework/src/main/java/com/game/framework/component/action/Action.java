package com.game.framework.component.action;

import com.game.framework.component.log.Log;
import com.game.framework.utils.RuntimeUtils;

/**
 * Action线程处理任务 Action.java
 * 
 * @author JiangBangMing 2019年1月3日下午12:50:56
 */
public abstract class Action implements Runnable {
	private ActionQueue queue; // 任务队列
	private long delay; // 延迟执行时间(ms毫秒)
	protected String createStackTraceString; // 创建位置的堆栈数据(用于查找所在位置)

	public Action() {
		this(0);
	}

	/** 创建延迟任务 **/
	public Action(long delay) {
		this.delay = delay;
		createStackTraceString = RuntimeUtils.getStackTraceString(4, 1);
	}

	@Override
	public final void run() {
		try {
			execute();
		} catch (Throwable e) {
			Log.error("Action execute catch a throwable ! " + this, e);
		}
	}

	/** 执行任务 **/
	public abstract void execute() throws Exception;

	public ActionQueue getQueue() {
		return queue;
	}

	protected void setQueue(ActionQueue queue) {
		this.queue = queue;
	}

	public long getDelay() {
		return delay;
	}

	/** 运行警告时间 **/
	public int getWarningTime() {
		return 500;
	}

	@Override
	public String toString() {
		// return "class : " + this.getClass();
		return "Action[" + this.getClass().getName() + " by " + createStackTraceString + " ]";
	}
}
