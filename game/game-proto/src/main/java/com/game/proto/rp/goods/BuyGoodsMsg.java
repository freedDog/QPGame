package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class BuyGoodsMsg extends RpMessage {
	// 架号
	private int id;
	// 数量
	private int count;

	/** 架号 */
	public int getId() {
		return id;
	}

	/** 架号 */
	public void setId(int value) {
		this.id = value;
	}

	/** 数量 */
	public int getCount() {
		return count;
	}

	/** 数量 */
	public void setCount(int value) {
		this.count = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putInt(buffer, count);
	}

	public static BuyGoodsMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static BuyGoodsMsg deserialize(ByteBuffer buffer) {
		BuyGoodsMsg messageInstance = new BuyGoodsMsg();
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		messageInstance.count = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BuyGoodsMsg[");
		sb.append("id=" + id + ", ");
		sb.append("count=" + count + ", ");
		sb.append("]");
		return sb.toString();
	}
}