package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomIdMsg extends RpMessage {
	// 房间类型, 0斗地主
	private int type;
	// 房间Id
	private long id;

	/** 房间类型, 0斗地主 */
	public int getType() {
		return type;
	}

	/** 房间类型, 0斗地主 */
	public void setType(int value) {
		this.type = value;
	}

	/** 房间Id */
	public long getId() {
		return id;
	}

	/** 房间Id */
	public void setId(long value) {
		this.id = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putLong(buffer, id);
	}

	public static RoomIdMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomIdMsg deserialize(ByteBuffer buffer) {
		RoomIdMsg messageInstance = new RoomIdMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.id = ByteBufferHelper.getLong(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomIdMsg[");
		sb.append("type=" + type + ", ");
		sb.append("id=" + id + ", ");
		sb.append("]");
		return sb.toString();
	}
}