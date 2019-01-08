package com.game.entity.shared;

/**
 * 常规玩家数据<br>
 * 
 */
public class SimplePlayerInfo {
	private long playerId; // 玩家Id
	private long userId; // 账号Id
	private String name; // 角色名
	private short sex; // 性别
	private int level; // 等级
	private int type; // 玩家类型
	private int fashionId; // 时装ID
	private int vipLv; // Vip等级
	private int titleId; // 称号Id
	private long proxyId; // 代理码
	private String headImgUrl;// 玩家头像
	private String ip; // ip地址
	private double locationX; // 定位X
	private double locationY; // 定位Y
	private int isAgency;
	private int isSuperAccount;
	private boolean visitor;
	
	public boolean isVisitor() {
		return visitor;
	}

	public void setVisitor(boolean visitor) {
		this.visitor = visitor;
	}

	public int getIsSuperAccount() {
		return isSuperAccount;
	}

	public void setIsSuperAccount(int isSuperAccount) {
		this.isSuperAccount = isSuperAccount;
	}

	public int getIsAgency() {
		return isAgency;
	}

	public void setIsAgency(int isAgency) {
		this.isAgency = isAgency;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getSex() {
		return sex;
	}

	public void setSex(short sex) {
		this.sex = sex;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFashionId() {
		return fashionId;
	}

	public void setFashionId(int fashionId) {
		this.fashionId = fashionId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getVipLv() {
		return vipLv;
	}

	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public long getProxyId() {
		return proxyId;
	}

	public void setProxyId(long proxyId) {
		this.proxyId = proxyId;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public double getLocationX() {
		return locationX;
	}

	public void setLocationX(double locationX) {
		this.locationX = locationX;
	}

	public double getLocationY() {
		return locationY;
	}

	public void setLocationY(double locationY) {
		this.locationY = locationY;
	}

	@Override
	public String toString() {
		return "SimplePlayerInfo [playerId=" + playerId + ", userId=" + userId + ", name=" + name + ", sex=" + sex + ", level=" + level + ", type=" + type + ", fashionId=" + fashionId + ", vipLv="
				+ vipLv + ", titleId=" + titleId + ", proxyId=" + proxyId + ", headImgUrl=" + headImgUrl + ", ip=" + ip + ", locationX=" + locationX + ", locationY=" + locationY + "]";
	}

}

