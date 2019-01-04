package com.game.framework.framework.rpc;

import java.util.Arrays;

import com.game.framework.component.SyncObject;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcCallMsg;
import com.game.framework.framework.rpc.msg.RpcInfoMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.framework.framework.rpc.msg.RpcProtocol;



/**
 *  * rpc设备<br>
 * 用于处理远程调用, 回调等处理<br>
 * 
 * 1.验证启用, 只是在进行远程调用和回调时检测参数正确性<br>
 * 2.类型检测, 发送的消息中带类型, 用于支持子类检验<br>
 * 
 * 优化<br>
 * 1. 远程调用校验. 不要校验的情况下也能正确运行, 校验情况下能在调用前检测错误.
 * RpcDevice.java
 * @author JiangBangMing
 * 2019年1月3日下午3:22:56
 */
public abstract class RpcDevice<T extends RpcChannel> extends RpcCallbackMgr<T> {
	/** 消息带参数类型 **/
	public static final int MODE_PARAMTYPE = 0x1;
	// public static final int MODE_TEST01 = 0x10;
	// public static final int MODE_TEST02 = 0x100;

	protected boolean verify = true; // rpc验证, 调用前检验参数正确性.

	public RpcDevice() {
	}

	/** 检测转发, 返回true为本地处理 **/
	protected boolean checkRoute(T channel, RpcMsg packet) {
		return true;
	}

	/** 接收rpc消息 **/
	protected void revc(T channel, RpcMsg packet) {
		// 检测转发
		if (!checkRoute(channel, packet)) {
			return; // 转发到别处了
		}
		try {
			// 消息code
			short code = packet.getCode();
			if (code == RpcProtocol.RPC_CALL) {
				RpcCallMsg msg = RpcUtils.toObject(packet.getData(), RpcCallMsg.class);
				if (msg.getCallbackId() > 0) {
					// 这个是需要返回参数的调用
					onCallSync(channel, msg, packet);
				} else {
					// 这个不需要返回参数
					onCall(channel, msg, packet);
				}
			} else if (code == RpcProtocol.RPC_CALLBACK) {
				RpcCallBackMsg msg = RpcUtils.toObject(packet.getData(), RpcCallBackMsg.class);
				onCallback(channel, msg, packet);
			} else if (code == RpcProtocol.RPC_KEEP) {
				// 心跳处理
			} else if (code == RpcProtocol.RPC_INFO) {
				RpcInfoMsg infoMsg = RpcUtils.toObject(packet.getData(), RpcInfoMsg.class);
				channel.setInfoMsg(infoMsg);
			} else {
				Log.error("未知rpc消息类型: code=" + code);
			}
		} catch (Exception e) {
			Log.error("rpc处理错误! ", e);
		}
	}

	/** 创建远程调用信息 **/
	protected RpcInfoMsg createInfoMsg() {
		return null;
	}

	/** rpc连接成功时 **/
	protected void onConnect(T channel) {
	}

	/** rpc连接断开时 **/
	protected void onClose(T channel) {
	}

	@Override
	protected void onCallback(T channel, RpcCallBackMsg callbackMsg, RpcMsg packet) {
		// 不加这个函数可能导致linux上无法继承这个函数.
		super.onCallback(channel, callbackMsg, packet);
	}

	/** 外部远程调用处理函数 **/
	public abstract Object call(T channel, RpcCallMsg rpcMsg, RpcMsg packet) throws Exception;

	/** rpc客户端申请调用函数 **/
	protected void onCall(T channel, RpcCallMsg rpcMsg, RpcMsg packet) {
		try {
			call(channel, rpcMsg, packet);
		} catch (Exception e) {
			Log.error("函数调用失败!", e);
		}
	}

	/** rpc客户端申请调用函数(阻塞, 必须返回对象) **/
	protected void onCallSync(T channel, RpcCallMsg rpcMsg, RpcMsg packet) {
		// 创建回调对象
		long callbackId = rpcMsg.getCallbackId();
		Class<?> retType = rpcMsg.getRetType();
		Class<?>[] paramTypes = new Class<?>[] { Integer.class, String.class, retType };
		RpcCallbackImpl callback = null;
		if (callbackId > 0) {
			callback = new RpcCallbackImpl(channel, callbackId, paramTypes, packet);
			callback.setType(RpcCallBackMsg.CALLBACKTYPE_SYNC); // 这个属于阻塞回调.
		}

		// 执行结果
		Object retObj = null;
		try {
			// 执行调用
			retObj = call(channel, rpcMsg, packet);
			// 没参数不验证.
			if (retObj != null) {
				// 验证回调结果
				if (retType != null) {
					if (!RpcUtils.checkType(retType, retObj)) {
						throw new Exception("返回类型不对! retObj=" + retObj.getClass());
					}

				}

				// 返回值参数类型写入
				if (retType == null || checkMode(packet.getMode(), MODE_PARAMTYPE)) {
					// 用返回对象的类型作为参数.
					paramTypes[2] = retObj.getClass();
				}
			}
		} catch (Exception e) {
			Log.error("远程阻塞调用错误! method=" + rpcMsg.getMethod(), e);
			// 失败返回
			callback.callBack(0, e.toString(), null);
			return;
		}

		// 成功返回
		callback.callBack(1, null, retObj);
	}

