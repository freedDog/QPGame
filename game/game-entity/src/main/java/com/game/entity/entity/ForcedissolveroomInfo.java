package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class ForcedissolveroomInfo extends EntityObject<ForcedissolveroomInfo> {
	private long id; // ID
	private long roomid; // 房间ID
	private int gameType; // 游戏类型
	private java.util.Date createTime; // 房间创建时间
	private int roomState; // 房间状态
	private int gameState; // 游戏状态
	private int roomNum; // 房间号
	private String betInfos; // 玩家下注情况
	private java.util.Date dissolveTime; // 解散时间
	private int roomlocation; // 房间位置

	/** 获取ID **/
	public long getId() {
		return id;
	}

	/** 设置ID **/
	public void setId(long id) {
		if (this.id == id) {
			return;
		}
		this.id = id;
		this.update();
	}

	/** 获取房间ID **/
	public long getRoomid() {
		return roomid;
	}

	/** 设置房间ID **/
	public void setRoomid(long roomid) {
		if (this.roomid == roomid) {
			return;
		}
		this.roomid = roomid;
		this.update();
	}

	/** 获取游戏类型 **/
	public int getGameType() {
		return gameType;
	}

	/** 设置游戏类型 **/
	public void setGameType(int gameType) {
		if (this.gameType == gameType) {
			return;
		}
		this.gameType = gameType;
		this.update();
	}

	/** 获取房间创建时间 **/
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/** 设置房间创建时间 **/
	public void setCreateTime(java.util.Date createTime) {
		if (this.createTime != null && this.createTime.equals(createTime)) {
			return;
		}
		this.createTime = createTime;
		this.update();
	}

	/** 获取房间状态 **/
	public int getRoomState() {
		return roomState;
	}

	/** 设置房间状态 **/
	public void setRoomState(int roomState) {
		if (this.roomState == roomState) {
			return;
		}
		this.roomState = roomState;
		this.update();
	}

	/** 获取游戏状态 **/
	public int getGameState() {
		return gameState;
	}

	/** 设置游戏状态 **/
	public void setGameState(int gameState) {
		if (this.gameState == gameState) {
			return;
		}
		this.gameState = gameState;
		this.update();
	}

	/** 获取房间号 **/
	public int getRoomNum() {
		return roomNum;
	}

	/** 设置房间号 **/
	public void setRoomNum(int roomNum) {
		if (this.roomNum == roomNum) {
			return;
		}
		this.roomNum = roomNum;
		this.update();
	}

	/** 获取玩家下注情况 **/
	public String getBetInfos() {
		return betInfos;
	}

	/** 设置玩家下注情况 **/
	public void setBetInfos(String betInfos) {
		if (this.betInfos != null && this.betInfos.equals(betInfos)) {
			return;
		}
		this.betInfos = betInfos;
		this.update();
	}

	/** 获取解散时间 **/
	public java.util.Date getDissolveTime() {
		return dissolveTime;
	}

	/** 设置解散时间 **/
	public void setDissolveTime(java.util.Date dissolveTime) {
		if (this.dissolveTime != null && this.dissolveTime.equals(dissolveTime)) {
			return;
		}
		this.dissolveTime = dissolveTime;
		this.update();
	}

	/** 获取房间位置 **/
	public int getRoomlocation() {
		return roomlocation;
	}

	/** 设置房间位置 **/
	public void setRoomlocation(int roomlocation) {
		if (this.roomlocation == roomlocation) {
			return;
		}
		this.roomlocation = roomlocation;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("ForcedissolveroomInfo[");
		strBdr.append("id=").append(id);
		strBdr.append(",");
		strBdr.append("roomid=").append(roomid);
		strBdr.append(",");
		strBdr.append("gameType=").append(gameType);
		strBdr.append(",");
		strBdr.append("createTime=").append(createTime);
		strBdr.append(",");
		strBdr.append("roomState=").append(roomState);
		strBdr.append(",");
		strBdr.append("gameState=").append(gameState);
		strBdr.append(",");
		strBdr.append("roomNum=").append(roomNum);
		strBdr.append(",");
		strBdr.append("betInfos=").append(betInfos);
		strBdr.append(",");
		strBdr.append("dissolveTime=").append(dissolveTime);
		strBdr.append(",");
		strBdr.append("roomlocation=").append(roomlocation);
		strBdr.append("]");
		return strBdr.toString();
	}
}
