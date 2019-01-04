package com.game.framework.framework.rpc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;


/**
 *  回调管理器
 * RpcCallbackMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:25:59
 */
public class RpcCallbackMgr<T extends RpcChannel> extends RpcSerializer {
	protected final AtomicLong callbackId; // 回调Id
	protected final ConcurrentMap<Long, Callback> callbacks;
	protected long timeoutCheckTime;

	public RpcCallbackMgr() {
		callbackId = new AtomicLong(0);
		callbacks = new ConcurrentHashMap<>();
	}

	/** 返回callback Id **/
	protected Callback addCallBack(RpcCallback callback) throws Exception {
		// 查找回馈函数
		Method method = RpcCallback.getCallbackMethod(callback);
		if (method == null) {
			throw new Exception("没有找到回调函数! " + RpcCallback.CALLBACK_FUNCNAME);
		}
		// 添加回馈
		long callbackId0 = callbackId.incrementAndGet();
		Callback c = new Callback(callbackId0, callback, method);
		callbacks.put(callbackId0, c);
		return c;
	}

	/** 从rpc调用回来的回调处理(这个处理不能调用阻塞rpc访问的阻塞在一起, 因为这个是回调用于解锁阻塞RPC调用) **/
	protected void onCallback(T channel, RpcCallBackMsg callbackMsg, RpcMsg packet) {
		long callbackId = callbackMsg.getCallbackId();
		if (callbackId == 0L) {
			return;
		}
		// 移除回调函数
		Callback cobj = callbacks.remove(callbackId);
		if (cobj == null) {
			Log.warn("回调已经不存在. cid=" + callbackId); // 超时了吧
			return;
		}
		// 执行回调
		Method method = cobj.method;
		RpcCallback callback = cobj.getCallback();

		// 获取函数参数, 优先消息的回调参数, 再试回调对象本身参数, 最后才是函数参数
		Class<?>[] parameterTypes = callbackMsg.getParamTypes();
		parameterTypes = (parameterTypes == null) ? callback.getParamTypes() : parameterTypes;
		parameterTypes = (parameterTypes == null) ? method.getParameterTypes() : parameterTypes;
		Object[] params = null;
		try {
			// 解析参数
			byte[] data = callbackMsg.getData();
			params = toObjects(channel, data, parameterTypes, packet);
			int tsize = (parameterTypes != null) ? parameterTypes.length : 0;
			int asize = (params != null) ? params.length : 0;
			if (tsize != asize) {
				Log.error("回调参数不相同!!! " + method + " parameterTypes=" + Arrays.toString(parameterTypes));
				return;
			}
		} catch (Exception e) {
			Log.error("回调执行失败! callback=" + callback, e);
			return;
		}

		try {
			// 回调执行
			method.setAccessible(true);
			method.invoke(callback, params);
		} catch (Exception e) {
			Log.error("回调执行失败! callback=" + callback + " paramTypes=" + Arrays.toString(parameterTypes) + " args=" + Arrays.toString(params), e);
			return;
		}
	}

	/** 获取回调数量 **/
	public int getCallBackCount() {
		return callbacks.size();
	}

	/** 检测回调超时,间隔时间控制 **/
	public void checkTimeOut(int intervalTime) {
		// 判断是否有间隔时间限制
		if (intervalTime > 0) {
			// 检测间隔时间
			long nowTime = System.currentTimeMillis();
			long dt = nowTime - timeoutCheckTime;
			if (dt <= intervalTime) {
				return; // 没过间隔时间
			}
			timeoutCheckTime = nowTime;
		}
		// 更新检测时间
		checkTimeOut();
	}

	/** 检测回调超时 **/
	protected void checkTimeOut() {
		// 遍历检测时间是否超时
		List<Callback> removes = new ArrayList<>();
		for (Callback callback : callbacks.values()) {
			if (callback.isAlive()) {
				continue;
			}
			removes.add(callback);
		}

		// 移除超时
		long nowTime = System.currentTimeMillis();
		for (Callback callback : removes) {
			// 尝试移除
			Callback remove = callbacks.remove(callback.getCallbackId());
			if (remove == null || remove != callback) {
				continue; // 已经被移除了.
			}
			// 触发超时
			long timeout = nowTime - callback.getStartTime();
			callback.getCallback().onTimeOut(timeout);
		}
	}

	/** 回调对象 **/
	protected class Callback {
		protected final long callbackId; // 回调ID
		protected final RpcCallback callback; // 回调对象
		protected final long startTime; // 开始时间
		protected final Method method; // 回调函数

		public Callback(long callbackId, RpcCallback callback, Method method) {
			super();
			this.callbackId = callbackId;
			this.callback = callback;
			this.startTime = System.currentTimeMillis();
			this.method = method;
		}

		public boolean isAlive() {
			long nowTime = System.currentTimeMillis();
			return nowTime - startTime < callback.getTimeOut();
		}

		public RpcCallback getCallback() {
			return callback;
		}

		public long getStartTime() {
			return startTime;
		}

		public Method getMethod() {
			return method;
		}

		public long getCallbackId() {
			return callbackId;
		}
	}
}
