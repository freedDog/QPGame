package com.game.framework.component.method;

import java.lang.reflect.Method;

/**
 * 基础函数函数执行器<br>
 * DefualtInvoker.java
 * @author JiangBangMing
 * 2019年1月3日下午1:45:26
 */
public class DefualtInvoker extends MethodInvoker<Object[]>
{
	public DefualtInvoker(Object obj, Method method)
	{
		super(obj, method);
	}

	@Override
	public Object[] parseArguments(Object[] objs) throws Exception
	{
		return objs;
	}

}
