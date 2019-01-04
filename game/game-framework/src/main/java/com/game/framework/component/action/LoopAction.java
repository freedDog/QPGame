package com.game.framework.component.action;

import com.game.framework.component.log.Log;
import com.game.framework.utils.RuntimeUtils;

/**
 * 循环action<br>
 * LoopAction.java
 * @author JiangBangMing
 * 2019年1月3日下午12:56:09
 */
public abstract class LoopAction extends Action {
	protected long updateTime; // 上次更新时间
	protected boolean loop; // 循环状态
	protected int index; // 执行次数
	protected long interval; // 循环间隔时间(毫秒)

	/** 创建循环Action, 间隔时间最少为1. **/
	public LoopAction(long delay) {
		this(delay, delay);
		createStackTraceString = RuntimeUtils.getStackTraceString(4, 1);
	}

	/** 创建循环Action, 间隔时间最少为1. **/
	public LoopAction(long delay, long interval) {
		super(Math.max(delay, 1));
		loop = true;
		updateTime = System.currentTimeMillis();
		this.interval = interval;
		index = 0;
	}

	@Override
	public final void execute() throws Exception {
		try {
			// 计算时间
			long nowTime = System.currentTimeMillis();
			long prevTime = updateTime;
			long dt = nowTime - prevTime;
			updateTime = nowTime;
			if (dt > 0) {
				index++;
				// 防止调时间过多执行
				update(nowTime, prevTime, dt, index);
			}
		} catch (Throwable e) {
			Log.error("Action Loop execute catch a throwable !" + this, e);
		}
		// 重新加入队列
		if (isLoop()) {
			ActionQueue queue = getQueue();
			if (queue == null) {
				Log.error("action loop error, no find queue! " + this);
				return;
			}
			queue.enqueue(this);
		}
	}

	@Override
	public long getDelay() {
		return (index > 0) ? interval : super.getDelay();
	}

	/** 更新函数 **/
	protected abstract void update(long now, long prev, long dt, int index);

	/** 是否循环中 **/
	public boolean isLoop() {
		return loop;
	}

	/** 修改循环状态, false代表停止循环了. **/
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

}
