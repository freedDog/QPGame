package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class GoodsTempInfo extends EntityObject<GoodsTempInfo> {
	private int templateId; // 实物Id
	private String name; // 实物名称
	private int type; // 实物类型
	private float cost; // 成本价
	private float market; // 市场价
	private String desc; // 说明
	private String icon; // 图标
	private String showImage01; // 展示图01
	private String showImage02; // 展示图02
	private String detailImage; // 详细的图
	private boolean isExist; // 是否上架
	private java.util.Date createTime; // 创建时间
	private java.util.Date upTime; // 上架时间
	private java.util.Date downTime; // 下架时间
	private int count; // 库存

	/** 获取实物Id **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置实物Id **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
		this.update();
	}

	/** 获取实物名称 **/
	public String getName() {
		return name;
	}

	/** 设置实物名称 **/
	public void setName(String name) {
		if (this.name != null && this.name.equals(name)) {
			return;
		}
		this.name = name;
		this.update();
	}

	/** 获取实物类型 **/
	public int getType() {
		return type;
	}

	/** 设置实物类型 **/
	public void setType(int type) {
		if (this.type == type) {
			return;
		}
		this.type = type;
		this.update();
	}

	/** 获取成本价 **/
	public float getCost() {
		return cost;
	}

	/** 设置成本价 **/
	public void setCost(float cost) {
		if (this.cost == cost) {
			return;
		}
		this.cost = cost;
		this.update();
	}

	/** 获取市场价 **/
	public float getMarket() {
		return market;
	}

	/** 设置市场价 **/
	public void setMarket(float market) {
		if (this.market == market) {
			return;
		}
		this.market = market;
		this.update();
	}

	/** 获取说明 **/
	public String getDesc() {
		return desc;
	}

	/** 设置说明 **/
	public void setDesc(String desc) {
		if (this.desc != null && this.desc.equals(desc)) {
			return;
		}
		this.desc = desc;
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

	/** 获取展示图01 **/
	public String getShowImage01() {
		return showImage01;
	}

	/** 设置展示图01 **/
	public void setShowImage01(String showImage01) {
		if (this.showImage01 != null && this.showImage01.equals(showImage01)) {
			return;
		}
		this.showImage01 = showImage01;
		this.update();
	}

	/** 获取展示图02 **/
	public String getShowImage02() {
		return showImage02;
	}

	/** 设置展示图02 **/
	public void setShowImage02(String showImage02) {
		if (this.showImage02 != null && this.showImage02.equals(showImage02)) {
			return;
		}
		this.showImage02 = showImage02;
		this.update();
	}

	/** 获取详细的图 **/
	public String getDetailImage() {
		return detailImage;
	}

	/** 设置详细的图 **/
	public void setDetailImage(String detailImage) {
		if (this.detailImage != null && this.detailImage.equals(detailImage)) {
			return;
		}
		this.detailImage = detailImage;
		this.update();
	}

	/** 获取是否上架 **/
	public boolean getIsExist() {
		return isExist;
	}

	/** 设置是否上架 **/
	public void setIsExist(boolean isExist) {
		if (this.isExist == isExist) {
			return;
		}
		this.isExist = isExist;
		this.update();
	}

	/** 获取创建时间 **/
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/** 设置创建时间 **/
	public void setCreateTime(java.util.Date createTime) {
		if (this.createTime != null && this.createTime.equals(createTime)) {
			return;
		}
		this.createTime = createTime;
		this.update();
	}

	/** 获取上架时间 **/
	public java.util.Date getUpTime() {
		return upTime;
	}

	/** 设置上架时间 **/
	public void setUpTime(java.util.Date upTime) {
		if (this.upTime != null && this.upTime.equals(upTime)) {
			return;
		}
		this.upTime = upTime;
		this.update();
	}

	/** 获取下架时间 **/
	public java.util.Date getDownTime() {
		return downTime;
	}

	/** 设置下架时间 **/
	public void setDownTime(java.util.Date downTime) {
		if (this.downTime != null && this.downTime.equals(downTime)) {
			return;
		}
		this.downTime = downTime;
		this.update();
	}

	/** 获取库存 **/
	public int getCount() {
		return count;
	}

	/** 设置库存 **/
	public void setCount(int count) {
		if (this.count == count) {
			return;
		}
		this.count = count;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("GoodsTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("name=").append(name);
		strBdr.append(",");
		strBdr.append("type=").append(type);
		strBdr.append(",");
		strBdr.append("cost=").append(cost);
		strBdr.append(",");
		strBdr.append("market=").append(market);
		strBdr.append(",");
		strBdr.append("desc=").append(desc);
		strBdr.append(",");
		strBdr.append("icon=").append(icon);
		strBdr.append(",");
		strBdr.append("showImage01=").append(showImage01);
		strBdr.append(",");
		strBdr.append("showImage02=").append(showImage02);
		strBdr.append(",");
		strBdr.append("detailImage=").append(detailImage);
		strBdr.append(",");
		strBdr.append("isExist=").append(isExist);
		strBdr.append(",");
		strBdr.append("createTime=").append(createTime);
		strBdr.append(",");
		strBdr.append("upTime=").append(upTime);
		strBdr.append(",");
		strBdr.append("downTime=").append(downTime);
		strBdr.append(",");
		strBdr.append("count=").append(count);
		strBdr.append("]");
		return strBdr.toString();
	}
}
