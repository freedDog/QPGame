package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomPlayerRankMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 胜场
	private int win;
	// 累计分数
	private int totalScore;

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 胜场 */
	public int getWin() {
		return win;
	}

	/** 胜场 */
	public void setWin(int value) {
		this.win = value;
	}

	/** 累计分数 */
	public int getTotalScore() {
		return totalScore;
	}

	/** 累计分数 */
	public void setTotalScore(int value) {
		this.totalScore = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putInt(buffer, win);
		ByteBufferHelper.putInt(buffer, totalScore);
	}

	public static RoomPlayerRankMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomPlayerRankMsg deserialize(ByteBuffer buffer) {
		RoomPlayerRankMsg messageInstance = new RoomPlayerRankMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.win = ByteBufferHelper.getInt(buffer);
		messageInstance.totalScore = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomPlayerRankMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("win=" + win + ", ");
		sb.append("totalScore=" + totalScore + ", ");
		sb.append("]");
		return sb.toString();
	}
}