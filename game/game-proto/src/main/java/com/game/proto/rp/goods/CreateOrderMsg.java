package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class CreateOrderMsg extends RpMessage {
	// 客户端平台
	private String platform;
	// 模板Id
	private int goodsId;
	// 额外数据
	private String extData;

	/** 客户端平台 */
	public String getPlatform() {
		return platform;
	}

	/** 客户端平台 */
	public void setPlatform(String value) {
		this.platform = value;
	}

	/** 模板Id */
	public int getGoodsId() {
		return goodsId;
	}

	/** 模板Id */
	public void setGoodsId(int value) {
		this.goodsId = value;
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
		ByteBufferHelper.putString(buffer, platform);
		ByteBufferHelper.putInt(buffer, goodsId);
		ByteBufferHelper.putString(buffer, extData);
	}

	public static CreateOrderMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static CreateOrderMsg deserialize(ByteBuffer buffer) {
		CreateOrderMsg messageInstance = new CreateOrderMsg();
		messageInstance.platform = ByteBufferHelper.getString(buffer);
		messageInstance.goodsId = ByteBufferHelper.getInt(buffer);
		messageInstance.extData = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 4;
		length += ByteBufferHelper.calcStringLength(platform);
		length += ByteBufferHelper.calcStringLength(extData);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CreateOrderMsg[");
		sb.append("platform=" + platform + ", ");
		sb.append("goodsId=" + goodsId + ", ");
		sb.append("extData=" + extData + ", ");
		sb.append("]");
		return sb.toString();
	}
}