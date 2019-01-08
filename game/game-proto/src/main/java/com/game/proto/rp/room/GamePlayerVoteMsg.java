package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GamePlayerVoteMsg extends RpMessage {
	// 玩家
	private long playerId;
	// 0:未操作 -1: 拒绝 1:同意
	private int state;

	/** 玩家 */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家 */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 0:未操作 -1: 拒绝 1:同意 */
	public int getState() {
		return state;
	}

	/** 0:未操作 -1: 拒绝 1:同意 */
	public void setState(int value) {
		this.state = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putInt(buffer, state);
	}

	public static GamePlayerVoteMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GamePlayerVoteMsg deserialize(ByteBuffer buffer) {
		GamePlayerVoteMsg messageInstance = new GamePlayerVoteMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.state = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GamePlayerVoteMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("state=" + state + ", ");
		sb.append("]");
		return sb.toString();
	}
}