package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.GamePlayerVoteMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameRoomVoteMsg extends RpMessage {
	// 投票类型
	private int type;
	// 各个玩家的状态
	private List<GamePlayerVoteMsg> player = new ArrayList<GamePlayerVoteMsg>();
	// 截止时间
	private int timeOut;
	// 截止默认状态, 1为默认赞成, 2为默认拒绝
	private int defaultState;

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

	/** 截止时间 */
	public int getTimeOut() {
		return timeOut;
	}

	/** 截止时间 */
	public void setTimeOut(int value) {
		this.timeOut = value;
	}

	/** 截止默认状态, 1为默认赞成, 2为默认拒绝 */
	public int getDefaultState() {
		return defaultState;
	}

	/** 截止默认状态, 1为默认赞成, 2为默认拒绝 */
	public void setDefaultState(int value) {
		this.defaultState = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putObjectArray(buffer, player);
		ByteBufferHelper.putInt(buffer, timeOut);
		ByteBufferHelper.putInt(buffer, defaultState);
	}

	public static GameRoomVoteMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameRoomVoteMsg deserialize(ByteBuffer buffer) {
		GameRoomVoteMsg messageInstance = new GameRoomVoteMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		int playerSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < playerSize; i++) {
			messageInstance.addPlayer(GamePlayerVoteMsg.deserialize(buffer));
		}
		messageInstance.timeOut = ByteBufferHelper.getInt(buffer);
		messageInstance.defaultState = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		length += ByteBufferHelper.calcObjectArrayLength(player);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameRoomVoteMsg[");
		sb.append("type=" + type + ", ");
		sb.append("player=" + player + ", ");
		sb.append("timeOut=" + timeOut + ", ");
		sb.append("defaultState=" + defaultState + ", ");
		sb.append("]");
		return sb.toString();
	}
}