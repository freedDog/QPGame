package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class AckOrderAddrMsg extends RpMessage {
	// 订单号
	private long id;
	// 收货地址
	private String shippingAddress;
	// 收货人
	private String shippingName;
	// 收货人电话
	private String shippingPhone;

	/** 订单号 */
	public long getId() {
		return id;
	}

	/** 订单号 */
	public void setId(long value) {
		this.id = value;
	}

	/** 收货地址 */
	public String getShippingAddress() {
		return shippingAddress;
	}

	/** 收货地址 */
	public void setShippingAddress(String value) {
		this.shippingAddress = value;
	}

	/** 收货人 */
	public String getShippingName() {
		return shippingName;
	}

	/** 收货人 */
	public void setShippingName(String value) {
		this.shippingName = value;
	}

	/** 收货人电话 */
	public String getShippingPhone() {
		return shippingPhone;
	}

	/** 收货人电话 */
	public void setShippingPhone(String value) {
		this.shippingPhone = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, id);
		ByteBufferHelper.putString(buffer, shippingAddress);
		ByteBufferHelper.putString(buffer, shippingName);
		ByteBufferHelper.putString(buffer, shippingPhone);
	}

	public static AckOrderAddrMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static AckOrderAddrMsg deserialize(ByteBuffer buffer) {
		AckOrderAddrMsg messageInstance = new AckOrderAddrMsg();
		messageInstance.id = ByteBufferHelper.getLong(buffer);
		messageInstance.shippingAddress = ByteBufferHelper.getString(buffer);
		messageInstance.shippingName = ByteBufferHelper.getString(buffer);
		messageInstance.shippingPhone = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		length += ByteBufferHelper.calcStringLength(shippingAddress);
		length += ByteBufferHelper.calcStringLength(shippingName);
		length += ByteBufferHelper.calcStringLength(shippingPhone);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AckOrderAddrMsg[");
		sb.append("id=" + id + ", ");
		sb.append("shippingAddress=" + shippingAddress + ", ");
		sb.append("shippingName=" + shippingName + ", ");
		sb.append("shippingPhone=" + shippingPhone + ", ");
		sb.append("]");
		return sb.toString();
	}
}