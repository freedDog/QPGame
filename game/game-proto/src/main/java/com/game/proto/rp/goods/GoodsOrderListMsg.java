package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.goods.GoodsOrderMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsOrderListMsg extends RpMessage {
	// 订单列表
	private List<GoodsOrderMsg> orderList = new ArrayList<GoodsOrderMsg>();
	// 删除的Id(更新删除的)
	private List<Long> removeId = new ArrayList<Long>();
	// 是否是全部订单信息
	private boolean all;

	public List<GoodsOrderMsg> getOrderList() {
		return orderList;
	}
		
	public void addOrderList(GoodsOrderMsg value) {
		this.orderList.add(value);
	}
		
	public void addAllOrderList(List<GoodsOrderMsg> values) {
		this.orderList.addAll(values);
	}

	public List<Long> getRemoveId() {
		return removeId;
	}
		
	public void addRemoveId(long value) {
		this.removeId.add(value);
	}
		
	public void addAllRemoveId(List<Long> values) {
		this.removeId.addAll(values);
	}

	/** 是否是全部订单信息 */
	public boolean getAll() {
		return all;
	}

	/** 是否是全部订单信息 */
	public void setAll(boolean value) {
		this.all = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, orderList);
		ByteBufferHelper.putLongArray(buffer, removeId);
		ByteBufferHelper.putBoolean(buffer, all);
	}

	public static GoodsOrderListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsOrderListMsg deserialize(ByteBuffer buffer) {
		GoodsOrderListMsg messageInstance = new GoodsOrderListMsg();
		int orderListSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < orderListSize; i++) {
			messageInstance.addOrderList(GoodsOrderMsg.deserialize(buffer));
		}
		ByteBufferHelper.readLongArray(buffer, messageInstance.removeId);
		messageInstance.all = ByteBufferHelper.getBoolean(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 1;
		length += ByteBufferHelper.calcObjectArrayLength(orderList);
		length += 2 + removeId.size() * 8;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoodsOrderListMsg[");
		sb.append("orderList=" + orderList + ", ");
		sb.append("removeId=" + removeId + ", ");
		sb.append("all=" + all + ", ");
		sb.append("]");
		return sb.toString();
	}
}