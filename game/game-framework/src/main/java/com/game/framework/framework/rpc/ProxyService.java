package com.game.framework.framework.rpc;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.game.framework.component.log.Log;
import com.game.framework.component.method.DefualtInvoker;
import com.game.framework.component.method.MethodInvoker;
import com.game.framework.component.method.MethodMgr;
import com.game.framework.component.method.MethodMgr.IFilter;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.handler.IProxyHandler;
import com.game.framework.framework.rpc.handler.ProxyHandler;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcCallMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;


/**
 * rpc服务<br>
 * 用于处理所有远程访问和回调.
 * ProxyService.java
 * @author JiangBangMing
 * 2019年1月3日下午3:53:17
 */
public class ProxyService extends RpcDevice<ProxyChannel> {
	protected static ProxyService instance;
	protected IProxyHandler handler;

	public static ProxyService getInstance() {
		return instance;
	}

	public ProxyService() {
		init();
	}

	public ProxyService(IProxyHandler handler, IFilter<Rpc, Rpc.RpcFunc> filter) {
		this();
		setHandler(handler, filter);
	}

	public ProxyService(IProxyHandler handler) {
		this(handler, null);
	}

	/** 初始化, 构造时调用. **/
	protected void init() {

	}

	@SuppressWarnings("unchecked")
	public <H extends IProxyHandler> H getHandler() {
		return (H) handler;
	}

	/** 设置处理句柄 **/
	public void setHandler(IProxyHandler handler, IFilter<Rpc, Rpc.RpcFunc> filter) {
		if (handler == null) {
			return;
		}
		if (this.handler != null) {
			Log.error("已经设置过handler!", true);
			return;
		}
		this.handler = handler;
		if (ProxyHandler.class.isInstance(handler)) {
			((ProxyHandler) this.handler).setService(this);
		}
		methods.register(handler, Rpc.class, Rpc.RpcFunc.class, filter);
	}

	@Override
	protected void onConnect(ProxyChannel channel) {
		if (handler != null) {
			handler.onConnect(channel);
		}
	}

	@Override
	protected void onClose(ProxyChannel channel) {
		if (handler != null) {
			handler.onClose(channel);
		}
	}

	@Override
	protected void onCallSync(final ProxyChannel channel, final RpcCallMsg rpcMsg, final RpcMsg packet) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ProxyService.super.onCallSync(channel, rpcMsg, packet);
			}
		};
		// 提交线程处理
		ServiceMgr.execute(r);
	}

	@Override
	protected void onCall(final ProxyChannel channel, final RpcCallMsg rpcMsg, final RpcMsg packet) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ProxyService.super.onCall(channel, rpcMsg, packet);
			}
		};
		// 提交线程处理
		ServiceMgr.execute(r);
	}

	@Override
	protected void onCallback(final ProxyChannel channel, final RpcCallBackMsg callbackMsg, final RpcMsg packet) {
		// 回调不要放到线程池, 因为涉及阻塞的回调可能会卡住线程.
		ProxyService.super.onCallback(channel, callbackMsg, packet);
	}

	/** rpc客户端申请调用函数 **/
	@Override
	public Object call(ProxyChannel channel, RpcCallMsg rpcMsg, RpcMsg packet) throws Exception {
		if (rpcMsg == null) {
			return null;
		}
		String name = rpcMsg.getMethod();
		MethodInvoker<Object[]> invoker = methods.getMethodInvoker(name);
		if (invoker == null) {
			Log.error("来自" + channel + "的远程的调用失败, 找不到函数! method=" + name);
			return null;
		}
		// 执行
		Object retObj = null;
		try {
			// 获取函数参数
			Class<?>[] parameterTypes = rpcMsg.getParamTypes();
			parameterTypes = (parameterTypes == null) ? invoker.getMethod().getParameterTypes() : parameterTypes;
			// 解析数据
			Object[] objs = toObjects(channel, rpcMsg.getData(), parameterTypes, packet);
			// 执行调用
			retObj = invoker.execute(objs);
		} catch (Exception e) {
			throw new RuntimeException("来自" + channel + "的远程调用失败! method=" + name, e);
			// return null;
		}
		return retObj;
	}

	/** 注册函数 **/
	public boolean register(Object impl, IFilter<Rpc, Rpc.RpcFunc> filter) {
		return methods.register(impl, Rpc.class, Rpc.RpcFunc.class, filter);
	}

	/** 注册函数 **/
	public boolean register(String packetName, IFilter<Rpc, Rpc.RpcFunc> filter) {
		return methods.register(packetName, Rpc.class, Rpc.RpcFunc.class, filter);
	}

	/** 函数绑定管理器 **/
	protected MethodMgr<String, Object[]> methods = new MethodMgr<String, Object[]>() {
		@Override
		protected String getKey(Annotation ca, Annotation ma, Class<?> clazz, Method method) {
			return method.getName();
		}

		@Override
		protected MethodInvoker<Object[]> createMethodInvoker(Annotation ca, Annotation ma, Class<?> clazz, Object obj, Method method) {
			return new DefualtInvoker(obj, method);
		}
	};

	/** rpc接口对象 **/
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	@Inherited
	@Documented
	public @interface Rpc {

		/** rpc接口函数 **/
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.METHOD })
		@Inherited
		@Documented
		public @interface RpcFunc {
		}
	}

}
