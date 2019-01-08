package com.game.core.player.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.game.framework.component.log.Log;


/**
 * 事件监听器<br>
 * 
 */
public class EventProxy {
	private Map<Short, List<IEventListener>> eventMap; // 事件监听
	private Object lock = new Object(); // 事件锁

	public EventProxy() {
		eventMap = new HashMap<>();
	}

	/**
	 * 添加事件的监听
	 */
	public void addEventListener(short eventType, IEventListener listener) {
		synchronized (lock) {
			// 获取事件列表
			List<IEventListener> eventList = eventMap.get(eventType);
			if (eventList == null) {
				eventList = new ArrayList<IEventListener>();
				eventMap.put(eventType, eventList);
			}
			// 检测是否添加过
			if (eventList.contains(listener)) {
				Log.error("addEventListener失败，该事件已经添加过了. eventType: " + eventType + ", listener: " + listener);
				return;
			}
			eventList.add(listener);
		}
	}

	/**
	 * 删除事件的监听
	 */
	public void removeEventListener(short eventType, IEventListener listener) {
		synchronized (lock) {
			// 获取事件列表
			List<IEventListener> eventList = eventMap.get(eventType);
			if (eventList == null) {
				return;
			}
			// 处理删除
			if (!eventList.remove(listener)) {
				Log.error("removeEventListener失败，该事件已经被删除. eventType: " + eventType + ", listener: " + listener);
				return;
			}
		}
	}

	/**
	 * 派发事件
	 */
	public void dispatchEvent(short eventType, Event event) {
		// 获取事件列表
		List<IEventListener> eventList = null;
		synchronized (lock) {
			// 尝试获取列表(map也并非线程安全, 也要放入lock检测.)
			List<IEventListener> eventList0 = eventMap.get(eventType);
			if (eventList0 == null) {
				return;
			}
			// 提取列表
			eventList = new ArrayList<>(eventList0);
		}
		// 检测绑定监听数
		int esize = (eventList != null) ? eventList.size() : 0;
		if (esize <= 0) {
			return;
		}

		try {
			// 正序上锁
			for (int i = 0; i < esize; i++) {
				IEventListener listener = eventList.get(i);
				listener.onEventLock();
			}

			// 遍历处理
			for (IEventListener listener : eventList) {
				try {
					listener.onEvent(event);
				} catch (Throwable t) {
					Log.error("Event notify exception, eventType: " + eventType, t);
				}
			}
		} finally {
			// 反序解锁
			for (int i = 0; i < esize; i++) {
				IEventListener listener = eventList.get(esize - i - 1);
				listener.onEventUnlock();
			}
		}
	}
}
