package com.game.framework.framework.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.component.log.Log.LogType;
import com.game.framework.utils.ReflectUtils;


/**
 * 组件管理器<br>
 * 用于记录每个组件是否初始化过了.<br>
 * 可继承这个类, 只是用作快速调用init方法和initStatic<br>
 * 使用的是统一的中央管理库, 所以分开对象继承也没问题<br>
 * ComponentMgr.java
 * @author JiangBangMing
 * 2019年1月3日上午11:55:51
 */
public class ComponentMgr {
	protected static final ComponentCentreMgr centreMgr = new ComponentCentreMgr();

	/** 初始化组件 **/
	protected static <T> boolean init(Class<T> key, T obj, Object... args) {
		return initComponent(key, obj, args);
	}

	/** 初始化组件 **/
	protected static <T> boolean initStatic(Class<T> key, Object... args) {
		return initComponent(key, null, args);
	}

	/** 销毁组件 **/
	protected static <T> boolean destroy(Class<T> key, T obj) {
		return destroyComponent(key, obj);
	}

	/** 销毁组件 **/
	protected static <T> boolean destroyStatic(Class<T> key) {
		return destroyComponent(key, null);
	}

	/** 初始化组件 **/
	private static <T> boolean initComponent(Class<T> key, T obj, Object... args) {
		if (!centreMgr.doInit(key, obj, args)) {
			ComponentLog.writeLog(LogType.ERROR, key.getSimpleName() + "初始化失败!", 6, false);
			return false;
		}
		ComponentLog.writeLog(LogType.INFO, key.getSimpleName() + "初始化成功!", 6, false);
		return true;
	}

	/** 销毁组件 **/
	private static <T> boolean destroyComponent(Class<T> key, T obj) {
		if (!centreMgr.doDestroy(key, obj)) {
			ComponentLog.writeLog(LogType.ERROR, key.getSimpleName() + "销毁失败!", 6, false);
			return false;
		}
		ComponentLog.writeLog(LogType.INFO, key.getSimpleName() + "销毁成功!", 6, false);
		return true;
	}

	/** 销毁所有组件 **/
	protected static void destroyAllComponent() {
		centreMgr.destroyAll();
	}

	/** 获取组件 **/
	protected static <T> T getComponent(Class<T> clazz) {
		return centreMgr.get(clazz);
	}

	/** 获取组件 **/
	protected static <T> T getComponent(Class<?> key, Class<T> clazz) {
		return centreMgr.get(key, clazz);
	}

	/** 中心管理器 **/
	protected static class ComponentCentreMgr {
		private static final Object empty = new Empty(); // 空对象
		/** 组件注册表, 用于检测一类组件是否注册过 **/
		protected final Map<Class<?>, Object> inits;

		public ComponentCentreMgr() {
			inits = new LinkedHashMap<>();
		}

		@SuppressWarnings("unchecked")
		public void destroyAll() {
			// 执行销毁
			List<Class<?>> keys = new ArrayList<>(inits.keySet());
			int ksize = (keys != null) ? keys.size() : 0;
			for (int i = 0; i < ksize; i++) {
				int index = ksize - i - 1;
				Class<?> key = keys.get(index);
				// 执行销毁
				doDestroy((Class<Object>) key, get(key));
				// 输出消息
				ComponentLog.writeLog(LogType.DEBUG, key.getSimpleName() + "销毁成功!", 6, false);
			}
		}

		/** 是否初始化过了 **/
		public boolean isInit(Class<?> clazz) {
			Object b = inits.get(clazz);
			return b != null;
		}

