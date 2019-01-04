package com.game.framework.component.data.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.MapUtils;



/**
 * 数据管理器<br>
 * ElementManager0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:40:32
 */
public abstract class ElementManager0<K, T> {
	protected final ConcurrentMap<K, T> elements;

	protected ElementManager0() {
		elements = new ConcurrentHashMap<>();
	}

	/** 检查内存中是否存在数据 **/
	protected boolean isExist(K key) {
		return elements.containsKey(key);
	}

	/** 从缓存中获取数据 **/
	protected T getFromCache(K key) {
		T entity = elements.get(key);
		if (entity != null) {
			onGet(key, entity);
		}
		return entity;
	}

	/** 创建添加(不建议外部使用, 最好只在初始化时加入, 触发创建事件), 如果存在数据返回当前数据 **/
	protected T addByCreate(K key, T element) {
		if (element == null) {
			return null;
		}

		// 插入数据
		T old = elements.putIfAbsent(key, element);
		if (old != null) {
			return old; // 已经存在数据.
		}

		// 加载数据
		if (IElement.class.isAssignableFrom(element.getClass())) {
			// 执行加载
			boolean result = false;
			try {
				result = ((IElement) element).load();
			} catch (Exception e) {
				Log.error("加载数据错误! element=" + element + " key=" + key, e);
			}
			// 判断结果
			if (!result) {
				elements.remove(key, element); // 移除数据
				Log.error("数据加载失败! element=" + element + " key=" + key);
				return null;
			}
		}
		// 触发创建事件
		onCreate(key, element); // 创建事件
		return element;
	}

	/** 加载创建玩家数据 **/
	protected abstract T create(K key);

	/** 筛选获取(不创建新数据) **/
	protected T get(MapUtils.IFilter<K, ? super T> filter) {
		List<T> out = getAll(filter, 1);
		return (out != null && out.size() > 0) ? out.get(0) : null;
	}

	/** 筛选获取(不创建新数据) **/
	protected List<T> getAll(MapUtils.IFilter<K, ? super T> filter, int count) {
		List<T> out = new ArrayList<>();
		// 遍历获取
		Iterator<Map.Entry<K, T>> iter = elements.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<K, T> entry = iter.next();
			K k = entry.getKey();
			T v = entry.getValue();
			// 筛选
			if (!filter.check(k, v)) {
				continue;
			}
			// 获取
			out.add(v);
			onGet(k, v);
			// 数量限制
			if (count > 0 && out.size() >= count) {
				break;
			}
		}
		return out;
	}

	/** 获取数据, 不存在调用create **/
	protected T get(K key) {
		if (key == null) {
			return null;
		}

		// 从缓存中获取玩家
		T cache = getFromCache(key);
		if (cache != null) {
			return cache;
		}
		T element = null;
		try {
			// 加载玩家数据
			element = create(key);
			element = addByCreate(key, element);
		} catch (Exception e) {
			Log.error("创建数据错误: key=" + key, e);
			return null;
		}
		// 触发获取事件
		if (element != null) {
			onGet(key, element);
		}
		return element;
	}

	/** 卸载数据(按照key移除) **/
	protected T remove(K key) {
		T element = elements.remove(key);
		if (element == null) {
			// 如果有传入entity, 验证是否是这个对象
			// Log.error("数据已经被卸载了! key=" + key + " remove=" + remove, true);
			return null;
		}
		try {
			// 先触发事件再unload
			onRemove(key, element);
			// 移除成功
			if (IElement.class.isAssignableFrom(element.getClass())) {
				((IElement) element).unload();
			}
		} catch (Exception e) {
			Log.error("删除数据错误!" + element, e);
		}
		return element;
	}

	/** 卸载数据(必须是Key和Value都符合才能移除) **/
	protected boolean remove(K key, T element) {
		// 执行删除
		if (!elements.remove(key, element)) {
			// 如果有传入entity, 验证是否是这个对象
			// Log.error("数据已经被卸载了! key=" + key + " remove=" + remove, true);
			return false;
		}
		try {
			// 移除事件
			onRemove(key, element);
			// 移除成功
			if (IElement.class.isAssignableFrom(element.getClass())) {
				((IElement) element).unload();
			}
		} catch (Exception e) {
			Log.error("删除数据错误!" + element, e);
		}
		return true;
	}

	protected void removeAll() {
		Map<K, T> copys = new HashMap<>(elements);
		for (Map.Entry<K, T> entry : copys.entrySet()) {
			K key = entry.getKey();
			T element = entry.getValue();
			if (!elements.remove(key, element)) {
				continue; // 在别的线程删掉了
			}
			try {
				// 移除事件
				onRemove(key, element);
				// 移除成功
				if (IElement.class.isAssignableFrom(element.getClass())) {
					((IElement) element).unload();
				}
			} catch (Exception e) {
				Log.error("删除数据错误!" + element, e);
			}
		}
	}

	protected int size() {
		return elements.size();
	}

	/** 遍历处理 **/
	protected void forech(MapUtils.Foreach<K, T> foreach) {
		MapUtils.action(elements, foreach, 0);
	}

	/** 获取数据事件 **/
	protected void onGet(K key, T element) {
	}

	/** 创建数据事件 **/
	protected void onCreate(K key, T element) {
	}

	/** 删除数据事件 **/
	protected void onRemove(K key, T element) {
	}
}
