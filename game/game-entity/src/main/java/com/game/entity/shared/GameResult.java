package com.game.entity.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏结果
 * GameResult.java
 * @author JiangBangMing
 * 2019年1月8日下午3:13:16
 */
public class GameResult {
	protected int gameType; // 游戏类型
	protected int roomLocationType; // 游戏房间类型
	protected int result; // 胜负, 1为胜利, 0为失败
	protected float doubleRate; // 玩家倍率
	protected long score; // 玩家分数变化(金钱)
	protected int currencyId; // 货币类型
	protected int kindType; // 大厅种类类型
//	protected VideoRecordMsg videoMsg; // 录像数据

	protected Map<Integer, Integer> records; // 游戏记录

	protected int lobbyId; // 大厅Id

	public GameResult() {
		records = new HashMap<>();
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	/** 是否获胜 **/
	public boolean isWin() {
		return result > 0;
	}

	public Map<Integer, Integer> getRecords() {
		return records;
	}

	public void setRecords(Map<Integer, Integer> records) {
		this.records = records;
	}

	public float getDoubleRate() {
		return doubleRate;
	}

	public void setDoubleRate(float doubleRate) {
		this.doubleRate = doubleRate;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public int getLobbyId() {
		return lobbyId;
	}

	public void setLobbyId(int lobbyId) {
		this.lobbyId = lobbyId;
	}

	public int getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}

	public int getKindType() {
		return kindType;
	}

	public void setKindType(int kindType) {
		this.kindType = kindType;
	}

//	public VideoRecordMsg getVideoMsg() {
//		return videoMsg;
//	}
//
//	public void setVideoMsg(VideoRecordMsg videoMsg) {
//		this.videoMsg = videoMsg;
//	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public int getRoomLocationType() {
		return roomLocationType;
	}

	public void setRoomLocationType(int roomLocationType) {
		this.roomLocationType = roomLocationType;
	}

}

