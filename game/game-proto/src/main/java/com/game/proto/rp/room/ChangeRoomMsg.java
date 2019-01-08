package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ChangeRoomMsg extends RpMessage {
	// 房间大厅Id
	private int id;
	// 过滤的房间Id
	private List<Long> filterIds = new ArrayList<Long>();

	/** 房间大厅Id */
	public int getId() {
		return id;
	}

	/** 房间大厅Id */
	public void setId(int value) {
		this.id = value;
	}

	public List<Long> getFilterIds() {
		return filterIds;
	}
		
	public void addFilterIds(long value) {
		this.filterIds.add(value);
	}
		
	public void addAllFilterIds(List<Long> values) {
		this.filterIds.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putLongArray(buffer, filterIds);
	}

	public static ChangeRoomMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ChangeRoomMsg deserialize(ByteBuffer buffer) {
		ChangeRoomMsg messageInstance = new ChangeRoomMsg();
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readLongArray(buffer, messageInstance.filterIds);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 4;
		length += 2 + filterIds.size() * 8;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ChangeRoomMsg[");
		sb.append("id=" + id + ", ");
		sb.append("filterIds=" + filterIds + ", ");
		sb.append("]");
		return sb.toString();
	}
}