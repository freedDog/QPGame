package com.game.framework.framework.loader;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 作用域类加载器<br>
 * ScopeClassLoader.java
 * @author JiangBangMing
 * 2019年1月3日下午3:00:10
 */
public class ScopeClassLoader extends URLClassLoader {
	protected String name;
	protected ConcurrentMap<String, Class<?>> classMap;
	protected Set<String> filters;

	public ScopeClassLoader(String name) {
		this(name, null);
	}

	public ScopeClassLoader(String name, Class<?>[] classes) {
		this(name, ((URLClassLoader) ScopeClassLoader.class.getClassLoader()), classes);
	}

	public ScopeClassLoader(String name, URLClassLoader parent, Class<?>[] classes) {
		super(parent.getURLs(), parent);
		this.name = name;
		classMap = new ConcurrentHashMap<>();

		// 初始化初始类
		filters = new HashSet<>();
		int csize = (classes != null) ? classes.length : 0;
		for (int i = 0; i < csize; i++) {
			Class<?> clazz = classes[i];
			filters.add(clazz.getName());
		}
	}

	/** 读取当当前类 **/
	protected Class<?> findLoadedClassBySelf(String name) {
		Class<?> clazz = classMap.get(name);
		// System.out.println("find class: " + name + " " + clazz);
		if (clazz != null) {
			return clazz;
		}
		// 从基础中加载
		return super.findLoadedClass(name);
	}

	/** 当前加载 **/
	protected Class<?> loadClassBySelf(String name) {
		try {
			// 检测过滤
			if (filters != null && filters.contains(name)) {
				return null; // 跳过.
			}

			// 查找读取类
			Class<?> c = findClass(name);
			if (c == null) {
				return null; // 没有咯.
			}

			// 判断加载是否成功
			Class<?> old = classMap.putIfAbsent(name, c);
			c = (old != null) ? old : c;

			// System.out.println("load class: " + name + " " + c);
			return c;
		} catch (ClassNotFoundException e) {
			// 加载失败, 不管.
		}

		return null;
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			// 加载已经存在的类
			Class<?> c = findLoadedClassBySelf(name);
			if (c == null) {
				c = loadClassBySelf(name);
			}

			// 还是没有, 从父加载器中加载.
			if (c == null) {
				ClassLoader parent = this.getParent();
				c = parent.loadClass(name);
			}

			// 最后检测
			if (c == null) {
				throw new ClassNotFoundException(name);
			}

			// 链接指定的类
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}

	@Override
	public String toString() {
		return "ScopeLoader [" + name + " " + Arrays.toString(this.getURLs()) + "]";
	}
}
