package com.game.entity.http.bean;

/**
 * http返回数据结果<br>
 * 通用结构
 * HttpDataResult.java
 * @author JiangBangMing
 * 2019年1月8日下午1:27:43
 */
public class HttpDataResult<D> extends HttpResult {
	protected D data;

	public D getData() {
		return data;
	}

	public void setData(D data) {
		this.data = data;
	}

	/** 创建消息 **/
	public static <D> HttpDataResult<D> create(int code, String msg, D data) {
		HttpDataResult<D> result = new HttpDataResult<>();
		result.setCode(code);
		result.setMsg(msg);
		result.setData(data);
		return result;
	}

}