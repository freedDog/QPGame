package com.game.base.service.player;

import java.util.Arrays;

public class PlayerCount {
	
	private String appName;
	private int[] playerCount; // 服务器内存人数    数组 int[0]在线总人数, int[1] 真实玩家数量
	private int roomPlayerCount; // 房间总人数
	private int roomPlayerWithoutRobet; // 房间真实玩家数量
	private int cardRoomSize; // 房卡场人数
	private int freeRoomSize; // 自有房人数
	
	
	
	public PlayerCount() {
	}


	public PlayerCount(String appName) {
		super();
		this.appName = appName;
	}


	public PlayerCount(String appName, int[] playerCount) {
		super();
		this.appName = appName;
		this.playerCount = playerCount;
	}

	public String getAppName() {
		return appName;
	}



	public void setAppName(String appName) {
		this.appName = appName;
	}



	public int[] getPlayerCount() {
		return playerCount;
	}



	public void setPlayerCount(int[] playerCount) {
		this.playerCount = playerCount;
	}
	

	public int getRoomPlayerCount() {
		return roomPlayerCount;
	}


	public void setRoomPlayerCount(int roomPlayerCount) {
		this.roomPlayerCount = roomPlayerCount;
	}

	public int getRoomPlayerWithoutRobet() {
		return roomPlayerWithoutRobet;
	}

	public void setRoomPlayerWithoutRobet(int roomPlayerWithoutRobet) {
		this.roomPlayerWithoutRobet = roomPlayerWithoutRobet;
	}

	public int getCardRoomSize() {
		return cardRoomSize;
	}


	public void setCardRoomSize(int cardRoomSize) {
		this.cardRoomSize = cardRoomSize;
	}


	public int getFreeRoomSize() {
		return freeRoomSize;
	}


	public void setFreeRoomSize(int freeRoomSize) {
		this.freeRoomSize = freeRoomSize;
	}


	@Override
	public String toString() {
		return "PlayerCount [name=" + appName + ", ptc="
				+ Arrays.toString(playerCount) + ", rpc="
				+ roomPlayerCount + ", rpwrc="
				+ roomPlayerWithoutRobet + ", crc=" + cardRoomSize
				+ ", frc=" + freeRoomSize + "]";
	}

	
}
