package com.game.entity.shared;

/**
 * 房卡分数
 * RoomCardGamePlayerInfo.java
 * @author JiangBangMing
 * 2019年1月8日下午5:18:49
 */
public class RoomCardGamePlayerInfo {
	// 玩家头像
	private String headImgUrl;
	// 用户ID
	private long playerId;
	// 玩家名
	private String name;
	// 玩家形象
	private int fashionId;
	//分数
	private int scope;
	//胜负平
	private byte sfp;
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
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
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}
	public byte getSfp() {
		return sfp;
	}
	public void setSfp(byte sfp) {
		this.sfp = sfp;
	}
	public int getFashionId() {
		return fashionId;
	}
	public void setFashionId(int fashionId) {
		this.fashionId = fashionId;
	}
}

