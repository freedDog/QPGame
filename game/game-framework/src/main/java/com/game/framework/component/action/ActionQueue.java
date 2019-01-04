package com.game.framework.component.action;

import java.util.concurrent.atomic.AtomicInteger;

import com.game.framework.component.log.Log;
import com.game.framework.component.task.TaskQueue;


/**
 * 任务队列<br>
 * 支持延迟执行功能 ActionQueue.java
 * 
 * @author JiangBangMing 2019年1月3日下午12:51:59
 */
public class ActionQueue extends TaskQueue<ActionExecutor, Runnable> {
	protected final AtomicInteger delayCounter; // 延迟任务数

	public ActionQueue(ActionExecutor executor) {
		super(executor);
		delayCounter = new AtomicInteger();
	}

	/** 加入队列(不检测延迟的处理) **/
	protected void enqueueToNext(Runnable runnable) {
		super.enqueue(runnable);
	}

	@Override
	public int size() {
		return super.size() + delayCounter.get();
	}

	@Override
	protected void onEnqueue(Runnable runnable) {
		// 消息过多提示
		int size = size();
		if (size > this.getWarningSize()) {
			// 提醒消息累计过多
			Log.warn("queue is too much ! size=" + size + " runnable=" + runnable);
		}
	}

	@Override
	protected void onFinish(Runnable runnable, long useTime) {
		// 检测任务类型
		int warningTime = 500;
		if (Action.class.isInstance(runnable)) {
			Action action = (Action) runnable;
			warningTime = action.getWarningTime();
		}
		// 检测警告时间
		if (useTime <= warningTime) {
			return;
		}
		// 输出警告
		Log.warn("Execute Action took long time(" + useTime + "ms), action : " + runnable);
	}

	/** action分流 **/
	public void enqueue(final Action action) {
		action.setQueue(this);
		long delay = action.getDelay();
		if (delay > 0) {
			// 延迟加入队列
			delayCounter.incrementAndGet();
			getExecutor().addDelayAction(delay, new Runnable() {
				@Override
				public void run() {
					ActionQueue.this.enqueueToNext(action);
					delayCounter.decrementAndGet(); // 延迟任务数-1
				}
			});
		} else {
			// 直接加入队列
			super.enqueue((Runnable) action);
		}
	}

	@Override
	public void enqueue(Runnable runnable) {
		// 检测action分流
		if (Action.class.isInstance(runnable)) {
			enqueue((Action) runnable);
			return;
		}

		// 正常加入队列
		super.enqueue(runnable);
	}

	/** 警告任务数(当任务超过这个数量提示警告任务过多) **/
	public int getWarningSize() {
		return 1000;
	}

}
