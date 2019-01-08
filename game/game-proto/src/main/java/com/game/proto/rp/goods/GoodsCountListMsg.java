package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.goods.GoodsCountMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsCountListMsg extends RpMessage {
	// 实物库存列表
	private List<GoodsCountMsg> listMsg = new ArrayList<GoodsCountMsg>();

	public List<GoodsCountMsg> getListMsg() {
		return listMsg;
	}
		
	public void addListMsg(GoodsCountMsg value) {
		this.listMsg.add(value);
	}
		
	public void addAllListMsg(List<GoodsCountMsg> values) {
		this.listMsg.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, listMsg);
	}

	public static GoodsCountListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsCountListMsg deserialize(ByteBuffer buffer) {
		GoodsCountListMsg messageInstance = new GoodsCountListMsg();
		int listMsgSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < listMsgSize; i++) {
			messageInstance.addListMsg(GoodsCountMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(listMsg);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoodsCountListMsg[");
		sb.append("listMsg=" + listMsg + ", ");
		sb.append("]");
		return sb.toString();
	}
}