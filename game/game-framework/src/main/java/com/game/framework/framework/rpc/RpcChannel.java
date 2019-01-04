package com.game.framework.framework.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcInfoMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;


/**
 * Rpc调用渠道 RpcChannel.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:15:54
 */
public abstract class RpcChannel {
	protected RpcInfoMsg infoMsg; // rpc远程调用信息(用于校验)
	protected final ConcurrentMap<Class<?>, Object> impls; // impl缓存
	protected final ConcurrentMap<Object, Object> params; // 绑定属性

	protected RpcChannel() {
		impls = new ConcurrentHashMap<>();
		params = new ConcurrentHashMap<>();
	}

	/** 设置参数 **/
	public Object setParam(Object key, Object v) {
		return params.put(key, v);
	}

	/** 获取参数 **/
	@SuppressWarnings("unchecked")
	public <T> T getParam(Object key) {
		return (T) params.get(key);
	}

	/** 创建动态对象 **/
	@SuppressWarnings("unchecked")
	public <T> T createImpl(Class<T> clazz) {
		Object impl = impls.get(clazz);
		if (impl != null) {
			return (T) impl;
		}

		// 生成动态类
		ClassLoader classLoader = this.getClass().getClassLoader();
		Object proxy = Proxy.newProxyInstance(classLoader, new Class<?>[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Class<?> retType = method.getReturnType();
				return execute(method.getName(), method.getParameterTypes(), args, retType);
			}
		});
		Object old = impls.putIfAbsent(clazz, proxy);
		proxy = (old != null) ? old : proxy;
		return (T) proxy;
	}

	/** 关闭 **/
	public abstract void close();

	/** 是否连接 **/
	public abstract boolean isConnect();

	/** rpc连接成功时 **/
	@SuppressWarnings("unchecked")
	protected void onConnect() {
		RpcDevice<RpcChannel> device = (RpcDevice<RpcChannel>) getDevice();
		if (device != null) {
			device.onConnect(this);
		}
	}

	/** rpc连接断开时 **/
	@SuppressWarnings("unchecked")
	protected void onClose() {
		RpcDevice<RpcChannel> device = (RpcDevice<RpcChannel>) getDevice();
		if (device != null) {
			device.onClose(this);
		}
	}

	/** 接收rpc消息 **/
	protected boolean revc(RpcMsg packet) {
		@SuppressWarnings("unchecked")
		RpcDevice<RpcChannel> device = (RpcDevice<RpcChannel>) getDevice();
		if (device == null) {
			Log.error("rpc没有绑定设备!");
			return false;
		}
		// 处理消息
		device.revc(this, packet);
		return true;
	}

	/** 远程执行, 无返回, 自动参数. **/
	@SuppressWarnings("unchecked")
	public boolean execute(String method, Object... args) {
		if (!this.isConnect()) {
			Log.error("channel尚未连接! method=" + method + " " + Arrays.toString(args));
			return false;
		}

		// 获取设备
		RpcDevice<RpcChannel> device = (RpcDevice<RpcChannel>) getDevice();
		if (device == null) {
			Log.error("没找到连接的设备! channel=" + this, true);
			return false;
		}

		try {
			// 启动验证
			Class<?>[] paramTypes = null;
			RpcInfoMsg infoMsg = this.getInfoMsg();
			if (device.isVerify() && infoMsg != null) {
				// RpcInfoMsg infoMsg = channel.getInfoMsg();
				// if (infoMsg == null)
				// {
				// Log.error("尚未同步远程rpc信息!");
				// return false;
				// }
				// 检测函数
				Map<String, Class<?>[]> methods = infoMsg.getMethods();
				paramTypes = (methods != null) ? methods.get(method) : null;
				if (paramTypes == null) {
					Log.error("远程调用找不到函数! method=" + method, true);
					return false;
				}
			} else {
				// 根据参数创建类型
				paramTypes = RpcUtils.createTypes(args);
			}
			return device.execute(this, method, paramTypes, args, 0);
		} catch (Exception e) {
			Log.error("execute执行失败!", e);
		}
		return false;
	}

	/** 远程执行, 无返回, 带参数. **/
	@SuppressWarnings("unchecked")
	public boolean execute(String method, Class<?>[] paramTypes, Object[] args) {
		try {
			return ((RpcDevice<RpcChannel>) getDevice()).execute(this, method, paramTypes, args, 0);
		} catch (Exception e) {
			Log.error("execute执行失败!", e);
		}
		return false;
	}

	/** 远程执行,阻塞等待参数, 传入参数(参数必须与远程调用相同, 否则产生错误). **/
	@SuppressWarnings("unchecked")
	public <R> R execute(final String method, final Class<?>[] paramTypes, final Object[] args, final Class<R> retType,
			int mode) throws Exception {
		RpcDevice<RpcChannel> device = ((RpcDevice<RpcChannel>) getDevice());
		if (device == null) {
			throw new Exception("没有绑定的rpc设备!" + this);
		}
		return device.execute(this, method, paramTypes, args, retType, mode);
	}

	/** 远程执行,阻塞调用. **/
	public <R> R execute(final String method, final Class<?>[] paramTypes, final Object[] args, final Class<R> retType)
			throws Exception {
		return this.execute(method, paramTypes, args, retType, 0);
	}

	/** 发送消息 **/
	protected abstract void write(RpcMsg msg);

	/** 获取对应设备 **/
	public abstract RpcDevice<?> getDevice();

	@SuppressWarnings("unchecked")
	public <T extends RpcChannel> RpcDevice<T> getDevice(Class<?> clazz) {
		return (RpcDevice<T>) getDevice();
	}

	/** 设置远程调用信息 **/
	protected void setInfoMsg(RpcInfoMsg infoMsg) {
		this.infoMsg = infoMsg;
	}

	/** 获取远程调用信息 **/
	protected RpcInfoMsg getInfoMsg() {
		return infoMsg;
	}

}
