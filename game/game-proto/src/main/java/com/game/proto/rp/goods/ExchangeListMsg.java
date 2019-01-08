package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.goods.ExchangeMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ExchangeListMsg extends RpMessage {
	// 实物列表
	private List<ExchangeMsg> goodList = new ArrayList<ExchangeMsg>();

	public List<ExchangeMsg> getGoodList() {
		return goodList;
	}
		
	public void addGoodList(ExchangeMsg value) {
		this.goodList.add(value);
	}
		
	public void addAllGoodList(List<ExchangeMsg> values) {
		this.goodList.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, goodList);
	}

	public static ExchangeListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ExchangeListMsg deserialize(ByteBuffer buffer) {
		ExchangeListMsg messageInstance = new ExchangeListMsg();
		int goodListSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < goodListSize; i++) {
			messageInstance.addGoodList(ExchangeMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(goodList);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ExchangeListMsg[");
		sb.append("goodList=" + goodList + ", ");
		sb.append("]");
		return sb.toString();
	}
}