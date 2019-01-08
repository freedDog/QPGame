package com.game.entity.configuration;

public class IPDataTempInfo {
	private int templateId; // id
	private String iP; // IP位置
	private String area; // 地区
	private String coordinate; // 坐标（经度，维度）

	/** 获取id **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置id **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
	}

	/** 获取IP位置 **/
	public String getIP() {
		return iP;
	}

	/** 设置IP位置 **/
	public void setIP(String iP) {
		if (this.iP != null && this.iP.equals(iP)) {
			return;
		}
		this.iP = iP;
	}

	/** 获取地区 **/
	public String getArea() {
		return area;
	}

	/** 设置地区 **/
	public void setArea(String area) {
		if (this.area != null && this.area.equals(area)) {
			return;
		}
		this.area = area;
	}

	/** 获取坐标（经度，维度） **/
	public String getCoordinate() {
		return coordinate;
	}

	/** 设置坐标（经度，维度） **/
	public void setCoordinate(String coordinate) {
		if (this.coordinate != null && this.coordinate.equals(coordinate)) {
			return;
		}
		this.coordinate = coordinate;
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("IPDataTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("iP=").append(iP);
		strBdr.append(",");
		strBdr.append("area=").append(area);
		strBdr.append(",");
		strBdr.append("coordinate=").append(coordinate);
		strBdr.append("]");
		return strBdr.toString();
	}
}