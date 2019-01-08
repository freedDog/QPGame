package com.game.proto.rp.text;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class ProductErrorMsg extends RpMessage {
	// 产品类型
	private int type;
	// 产品Id
	private int id;
	// 所需数量
	private long count;

	/** 产品类型 */
	public int getType() {
		return type;
	}

	/** 产品类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 产品Id */
	public int getId() {
		return id;
	}

	/** 产品Id */
	public void setId(int value) {
		this.id = value;
	}

	/** 所需数量 */
	public long getCount() {
		return count;
	}

	/** 所需数量 */
	public void setCount(long value) {
		this.count = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putLong(buffer, count);
	}

	public static ProductErrorMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ProductErrorMsg deserialize(ByteBuffer buffer) {
		ProductErrorMsg messageInstance = new ProductErrorMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		messageInstance.count = ByteBufferHelper.getLong(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProductErrorMsg[");
		sb.append("type=" + type + ", ");
		sb.append("id=" + id + ", ");
		sb.append("count=" + count + ", ");
		sb.append("]");
		return sb.toString();
	}
}