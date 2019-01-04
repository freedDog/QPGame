package com.game.framework.framework.session;

import java.util.List;

/**
 * Session集合<br>
 * 连接集合中每一个Session都应该是等价的.
 * SessionGather.java
 * @author JiangBangMing
 * 2019年1月3日下午2:43:28
 */
public class SessionGather<T extends Session> extends Session {
	protected List<T> sessions;

	public SessionGather() {
	}

	public int size() {
		return (sessions != null) ? sessions.size() : 0;
	}

	@Override
	public boolean write(Object msg) {
		int size = (sessions != null) ? sessions.size() : 0;
		if (size <= 0) {
			return false;
		}
		int r = (int) (Math.random() * size); // 随机一个

		// 遍历找出能用的一个
		for (int i = 0; i < size; i++) {
			T session = sessions.get(r + i);
			if (!session.isConnect()) {
				continue;
			}
			// 这个能用, 写入.
			session.write(msg);
			return true;
		}
		return false;
	}

}
