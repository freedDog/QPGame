package com.game.framework.component.method;

import java.lang.reflect.Method;

/**
 * 函数执行器<br>
 * MethodInvoker.java
 * @author JiangBangMing
 * 2019年1月3日下午1:44:52
 */
public abstract class MethodInvoker<T> {
	protected final Object obj;
	protected final Method method;

	/**
	 * @param obj
	 *            对象
	 * @param method
	 *            函数
	 */
	public MethodInvoker(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
		// 开放函数访问权限
		method.setAccessible(true);
	}

	/**
	 * 执行函数, 使用解析接口处理
	 * 
	 * @param obj
	 * @return
	 */
	public Object execute(T obj, IParseArguments<T> handler) throws Exception {
		Object[] args = (handler != null) ? handler.parseArguments(method, obj) : parseArguments(obj);
		if (args == null) {
			return null; // 解析不出参数
		}
		return this.execute0(args);
	}

	/**
	 * 执行函数
	 * 
	 * @param obj
	 * @return
	 */
	public Object execute(T obj) throws Exception {
		return execute(obj, null);
	}

	/**
	 * 执行处理
	 * 
	 * @param args
	 * @return
	 */
	protected Object execute0(Object... args) throws Exception {
		return method.invoke(obj, args);
	}

	/**
	 * 解析参数
	 * 
	 * @param buf
	 * @return
	 */
	public abstract Object[] parseArguments(T obj) throws Exception;

	/**
	 * 获取函数的参数类型
	 * 
	 * @param index
	 * @return
	 */
	public Class<?> getParameterType(int index) {
		if (index < 0) {
			return null;
		}
		// 获取函数的参数
		Class<?>[] parameterTypes = method.getParameterTypes();
		int paramCount = (parameterTypes != null) ? parameterTypes.length : 0;
		if (index >= paramCount) {
			return null;
		}
		return parameterTypes[index];
	}

	public Object getObj() {
		return obj;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "MethodInvoker [obj=" + obj + ", method=" + method + "]";
	}

	/** 参数解析 **/
	public interface IParseArguments<V> {
		/** 参数解析 **/
		Object[] parseArguments(Method method, V obj) throws Exception;
	}
}
