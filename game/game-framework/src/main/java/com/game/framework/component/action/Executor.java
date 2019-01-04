package com.game.framework.component.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.game.framework.component.log.Log;


/**
 * 线程执行器(线程池) Executor.java
 * 
 * @author JiangBangMing 2019年1月3日下午12:58:32
 */
public class Executor implements java.util.concurrent.Executor {
	private ThreadPoolExecutor executor;
	private String name;

	/**
	 * 由于LinkedBlockingQueue未限制长度,可以认为线程池的maxPoolSize无效
	 * 
	 * @param name
	 * @param corePoolSize
	 *            核心线程数(保持运行数)
	 * @param maximumPoolSize
	 *            最大线程数
	 */
	public Executor(String name, int corePoolSize, int maximumPoolSize) {
		this.name = name;
		// 检测时间
		TimeUnit unit = TimeUnit.MINUTES; // 1s维护线程
		int keepAliveTime = 1;
		// 创建队列接口
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();// 不能执行的任务删除
		DefaultThreadFactory threadFactory = new DefaultThreadFactory(name);
		// 创建线程池
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
				handler);
	}

	public int getMaxPoolSize() {
		return executor.getMaximumPoolSize();
	}

	public void execute(Runnable task) {
		executor.execute(task);
	}

	public String getName() {
		return name;
	}

	public void stop() {
		try {
			// Log.info("正在关闭线程池" + name + "...");
			executor.shutdown();
			executor.awaitTermination(2, TimeUnit.MINUTES);
			// Log.info("线程池" + name + "已关闭!");
		} catch (Exception e) {
			Log.error("关闭线程池异常", e);
		}
	}

}
