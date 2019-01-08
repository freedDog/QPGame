package com.game.entity.configuration;

public class RobotwinrateTempInfo {
	private int templateId; // 模板id
	private String factor; // 计算因子
	private int gameType; // 游戏类型
	private String winRate; // 机器人胜负概率(必输概率，随机概率，必赢概率)

	/** 获取模板id **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置模板id **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
	}

	/** 获取计算因子 **/
	public String getFactor() {
		return factor;
	}

	/** 设置计算因子 **/
	public void setFactor(String factor) {
		if (this.factor != null && this.factor.equals(factor)) {
			return;
		}
		this.factor = factor;
	}

	/** 获取游戏类型 **/
	public int getGameType() {
		return gameType;
	}

	/** 设置游戏类型 **/
	public void setGameType(int gameType) {
		if (this.gameType == gameType) {
			return;
		}
		this.gameType = gameType;
	}

	/** 获取机器人胜负概率(必输概率，随机概率，必赢概率) **/
	public String getWinRate() {
		return winRate;
	}

	/** 设置机器人胜负概率(必输概率，随机概率，必赢概率) **/
	public void setWinRate(String winRate) {
		if (this.winRate != null && this.winRate.equals(winRate)) {
			return;
		}
		this.winRate = winRate;
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("RobotwinrateTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("factor=").append(factor);
		strBdr.append(",");
		strBdr.append("gameType=").append(gameType);
		strBdr.append(",");
		strBdr.append("winRate=").append(winRate);
		strBdr.append("]");
		return strBdr.toString();
	}
}
