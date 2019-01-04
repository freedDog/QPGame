package com.game.framework.jdbc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.game.framework.jdbc.annotation.BatchUpdata;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.SQL;
import com.game.framework.jdbc.executer.JdbcExecuter;
import com.game.framework.jdbc.executer.JdbcFUpdate;
import com.game.framework.jdbc.executer.JdbcUpdate;
import com.game.framework.utils.ThreadUtils;
import com.game.framework.utils.TimeUtils;


/**
 * JDBC 批量更新工厂<br>
 * 参数:<br>
 * batchUpdataInterval: 批量更新间隔时间(毫秒), 默认为1000. <br>
 * batchHandler: 批量处理接口 {@link BatchHandler}<br>
 * 
 * @see JadeBatchBeanFactory.BatchHandler
 */
// @Component
public class JadeBatchBeanFactory extends JadeBeanFactory
{
	protected int batchUpdataInterval = 1000; // 更新间隔时间
	private volatile boolean batchRunning = false; // 用于停止线程更新
	protected BatchHandler batchHandler; // 批量更新接口
	protected final ReadWriteLock batchLock = new ReentrantReadWriteLock(); // 批量队列锁
	protected boolean logger = true;

	/** 批处理列表(初始化时插入, 后续只负责读取) **/
	protected final TreeMap<JdbcBatchUpdate<?>, BatchProcess> batchMap = new TreeMap<JdbcBatchUpdate<?>, BatchProcess>(JdbcBatchUpdate.comparator);

