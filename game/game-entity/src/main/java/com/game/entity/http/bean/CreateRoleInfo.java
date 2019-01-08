package com.game.entity.http.bean;

import com.game.entity.bean.Device;

/**
 * 创建角色消息
 * 
 */
public class CreateRoleInfo {
	private long userId; // 角色Id
	private int gameZoneId; // 区服Id
	private String key; // key
	private int sex; // 性别
	private String name; // 名字
	private String connectIp;// 注册IP
	private String headImgUrl;// 头像
	private String agencyNumber; // 代理码
	private Device device; // 设备信息

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getConnectIp() {
		return connectIp;
	}

	public void setConnectIp(String connectIp) {
		this.connectIp = connectIp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGameZoneId() {
		return gameZoneId;
	}

	public void setGameZoneId(int gameZoneId) {
		this.gameZoneId = gameZoneId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAgencyNumber() {
		return agencyNumber;
	}

	public void setAgencyNumber(String agencyNumber) {
		this.agencyNumber = agencyNumber;
	}

	@Override
	public String toString() {
		return "CreateRoleInfo [userId=" + userId + ", gameZoneId=" + gameZoneId + ", key=" + key + ", sex=" + sex + ", name=" + name + ",headImgUrl" + headImgUrl + ", device=" + device
				+ ",agencyNumber" + agencyNumber + "]";
	}
}

