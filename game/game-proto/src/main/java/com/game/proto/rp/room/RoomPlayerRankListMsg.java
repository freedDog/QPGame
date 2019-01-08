package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.RoomPlayerRankMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomPlayerRankListMsg extends RpMessage {
	// 房间ID
	private long roomId;
	// 游戏类型
	private int gameType;
	// 排名数据
	private List<RoomPlayerRankMsg> rank = new ArrayList<RoomPlayerRankMsg>();

	/** 房间ID */
	public long getRoomId() {
		return roomId;
	}

	/** 房间ID */
	public void setRoomId(long value) {
		this.roomId = value;
	}

	/** 游戏类型 */
	public int getGameType() {
		return gameType;
	}

	/** 游戏类型 */
	public void setGameType(int value) {
		this.gameType = value;
	}

	public List<RoomPlayerRankMsg> getRank() {
		return rank;
	}
		
	public void addRank(RoomPlayerRankMsg value) {
		this.rank.add(value);
	}
		
	public void addAllRank(List<RoomPlayerRankMsg> values) {
		this.rank.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, roomId);
		ByteBufferHelper.putInt(buffer, gameType);
		ByteBufferHelper.putObjectArray(buffer, rank);
	}

	public static RoomPlayerRankListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomPlayerRankListMsg deserialize(ByteBuffer buffer) {
		RoomPlayerRankListMsg messageInstance = new RoomPlayerRankListMsg();
		messageInstance.roomId = ByteBufferHelper.getLong(buffer);
		messageInstance.gameType = ByteBufferHelper.getInt(buffer);
		int rankSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < rankSize; i++) {
			messageInstance.addRank(RoomPlayerRankMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		length += ByteBufferHelper.calcObjectArrayLength(rank);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomPlayerRankListMsg[");
		sb.append("roomId=" + roomId + ", ");
		sb.append("gameType=" + gameType + ", ");
		sb.append("rank=" + rank + ", ");
		sb.append("]");
		return sb.toString();
	}
}