	/**
	 * 初始化工厂
	 * */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
	{
		super.postProcessBeanFactory(beanFactory);

		// 检测带批量处理的函数数量
		int batchCount = batchMap.size();
		if (batchCount <= 0)
		{
			return; // 不需要开线程处理
		}

		// 线程结束补处理
		batchRunning = true;
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			// 结束检测
			@Override
			public void run()
			{
				// 执行关闭
				stop0();
			}
		});
		// 创建线程处理批量处理
		Thread thread = new Thread("jdbc batch")
		{
			@Override
			public void run()
			{
				int batchIndex = 0;
				while (batchRunning)
				{
					// 执行提交
					boolean result = updateToBatch(); // 批量提交(线程安全)
					if (result && JadeBatchBeanFactory.this.isLogger())
					{
						batchIndex++;
						System.err.println(TimeUtils.toString(System.currentTimeMillis()) + " : jade batch " + batchIndex);
					}

					// 间隔休眠
					long waitTime = (!result) ? batchUpdataInterval : (long) (batchUpdataInterval * 0.5); // 如果这次提交成功,
																											// 立马进行第二次处理.
					// long waitTime = batchUpdataInterval;
					ThreadUtils.sleep(waitTime); // 间隔休眠
				}
				// stop
			}
		};
		thread.start();
	}

	/**
	 * 结束关闭
	 */
	protected synchronized boolean stop0()
	{
		// 检测是否运行中
		if (!batchRunning)
		{
			return false; // 结束了
		}
		// 执行关闭
		batchRunning = false;
		// 结束提醒
		System.err.println(TimeUtils.toString(System.currentTimeMillis()) + " : jade shutdowning ...");

		// 最后的数据提交
		while (true)
		{
			if (!updateToBatch())
			{
				break; // 没有可更新的了
			}
		}

		// 完成提醒
		System.err.println(TimeUtils.toString(System.currentTimeMillis()) + " : jade shutdown finish");

		return true;
	}

	public void stopAndAwait()
	{
		this.stop0(); // 关闭处理

	}

	@Override
	protected Object DAOInvoke(Object proxy, Method method, Object[] args) throws Exception
	{
		// 基础函数处理
		Object[] rets = baseInvoke(proxy, method, args);
		if (rets != null && ((Boolean) rets[0]) == true)
		{
			return rets[1];
		}

		// 获取函数对应的执行器
		JdbcExecuter<?> executer = map.get(method);
		if (executer == null)
		{
			throw new Exception("没找到对应执行对象:" + method);
		}

		// 根据参数生成执行过程
		JdbcExecuter.Process process = executer.process(args);
		// 判断是否批量更新
		if (JdbcBatchUpdate.class.isInstance(executer))
		{
			// 批量处理不返回结果, 后台处理.
			addToBatch(executer, (BatchProcess) process);
			return executer.createEmptyValue(); // 批量处理, 不返回结果
		}
		// 判断是否返回数组列表
		Class<?> clazz = method.getReturnType();
		if (clazz == List.class)
		{
			return executer.execute(process); // 数组返回
		}
		return executer.executeOnce(process); // 单对象返回
	}

	// 单线程写入
	@SuppressWarnings("unchecked")
	protected synchronized boolean updateToBatch()
	{
		// 处理队列
		List<JdbcBatchUpdate<?>> executers = new ArrayList<JdbcBatchUpdate<?>>();
		List<BatchProcess> processs = new ArrayList<BatchProcess>();

		batchLock.writeLock().lock();
		// 遍历获取出提交队列
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
			// 检测数量
			BatchProcess process0 = null;
			int count = process.count();
			if (count <= 0)
			{
				continue;
			}
			// 拆分数量
			int batchCount = Math.max(1, executer.getMaxBatch());
			process0 = process.extract(batchCount);
			// 不能拆分
			int processBatchCount = (process0 != null) ? process0.count() : 0;
			if (processBatchCount <= 0)
			{
				continue; // 没有处理数据
			}

			// 加入处理队列
			executers.add(executer);
			processs.add(process0);
		}
		batchLock.writeLock().unlock();

		// 处理队列
		int actionCount = (processs != null) ? processs.size() : 0;
		for (int i = 0; i < actionCount; i++)
		{
			JdbcBatchUpdate<?> executer = executers.get(i);
			BatchProcess process = processs.get(i);

			// 提交处理
			try
			{
				// 记录开始时间
				long starttime = System.currentTimeMillis();
				// 执行提交
				List<?> retList = executer.execute(process);
				// 计算所用时间
				long endtime = System.currentTimeMillis();
				if (this.isLogger())
				{
					long useTime = endtime - starttime;
					int processBatchCount = process.count(); // 提交数量
					System.err.println(TimeUtils.toString(endtime) + " : jade batch process:" + executer + " usetime: " + useTime + "ms batch: "
							+ processBatchCount);
				}

				// 更新处理
				BatchHandler handler = executer.getHandler();
				handler = (handler != null) ? handler : batchHandler;
				if (handler != null)
				{
					handler.batchUpdate(executer.getMethod(), executer.getSql(), process.batchArgs, process.batchBaseArgs, (List<Object>) retList);
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
					handler.batchError(e, executer.getMethod(), executer.getSql(), process.batchArgs, process.batchBaseArgs);
				}
			}
		}

		return actionCount > 0;
	}

	/**
	 * 创建更新器
	 * */
	@Override
	protected <T> JdbcUpdate<T> createUpdate(Method method, String sql, DAO dao, SQL sql0) throws Exception
	{
		// 检测是否是批量处理
		BatchUpdata batchUpdata = method.getAnnotation(BatchUpdata.class);
		if (batchUpdata != null)
		{
			// 创建执行器
			JdbcBatchUpdate<T> executer0 = new JdbcBatchUpdate<T>(this, method, sql);
			// 加入批量过程列表
			BatchProcess process = new BatchProcess();
			batchMap.put(executer0, process);
			return executer0;
		}

		// 常规处理
		return new JdbcFUpdate<T>(this, method, sql);
	}

	/**
	 * 添加到过程
	 * 
	 * @param executer
	 * @param process
	 * @return
	 */
	protected boolean addToBatch(JdbcExecuter<?> executer, BatchProcess process)
	{
		// 获取寄存
		BatchProcess process0 = batchMap.get(executer);
		if (process0 == null)
		{
			return false; // 不存在这个批量处理
		}
		// 合并过程
		try
		{
			batchLock.writeLock().lock();
			return process0.combine(process);
		}
		finally
		{
			batchLock.writeLock().unlock();
		}
	}

	public int getBatchUpdataInterval()
	{
		return batchUpdataInterval;
	}

	public void setBatchUpdataInterval(int batchUpdataInterval)
	{
		this.batchUpdataInterval = batchUpdataInterval;
	}

	public BatchHandler getBatchHandler()
	{
		return batchHandler;
	}

	public void setBatchHandler(BatchHandler batchHandler)
	{
		this.batchHandler = batchHandler;
	}

	/** 批量处理接口 **/
	public interface BatchHandler
	{
		void batchError(Exception e, Method method, String sql, List<Object[]> batchArgs, List<Object[]> batchBaseArgs);

		void batchUpdate(Method method, String sql, List<Object[]> batchArgs, List<Object[]> batchBaseArgs, List<Object> retList);
	}

	/** 是否启动日志输出 **/
	public boolean isLogger()
	{
		return logger;
	}

	/** 设置启动日志输出 **/
	public void setLogger(boolean logger)
	{
		this.logger = logger;
	}
}