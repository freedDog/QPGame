package com.game.entity.bean;

/**
 * 账号信息
 * AccountData.java
 * @author JiangBangMing
 * 2019年1月8日下午1:56:59
 */
public class AccountData {
	protected long accountId; // 账号Id
	protected String account; // 账号
	protected String platform; // 平台
	protected String name; // 账号名
	protected int sex; // 性别
	protected int random; // 随机值
	protected String modilePhone; // 绑定的手机号码
	protected String headImgUrl;// 头像
	protected String agencyNumber;// 代理码

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}

	public String getModilePhone() {
		return modilePhone;
	}

	public void setModilePhone(String modilePhone) {
		this.modilePhone = modilePhone;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getAgencyNumber() {
		return agencyNumber;
	}

	public void setAgencyNumber(String agencyNumber) {
		this.agencyNumber = agencyNumber;
	}

	@Override
	public String toString() {
		return "AccountInfo [accountId=" + accountId + ", account=" + account + ", platform=" + platform + ", name=" + name + ", sex=" + sex + ", random=" + random + ", modilePhone=" + modilePhone
				+ ",headImgUrl" + headImgUrl + ",agencyNumber" + agencyNumber + "]";
	}
}

