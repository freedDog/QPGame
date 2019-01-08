package com.game.entity.configuration;

public class TextTempInfo {
	private int templateId; // Id(共用0-999,服务器1000-99999,客户端100000-int.Max)
	private String text; // 文本
	private String desc; // 说明

	/** 获取Id(共用0-999,服务器1000-99999,客户端100000-int.Max) **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置Id(共用0-999,服务器1000-99999,客户端100000-int.Max) **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
	}

	/** 获取文本 **/
	public String getText() {
		return text;
	}

	/** 设置文本 **/
	public void setText(String text) {
		if (this.text != null && this.text.equals(text)) {
			return;
		} 
		this.text = text;
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
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("TextTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("text=").append(text);
		strBdr.append(",");
		strBdr.append("desc=").append(desc);
		strBdr.append("]");
		return strBdr.toString();
	}
}