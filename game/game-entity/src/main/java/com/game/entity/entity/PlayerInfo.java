package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class PlayerInfo extends EntityObject<PlayerInfo> {
	private long playerId; // 玩家Id
	private long userId; // 账号Id
	private String name; // 角色名
	private long gold; // 金币
	private long point; // 钻石
	private short sex; // 性别
	private int exp; // 经验
	private int level; // 等级
	private int type; // 玩家类型
	private short onlineState; // 在线状态
	private java.util.Date createTime; // 创建时间
	private java.util.Date updateTime; // 更新时间
	private java.util.Date loginTime; // 登陆时间
	private java.util.Date logoutTime; // 登出时间
	private int fashionId; // 时装ID
	private int vipLv; // Vip等级
	private String headImgUrl; // 玩家头像
	private String createIP; // 注册IP
	private long proxyId; // 代理码
	private String agencyNumber; // 代理码
	private long rankScore; // 
	private String safeBoxPwd; // 保险柜密码
	private int isSuperAccount; // 
	private int isAgency; // 

	/** 获取玩家Id **/
	public long getPlayerId() {
		return playerId;
	}

	/** 设置玩家Id **/
	public void setPlayerId(long playerId) {
		if (this.playerId == playerId) {
			return;
		}
		this.playerId = playerId;
		this.update();
	}

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

	/** 获取角色名 **/
	public String getName() {
		return name;
	}

	/** 设置角色名 **/
	public void setName(String name) {
		if (this.name != null && this.name.equals(name)) {
			return;
		}
		this.name = name;
		this.update();
	}

	/** 获取金币 **/
	public long getGold() {
		return gold;
	}

	/** 设置金币 **/
	public void setGold(long gold) {
		if (this.gold == gold) {
			return;
		}
		this.gold = gold;
		this.update();
	}

	/** 获取钻石 **/
	public long getPoint() {
		return point;
	}

	/** 设置钻石 **/
	public void setPoint(long point) {
		if (this.point == point) {
			return;
		}
		this.point = point;
		this.update();
	}

	/** 获取性别 **/
	public short getSex() {
		return sex;
	}

	/** 设置性别 **/
	public void setSex(short sex) {
		if (this.sex == sex) {
			return;
		}
		this.sex = sex;
		this.update();
	}

	/** 获取经验 **/
	public int getExp() {
		return exp;
	}

	/** 设置经验 **/
	public void setExp(int exp) {
		if (this.exp == exp) {
			return;
		}
		this.exp = exp;
		this.update();
	}

	/** 获取等级 **/
	public int getLevel() {
		return level;
	}

	/** 设置等级 **/
	public void setLevel(int level) {
		if (this.level == level) {
			return;
		}
		this.level = level;
		this.update();
	}

	/** 获取玩家类型 **/
	public int getType() {
		return type;
	}

	/** 设置玩家类型 **/
	public void setType(int type) {
		if (this.type == type) {
			return;
		}
		this.type = type;
		this.update();
	}

	/** 获取在线状态 **/
	public short getOnlineState() {
		return onlineState;
	}

	/** 设置在线状态 **/
	public void setOnlineState(short onlineState) {
		if (this.onlineState == onlineState) {
			return;
		}
		this.onlineState = onlineState;
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

	/** 获取更新时间 **/
	public java.util.Date getUpdateTime() {
		return updateTime;
	}

	/** 设置更新时间 **/
	public void setUpdateTime(java.util.Date updateTime) {
		if (this.updateTime != null && this.updateTime.equals(updateTime)) {
			return;
		}
		this.updateTime = updateTime;
		this.update();
	}

	/** 获取登陆时间 **/
	public java.util.Date getLoginTime() {
		return loginTime;
	}

	/** 设置登陆时间 **/
	public void setLoginTime(java.util.Date loginTime) {
		if (this.loginTime != null && this.loginTime.equals(loginTime)) {
			return;
		}
		this.loginTime = loginTime;
		this.update();
	}

	/** 获取登出时间 **/
	public java.util.Date getLogoutTime() {
		return logoutTime;
	}

	/** 设置登出时间 **/
	public void setLogoutTime(java.util.Date logoutTime) {
		if (this.logoutTime != null && this.logoutTime.equals(logoutTime)) {
			return;
		}
		this.logoutTime = logoutTime;
		this.update();
	}

	/** 获取时装ID **/
	public int getFashionId() {
		return fashionId;
	}

	/** 设置时装ID **/
	public void setFashionId(int fashionId) {
		if (this.fashionId == fashionId) {
			return;
		}
		this.fashionId = fashionId;
		this.update();
	}

	/** 获取Vip等级 **/
	public int getVipLv() {
		return vipLv;
	}

	/** 设置Vip等级 **/
	public void setVipLv(int vipLv) {
		if (this.vipLv == vipLv) {
			return;
		}
		this.vipLv = vipLv;
		this.update();
	}

	/** 获取玩家头像 **/
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	/** 设置玩家头像 **/
	public void setHeadImgUrl(String headImgUrl) {
		if (this.headImgUrl != null && this.headImgUrl.equals(headImgUrl)) {
			return;
		}
		this.headImgUrl = headImgUrl;
		this.update();
	}

	/** 获取注册IP **/
	public String getCreateIP() {
		return createIP;
	}

	/** 设置注册IP **/
	public void setCreateIP(String createIP) {
		if (this.createIP != null && this.createIP.equals(createIP)) {
			return;
		}
		this.createIP = createIP;
		this.update();
	}

	/** 获取代理码 **/
	public long getProxyId() {
		return proxyId;
	}

	/** 设置代理码 **/
	public void setProxyId(long proxyId) {
		if (this.proxyId == proxyId) {
			return;
		}
		this.proxyId = proxyId;
		this.update();
	}

	/** 获取代理码 **/
	public String getAgencyNumber() {
		return agencyNumber;
	}

	/** 设置代理码 **/
	public void setAgencyNumber(String agencyNumber) {
		if (this.agencyNumber != null && this.agencyNumber.equals(agencyNumber)) {
			return;
		}
		this.agencyNumber = agencyNumber;
		this.update();
	}

	/** 获取 **/
	public long getRankScore() {
		return rankScore;
	}

	/** 设置 **/
	public void setRankScore(long rankScore) {
		if (this.rankScore == rankScore) {
			return;
		}
		this.rankScore = rankScore;
		this.update();
	}

	/** 获取保险柜密码 **/
	public String getSafeBoxPwd() {
		return safeBoxPwd;
	}

	/** 设置保险柜密码 **/
	public void setSafeBoxPwd(String safeBoxPwd) {
		if (this.safeBoxPwd != null && this.safeBoxPwd.equals(safeBoxPwd)) {
			return;
		}
		this.safeBoxPwd = safeBoxPwd;
		this.update();
	}

	/** 获取 **/
	public int getIsSuperAccount() {
		return isSuperAccount;
	}

	/** 设置 **/
	public void setIsSuperAccount(int isSuperAccount) {
		if (this.isSuperAccount == isSuperAccount) {
			return;
		}
		this.isSuperAccount = isSuperAccount;
		this.update();
	}

	/** 获取 **/
	public int getIsAgency() {
		return isAgency;
	}

	/** 设置 **/
	public void setIsAgency(int isAgency) {
		if (this.isAgency == isAgency) {
			return;
		}
		this.isAgency = isAgency;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("PlayerInfo[");
		strBdr.append("playerId=").append(playerId);
		strBdr.append(",");
		strBdr.append("userId=").append(userId);
		strBdr.append(",");
		strBdr.append("name=").append(name);
		strBdr.append(",");
		strBdr.append("gold=").append(gold);
		strBdr.append(",");
		strBdr.append("point=").append(point);
		strBdr.append(",");
		strBdr.append("sex=").append(sex);
		strBdr.append(",");
		strBdr.append("exp=").append(exp);
		strBdr.append(",");
		strBdr.append("level=").append(level);
		strBdr.append(",");
		strBdr.append("type=").append(type);
		strBdr.append(",");
		strBdr.append("onlineState=").append(onlineState);
		strBdr.append(",");
		strBdr.append("createTime=").append(createTime);
		strBdr.append(",");
		strBdr.append("updateTime=").append(updateTime);
		strBdr.append(",");
		strBdr.append("loginTime=").append(loginTime);
		strBdr.append(",");
		strBdr.append("logoutTime=").append(logoutTime);
		strBdr.append(",");
		strBdr.append("fashionId=").append(fashionId);
		strBdr.append(",");
		strBdr.append("vipLv=").append(vipLv);
		strBdr.append(",");
		strBdr.append("headImgUrl=").append(headImgUrl);
		strBdr.append(",");
		strBdr.append("createIP=").append(createIP);
		strBdr.append(",");
		strBdr.append("proxyId=").append(proxyId);
		strBdr.append(",");
		strBdr.append("agencyNumber=").append(agencyNumber);
		strBdr.append(",");
		strBdr.append("rankScore=").append(rankScore);
		strBdr.append(",");
		strBdr.append("safeBoxPwd=").append(safeBoxPwd);
		strBdr.append(",");
		strBdr.append("isSuperAccount=").append(isSuperAccount);
		strBdr.append(",");
		strBdr.append("isAgency=").append(isAgency);
		strBdr.append("]");
		return strBdr.toString();
	}
}