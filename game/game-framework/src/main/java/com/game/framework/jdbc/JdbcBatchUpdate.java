package com.game.framework.jdbc;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.game.framework.jdbc.annotation.BatchUpdata;
import com.game.framework.jdbc.executer.JdbcFUpdate;


/**
 * 批量处理执行器
 * JdbcBatchUpdate.java
 * @author JiangBangMing
 * 2019年1月3日下午5:20:48
 */
public class JdbcBatchUpdate<T> extends JdbcFUpdate<T> {

	protected BatchUpdata batchUpdata;
	protected JadeBatchBeanFactory.BatchHandler handler;

	public JdbcBatchUpdate(JadeBaseFactory factory, Method method, String sql) throws Exception
	{
		super(factory, method, sql);
		this.batchUpdata = method.getAnnotation(BatchUpdata.class);

		// 处理接口
		Class<? extends JadeBatchBeanFactory.BatchHandler> handlerClass = this.batchUpdata.handler();
		if (handlerClass != null && handlerClass != JadeBatchBeanFactory.BatchHandler.class)
		{
			try
			{
				handler = handlerClass.newInstance(); // 创建处理接口
			}
			catch (Exception e)
			{
				e.printStackTrace();
				handler = null;
			}
		}

	}

	public String getSql()
	{
		return super.sql;
	}

	public Method getMethod()
	{
		return method;
	}

	public int getPriority()
	{
		return (batchUpdata != null) ? batchUpdata.priority() : 0;
	}

	public int getMaxBatch()
	{
		return (batchUpdata != null) ? batchUpdata.maxbatch() : 0;
	}

	public JadeBatchBeanFactory.BatchHandler getHandler()
	{
		return handler;
	}

	public static final Comparator<JdbcBatchUpdate<?>> comparator = new Comparator<JdbcBatchUpdate<?>>()
	{
		@Override
		public int compare(JdbcBatchUpdate<?> o1, JdbcBatchUpdate<?> o2)
		{
			// 先判断是不是同一个
			if (o1 == o2)
			{
				return 0;
			}
			// 根据优先级判断
			int a1 = o1.getPriority();
			int a2 = o2.getPriority();
			if (a2 > a1)
			{
				return 1;
			}
			else if (a1 == a2)
			{
				// 根据对象本身判断
				return Integer.compare(o1.hashCode(), o2.hashCode());
			}
			return -1;
		}
	};

	@Override
	public String toString()
	{
		String methodName = method.getName();
		Class<?> clazz = method.getDeclaringClass();
		return clazz.getSimpleName() + "." + methodName;
	}
}
