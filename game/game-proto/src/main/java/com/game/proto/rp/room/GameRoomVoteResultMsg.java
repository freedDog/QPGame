package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.GamePlayerVoteMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameRoomVoteResultMsg extends RpMessage {
	// 投票类型
	private int type;
	// 各个玩家的状态
	private List<GamePlayerVoteMsg> player = new ArrayList<GamePlayerVoteMsg>();
	// 是否成功
	private boolean success;

	/** 投票类型 */
	public int getType() {
		return type;
	}

	/** 投票类型 */
	public void setType(int value) {
		this.type = value;
	}

	public List<GamePlayerVoteMsg> getPlayer() {
		return player;
	}
		
	public void addPlayer(GamePlayerVoteMsg value) {
		this.player.add(value);
	}
		
	public void addAllPlayer(List<GamePlayerVoteMsg> values) {
		this.player.addAll(values);
	}

	/** 是否成功 */
	public boolean getSuccess() {
		return success;
	}

	/** 是否成功 */
	public void setSuccess(boolean value) {
		this.success = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putObjectArray(buffer, player);
		ByteBufferHelper.putBoolean(buffer, success);
	}

	public static GameRoomVoteResultMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameRoomVoteResultMsg deserialize(ByteBuffer buffer) {
		GameRoomVoteResultMsg messageInstance = new GameRoomVoteResultMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		int playerSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < playerSize; i++) {
			messageInstance.addPlayer(GamePlayerVoteMsg.deserialize(buffer));
		}
		messageInstance.success = ByteBufferHelper.getBoolean(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 5;
		length += ByteBufferHelper.calcObjectArrayLength(player);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameRoomVoteResultMsg[");
		sb.append("type=" + type + ", ");
		sb.append("player=" + player + ", ");
		sb.append("success=" + success + ", ");
		sb.append("]");
		return sb.toString();
	}
}