package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.GamePlayerMsg;
import com.game.proto.rp.room.GameRoomMsg;
import com.game.proto.rp.room.GameRoomTaskMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameRoomInitMsg extends RpMessage {
	// 房间信息
	private GameRoomMsg room;
	// 房间内玩家信息
	private List<GamePlayerMsg> player = new ArrayList<GamePlayerMsg>();
	// 房间任务
	private GameRoomTaskMsg task;
	// 最后出牌的玩家Id
	private long lastPlayerId;
	// 最后出的牌
	private List<Integer> lastCards = new ArrayList<Integer>();

	/** 房间信息 */
	public GameRoomMsg getRoom() {
		return room;
	}

	/** 房间信息 */
	public void setRoom(GameRoomMsg value) {
		this.room = value;
	}

	public List<GamePlayerMsg> getPlayer() {
		return player;
	}
		
	public void addPlayer(GamePlayerMsg value) {
		this.player.add(value);
	}
		
	public void addAllPlayer(List<GamePlayerMsg> values) {
		this.player.addAll(values);
	}

	/** 房间任务 */
	public GameRoomTaskMsg getTask() {
		return task;
	}

	/** 房间任务 */
	public void setTask(GameRoomTaskMsg value) {
		this.task = value;
	}

	/** 最后出牌的玩家Id */
	public long getLastPlayerId() {
		return lastPlayerId;
	}

	/** 最后出牌的玩家Id */
	public void setLastPlayerId(long value) {
		this.lastPlayerId = value;
	}

	public List<Integer> getLastCards() {
		return lastCards;
	}
		
	public void addLastCards(int value) {
		this.lastCards.add(value);
	}
		
	public void addAllLastCards(List<Integer> values) {
		this.lastCards.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObject(buffer, room);
		ByteBufferHelper.putObjectArray(buffer, player);
		ByteBufferHelper.putObject(buffer, task);
		ByteBufferHelper.putLong(buffer, lastPlayerId);
		ByteBufferHelper.putIntArray(buffer, lastCards);
	}

	public static GameRoomInitMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameRoomInitMsg deserialize(ByteBuffer buffer) {
		GameRoomInitMsg messageInstance = new GameRoomInitMsg();
		if (buffer.getShort() > 0) {
			messageInstance.room = GameRoomMsg.deserialize(buffer);
		}
		int playerSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < playerSize; i++) {
			messageInstance.addPlayer(GamePlayerMsg.deserialize(buffer));
		}
		if (buffer.getShort() > 0) {
			messageInstance.task = GameRoomTaskMsg.deserialize(buffer);
		}
		messageInstance.lastPlayerId = ByteBufferHelper.getLong(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.lastCards);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		length += ByteBufferHelper.calcObjectLength(room);
		length += ByteBufferHelper.calcObjectArrayLength(player);
		length += ByteBufferHelper.calcObjectLength(task);
		length += 2 + lastCards.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameRoomInitMsg[");
		sb.append("room=" + room + ", ");
		sb.append("player=" + player + ", ");
		sb.append("task=" + task + ", ");
		sb.append("lastPlayerId=" + lastPlayerId + ", ");
		sb.append("lastCards=" + lastCards + ", ");
		sb.append("]");
		return sb.toString();
	}
}