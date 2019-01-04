package com.game.entity.http.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.framework.utils.struct.result.Result;

/**
 * http返回结果<br>
 * 通用结构
 * 
 */
public class HttpResult extends Result {
	/** 创建消息 **/
	public static HttpResult create(int code, String msg) {
		HttpResult result = new HttpResult();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	/** 创建成功 **/
	public static HttpResult success() {
		return create(SUCCESS, null);
	}

	/** 创建错误消息, code=0 **/
	public static HttpResult error(String msg) {
		HttpResult result = new HttpResult();
		result.setCode(FAIL);
		result.setMsg(msg);
		return result;
	}

	@JSONField(serialize = false)
	@Override
	public boolean isSucceed() {
		return super.isSucceed();
	}

}
