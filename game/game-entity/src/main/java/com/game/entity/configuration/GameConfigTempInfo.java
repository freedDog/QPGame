package com.game.entity.configuration;

public class GameConfigTempInfo {
	private String key; // 配置key
	private String value; // 配置参数
	private String desc; // 说明

	/** 获取配置key **/
	public String getKey() {
		return key;
	}

	/** 设置配置key **/
	public void setKey(String key) {
		if (this.key != null && this.key.equals(key)) {
			return;
		}
		this.key = key;
	}

	/** 获取配置参数 **/
	public String getValue() {
		return value;
	}

	/** 设置配置参数 **/
	public void setValue(String value) {
		if (this.value != null && this.value.equals(value)) {
			return;
		}
		this.value = value;
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
		strBdr.append("GameConfigTempInfo[");
		strBdr.append("key=").append(key);
		strBdr.append(",");
		strBdr.append("value=").append(value);
		strBdr.append(",");
		strBdr.append("desc=").append(desc);
		strBdr.append("]");
		return strBdr.toString();
	}
}
