package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsCountMsg extends RpMessage {
	// 实物Id
	private int templateId;
	// 实物库存
	private int count;

	/** 实物Id */
	public int getTemplateId() {
		return templateId;
	}

	/** 实物Id */
	public void setTemplateId(int value) {
		this.templateId = value;
	}

	/** 实物库存 */
	public int getCount() {
		return count;
	}

	/** 实物库存 */
	public void setCount(int value) {
		this.count = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, templateId);
		ByteBufferHelper.putInt(buffer, count);
	}

	public static GoodsCountMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsCountMsg deserialize(ByteBuffer buffer) {
		GoodsCountMsg messageInstance = new GoodsCountMsg();
		messageInstance.templateId = ByteBufferHelper.getInt(buffer);
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
		sb.append("GoodsCountMsg[");
		sb.append("templateId=" + templateId + ", ");
		sb.append("count=" + count + ", ");
		sb.append("]");
		return sb.toString();
	}
}