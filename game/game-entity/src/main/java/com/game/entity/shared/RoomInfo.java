package com.game.entity.shared;

import java.util.ArrayList;
import java.util.List;

public class RoomInfo {
	
	private long roomId; // 房间ID
	private long createTime; // 创建时间
	private int roomState; // 房间状态
	private int gameState; // 游戏状态
	private int roomlocation;	// 房间位置
	private int roomNum;	// 房间号
	private int gameType;	// 游戏类型
	private List<PlayerInfo> players; // 玩家信息
	
	public RoomInfo() {
		this.players = new ArrayList<>();
	}

	public RoomInfo(long roomId, long createTime, int roomState, int gameState,
			int roomlocation, int roomNum, int gameType) {
		this.roomId = roomId;
		this.createTime = createTime;
		this.roomState = roomState;
		this.gameState = gameState;
		this.roomlocation = roomlocation;
		this.roomNum = roomNum;
		this.gameType = gameType;
		this.players = new ArrayList<>();
	}

	public void addPlayerInfo(PlayerInfo playerInfo) {
		this.players.add(playerInfo);
	}
	
	public long getRoomId() {
		return roomId;
	}
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getRoomState() {
		return roomState;
	}

	public void setRoomState(int roomState) {
		this.roomState = roomState;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}
	
	public List<PlayerInfo> getPlayers() {
		return players;
	}
	public void setPlayers(List<PlayerInfo> players) {
		this.players = players;
	}

	public int getRoomlocation() {
		return roomlocation;
	}

	public void setRoomlocation(int roomlocation) {
		this.roomlocation = roomlocation;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}
	
	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	@Override
	public String toString() {
		return "RoomInfo [roomId=" + roomId + ", createTime=" + createTime
				+ ", roomState=" + roomState + ", gameState=" + gameState
				+ ", roomlocation=" + roomlocation + ", roomNum=" + roomNum
				+ ", gameType=" + gameType + ", players=" + players + "]";
	}

}
