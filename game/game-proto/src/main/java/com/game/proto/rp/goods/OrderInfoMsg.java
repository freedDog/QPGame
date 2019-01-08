package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class OrderInfoMsg extends RpMessage {
	// 订单Id
	private long orderId;
	// 额外数据
	private String extData;

	/** 订单Id */
	public long getOrderId() {
		return orderId;
	}

	/** 订单Id */
	public void setOrderId(long value) {
		this.orderId = value;
	}

	/** 额外数据 */
	public String getExtData() {
		return extData;
	}

	/** 额外数据 */
	public void setExtData(String value) {
		this.extData = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, orderId);
		ByteBufferHelper.putString(buffer, extData);
	}

	public static OrderInfoMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static OrderInfoMsg deserialize(ByteBuffer buffer) {
		OrderInfoMsg messageInstance = new OrderInfoMsg();
		messageInstance.orderId = ByteBufferHelper.getLong(buffer);
		messageInstance.extData = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		length += ByteBufferHelper.calcStringLength(extData);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OrderInfoMsg[");
		sb.append("orderId=" + orderId + ", ");
		sb.append("extData=" + extData + ", ");
		sb.append("]");
		return sb.toString();
	}
}