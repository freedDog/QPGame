package com.game.framework.component;

import com.game.framework.component.log.Log;

/**
 * 同步对象<br>
 * 用于2个线程间同步传输数据使用.
 * SyncObject.java
 * @author JiangBangMing
 * 2019年1月3日下午1:58:36
 */
public class SyncObject<T> {
	private static final int RUNSTATE_NONE = 0; // 没启动
	private static final int RUNSTATE_RUN = 1; // 运行中
	private static final int RUNSTATE_COMPLETE = 2; // 完成

	protected volatile int running; // 是否运行中
	protected long startTime;
	protected T obj;
	protected boolean result; // 结果
	protected String msg; // 结果消息

	public SyncObject() {
		running = RUNSTATE_NONE;
	}

	public synchronized void start() {
		this.startTime = System.currentTimeMillis();
		running = RUNSTATE_RUN;
		result = false;
		this.obj = null;
	}

	/** 等待, 超时10s. **/
	public boolean waiting() {
		return waiting(10 * 1000);
	}

	/** 等待处理, 超过等待时间, 返回false. while (syncObj.waiting(waitTime)) 即可 **/
	public synchronized boolean waiting(int timeout) {
		if (running != RUNSTATE_RUN) {
			return false; // 结束运行, 不需要等待了.
		}
		// 检测超时
		long nowTime = System.currentTimeMillis();
		long dt = nowTime - startTime;
		if (dt >= timeout) {
			running = RUNSTATE_COMPLETE;
			msg = "等待超时!";
			result = false;
			this.obj = null;
			return false; // 超时了, 不用等了.
		}
		return true;
	}

	/** 完成 **/
	public synchronized boolean complete(boolean result, String msg, T obj) {
		// 判断是否同步中
		if (running != RUNSTATE_RUN) {
			if (running == RUNSTATE_NONE) {
				Log.error("同步对象尚未启动!", true);
			}
			return false; // 已经结束了
		}
		// 完成
		running = RUNSTATE_COMPLETE;
		this.result = result;
		this.msg = msg;
		this.obj = obj;
		return true;
	}

	/** 成功 **/
	public void success(T obj) {
		complete(true, null, obj);
	}

	/** 失败, 按照完成处理 **/
	public void fail(String errStr) {
		complete(false, errStr, null);
	}

	public T getObj() {
		return obj;
	}

	/** 是否成功 **/
	public boolean isSucceed() {
		return result;
	}

	public String getMsg() {
		return msg;
	}
}
