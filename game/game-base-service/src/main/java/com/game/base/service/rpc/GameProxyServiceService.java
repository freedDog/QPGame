package com.game.base.service.rpc;

import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.handler.IProxyHandler;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;

/**
 * 游戏rpc服务
 * GameProxyServiceService.java
 * @author JiangBangMing
 * 2019年1月4日下午3:45:01
 */
public class GameProxyServiceService extends ProxyService {
	private static ActionExecutor callbackExecutor; // 回调线程池

	public GameProxyServiceService(IProxyHandler handler) {
		super(handler, null);
	}

	@Override
	protected void init() {
		super.init();
		instance = this;
	}

	public boolean start() {
		if (callbackExecutor != null) {
			Log.error("重复启动线程!", true);
			return false;
		}

		// 启动回调线程
		int thread = Runtime.getRuntime().availableProcessors();
		callbackExecutor = new ActionExecutor("ActionExecutor-Callback", thread, thread);
		return true;
	}

	public void stop() {
		if (callbackExecutor != null) {
			callbackExecutor.stop();
			callbackExecutor = null;
		}
	}

	@Override
	protected void onCallback(final ProxyChannel channel, final RpcCallBackMsg callbackMsg, final RpcMsg packet) {
		callbackExecutor.execute(new Runnable() {
			@Override
			public void run() {
				GameProxyServiceService.super.onCallback(channel, callbackMsg, packet);
			}
		});
	}
}
