package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GetGoodsListMsg extends RpMessage {
	// 实物类型
	private int type;
	// 开始值
	private int start;
	// 结束值
	private int last;

	/** 实物类型 */
	public int getType() {
		return type;
	}

	/** 实物类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 开始值 */
	public int getStart() {
		return start;
	}

	/** 开始值 */
	public void setStart(int value) {
		this.start = value;
	}

	/** 结束值 */
	public int getLast() {
		return last;
	}

	/** 结束值 */
	public void setLast(int value) {
		this.last = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, start);
		ByteBufferHelper.putInt(buffer, last);
	}

	public static GetGoodsListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GetGoodsListMsg deserialize(ByteBuffer buffer) {
		GetGoodsListMsg messageInstance = new GetGoodsListMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.start = ByteBufferHelper.getInt(buffer);
		messageInstance.last = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetGoodsListMsg[");
		sb.append("type=" + type + ", ");
		sb.append("start=" + start + ", ");
		sb.append("last=" + last + ", ");
		sb.append("]");
		return sb.toString();
	}
}