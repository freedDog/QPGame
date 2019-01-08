package com.game.entity.http.bean;

import java.util.Map;

import com.game.entity.bean.Device;

/**
 * 登陆信息消息
 * 
 */
public class LoginInfo {
	private String appId; // 应用Id
	private String packageId; // 包Id
	private String thirdAppId; // 第三方应用Id
	private Device device; // 设备信息
	private String exInfo; // 额外信息
	private String connectIp; // 登陆Ip
	private String loginKey; // 登陆key(测试环境下保留)
	private String location; // 定位地址
	private Map<String, String> extra; // 额外数据

	public Map<String, String> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getThirdAppId() {
		return thirdAppId;
	}

	public void setThirdAppId(String thirdAppId) {
		this.thirdAppId = thirdAppId;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getExInfo() {
		return exInfo;
	}

	public void setExInfo(String exInfo) {
		this.exInfo = exInfo;
	}

	public String getConnectIp() {
		return connectIp;
	}

	public void setConnectIp(String connectIp) {
		this.connectIp = connectIp;
	}

	@Override
	public String toString() {
		return "LoginInfo [appId=" + appId + ", packageId=" + packageId + ", thirdAppId=" + thirdAppId + ", device=" + device + ", exInfo=" + exInfo + ", connectIp=" + connectIp + "]";
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}

