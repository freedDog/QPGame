package com.game.framework.component.component;

import java.util.ArrayList;
import java.util.List;

import com.game.framework.component.annotation.AnnotationUtils;
import com.game.framework.component.annotation.Ignore;
import com.game.framework.component.log.Log;
import com.game.framework.utils.ResourceUtils;
import com.game.framework.utils.collections.ListUtils;

/**
 * 组件集合
 * ComponentList.java
 * @author JiangBangMing
 * 2019年1月3日下午1:35:10
 */
public abstract class ComponentList<T> {
	protected final List<T> components;

	public ComponentList() {
		components = new ArrayList<>();
		load();
	}

	/** 加载数据 **/
	protected abstract boolean load();

	/** 初始化列表 **/
	protected boolean init(List<Class<?>> classes) {
		int csize = (classes != null) ? classes.size() : 0;
		for (int i = 0; i < csize; i++) {
			Class<?> clazz0 = classes.get(i);
			// 检测忽略
			if (clazz0.getAnnotation(Ignore.class) != null) {
				continue;
			}
			// 创建对象
			try {
				@SuppressWarnings("unchecked")
				T obj = (T) clazz0.newInstance();
				components.add(obj);
			} catch (Exception e) {
				Log.error("创建组件失败! class=" + clazz0, e);
			}
		}
		return true;
	}

	/** 遍历处理 **/
	public void forech(ListUtils.Foreach<T> foreach) {
		ListUtils.action(components, foreach, 0);
	}

	/**
	 * 查找对象
	 * 
	 * @param filter
	 * @return
	 */
	public T find(ListUtils.IFilter<? super T> filter) {
		return ListUtils.find(components, filter);
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param list
	 *            列表
	 * @param filter
	 *            过滤器
	 * @param maxCount
	 *            最大数量, 0为全部
	 * @return 符合条件的数据数组
	 */
	public List<T> findAll(ListUtils.IFilter<? super T> filter, int maxCount) {
		return ListUtils.findAll(components, filter, maxCount);
	}

	/** 获取全部数据 **/
	public List<T> getAll() {
		return components;
	}

	public int size() {
		return components.size();
	}

	public boolean isEmpty() {
		return components.isEmpty();
	}

	/** 通用创建 **/
	public static <T> ComponentList<T> createByClass(final Class<?> clazz, final String packet) {
		return new ComponentList<T>() {
			@Override
			protected boolean load() {
				// 筛选符合条件的类
				String regex = ResourceUtils.getPacketRegex(packet);
				List<Class<?>> classes = ResourceUtils.getClassesByClass(clazz, regex);
				// 处理排序
				int csize = (classes != null) ? classes.size() : 0;
				if (csize >= 2) {
					AnnotationUtils.sortByClassPriority(classes);
				}
				return super.init(classes);
			}
		};
	}
}
