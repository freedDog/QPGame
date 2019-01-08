package com.game.entity.bean;

/**
 * 用户设备信息
 * Device.java
 * @author JiangBangMing
 * 2019年1月8日下午1:23:26
 */
public class Device {
	private String deviceIDFA;
	private String deviceIDFV;
	private String deviceMachine;
	private String systemVersion;
	private String network;
	private String mac;
	private String gameVersion;
	private String sdkVersion;
	private String platOS;
	private String channelName;

	public String getDeviceIDFA() {
		return deviceIDFA;
	}

	public void setDeviceIDFA(String deviceIDFA) {
		this.deviceIDFA = deviceIDFA;
	}

	public String getDeviceIDFV() {
		return deviceIDFV;
	}

	public void setDeviceIDFV(String deviceIDFV) {
		this.deviceIDFV = deviceIDFV;
	}

	public String getDeviceMachine() {
		return deviceMachine;
	}

	public void setDeviceMachine(String deviceMachine) {
		this.deviceMachine = deviceMachine;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getPlatOS() {
		return platOS;
	}

	public void setPlatOS(String platOS) {
		this.platOS = platOS;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
