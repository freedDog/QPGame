package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class RankDataInfo extends EntityObject<RankDataInfo> {
	private long playerId; // 玩家ID
	private int diamond; // 钻石数量
	private int fquan; // 福券数量
	private int roomCard; // 房卡数量
	private int updateTime; // 排行榜时间

	/** 获取玩家ID **/
	public long getPlayerId() {
		return playerId;
	}

	/** 设置玩家ID **/
	public void setPlayerId(long playerId) {
		if (this.playerId == playerId) {
			return;
		}
		this.playerId = playerId;
		this.update();
	}

	/** 获取钻石数量 **/
	public int getDiamond() {
		return diamond;
	}

	/** 设置钻石数量 **/
	public void setDiamond(int diamond) {
		if (this.diamond == diamond) {
			return;
		}
		this.diamond = diamond;
		this.update();
	}

	/** 获取福券数量 **/
	public int getFquan() {
		return fquan;
	}

	/** 设置福券数量 **/
	public void setFquan(int fquan) {
		if (this.fquan == fquan) {
			return;
		}
		this.fquan = fquan;
		this.update();
	}

	/** 获取房卡数量 **/
	public int getRoomCard() {
		return roomCard;
	}

	/** 设置房卡数量 **/
	public void setRoomCard(int roomCard) {
		if (this.roomCard == roomCard) {
			return;
		}
		this.roomCard = roomCard;
		this.update();
	}

	/** 获取排行榜时间 **/
	public int getUpdateTime() {
		return updateTime;
	}

	/** 设置排行榜时间 **/
	public void setUpdateTime(int updateTime) {
		if (this.updateTime == updateTime) {
			return;
		}
		this.updateTime = updateTime;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("RankDataInfo[");
		strBdr.append("playerId=").append(playerId);
		strBdr.append(",");
		strBdr.append("diamond=").append(diamond);
		strBdr.append(",");
		strBdr.append("fquan=").append(fquan);
		strBdr.append(",");
		strBdr.append("roomCard=").append(roomCard);
		strBdr.append(",");
		strBdr.append("updateTime=").append(updateTime);
		strBdr.append("]");
		return strBdr.toString();
	}
}
