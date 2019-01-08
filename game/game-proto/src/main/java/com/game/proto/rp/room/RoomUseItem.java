package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomUseItem extends RpMessage {
	// 物品Id
	private int itemId;
	// 是否使用
	private int enable;

	/** 物品Id */
	public int getItemId() {
		return itemId;
	}

	/** 物品Id */
	public void setItemId(int value) {
		this.itemId = value;
	}

	/** 是否使用 */
	public int getEnable() {
		return enable;
	}

	/** 是否使用 */
	public void setEnable(int value) {
		this.enable = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, itemId);
		ByteBufferHelper.putInt(buffer, enable);
	}

	public static RoomUseItem deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomUseItem deserialize(ByteBuffer buffer) {
		RoomUseItem messageInstance = new RoomUseItem();
		messageInstance.itemId = ByteBufferHelper.getInt(buffer);
		messageInstance.enable = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomUseItem[");
		sb.append("itemId=" + itemId + ", ");
		sb.append("enable=" + enable + ", ");
		sb.append("]");
		return sb.toString();
	}
}