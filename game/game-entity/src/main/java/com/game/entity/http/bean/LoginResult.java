package com.game.entity.http.bean;

import java.util.Map;

/**
 *  登陆回馈
 * LoginResult.java
 * @author JiangBangMing
 * 2019年1月8日下午1:21:59
 */
public class LoginResult extends HttpResult {
	/** 登陆成功 **/
	public static final int RESP_LOGIN_SUCC = 1;
	/** 需要注册 **/
	public static final int RESP_REGISTER = 2;
	/** 登陆失败 **/
	public static final int RESP_LOGIN_FAILD = 0;
	/** 账号被封 **/
	public static final int RESP_LOGIN_BAN = -1;

	private long userId; // 玩家Id
	private long playerId; // 账号ID
	private String key; // 登陆key
	private String host; // socket host
	private int port; // socket port
	private String sdkUID; // sdk玩家Id
	private Map<String, String> extra; // 额外数据

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSdkUID() {
		return sdkUID;
	}

	public void setSdkUID(String sdkUID) {
		this.sdkUID = sdkUID;
	}

	/** 错误返回 **/
	public static LoginResult error(String msg) {
		return create(LoginResult.RESP_LOGIN_FAILD, msg);
	}

	public Map<String, String> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}

	/** 创建 **/
	public static LoginResult create(int code, String msg) {
		LoginResult loginResult = new LoginResult();
		loginResult.setCode(code);
		loginResult.setMsg(msg);
		return loginResult;
	}
}

