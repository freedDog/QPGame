package com.game.base.service.mgr;

/**
 * 计数统计管理<br>
 * 
 */
public class CountMgr {
	private static final String KEY_COUNTER = "CountMgr";

	/** 连接的客户端 **/
	public static final String Key_ConnectClient = "connectClient";

	protected static boolean init() {
		resetCount();
		return true;
	}

	/** 清除数量 **/
	protected static void resetCount() {
		SharedCounterMgr.clearAll(KEY_COUNTER);
	}

	/** 修改数量 **/
	public static long changeCount(String key, long change) {
		return SharedCounterMgr.change(KEY_COUNTER, key, change);
	}

	/** 获取数量 **/
	public static long getCount(String key) {
		return SharedCounterMgr.get(KEY_COUNTER, key);
	}

}