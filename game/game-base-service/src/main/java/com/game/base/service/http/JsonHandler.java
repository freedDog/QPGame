package com.game.base.service.http;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.entity.http.bean.HttpResult;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.utils.StringUtils;

/**
 * http json处理接口
 * JsonHandler.java
 * @author JiangBangMing
 * 2019年1月8日下午2:12:09
 */
public abstract class JsonHandler<T> implements IHttpHandler {
	private volatile Class<T> dataClass;

	@Override
	public Object execute(Map<String, String> params, ProxyChannel channel, RpcCallback callback) {
		// 获取数据结构
		String jsonStr = params.get("data");
		if (StringUtils.isEmpty(jsonStr)) {
			return HttpResult.error(LanguageSet.get(TextTempId.ID_9, "data"));
		}
		// 处理消息
		Object retObj = null;
		try {
			// 这里不解析,从网关来的数据应该都是解码过一次的.但是我忘了为啥当初要加上个解码的.
			// jsonStr = URLDecoder.decode(jsonStr, "UTF-8");
			// 解析json
			T data = JSON.parseObject(jsonStr, getDataClass());
			// 执行
			retObj = execute(data, params, channel, callback);
		} catch (Exception e) {
			Log.error("json接口处理错误! jsonStr=" + jsonStr, e);
			return HttpResult.error(LanguageSet.get(TextTempId.ID_7, "json接口处理错误!"));
		}
		return retObj;
	}

	public abstract Object execute(T data, Map<String, String> params, ProxyChannel channel, RpcCallback callback);

	/** 获取json接口类型 **/
	@SuppressWarnings("unchecked")
	protected synchronized Class<T> getDataClass() {
		if (dataClass == null) {
			dataClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return dataClass;
	}
}