package com.game.proto.rp.struct;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ProductMsg extends RpMessage {
	// 商品类型
	private int type;
	// 商品的模板Id
	private int id;
	// 商品数量
	private long count;

	/** 商品类型 */
	public int getType() {
		return type;
	}

	/** 商品类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 商品的模板Id */
	public int getId() {
		return id;
	}

	/** 商品的模板Id */
	public void setId(int value) {
		this.id = value;
	}

	/** 商品数量 */
	public long getCount() {
		return count;
	}

	/** 商品数量 */
	public void setCount(long value) {
		this.count = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putLong(buffer, count);
	}

	public static ProductMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ProductMsg deserialize(ByteBuffer buffer) {
		ProductMsg messageInstance = new ProductMsg();
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
		sb.append("ProductMsg[");
		sb.append("type=" + type + ", ");
		sb.append("id=" + id + ", ");
		sb.append("count=" + count + ", ");
		sb.append("]");
		return sb.toString();
	}
}