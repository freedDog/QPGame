package com.game.framework.framework.mgr;

import java.util.ArrayList;
import java.util.List;

import com.game.framework.component.action.Action;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.log.Log;



/**
 * 服务管理<br>
 * 统一管理服务器线程
 * ServiceMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:00:59
 */
public class ServiceMgr {
	protected static ActionExecutor executor; // 线程池
	// 队列
	protected static ActionQueue systemQueue; // 系统队列
	protected static List<ActionQueue> queues; // 线程队列

	static {
		init();
	}

	/** 初始化 **/
	public static boolean init() {
		// 判断是否初始化过了
		if (executor != null) {
			return true;
		}
		// 创建线程
		int thread = Runtime.getRuntime().availableProcessors();
		thread = Math.max(thread, 4);
		return init(thread * 2);
	}

	/** 初始化, 生成线程队列和系统队列 **/
	protected synchronized static boolean init(int thread) {
		destroy();
		// 创建
		executor = new ActionExecutor("ActionExecutor", thread, thread);
		// 队列管理
		queues = new ArrayList<>();
		int queueSize = thread * 2;
		for (int i = 0; i < queueSize; i++) {
			queues.add(new ActionQueue(executor));
		}
		systemQueue = new ActionQueue(executor);
		return true;
	}

	/** 关闭服务 **/
	public static synchronized void destroy() {
		try {
			if (executor != null) {
				executor.stop();
				executor = null;
			}
		} catch (Exception e) {
			Log.error("关闭线程池异常", e);
		}
	}

	/** 获取队列数量 **/
	public static int getQueueSize() {
		return queues.size();
	}

	/** 根据Id获取执行队列 **/
	public static ActionQueue get(long index) {
		int r = (int) (Math.abs(index) % queues.size());
		return queues.get(r);
	}

	/** 执行任务 **/
	public static void enqueue(long index, Runnable r) {
		ActionQueue queue = get(index);
		queue.enqueue(r);
	}

	/** 用系统队列执行任务 **/
	public static void enqueue(Runnable r) {
		systemQueue.enqueue(r);
	}

	/** 直接放入线程池执行任务(不支持Action任务) **/
	public static void execute(Runnable runnable) {
		// 检测延迟任务Action
		if (Action.class.isInstance(runnable)) {
			Action action = (Action) runnable;
			if (action.getDelay() > 0) {
				Log.warn("execute不能处理延迟Action, 只能直接执行!", true);
			}
		}
		// 放入线程池执行
		executor.execute(runnable);
	}

	public static ActionExecutor getExecutor() {
		return executor;
	}
}
