package com.game.base.service.struct;

public class GoodsOrderInit {

	private int templateId; // 实物ID
	private int count; // 数量
	private String expend; // 花费
	private short orderType; // 订单类型
	private long orderSourceId; // 订单生成源Id
	private String orderDesc; // 订单生成说明
	private long luckNumber;// 幸运号码

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

	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("GoodsInfo[");

		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");

		strBdr.append("count=").append(count);

		strBdr.append("expend=").append(expend);
		strBdr.append(",");

		strBdr.append(",");

		return strBdr.toString();
	}

	public long getOrderSourceId() {
		return orderSourceId;
	}

	public void setOrderSourceId(long orderSourceId) {
		this.orderSourceId = orderSourceId;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

}
