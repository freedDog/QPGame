package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class UserInfo extends EntityObject<UserInfo> {
	private long userId; // 账号Id
	private int gameZoneId; // 区服Id
	private String account; // 账号
	private String platform; // 平台
	private java.util.Date banTime; // 封号时间
	private java.util.Date createTime; // 创建时间
	private String mobilePhone; // 绑定的电话号码

	/** 获取账号Id **/
	public long getUserId() {
		return userId;
	}

	/** 设置账号Id **/
	public void setUserId(long userId) {
		if (this.userId == userId) {
			return;
		}
		this.userId = userId;
		this.update();
	}

	/** 获取区服Id **/
	public int getGameZoneId() {
		return gameZoneId;
	}

	/** 设置区服Id **/
	public void setGameZoneId(int gameZoneId) {
		if (this.gameZoneId == gameZoneId) {
			return;
		}
		this.gameZoneId = gameZoneId;
		this.update();
	}

	/** 获取账号 **/
	public String getAccount() {
		return account;
	}

	/** 设置账号 **/
	public void setAccount(String account) {
		if (this.account != null && this.account.equals(account)) {
			return;
		}
		this.account = account;
		this.update();
	}

	/** 获取平台 **/
	public String getPlatform() {
		return platform;
	}

	/** 设置平台 **/
	public void setPlatform(String platform) {
		if (this.platform != null && this.platform.equals(platform)) {
			return;
		}
		this.platform = platform;
		this.update();
	}

	/** 获取封号时间 **/
	public java.util.Date getBanTime() {
		return banTime;
	}

	/** 设置封号时间 **/
	public void setBanTime(java.util.Date banTime) {
		if (this.banTime != null && this.banTime.equals(banTime)) {
			return;
		}
		this.banTime = banTime;
		this.update();
	}

	/** 获取创建时间 **/
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/** 设置创建时间 **/
	public void setCreateTime(java.util.Date createTime) {
		if (this.createTime != null && this.createTime.equals(createTime)) {
			return;
		}
		this.createTime = createTime;
		this.update();
	}

	/** 获取绑定的电话号码 **/
	public String getMobilePhone() {
		return mobilePhone;
	}

	/** 设置绑定的电话号码 **/
	public void setMobilePhone(String mobilePhone) {
		if (this.mobilePhone != null && this.mobilePhone.equals(mobilePhone)) {
			return;
		}
		this.mobilePhone = mobilePhone;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("UserInfo[");
		strBdr.append("userId=").append(userId);
		strBdr.append(",");
		strBdr.append("gameZoneId=").append(gameZoneId);
		strBdr.append(",");
		strBdr.append("account=").append(account);
		strBdr.append(",");
		strBdr.append("platform=").append(platform);
		strBdr.append(",");
		strBdr.append("banTime=").append(banTime);
		strBdr.append(",");
		strBdr.append("createTime=").append(createTime);
		strBdr.append(",");
		strBdr.append("mobilePhone=").append(mobilePhone);
		strBdr.append("]");
		return strBdr.toString();
	}
}