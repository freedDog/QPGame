package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomMsg extends RpMessage {
	// 房间Id
	private long id;
	// 房间类型, 0斗地主
	private int type;

	/** 房间Id */
	public long getId() {
		return id;
	}

	/** 房间Id */
	public void setId(long value) {
		this.id = value;
	}

	/** 房间类型, 0斗地主 */
	public int getType() {
		return type;
	}

	/** 房间类型, 0斗地主 */
	public void setType(int value) {
		this.type = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, id);
		ByteBufferHelper.putInt(buffer, type);
	}

	public static RoomMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomMsg deserialize(ByteBuffer buffer) {
		RoomMsg messageInstance = new RoomMsg();
		messageInstance.id = ByteBufferHelper.getLong(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomMsg[");
		sb.append("id=" + id + ", ");
		sb.append("type=" + type + ", ");
		sb.append("]");
		return sb.toString();
	}
}