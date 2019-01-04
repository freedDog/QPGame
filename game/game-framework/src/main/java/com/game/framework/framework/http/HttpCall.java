package com.game.framework.framework.http;

import java.lang.reflect.Type;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.framework.framework.server.http.utils.HttpUtils;
import com.game.framework.utils.struct.result.Result;

/**
 * http访问器
 * HttpCall.java
 * @author JiangBangMing
 * 2019年1月3日下午2:59:35
 */
public class HttpCall {
	protected String enc; // 编码格式
	protected String callUrl; // url前缀

	public HttpCall() {
		enc = "UTF-8";
		callUrl = "";
	}

	/** 访问服务 **/
	public <P> Result call(String url, Map<String, P> params) {
		return call(url, params, Result.class);
	}

	/** 访问服务 **/
	public <P, R> R call(String url, Map<String, P> params, Type retType) {
		return call(callUrl + url, params, retType, enc);
	}

	/** 访问服务 **/
	public <P, R> R call(String url, Map<String, P> params, TypeReference<R> typeRef) {
		// Type t = typeRef.getType();
		// Log.debug(t + " " + t.getClass());
		return call(url, params, typeRef.getType());
	}

	/** 访问服务 **/
	public static <P> Result call(String url, Map<String, P> params, String enc) {
		return call(url, params, Result.class, enc);
	}

	/** 访问服务 **/
	public static <P, R> R call(String url, Map<String, P> params, Type retType, String enc) {
		// 访问
		String urlString = url;
		byte[] retData = HttpUtils.post(urlString, params);
		String retStr = HttpUtils.toString(retData, enc);
		// 转化成结果
		R result = JSON.parseObject(retStr, retType);
		return result;
	}

	/** 访问服务 **/
	public static <P, R> R call(String url, Map<String, P> params, TypeReference<R> typeRef, String enc) {
		return call(url, params, typeRef.getType(), enc);
	}

}
