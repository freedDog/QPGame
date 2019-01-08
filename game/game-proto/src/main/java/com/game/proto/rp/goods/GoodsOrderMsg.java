package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.rp.goods.GoodsTempMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsOrderMsg extends RpMessage {
	// 订单号
	private long id;
	// 订单来源
	private long orderSourceId;
	// 实物
	private GoodsTempMsg good;
	// 数量
	private int count;
	// 备注
	private String desc;
	// 收货地址
	private String shippingAddress;
	// 收货人
	private String shippingName;
	// 收货人电话
	private String shippingPhone;
	// 花费
	private String expend;
	// 订单状态
	private short State;
	// 创建时间
	private String startTime;
	// 确认地址的时间
	private String ackTime;
	// 发货的时间
	private String sendTime;
	// 收货的时间
	private String doneTime;
	// 晒单时间
	private String showTime;
	// 运单号
	private String trackingNumber;
	// 物流公司
	private String trackingName;
	// 订单描述
	private String orderDesc;
	// 订单类型
	private int orderType;

	/** 订单号 */
	public long getId() {
		return id;
	}

	/** 订单号 */
	public void setId(long value) {
		this.id = value;
	}

	/** 订单来源 */
	public long getOrderSourceId() {
		return orderSourceId;
	}

	/** 订单来源 */
	public void setOrderSourceId(long value) {
		this.orderSourceId = value;
	}

	/** 实物 */
	public GoodsTempMsg getGood() {
		return good;
	}

	/** 实物 */
	public void setGood(GoodsTempMsg value) {
		this.good = value;
	}

	/** 数量 */
	public int getCount() {
		return count;
	}

	/** 数量 */
	public void setCount(int value) {
		this.count = value;
	}

	/** 备注 */
	public String getDesc() {
		return desc;
	}

	/** 备注 */
	public void setDesc(String value) {
		this.desc = value;
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

	/** 花费 */
	public String getExpend() {
		return expend;
	}

	/** 花费 */
	public void setExpend(String value) {
		this.expend = value;
	}

	/** 订单状态 */
	public short getState() {
		return State;
	}

	/** 订单状态 */
	public void setState(short value) {
		this.State = value;
	}

	/** 创建时间 */
	public String getStartTime() {
		return startTime;
	}

	/** 创建时间 */
	public void setStartTime(String value) {
		this.startTime = value;
	}

	/** 确认地址的时间 */
	public String getAckTime() {
		return ackTime;
	}

	/** 确认地址的时间 */
	public void setAckTime(String value) {
		this.ackTime = value;
	}

	/** 发货的时间 */
	public String getSendTime() {
		return sendTime;
	}

	/** 发货的时间 */
	public void setSendTime(String value) {
		this.sendTime = value;
	}

	/** 收货的时间 */
	public String getDoneTime() {
		return doneTime;
	}

	/** 收货的时间 */
	public void setDoneTime(String value) {
		this.doneTime = value;
	}

	/** 晒单时间 */
	public String getShowTime() {
		return showTime;
	}

	/** 晒单时间 */
	public void setShowTime(String value) {
		this.showTime = value;
	}

	/** 运单号 */
	public String getTrackingNumber() {
		return trackingNumber;
	}

	/** 运单号 */
	public void setTrackingNumber(String value) {
		this.trackingNumber = value;
	}

	/** 物流公司 */
	public String getTrackingName() {
		return trackingName;
	}

	/** 物流公司 */
	public void setTrackingName(String value) {
		this.trackingName = value;
	}

	/** 订单描述 */
	public String getOrderDesc() {
		return orderDesc;
	}

	/** 订单描述 */
	public void setOrderDesc(String value) {
		this.orderDesc = value;
	}

	/** 订单类型 */
	public int getOrderType() {
		return orderType;
	}

	/** 订单类型 */
	public void setOrderType(int value) {
		this.orderType = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, id);
		ByteBufferHelper.putLong(buffer, orderSourceId);
		ByteBufferHelper.putObject(buffer, good);
		ByteBufferHelper.putInt(buffer, count);
		ByteBufferHelper.putString(buffer, desc);
		ByteBufferHelper.putString(buffer, shippingAddress);
		ByteBufferHelper.putString(buffer, shippingName);
		ByteBufferHelper.putString(buffer, shippingPhone);
		ByteBufferHelper.putString(buffer, expend);
		ByteBufferHelper.putShort(buffer, State);
		ByteBufferHelper.putString(buffer, startTime);
		ByteBufferHelper.putString(buffer, ackTime);
		ByteBufferHelper.putString(buffer, sendTime);
		ByteBufferHelper.putString(buffer, doneTime);
		ByteBufferHelper.putString(buffer, showTime);
		ByteBufferHelper.putString(buffer, trackingNumber);
		ByteBufferHelper.putString(buffer, trackingName);
		ByteBufferHelper.putString(buffer, orderDesc);
		ByteBufferHelper.putInt(buffer, orderType);
	}

	public static GoodsOrderMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsOrderMsg deserialize(ByteBuffer buffer) {
		GoodsOrderMsg messageInstance = new GoodsOrderMsg();
		messageInstance.id = ByteBufferHelper.getLong(buffer);
		messageInstance.orderSourceId = ByteBufferHelper.getLong(buffer);
		if (buffer.getShort() > 0) {
			messageInstance.good = GoodsTempMsg.deserialize(buffer);
		}
		messageInstance.count = ByteBufferHelper.getInt(buffer);
		messageInstance.desc = ByteBufferHelper.getString(buffer);
		messageInstance.shippingAddress = ByteBufferHelper.getString(buffer);
		messageInstance.shippingName = ByteBufferHelper.getString(buffer);
		messageInstance.shippingPhone = ByteBufferHelper.getString(buffer);
		messageInstance.expend = ByteBufferHelper.getString(buffer);
		messageInstance.State = ByteBufferHelper.getShort(buffer);
		messageInstance.startTime = ByteBufferHelper.getString(buffer);
		messageInstance.ackTime = ByteBufferHelper.getString(buffer);
		messageInstance.sendTime = ByteBufferHelper.getString(buffer);
		messageInstance.doneTime = ByteBufferHelper.getString(buffer);
		messageInstance.showTime = ByteBufferHelper.getString(buffer);
		messageInstance.trackingNumber = ByteBufferHelper.getString(buffer);
		messageInstance.trackingName = ByteBufferHelper.getString(buffer);
		messageInstance.orderDesc = ByteBufferHelper.getString(buffer);
		messageInstance.orderType = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 26;
		length += ByteBufferHelper.calcObjectLength(good);
		length += ByteBufferHelper.calcStringLength(desc);
		length += ByteBufferHelper.calcStringLength(shippingAddress);
		length += ByteBufferHelper.calcStringLength(shippingName);
		length += ByteBufferHelper.calcStringLength(shippingPhone);
		length += ByteBufferHelper.calcStringLength(expend);
		length += ByteBufferHelper.calcStringLength(startTime);
		length += ByteBufferHelper.calcStringLength(ackTime);
		length += ByteBufferHelper.calcStringLength(sendTime);
		length += ByteBufferHelper.calcStringLength(doneTime);
		length += ByteBufferHelper.calcStringLength(showTime);
		length += ByteBufferHelper.calcStringLength(trackingNumber);
		length += ByteBufferHelper.calcStringLength(trackingName);
		length += ByteBufferHelper.calcStringLength(orderDesc);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoodsOrderMsg[");
		sb.append("id=" + id + ", ");
		sb.append("orderSourceId=" + orderSourceId + ", ");
		sb.append("good=" + good + ", ");
		sb.append("count=" + count + ", ");
		sb.append("desc=" + desc + ", ");
		sb.append("shippingAddress=" + shippingAddress + ", ");
		sb.append("shippingName=" + shippingName + ", ");
		sb.append("shippingPhone=" + shippingPhone + ", ");
		sb.append("expend=" + expend + ", ");
		sb.append("State=" + State + ", ");
		sb.append("startTime=" + startTime + ", ");
		sb.append("ackTime=" + ackTime + ", ");
		sb.append("sendTime=" + sendTime + ", ");
		sb.append("doneTime=" + doneTime + ", ");
		sb.append("showTime=" + showTime + ", ");
		sb.append("trackingNumber=" + trackingNumber + ", ");
		sb.append("trackingName=" + trackingName + ", ");
		sb.append("orderDesc=" + orderDesc + ", ");
		sb.append("orderType=" + orderType + ", ");
		sb.append("]");
		return sb.toString();
	}
}