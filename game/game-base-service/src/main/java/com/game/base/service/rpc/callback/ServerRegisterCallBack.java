package com.game.base.service.rpc.callback;

import com.game.base.service.config.ServerConfig;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;

/**
 * 统一的注册服务回调消息
 * ServerRegisterCallBack.java
 * @author JiangBangMing
 * 2019年1月7日下午6:09:24
 */
public class ServerRegisterCallBack extends RpcCallback {
	protected ProxyChannel channel;

	public ServerRegisterCallBack(ProxyChannel channel) {
		this.channel = channel;
	}

	protected void onCallBack(int result, String msg, ServerConfig config) {
		if (result <= 0) {
			Log.error("注册连接失败! result=" + result + " msg=" + msg + " channel=" + channel);
			channel.close();
			return;
		}
		channel.setConfig(config);
		Log.info("注册连接成功! " + config + " channel=" + channel);
	}

	@Override
	protected void onTimeOut() {
		Log.error("注册连接超时! channel=" + channel);
		channel.close();
	}
}