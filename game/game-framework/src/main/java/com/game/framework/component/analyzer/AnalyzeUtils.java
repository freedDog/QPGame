package com.game.framework.component.analyzer;

import java.util.concurrent.atomic.AtomicLong;

import com.game.framework.component.log.Log;
import com.game.framework.utils.ThreadUtils;
import com.game.framework.utils.struct.result.Result;



/**
 * 分析器<br>
 * 对一段代码进行运行分析, 生成分析表.
 * AnalyzeUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午12:59:28
 */
public class AnalyzeUtils {

	public static Result run(String name, final IUint uint, int total, final int oneCount, int thread) {
		if (total <= 0) {
			return Result.error("执行总数为0!");
		}
		thread = Math.max(thread, 1); // 最少1个线程.
		int batchCount = (int) Math.ceil(total / (double) thread); // 每个线程多少个

		// 运行计数器
		final AtomicLong running = new AtomicLong();
		final AtomicLong totalTime = new AtomicLong(); // 总用时
		final AtomicLong runCounter = new AtomicLong(); // 完成数量

		// 遍历创建线程
		for (int i = 0; i < thread; i++) {
			final int start = i * batchCount;
			final int size = Math.min(total - start, batchCount); // 计算这轮的数量

			// 提交线程
			ThreadUtils.run(new Runnable() {
				@Override
				public void run() {
					try {
						running.incrementAndGet();

						// 遍历循环
						for (int i = 0; i < size; i++) {
							try {
								int index = start + i;

								// 执行
								long startTime = System.currentTimeMillis();
								uint.run(index, oneCount);
								long endTime = System.currentTimeMillis();

								// 计算时间
								long dt = endTime - startTime;

								// 记录时间
								totalTime.addAndGet(dt);
								runCounter.incrementAndGet();
							} catch (Exception e) {
								Log.error("执行测试用例错误!", e);
							}
						}

					} finally {
						running.decrementAndGet();
					}
				}

			});

		}

		// 等待线程启动
		ThreadUtils.sleep(100);

		// 等待处理结束.
		do {
			Thread.yield();
		} while (running.get() > 0);

		// 完成输出结果
		long runCount0 = Math.max(runCounter.get(), 1);
		long totalCount = runCount0 * oneCount;
		long totalTime0 = totalTime.get();
		double avg = totalTime0 / ((double) totalCount);
		String info = name + "结果: avg=" + avg + " totaltime=" + totalTime0 + " totalcount=" + totalCount;

		return Result.create(Result.SUCCESS, info);
	}

	/** 测试用例接口 **/
	public interface IUint {
		/** 测试 **/
		void run(int index, int count);
	}

	/** 测试用例接口 **/
	public abstract class Uint implements IUint {
		public abstract void run(int index);
	}

	public static void main(String[] args0) throws Exception {

		Result result = run("test", new IUint() {
			@Override
			public void run(int index, int count) {
				// Log.debug("");
			}
		}, 99, 10, 10);
		Log.debug(result.getMsg());
	}
}