	/**
	 * rpc执行方法<br>
	 * 
	 * @param mode
	 *            消息模式(比如带参数(支持子类回调))
	 **/
	protected boolean execute(T channel, String method, Class<?>[] paramTypes, Object[] args, Class<?> retType, long callbackId, int mode) throws Exception {
		int psize = (paramTypes != null) ? paramTypes.length : 0;
		int asize = (args != null) ? args.length : 0;
		if (psize != asize || (args != null && !RpcUtils.checkType(paramTypes, args))) {
			throw new Exception("调用失败, 参数类型不对! method=" + method + " paramTypes=" + Arrays.toString(paramTypes) + " args=" + Arrays.toString(args));
		}
		// 判断连接
		if (!channel.isConnect()) {
			throw new Exception("channel连接断开! method=" + method);
		}
		// 封装消息
		byte[] bytes = toBytes(channel, args, paramTypes);

		// 生成消息
		RpcCallMsg msg = new RpcCallMsg();
		msg.setMethod(method);
		msg.setData(bytes);
		msg.setCallbackId(callbackId);
		// 写入参数类型, 用于支持子类调用.
		boolean sp = checkMode(mode, MODE_PARAMTYPE);
		if (sp) {
			msg.setParamTypes(paramTypes);
		}
		if (sp || this.isVerify()) {
			msg.setRetType(retType);
		}
		RpcMsg sendMsg = RpcMsg.create(RpcProtocol.RPC_CALL, RpcUtils.toByte(msg));
		sendMsg.setMode(mode);
		// 发送消息
		channel.write(sendMsg);
		return true;
	}

	/** 远程执行, 无返回类型 **/
	protected boolean execute(T channel, String method, Class<?>[] paramTypes, Object[] args, int mode) throws Exception {
		return execute(channel, method, paramTypes, args, null, 0L, mode);
	}

	/**
	 * 远程调用, 阻塞处理返回结果(调用失败会报错)<br>
	 * 远程调用的函数必须有个回调对象, 并且回调对象必须为null.<br>
	 * 回调参数: int result(大于0成功), String msg(失败消息), R retObj(返回结果)<br>
	 **/
	@SuppressWarnings("unchecked")
	public <R> R execute(T channel, final String method, final Class<?>[] paramTypes, final Object[] args, final Class<R> retType, int mode) throws Exception {
		// 检测返回值
		if (retType == null || retType == Void.class || retType == void.class) {
			// 用非阻塞执行
			if (!execute(channel, method, paramTypes, args, null, 0L, mode)) {
				throw new Exception("远程调用函数执行错误! method=" + method + " args=" + Arrays.toString(args));
			}
			return null;
		}

		// 检测参数
		int psize = (paramTypes != null) ? paramTypes.length : 0;
		int asize = (args != null) ? args.length : 0;
		if (psize != asize) {
			throw new Exception("调用失败, 参数类型不对! method=" + method + " psize=" + psize + " asize=" + asize);
		}

		// 判断连接
		if (!channel.isConnect()) {
			throw new Exception("远程调用连接断开! method=" + method + " channel=" + channel);
		}

		// 同步对象
		final SyncObject<Object> syncObj = new SyncObject<>();
		// 生成回调函数
		RpcCallback callback = new RpcCallback() {
			@SuppressWarnings("unused")
			protected void onCallBack(int result, String msg, Object obj) {
				syncObj.complete(result > 0, msg, obj);
			}

			@Override
			public void onTimeOut() {
				String errStr = "远程调用函数超时! method=" + method + " args=" + Arrays.toString(args);
				syncObj.complete(false, errStr, null);
			}

			@Override
			public Class<?>[] getParamTypes() {
				return new Class<?>[] { int.class, String.class, retType };
			}
		};
		// 绑定回调数据
		Callback c = addCallBack(callback);
		if (c == null) {
			throw new Exception("添加回调失败! method=" + method + " channel=" + channel);
		}

		// 远程调用
		syncObj.start();

		// rpc执行
		if (!execute(channel, method, paramTypes, args, retType, c.getCallbackId(), mode)) {
			throw new Exception("执行失败! method=" + method + " channel=" + channel);
		}

		// 阻塞等待
		int waitTime = callback.getTimeOut() + 3 * 1000;
		while (syncObj.waiting(waitTime)) {
			// Thread.sleep(1); // 1是最合理的, 0反倒不合理(随机激活).
			Thread.yield(); // 切换线程等待, 效率最高.
		}
		// 判断失败
		if (!syncObj.isSucceed()) {
			throw new Exception("远程调用失败! " + syncObj.getMsg() + " callbackId=" + c.getCallbackId());
		}
		Object retObj = syncObj.getObj();
		return (R) retObj;
	}

	/** 是否启用验证模式 **/
	public boolean isVerify() {
		return verify;
	}

	/** 设置为rpc是否启用验证模式 **/
	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	/** 检测模式参数 **/
	public static boolean checkMode(int mode, int v) {
		return (mode & v) > 0;
	}

}
