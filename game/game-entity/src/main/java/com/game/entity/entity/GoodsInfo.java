package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class GoodsInfo extends EntityObject<GoodsInfo> {
	private long playerId; // 玩家ID
	private long id; // 订单号
	private String playerName; // 用户名称
	private int templateId; // 实物ID
	private String icon; // 图标
	private String templateName; // 物品名称
	private int count; // 数量
	private String address; // 收货地址
	private String name; // 收货人
	private String phone; // 收货电话
	private String expend; // 花费
	private short state; // 订单状态
	private java.util.Date startTime; // 创建时间
	private java.util.Date ackTime; // 确认地址的时间
	private java.util.Date sendTime; // 发货的时间
	private java.util.Date doneTime; // 收货的时间
	private String trackingNumber; // 运单号
	private java.util.Date showTime; // 晒单时间
	private short orderType; // 订单类型
	private String trackingName; // 物流公司
	private long luckNumber; // 幸运号码
	private java.util.Date changeTime; // 最近一次修改的时间
	private long orderSourceId; // 订单来源Id
	private String desc; // 订单备注
	private String historyDesc; // 历史备注
	private String orderDesc; // 订单说明

	/** 获取玩家ID **/
	public long getPlayerId() {
		return playerId;
	}

	/** 设置玩家ID **/
	public void setPlayerId(long playerId) {
		if (this.playerId == playerId) {
			return;
		}
		this.playerId = playerId;
		this.update();
	}

	/** 获取订单号 **/
	public long getId() {
		return id;
	}

	/** 设置订单号 **/
	public void setId(long id) {
		if (this.id == id) {
			return;
		}
		this.id = id;
		this.update();
	}

	/** 获取用户名称 **/
	public String getPlayerName() {
		return playerName;
	}

	/** 设置用户名称 **/
	public void setPlayerName(String playerName) {
		if (this.playerName != null && this.playerName.equals(playerName)) {
			return;
		}
		this.playerName = playerName;
		this.update();
	}

	/** 获取实物ID **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置实物ID **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
		this.update();
	}

	/** 获取图标 **/
	public String getIcon() {
		return icon;
	}

	/** 设置图标 **/
	public void setIcon(String icon) {
		if (this.icon != null && this.icon.equals(icon)) {
			return;
		}
		this.icon = icon;
		this.update();
	}

	/** 获取物品名称 **/
	public String getTemplateName() {
		return templateName;
	}

	/** 设置物品名称 **/
	public void setTemplateName(String templateName) {
		if (this.templateName != null && this.templateName.equals(templateName)) {
			return;
		}
		this.templateName = templateName;
		this.update();
	}

	/** 获取数量 **/
	public int getCount() {
		return count;
	}

	/** 设置数量 **/
	public void setCount(int count) {
		if (this.count == count) {
			return;
		}
		this.count = count;
		this.update();
	}

	/** 获取收货地址 **/
	public String getAddress() {
		return address;
	}

	/** 设置收货地址 **/
	public void setAddress(String address) {
		if (this.address != null && this.address.equals(address)) {
			return;
		}
		this.address = address;
		this.update();
	}

	/** 获取收货人 **/
	public String getName() {
		return name;
	}

	/** 设置收货人 **/
	public void setName(String name) {
		if (this.name != null && this.name.equals(name)) {
			return;
		}
		this.name = name;
		this.update();
	}

	/** 获取收货电话 **/
	public String getPhone() {
		return phone;
	}

	/** 设置收货电话 **/
	public void setPhone(String phone) {
		if (this.phone != null && this.phone.equals(phone)) {
			return;
		}
		this.phone = phone;
		this.update();
	}

	/** 获取花费 **/
	public String getExpend() {
		return expend;
	}

	/** 设置花费 **/
	public void setExpend(String expend) {
		if (this.expend != null && this.expend.equals(expend)) {
			return;
		}
		this.expend = expend;
		this.update();
	}

	/** 获取订单状态 **/
	public short getState() {
		return state;
	}

	/** 设置订单状态 **/
	public void setState(short state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
		this.update();
	}

	/** 获取创建时间 **/
	public java.util.Date getStartTime() {
		return startTime;
	}

	/** 设置创建时间 **/
	public void setStartTime(java.util.Date startTime) {
		if (this.startTime != null && this.startTime.equals(startTime)) {
			return;
		}
		this.startTime = startTime;
		this.update();
	}

	/** 获取确认地址的时间 **/
	public java.util.Date getAckTime() {
		return ackTime;
	}

	/** 设置确认地址的时间 **/
	public void setAckTime(java.util.Date ackTime) {
		if (this.ackTime != null && this.ackTime.equals(ackTime)) {
			return;
		}
		this.ackTime = ackTime;
		this.update();
	}

	/** 获取发货的时间 **/
	public java.util.Date getSendTime() {
		return sendTime;
	}

	/** 设置发货的时间 **/
	public void setSendTime(java.util.Date sendTime) {
		if (this.sendTime != null && this.sendTime.equals(sendTime)) {
			return;
		}
		this.sendTime = sendTime;
		this.update();
	}

	/** 获取收货的时间 **/
	public java.util.Date getDoneTime() {
		return doneTime;
	}

	/** 设置收货的时间 **/
	public void setDoneTime(java.util.Date doneTime) {
		if (this.doneTime != null && this.doneTime.equals(doneTime)) {
			return;
		}
		this.doneTime = doneTime;
		this.update();
	}

	/** 获取运单号 **/
	public String getTrackingNumber() {
		return trackingNumber;
	}

	/** 设置运单号 **/
	public void setTrackingNumber(String trackingNumber) {
		if (this.trackingNumber != null && this.trackingNumber.equals(trackingNumber)) {
			return;
		}
		this.trackingNumber = trackingNumber;
		this.update();
	}

	/** 获取晒单时间 **/
	public java.util.Date getShowTime() {
		return showTime;
	}

	/** 设置晒单时间 **/
	public void setShowTime(java.util.Date showTime) {
		if (this.showTime != null && this.showTime.equals(showTime)) {
			return;
		}
		this.showTime = showTime;
		this.update();
	}

	/** 获取订单类型 **/
	public short getOrderType() {
		return orderType;
	}

	/** 设置订单类型 **/
	public void setOrderType(short orderType) {
		if (this.orderType == orderType) {
			return;
		}
		this.orderType = orderType;
		this.update();
	}

	/** 获取物流公司 **/
	public String getTrackingName() {
		return trackingName;
	}

	/** 设置物流公司 **/
	public void setTrackingName(String trackingName) {
		if (this.trackingName != null && this.trackingName.equals(trackingName)) {
			return;
		}
		this.trackingName = trackingName;
		this.update();
	}

	/** 获取幸运号码 **/
	public long getLuckNumber() {
		return luckNumber;
	}

	/** 设置幸运号码 **/
	public void setLuckNumber(long luckNumber) {
		if (this.luckNumber == luckNumber) {
			return;
		}
		this.luckNumber = luckNumber;
		this.update();
	}

	/** 获取最近一次修改的时间 **/
	public java.util.Date getChangeTime() {
		return changeTime;
	}

	/** 设置最近一次修改的时间 **/
	public void setChangeTime(java.util.Date changeTime) {
		if (this.changeTime != null && this.changeTime.equals(changeTime)) {
			return;
		}
		this.changeTime = changeTime;
		this.update();
	}

	/** 获取订单来源Id **/
	public long getOrderSourceId() {
		return orderSourceId;
	}

	/** 设置订单来源Id **/
	public void setOrderSourceId(long orderSourceId) {
		if (this.orderSourceId == orderSourceId) {
			return;
		}
		this.orderSourceId = orderSourceId;
		this.update();
	}

	/** 获取订单备注 **/
	public String getDesc() {
		return desc;
	}

	/** 设置订单备注 **/
	public void setDesc(String desc) {
		if (this.desc != null && this.desc.equals(desc)) {
			return;
		}
		this.desc = desc;
		this.update();
	}

	/** 获取历史备注 **/
	public String getHistoryDesc() {
		return historyDesc;
	}

	/** 设置历史备注 **/
	public void setHistoryDesc(String historyDesc) {
		if (this.historyDesc != null && this.historyDesc.equals(historyDesc)) {
			return;
		}
		this.historyDesc = historyDesc;
		this.update();
	}

	/** 获取订单说明 **/
	public String getOrderDesc() {
		return orderDesc;
	}

	/** 设置订单说明 **/
	public void setOrderDesc(String orderDesc) {
		if (this.orderDesc != null && this.orderDesc.equals(orderDesc)) {
			return;
		}
		this.orderDesc = orderDesc;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("GoodsInfo[");
		strBdr.append("playerId=").append(playerId);
		strBdr.append(",");
		strBdr.append("id=").append(id);
		strBdr.append(",");
		strBdr.append("playerName=").append(playerName);
		strBdr.append(",");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("icon=").append(icon);
		strBdr.append(",");
		strBdr.append("templateName=").append(templateName);
		strBdr.append(",");
		strBdr.append("count=").append(count);
		strBdr.append(",");
		strBdr.append("address=").append(address);
		strBdr.append(",");
		strBdr.append("name=").append(name);
		strBdr.append(",");
		strBdr.append("phone=").append(phone);
		strBdr.append(",");
		strBdr.append("expend=").append(expend);
		strBdr.append(",");
		strBdr.append("state=").append(state);
		strBdr.append(",");
		strBdr.append("startTime=").append(startTime);
		strBdr.append(",");
		strBdr.append("ackTime=").append(ackTime);
		strBdr.append(",");
		strBdr.append("sendTime=").append(sendTime);
		strBdr.append(",");
		strBdr.append("doneTime=").append(doneTime);
		strBdr.append(",");
		strBdr.append("trackingNumber=").append(trackingNumber);
		strBdr.append(",");
		strBdr.append("showTime=").append(showTime);
		strBdr.append(",");
		strBdr.append("orderType=").append(orderType);
		strBdr.append(",");
		strBdr.append("trackingName=").append(trackingName);
		strBdr.append(",");
		strBdr.append("luckNumber=").append(luckNumber);
		strBdr.append(",");
		strBdr.append("changeTime=").append(changeTime);
		strBdr.append(",");
		strBdr.append("orderSourceId=").append(orderSourceId);
		strBdr.append(",");
		strBdr.append("desc=").append(desc);
		strBdr.append(",");
		strBdr.append("historyDesc=").append(historyDesc);
		strBdr.append(",");
		strBdr.append("orderDesc=").append(orderDesc);
		strBdr.append("]");
		return strBdr.toString();
	}
}