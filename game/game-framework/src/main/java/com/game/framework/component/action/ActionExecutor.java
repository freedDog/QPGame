package com.game.framework.component.action;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行器<br>
 * 在线程池的基础上增加延迟处理器. ActionExecutor.java
 * 
 * @author JiangBangMing 2019年1月3日下午12:51:27
 */
public class ActionExecutor extends Executor {
	private ScheduledExecutorService scheduler; // 延迟执行处理器

	/**
	 * 创建任务执行器
	 * 
	 * @param name
	 *            名称
	 * @param corePoolSize
	 *            核心线程数(保持运行数)
	 * 
	 * @param maximumPoolSize
	 *            最大线程数
	 */
	public ActionExecutor(String name, int corePoolSize, int maximumPoolSize) {
		super(name, corePoolSize, maximumPoolSize);
		scheduler = Executors.newScheduledThreadPool(1, new DefaultThreadFactory(name + "-Delay")); // 创建一个线程处理延迟
	}

	/** 添加延迟任务 **/
	protected void addDelayAction(long delay, Runnable runnable) {

		// 判断延迟期是否关闭
		if (scheduler.isShutdown()) {
			return;
		}
		scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);

	}

	public void stop() {
		scheduler.shutdownNow();
		super.stop();
	}

}
