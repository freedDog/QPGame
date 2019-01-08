package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.rp.goods.GoodsTempMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ExchangeMsg extends RpMessage {
	// 架号
	private int id;
	// 消耗
	private String expend;
	// 实物模板
	private GoodsTempMsg goods;

	/** 架号 */
	public int getId() {
		return id;
	}

	/** 架号 */
	public void setId(int value) {
		this.id = value;
	}

	/** 消耗 */
	public String getExpend() {
		return expend;
	}

	/** 消耗 */
	public void setExpend(String value) {
		this.expend = value;
	}

	/** 实物模板 */
	public GoodsTempMsg getGoods() {
		return goods;
	}

	/** 实物模板 */
	public void setGoods(GoodsTempMsg value) {
		this.goods = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putString(buffer, expend);
		ByteBufferHelper.putObject(buffer, goods);
	}

	public static ExchangeMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ExchangeMsg deserialize(ByteBuffer buffer) {
		ExchangeMsg messageInstance = new ExchangeMsg();
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		messageInstance.expend = ByteBufferHelper.getString(buffer);
		if (buffer.getShort() > 0) {
			messageInstance.goods = GoodsTempMsg.deserialize(buffer);
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 4;
		length += ByteBufferHelper.calcStringLength(expend);
		length += ByteBufferHelper.calcObjectLength(goods);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ExchangeMsg[");
		sb.append("id=" + id + ", ");
		sb.append("expend=" + expend + ", ");
		sb.append("goods=" + goods + ", ");
		sb.append("]");
		return sb.toString();
	}
}