		/** 执行销毁 **/
		protected <T> boolean doDestroy(Class<? super T> clazz, T obj) {
			// 检测是否初始化过
			if (!isInit(clazz)) {
				return true; // 没初始化过这个对象
			}

			// 释放
			synchronized (this) {
				Object v = (obj != null) ? obj : empty; // 静态存放的是类
				Object now = inits.get(clazz);
				if (now != v) {
					return false; // 被替换了, 不过是不可能的.
				}
				inits.remove(clazz);
			}

			// 检测是否有初始化函数(这个最好识别参数)
			final String funcName = "destroy";
			Class<?> objClass = (obj != null) ? obj.getClass() : clazz;
			Method method = ReflectUtils.getMethodByName(objClass, funcName, new Class<?>[] {});
			// Method method = ReflectUtils.getMethodByNameByJava(objClass, funcName, params);
			if (method != null) {
				try {
					// 调用初始化函数
					method.setAccessible(true);
					Object retObj = method.invoke(obj);
					if (retObj != null && !((Boolean) retObj)) {
						Log.error("组件" + clazz + "销毁失败!");
						return false;
					}
				} catch (Exception e) {
					Log.error("调用销毁函数失败! class=" + clazz + " obj=" + obj, e);
					return false;
				}
			} else {
				// 检测是否存在其他名称的这个函数
				method = ReflectUtils.getMethodByName(objClass, funcName);
				if (method != null) {
					Log.error("销毁函数参数不对应!" + method, true);
					return false;
				}
			}
			return true;
		}

		/** 设置初始化函数对象, 如果有init函数会自动调用. **/
		protected <T> boolean doInit(Class<? super T> clazz, T obj, Object... args) {
			// 检测是否初始化过
			if (isInit(clazz)) {
				return true;
			}

			// 检测是否有初始化函数(这个最好识别参数)
			final String funcName = "init";
			Class<?> objClass = (obj != null) ? obj.getClass() : clazz;
			Class<?>[] params = ReflectUtils.createClasses(args);
			Method method = ReflectUtils.getMethodByName(objClass, funcName, params);
			// Method method = ReflectUtils.getMethodByNameByJava(objClass, funcName, params);
			if (method != null) {
				try {
					// 调用初始化函数
					method.setAccessible(true);
					Object retObj = method.invoke(obj, args);
					if (retObj != null && !((Boolean) retObj)) {
						// Log.error("组件" + clazz + "初始化失败!"); //外部输出就好了.
						return false;
					}
				} catch (Exception e) {
					Log.error("调用初始化函数失败! method=" + method + " obj=" + obj, e);
					return false;
				}
			} else {
				// 检测参数
				if (args != null && args.length > 0) {
					Log.error("没找到初始化函数(init) 缺带有参数! class=" + clazz + " obj=" + obj + " args" + Arrays.toString(args), true);
					return false;
				}
				// 检测是否存在其他名称的这个函数
				method = ReflectUtils.getMethodByName(objClass, funcName);
				if (method != null) {
					Log.error("销毁函数参数不对应!" + method, true);
					return false;
				}

			}

			// 初始化成功
			synchronized (this) {
				Object v = (obj != null) ? obj : empty; // 静态存放的是类
				inits.put(clazz, v);
			}
			return true;
		}

		/** 获取组件 **/
		protected <T> T get(Class<T> clazz) {
			return get(clazz, clazz);
		}

		/** 获取组件 **/
		@SuppressWarnings("unchecked")
		protected <T> T get(Class<?> key, Class<T> clazz) {
			Object b = inits.get(clazz);
			if (b == null || b == empty || !clazz.isInstance(b)) {
				return null;
			}
			return (T) b;
		}
	}

	/** 空对象 **/
	static class Empty extends Object {

		@Override
		public String toString() {
			return "Empty";
		}
	}

	static class ComponentLog extends Log {
		/**
		 * 输出日志<br>
		 * 
		 * @param funcLevel
		 *            函数层级
		 * @param t
		 *            错误对象
		 **/
		public static void writeLog(LogType type, Object msg, int funcLevel, Throwable t) {
			Log.writeLog(type, msg, funcLevel, t);
		}

		/**
		 * 输出日志<br>
		 * 
		 * @param funcLevel
		 *            函数层级
		 * @param showStackTrace
		 *            是否显示堆栈
		 **/
		public static void writeLog(LogType type, Object msg, int funcLevel, boolean showStackTrace) {
			Log.writeLog(type, msg, funcLevel, showStackTrace);
		}
	}

}
