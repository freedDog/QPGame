package com.game.base.service.cmd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.game.framework.component.method.MethodInvoker;
import com.game.framework.component.method.MethodInvoker.IParseArguments;
import com.game.framework.component.method.MethodMgr;

/**
 * 指令管理器<br>
 * 
 */
public class CmdMgr {
	/** 注册函数 **/
	public static boolean register(String packet, MethodMgr.IFilter<Cmd, Cmd.CmdFunc> filter) {
		return methods.register(packet, Cmd.class, Cmd.CmdFunc.class, filter);
	}

	/** 执行函数 **/
	public static Object execute(short key, Object[] args) throws Exception {
		return methods.execute(key, args);
	}

	/** 执行函数 **/
	public static Object execute(short key, Object[] args, IParseArguments<Object[]> handler) throws Exception {
		return methods.execute(key, args, handler);
	}

	/** 函数绑定管理器 **/
	protected static MethodMgr<Short, Object[]> methods = new MethodMgr<Short, Object[]>() {
		@Override
		protected Short getKey(Annotation ca, Annotation ma, Class<?> clazz, Method method) {
			return ((Cmd.CmdFunc) ma).value();
		}

		@Override
		protected MethodInvoker<Object[]> createMethodInvoker(Annotation ca, Annotation ma, Class<?> clazz, Object obj, Method method) {
			return new CmdInvoker(obj, method);
		}
	};

}