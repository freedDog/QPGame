package com.game.framework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具
 * ThreadUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午2:35:08
 */
public class ThreadUtils {

	public static String threadName() {
		return Thread.currentThread().getName();
	}

	/** 休眠 **/
	public static void sleep(long millis) {
		if (millis <= 0) {
			return;
		}
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean run(String name, Runnable runnable) {
		return run(runnable, 0, name);
	}

	public static boolean run(Runnable runnable) {
		return run(runnable, 0);
	}

	public static boolean run(Runnable runnable, int waitTime) {
		return run(runnable, waitTime, null);
	}

	public static boolean run(Runnable runnable, int waitTime, String name) {
		if (waitTime > 0) {
			sleep(waitTime);
		}
		Thread t = new Thread(runnable);
		if (!StringUtils.isEmpty(name)) {
			t.setName(name);
		}
		t.start();
		return true;
	}

	/**
	 * 批量线程运行, 等待结束
	 * 
	 * 参考 AnalyzeUtils
	 * **/
	public static void runBatch(int batch, final Runnable runnable) {
		// 多线程提交
		final AtomicInteger counter = new AtomicInteger();
		for (int i = 0; i < batch; i++) {
			counter.incrementAndGet();
			// 提交任务
			run(new Runnable() {
				@Override
				public void run() {
					try {
						runnable.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 计数器减少
					counter.decrementAndGet();
				}
			});
		}

		// 等待任务结束
		while (counter.get() > 0) {
			Thread.yield();
		}
	}

	/**
	 * 阻塞等待
	 * 
	 * @param time
	 */
	public static void waitTime(int time) {
		long startTimeL = System.currentTimeMillis();
		while (true) {
			long nowTimeL = System.currentTimeMillis();
			long dt = nowTimeL - startTimeL;
			if (dt >= time) {
				break;
			}
		}

	}

	/** 异步回馈数据 **/
	public static class AsynResult {
		protected final Map<Object, Object> map = new HashMap<Object, Object>();

		/** 设置参数0(默认) **/
		public boolean set(boolean enable) {
			set(0, enable);
			return true;
		}

		/** 异步获取0参数, 如果超时也是false(毫秒). **/
		public boolean asynGet(int maxTime) {
			Boolean result = asynGet(0, Boolean.class, maxTime);
			return (result != null) ? result : false;
		}

		/** 设置参数 **/
		public synchronized Object set(Object key, Object value) {
			return map.put(key, value);
		}

		/** 获取参数 **/
		public synchronized Object get(Object key) {
			return map.get(key);
		}

		/** 异步等待获取变量(毫秒) **/
		@SuppressWarnings("unchecked")
		public <T> T asynGet(Object key, Class<T> clazz, int maxTime) {
			long startTime = System.currentTimeMillis();
			while (true) {
				Object obj = get(key);
				if (obj != null) {
					return (T) obj;
				}

				// 检测是否超过最大上限时间
				long nowTime = System.currentTimeMillis();
				if (nowTime - startTime >= maxTime) {
					break;
				}

				// 线程休眠等待
				try {
					Thread.yield(); // 切换线程进入休眠.
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

}
