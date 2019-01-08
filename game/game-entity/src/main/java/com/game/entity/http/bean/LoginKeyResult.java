package com.game.entity.http.bean;

import java.util.Map;

/**
 * 登陆服登陆key结果
 * LoginKeyResult.java
 * @author JiangBangMing
 * 2019年1月8日下午1:28:23
 */
public class LoginKeyResult extends HttpDataResult<String> {
	protected Map<String, String> extra; // 额外数据

	public Map<String, String> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}

}
