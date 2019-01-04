package com.game.framework.component.task;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import com.game.framework.component.log.Log;
import com.game.framework.component.service.record.TimeRecordManager;



/**
 * 任务队列<br>
 * 延迟建议使用定时器后再添加入队列.
 * TaskQueue.java
 * @author JiangBangMing
 * 2019年1月3日下午1:51:55
 */
public class TaskQueue<E extends Executor, R extends Runnable> {
	protected final LinkedList<R> queue;
	protected final E executor;

	public TaskQueue(E executor) {
		this.executor = executor;
		queue = new LinkedList<R>();
	}

	/** 执行任务 **/
	protected boolean execute(final R runnable) {

		// 封装成壳
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					// 记录运行开始时间
					long start = System.currentTimeMillis();
					// 运行程序
					runnable.run();
					// 计算运行时间
					long end = System.currentTimeMillis();
					long useTime = end - start;
					onFinish(runnable, useTime);

					// 添加运行记录
					TimeRecordManager.getInstance().addTime(runnable.getClass(), (int) useTime, runnable.toString());
				} catch (Throwable e) {
					Log.error("Action execute catch a throwable ! " + this, e);
					// 添加错误记录
					TimeRecordManager.getInstance().addWarn(runnable.getClass());
				}

				// 完成任务, 执行移除
				try {
					TaskQueue.this.dequeue(runnable);
				} catch (Throwable e) {
					Log.error("dequeue error! " + queue + " action:" + runnable, e);
				}
			}
		};

		try {
			// 提交到线程池执行
			executor.execute(r);
		} catch (Exception e) {
			if (NullPointerException.class == e.getClass()) {
				Log.warn("线程池关闭了, 提交任务失败! action:" + runnable);
				return false; // 线程池关闭了, 提交失败
			}
			Log.error("线程池提交任务错误!", e);
			return false; // 线程池关闭了, 提交失败
		}
		return true;
	}

	/** 任务完成 **/
	protected void onFinish(R runnable, long useTime) {
		if (useTime > 500) {
			Log.warn("Execute Action took long time(" + useTime + "ms), action : " + runnable);
		}
	}

	/** 任务提交 **/
	protected void onEnqueue(R runnable) {

	}

	/** 加入队列 **/
	public void enqueue(R runnable) {
		int queueSize = 0;
		// 加入消息
		synchronized (queue) {
			queue.add(runnable);
			queueSize = queue.size();
		}
		// 如果消息数量为1个, 说明之前队列已经为空.
		if (queueSize == 1) {
			execute(runnable); // 立马执行
		}
		// 提交事件
		onEnqueue(runnable);
	}

	/** 执行完毕, 移除队列 **/
	protected void dequeue(Runnable r) {
		R nextTask = null;
		synchronized (queue) {
			// 检测队列头是否是当前任务
			R frist = queue.peek();
			if (frist != r) {
				Log.error("队列移除错误, 当前队列头不是这个任务!(" + r.getClass() + ")" + r);
				return;
			}

			// 移除队列头
			R temp = queue.poll(); // remove如果没有抛出异常, poll是返回null
			if (temp == null) {
				Log.error("队列移除错误, 移除失败!(" + r.getClass() + ")" + r);
				return;
			}

			// 获取下一个任务
			int rsize = queue.size();
			if (rsize > 0) {
				nextTask = queue.peek();
			}
		}
		// 继续执行下一个任务
		if (nextTask != null) {
			execute(nextTask); // 立马执行
		}
	}

	/** 获取当前队列的数量 **/
	public int size() {
		int queueSize = 0;
		synchronized (queue) {
			queueSize = queue.size();
		}
		return queueSize;
	}

	/** 获取执行器 **/
	public E getExecutor() {
		return executor;
	}
}
