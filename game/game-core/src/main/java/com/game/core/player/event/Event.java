package com.game.core.player.event;

/**
 * 事件对象<br>
 * Event.java
 * @author JiangBangMing
 * 2019年1月8日下午1:14:58
 */
public class Event {
	private Object[] args;

	public Event(Object... args) {
		this.args = args;
	}

	/** 获取数据, 0~N **/
	@SuppressWarnings("unchecked")
	public <T> T getArg(int index, Class<T> clazz) {
		int size = (args != null) ? args.length : 0;
		if (index < 0 || index >= size) {
			return null;
		}
		// 参数获取
		Object v = args[index];
		if (v != null && !clazz.isInstance(v)) {
			return null;
		}
		return (T) v;
	}

	/** 获取数据, 0~N **/
	@SuppressWarnings("unchecked")
	public <T> T getArg(int index) {
		return (T) getArg(index, Object.class);
	}

	/** 获取int数据, 0~N **/
	public int getIntArg(int index) {
		Integer v = getArg(index, Integer.class);
		return (v != null) ? v : 0;
	}
}
