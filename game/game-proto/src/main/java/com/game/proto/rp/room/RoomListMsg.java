package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.RoomMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomListMsg extends RpMessage {
	// 房间信息
	private List<RoomMsg> room = new ArrayList<RoomMsg>();

	public List<RoomMsg> getRoom() {
		return room;
	}
		
	public void addRoom(RoomMsg value) {
		this.room.add(value);
	}
		
	public void addAllRoom(List<RoomMsg> values) {
		this.room.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, room);
	}

	public static RoomListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomListMsg deserialize(ByteBuffer buffer) {
		RoomListMsg messageInstance = new RoomListMsg();
		int roomSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < roomSize; i++) {
			messageInstance.addRoom(RoomMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(room);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomListMsg[");
		sb.append("room=" + room + ", ");
		sb.append("]");
		return sb.toString();
	}
}