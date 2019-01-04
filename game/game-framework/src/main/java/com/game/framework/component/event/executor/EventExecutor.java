package com.game.framework.component.event.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 事件执行器<br>
 * EventExecutor.java
 * @author JiangBangMing
 * 2019年1月3日下午1:43:23
 */
public class EventExecutor<K, M, S> implements IEventExecutor<K, M, S>
{
	protected final Map<K, List<IEventListener<? super K, ? super M, ? super S>>> map;

	public EventExecutor()
	{
		map = new HashMap<>();
	}

	/** 添加事件监听 **/
	// @Override
	protected boolean addListener(K key, IEventListener<? super K, ? super M, ? super S> listener)
	{
		List<IEventListener<? super K, ? super M, ? super S>> list = map.get(key);
		if (list == null)
		{
			list = new ArrayList<>();
			map.put(key, list);
		}
		return list.add(listener);
	}

	/** 移除事件监听 **/
	// @Override
	protected boolean removeListener(K key, IEventListener<? super K, ? super M, ? super S> listener)
	{
		List<IEventListener<? super K, ? super M, ? super S>> list = map.get(key);
		if (list == null)
		{
			return false;
		}
		return list.remove(listener);
	}

	public boolean hashEvent(K key)
	{
		List<IEventListener<? super K, ? super M, ? super S>> list = map.get(key);
		return (list != null) ? list.size() > 0 : false;
	}

	@Override
	public boolean exec(K key, M message, S session)
	{
		List<IEventListener<? super K, ? super M, ? super S>> list = map.get(key);
		if (list == null)
		{
			return false;
		}
		// 遍历处理
		Iterator<IEventListener<? super K, ? super M, ? super S>> iter = list.iterator();
		while (iter.hasNext())
		{
			IEventListener<? super K, ? super M, ? super S> listener = iter.next();
			boolean reslt = listener.exec(key, message, session);
			if (!reslt)
			{
				return false;
			}
		}
		return true;
	}

}
