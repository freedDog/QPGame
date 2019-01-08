package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class QuickJoinRoomMsg extends RpMessage {
	// 房间大厅Id
	private int id;
	// 房间模式
	private int mode;
	// 局数 0:表示不是开房间 1:4局 2：8局 3：16局
	private int inner;
	// 参数1
	private int val;
	// 参数列表
	private List<Integer> vals = new ArrayList<Integer>();

	/** 房间大厅Id */
	public int getId() {
		return id;
	}

	/** 房间大厅Id */
	public void setId(int value) {
		this.id = value;
	}

	/** 房间模式 */
	public int getMode() {
		return mode;
	}

	/** 房间模式 */
	public void setMode(int value) {
		this.mode = value;
	}

	/** 局数 0:表示不是开房间 1:4局 2：8局 3：16局 */
	public int getInner() {
		return inner;
	}

	/** 局数 0:表示不是开房间 1:4局 2：8局 3：16局 */
	public void setInner(int value) {
		this.inner = value;
	}

	/** 参数1 */
	public int getVal() {
		return val;
	}

	/** 参数1 */
	public void setVal(int value) {
		this.val = value;
	}

	public List<Integer> getVals() {
		return vals;
	}
		
	public void addVals(int value) {
		this.vals.add(value);
	}
		
	public void addAllVals(List<Integer> values) {
		this.vals.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putInt(buffer, mode);
		ByteBufferHelper.putInt(buffer, inner);
		ByteBufferHelper.putInt(buffer, val);
		ByteBufferHelper.putIntArray(buffer, vals);
	}

	public static QuickJoinRoomMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static QuickJoinRoomMsg deserialize(ByteBuffer buffer) {
		QuickJoinRoomMsg messageInstance = new QuickJoinRoomMsg();
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		messageInstance.mode = ByteBufferHelper.getInt(buffer);
		messageInstance.inner = ByteBufferHelper.getInt(buffer);
		messageInstance.val = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.vals);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		length += 2 + vals.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("QuickJoinRoomMsg[");
		sb.append("id=" + id + ", ");
		sb.append("mode=" + mode + ", ");
		sb.append("inner=" + inner + ", ");
		sb.append("val=" + val + ", ");
		sb.append("vals=" + vals + ", ");
		sb.append("]");
		return sb.toString();
	}
}