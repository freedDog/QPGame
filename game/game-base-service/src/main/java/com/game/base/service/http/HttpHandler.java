package com.game.base.service.http;

import com.alibaba.fastjson.JSON;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.entity.http.bean.HttpResult;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;

/**
 * http处理接口
 * HttpHandler.java
 * @author JiangBangMing
 * 2019年1月4日下午4:39:08
 */
public abstract class HttpHandler implements IHttpHandler {

	/** http异步执行 **/
	public static abstract class HttpRunnable implements Runnable {
		protected final ProxyChannel channel;
		protected final RpcCallback callback;

		public HttpRunnable(ProxyChannel channel, RpcCallback callback) {
			this.channel = channel;
			this.callback = callback;
		}

		@Override
		public final void run() {
			// 检测连接是否还激活
			if (!channel.isConnect()) {
				Log.warn("连接断开! channel=" + channel);
				return;
			}

			// 执行處理
			Object retObj = null;
			try {
				retObj = execute(channel, callback); // 返回空是个异步的处理
				if (retObj == null || retObj.equals("")) {
					return; // 内部异步返回
				}
			} catch (Exception e) {
				Log.error("http执行错误!", e);
				String errStr = LanguageSet.get(TextTempId.ID_7, e.toString());
				// 返回错误
				httpCallback(callback, 0, HttpResult.create(0, errStr));
				return;
			}

			// 返回结果
			httpCallback(callback, 1, retObj);
		}

		/** 异步回调 **/
		public static void httpCallback(RpcCallback callback, int code, Object retObj) {
			// 字符串输出
			final String retStr;
			if (String.class.isInstance(retObj)) {
				retStr = (String) retObj;
			} else {
				retStr = JSON.toJSONString(retObj);
			}

			// 返回结果
			callback.callBack(code, retStr);
		}

		/** 执行, 返回null为内部异步返回. **/
		protected abstract Object execute(ProxyChannel channel, RpcCallback callback) throws Exception;

	}
}
