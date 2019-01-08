package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.goods.GoodsTempMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsTempListMsg extends RpMessage {
	// 实物模板
	private List<GoodsTempMsg> goods = new ArrayList<GoodsTempMsg>();

	public List<GoodsTempMsg> getGoods() {
		return goods;
	}
		
	public void addGoods(GoodsTempMsg value) {
		this.goods.add(value);
	}
		
	public void addAllGoods(List<GoodsTempMsg> values) {
		this.goods.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, goods);
	}

	public static GoodsTempListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsTempListMsg deserialize(ByteBuffer buffer) {
		GoodsTempListMsg messageInstance = new GoodsTempListMsg();
		int goodsSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < goodsSize; i++) {
			messageInstance.addGoods(GoodsTempMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(goods);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoodsTempListMsg[");
		sb.append("goods=" + goods + ", ");
		sb.append("]");
		return sb.toString();
	}
}