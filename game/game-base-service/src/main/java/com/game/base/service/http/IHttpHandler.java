package com.game.base.service.http;

import java.util.Map;

import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;

/**
 * http处理接口
 * IHttpHandler.java
 * @author JiangBangMing
 * 2019年1月4日下午4:22:08
 */
public interface IHttpHandler {
	/** 处理函数，返回值为要返回给客户端显示的内容,如果返回null不处理(内部可以用别的异步处理) **/
	Object execute(Map<String, String> params, ProxyChannel channel, RpcCallback callback) throws Exception;
}
