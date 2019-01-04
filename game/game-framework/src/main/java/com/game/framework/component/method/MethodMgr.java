package com.game.framework.component.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.component.method.MethodInvoker.IParseArguments;
import com.game.framework.utils.ResourceUtils;


/**
 * 函数管理器<br>
 * MethodMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午1:46:07
 */
public abstract class MethodMgr<K, V> {
	protected Map<K, MethodInvoker<V>> methods = new HashMap<>();

	/** 获取关联key **/
	protected abstract K getKey(Annotation ca, Annotation ma, Class<?> clazz, Method method);

	/** 创建函数执行器 **/
	protected abstract MethodInvoker<V> createMethodInvoker(Annotation ca, Annotation ma, Class<?> clazz, Object obj, Method method);

	/** 创建对象 **/
	protected Object createObject(Annotation ca, Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			Log.error("无法创建指令对象: class=" + clazz, e);
		}
		return null;
	}

	/** 根据反射创建 **/
	public <CA extends Annotation, MA extends Annotation> boolean register(Object obj, Class<CA> caclass, Class<MA> maclass, IFilter<CA, MA> filter) {
		// 获取标签
		Class<?> clazz0 = obj.getClass();
		CA ca = clazz0.getAnnotation(caclass);
		if (ca == null) {
			return false;
		}

		// 判断是否继承函数
		for (Method method : clazz0.getDeclaredMethods()) {
			// 检测是否过期
			Deprecated deprecated0 = method.getAnnotation(Deprecated.class);
			if (deprecated0 != null) {
				continue; // 过期
			}
			// 判断是否带指令标签
			MA ma = null;
			if (maclass != null) {
				// 有邀请函数必须带标签情况
				ma = method.getAnnotation(maclass);
				if (ma == null) {
					continue;
				}
			}

			// 检测额外筛选器
			if (filter != null) {
				if (!filter.filter(ca, ma, clazz0, method)) {
					continue; // 跳过
				}
			}

			// 判断参数
			K key = getKey(ca, ma, clazz0, method);
			Object old = methods.get(key);
			if (old != null) {
				// Log.error("存在重复key: key=" + key + " invoker=" + old);
				throw new RuntimeException("存在重复key: key=" + key + " by " + clazz0 + " invoker=" + old);
				// return false;
			}
			// 创建执行对象
			MethodInvoker<V> invoker = createMethodInvoker(ca, ma, clazz0, obj, method);
			if (invoker == null) {
				return false;
			}
			methods.put(key, invoker);
		}

		return true;
	}

	/** 根据反射创建 **/
	public <CA extends Annotation, MA extends Annotation> boolean register(String packet, Class<CA> caclass, Class<MA> maclass, IFilter<CA, MA> filter) {
		// 创建
		String packetRegex = ResourceUtils.getPacketRegex(packet);
		List<Class<?>> classes = ResourceUtils.getClassesByAnnotation(caclass, packetRegex);
		for (Class<?> clazz0 : classes) {
			Object obj = null;
			// 检测是否过期
			Deprecated deprecated = clazz0.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue; // 过期
			}
			// 获取标签
			Annotation ca = clazz0.getAnnotation(caclass);
			if (ca == null) {
				continue;
			}

			// 创建对象
			if (obj == null) {
				obj = createObject(ca, clazz0);
				if (obj == null) {
					return false;
				}
			}

			// 注册对象
			if (!register(obj, caclass, maclass, filter)) {
				return false;
			}
		}
		return true;
	}

	/** 获取函数执行器 **/
	public MethodInvoker<V> getMethodInvoker(K key) {
		return methods.get(key);
	}

	/** 获取所有函数执行对象 **/
	public List<MethodInvoker<V>> getMethodInvokers() {
		return new ArrayList<>(methods.values());
	}

	/** 执行函数 **/
	public Object execute(K key, V args) throws Exception {
		return execute(key, args, null);
	}

	/** 执行函数 **/
	public Object execute(K key, V args, IParseArguments<V> handler) throws Exception {
		MethodInvoker<V> invoker = methods.get(key);
		if (invoker == null) {
			// throw new Exception("没有找到对应函数: key=" + key + " args=" + args);
			Log.warn("没有找到对应函数: key=" + key + " args=" + args);
			return null;
		}
		return invoker.execute(args, handler);
	}

	/** 过滤器 **/
	public interface IFilter<CA extends Annotation, MA extends Annotation> {
		/** 筛选检测 **/
		boolean filter(CA ca, MA ma, Class<?> clazz, Method method);
	}
}
