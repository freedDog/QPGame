package com.game.entity.shared;

import java.util.HashMap;
import java.util.Map;

import com.game.proto.msg.RpMessage;

/**
 * 房间模块玩家信息
 * 
 * RoomPlayerInfo.java
 * @author JiangBangMing
 * 2019年1月8日下午4:35:54
 */
public class RoomPlayerInfo extends SimplePlayerInfo {

	private int banTime;
	private int playerCount;
	private RpMessage reqMsg; // 房间配置数据
	private Object data; // 通用数据，每个游戏可以根据自己的需求填入

	// 货币数量
	private Map<Integer, Long> currencys;
	// 是否使用记牌器
	private boolean buffJPQ;
	// AI等级
	private int aiLv;
	// 是否透视
	private boolean isPrespective;

	public RoomPlayerInfo() {
		currencys = new HashMap<>(6);
	}

	/** 设置货币 **/
	public void setCurrency(int currencyId, long value) {
		currencys.put(currencyId, value);
	}

	/** 获取货币 **/
	public long getCurrency(int currencyId) {
		Long value = currencys.get(currencyId);
		return (value != null) ? value : 0;
	}
	
	public Map<Integer, Long> getCurrencys() {
		return currencys;
	}

	public boolean isBuffJPQ() {
		return buffJPQ;
	}

	public void setBuffJPQ(boolean buffJPQ) {
		this.buffJPQ = buffJPQ;
	}

	public int getAiLv() {
		return aiLv;
	}

	public void setAiLv(int aiLv) {
		this.aiLv = aiLv;
	}

	public int getBanTime() {
		return banTime;
	}

	public void setBanTime(int banTime) {
		this.banTime = banTime;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public boolean isPrespective() {
		return isPrespective;
	}

	public void setPrespective(boolean isPrespective) {
		this.isPrespective = isPrespective;
	}

	public RpMessage getReqMsg() {
		return reqMsg;
	}

	public void setReqMsg(RpMessage reqMsg) {
		this.reqMsg = reqMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
