package com.game.framework.component.action;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认线程工厂, 用于创建线程生成名字
 * DefaultThreadFactory.java
 * @author JiangBangMing
 * 2019年1月3日下午12:57:40
 */
public class DefaultThreadFactory implements ThreadFactory
{
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public DefaultThreadFactory(String poolName)
	{
		SecurityManager securityManager = System.getSecurityManager();
		group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "" + poolName + "-thread-";
	}

	public Thread newThread(Runnable run)
	{
		Thread thread = new Thread(group, run, namePrefix + threadNumber.getAndIncrement(), 0);
		// 设置守护线程属性
		if (thread.isDaemon())
		{
			thread.setDaemon(false);
		}
		// 设置线程优先级
		if (thread.getPriority() != Thread.NORM_PRIORITY)
		{
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}
}