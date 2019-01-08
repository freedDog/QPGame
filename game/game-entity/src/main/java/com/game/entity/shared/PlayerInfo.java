package com.game.entity.shared;

public class PlayerInfo {
	private long playerId;
	private String name;
	private long point;
	private boolean isRobot;
	
	public PlayerInfo() {
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
	public long getPoint() {
		return point;
	}
	public void setPoint(long point) {
		this.point = point;
	}
	public boolean isRobot() {
		return isRobot;
	}
	public void setRobot(boolean isRobot) {
		this.isRobot = isRobot;
	}

	@Override
	public String toString() {
		return "PlayerInfo [playerId=" + playerId + ", name=" + name
				+ ", point=" + point + ", isRobot=" + isRobot + "]";
	}
}