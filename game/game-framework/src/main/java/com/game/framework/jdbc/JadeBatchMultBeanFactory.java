package com.game.framework.jdbc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.framework.utils.ThreadUtils;



/**
 * JDBC 批量更新工厂(多线程模式提交下, 优先级并不完全符合)<br>
 * 参数:<br>
 * batchUpdataInterval: 批量更新间隔时间(毫秒), 默认为1000. <br>
 * batchHandler: 错误接口 {@link BatchHandler}<br>
 * 
 * @see JadeBatchBeanFactory
 * @see JadeBatchBeanFactory.BatchHandler
 * JadeBatchMultBeanFactory.java
 * @author JiangBangMing
 * 2019年1月3日下午6:19:39
 */
public class JadeBatchMultBeanFactory extends JadeBatchBeanFactory
{
	protected final ExecutorService executor = Executors.newFixedThreadPool(5);
	protected final AtomicInteger counter = new AtomicInteger();

	// 更新处理(多线程提交)
	@Override
	protected synchronized boolean updateToBatch()
	{
		int actionCount = 0;
		counter.set(0);

		// 遍历更新
		Iterator<Map.Entry<JdbcBatchUpdate<?>, BatchProcess>> iter = batchMap.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<JdbcBatchUpdate<?>, BatchProcess> entry = iter.next();
			final JdbcBatchUpdate<?> executer = entry.getKey();
			BatchProcess process = entry.getValue();
			if (executer == null || process == null)
			{
				continue;
			}

			// 判断该处理是否还有数据要提交
			BatchProcess processT = null;
			synchronized (process)
			{
				if (process.count() <= 0)
				{
					continue; // 数量小于0
				}
				// 拆分批量处理数据
				int batchCount = Math.max(1, executer.getMaxBatch()); // 获取批量处理单次数量
				processT = process.extract(batchCount); // 拆分数据
			}

			// 判断拆分是否成功
			if (processT == null || processT.count() <= 0)
			{
				continue;
			}

			// 提交处理
			final BatchProcess process0 = processT;
			Runnable runnable = new Runnable()
			{
				@SuppressWarnings("unchecked")
				@Override
				public void run()
				{
					try
					{
						// long startTimeL = System.currentTimeMillis();
						// System.out.println("method:" + executer.getMethod());
						// System.out.println("process:" + process1);
						List<?> retList = executer.execute(process0);
						// long endTimeL = System.currentTimeMillis();
						// System.out.println("use time:" + (endTimeL -
						// startTimeL));

						// 直接处理, 不返回参数
						// executer.execute(process1);

						// 更新处理
						BatchHandler handler = executer.getHandler();
						handler = (handler != null) ? handler : batchHandler;
						if (handler != null)
						{
							handler.batchUpdate(executer.getMethod(), executer.getSql(), process0.batchArgs, process0.batchBaseArgs, (List<Object>) retList);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						// 错误处理
						BatchHandler handler = executer.getHandler();
						handler = (handler != null) ? handler : batchHandler;
						if (handler != null)
						{
							handler.batchError(e, executer.getMethod(), executer.getSql(), process0.batchArgs, process0.batchBaseArgs);
						}
					}
					counter.incrementAndGet(); // 完成计数+1
				}
			};
			executor.execute(runnable);
			actionCount++;
		}

		// 判断有没有可执行的数据
		if (actionCount <= 0)
		{
			return false; // 没有任务
		}

		// 等待执行完毕
		while (true)
		{
			ThreadUtils.sleep(100);
			if (counter.get() >= actionCount)
			{
				break;
			}
		}

		return true;

	}

	@Override
	protected synchronized boolean stop0()
	{
		boolean reuslt = super.stop0();
		if (reuslt)
		{
			// 关闭任务
			executor.shutdown();
			try
			{
				executor.awaitTermination(1, TimeUnit.MINUTES);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return reuslt;

	}

}
