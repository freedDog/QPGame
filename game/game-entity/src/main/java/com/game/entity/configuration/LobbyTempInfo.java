package com.game.entity.configuration;

public class LobbyTempInfo {
	private int templateId; // 大厅Id
	private String name; // 大厅名字
	private int gameType; // 游戏类型, 1斗地主
	private int isOpen; // 是否开放（0：关闭 1：开放）
	private int expend; // 每局消耗
	private int currencyLimitMin; // 资源最小限制
	private int currencyLimitMax; // 资源最大限制
	private int baseScore; // 底分
	private int currencyId; // 消耗货币Id
	private int kindType; // 大厅种类
	private String awards; // 奖励
	private String spriteId; // 图标Id,横幅Id,头像Id
	private int winExp; // 胜利增加经验
	private int lostExp; // 失败增加经验
	private String color; // 颜色值
	private int fancyEffect; // 样式特效
	private String starEffect; // 星星特效
	private int levelType; // 游戏场级，1代表低级，3代表高级，用于换皮，因为前面不能动
	private String genOnlineRoom; // 模拟在线人数用
	private String genOnlineLobby; // 模拟在线人数用
	private String rule; // 规则

	/** 获取大厅Id **/
	public int getTemplateId() {
		return templateId;
	}

	/** 设置大厅Id **/
	public void setTemplateId(int templateId) {
		if (this.templateId == templateId) {
			return;
		}
		this.templateId = templateId;
	}

	/** 获取大厅名字 **/
	public String getName() {
		return name;
	}

	/** 设置大厅名字 **/
	public void setName(String name) {
		if (this.name != null && this.name.equals(name)) {
			return;
		}
		this.name = name;
	}

	/** 获取游戏类型, 1斗地主 **/
	public int getGameType() {
		return gameType;
	}

	/** 设置游戏类型, 1斗地主 **/
	public void setGameType(int gameType) {
		if (this.gameType == gameType) {
			return;
		}
		this.gameType = gameType;
	}

	/** 获取是否开放（0：关闭 1：开放） **/
	public int getIsOpen() {
		return isOpen;
	}

	/** 设置是否开放（0：关闭 1：开放） **/
	public void setIsOpen(int isOpen) {
		if (this.isOpen == isOpen) {
			return;
		}
		this.isOpen = isOpen;
	}

	/** 获取每局消耗 **/
	public int getExpend() {
		return expend;
	}

	/** 设置每局消耗 **/
	public void setExpend(int expend) {
		if (this.expend == expend) {
			return;
		}
		this.expend = expend;
	}

	/** 获取资源最小限制 **/
	public int getCurrencyLimitMin() {
		return currencyLimitMin;
	}

	/** 设置资源最小限制 **/
	public void setCurrencyLimitMin(int currencyLimitMin) {
		if (this.currencyLimitMin == currencyLimitMin) {
			return;
		}
		this.currencyLimitMin = currencyLimitMin;
	}

	/** 获取资源最大限制 **/
	public int getCurrencyLimitMax() {
		return currencyLimitMax;
	}

	/** 设置资源最大限制 **/
	public void setCurrencyLimitMax(int currencyLimitMax) {
		if (this.currencyLimitMax == currencyLimitMax) {
			return;
		}
		this.currencyLimitMax = currencyLimitMax;
	}

	/** 获取底分 **/
	public int getBaseScore() {
		return baseScore;
	}

	/** 设置底分 **/
	public void setBaseScore(int baseScore) {
		if (this.baseScore == baseScore) {
			return;
		}
		this.baseScore = baseScore;
	}

	/** 获取消耗货币Id **/
	public int getCurrencyId() {
		return currencyId;
	}

	/** 设置消耗货币Id **/
	public void setCurrencyId(int currencyId) {
		if (this.currencyId == currencyId) {
			return;
		}
		this.currencyId = currencyId;
	}

	/** 获取大厅种类 **/
	public int getKindType() {
		return kindType;
	}

	/** 设置大厅种类 **/
	public void setKindType(int kindType) {
		if (this.kindType == kindType) {
			return;
		}
		this.kindType = kindType;
	}

	/** 获取奖励 **/
	public String getAwards() {
		return awards;
	}

	/** 设置奖励 **/
	public void setAwards(String awards) {
		if (this.awards != null && this.awards.equals(awards)) {
			return;
		}
		this.awards = awards;
	}

	/** 获取图标Id,横幅Id,头像Id **/
	public String getSpriteId() {
		return spriteId;
	}

	/** 设置图标Id,横幅Id,头像Id **/
	public void setSpriteId(String spriteId) {
		if (this.spriteId != null && this.spriteId.equals(spriteId)) {
			return;
		}
		this.spriteId = spriteId;
	}

	/** 获取胜利增加经验 **/
	public int getWinExp() {
		return winExp;
	}

	/** 设置胜利增加经验 **/
	public void setWinExp(int winExp) {
		if (this.winExp == winExp) {
			return;
		}
		this.winExp = winExp;
	}

	/** 获取失败增加经验 **/
	public int getLostExp() {
		return lostExp;
	}

	/** 设置失败增加经验 **/
	public void setLostExp(int lostExp) {
		if (this.lostExp == lostExp) {
			return;
		}
		this.lostExp = lostExp;
	}

	/** 获取颜色值 **/
	public String getColor() {
		return color;
	}

	/** 设置颜色值 **/
	public void setColor(String color) {
		if (this.color != null && this.color.equals(color)) {
			return;
		}
		this.color = color;
	}

	/** 获取样式特效 **/
	public int getFancyEffect() {
		return fancyEffect;
	}

	/** 设置样式特效 **/
	public void setFancyEffect(int fancyEffect) {
		if (this.fancyEffect == fancyEffect) {
			return;
		}
		this.fancyEffect = fancyEffect;
	}

	/** 获取星星特效 **/
	public String getStarEffect() {
		return starEffect;
	}

	/** 设置星星特效 **/
	public void setStarEffect(String starEffect) {
		if (this.starEffect != null && this.starEffect.equals(starEffect)) {
			return;
		}
		this.starEffect = starEffect;
	}

	/** 获取游戏场级，1代表低级，3代表高级，用于换皮，因为前面不能动 **/
	public int getLevelType() {
		return levelType;
	}

	/** 设置游戏场级，1代表低级，3代表高级，用于换皮，因为前面不能动 **/
	public void setLevelType(int levelType) {
		if (this.levelType == levelType) {
			return;
		}
		this.levelType = levelType;
	}

	/** 获取模拟在线人数用 **/
	public String getGenOnlineRoom() {
		return genOnlineRoom;
	}

	/** 设置模拟在线人数用 **/
	public void setGenOnlineRoom(String genOnlineRoom) {
		if (this.genOnlineRoom != null && this.genOnlineRoom.equals(genOnlineRoom)) {
			return;
		}
		this.genOnlineRoom = genOnlineRoom;
	}

	/** 获取模拟在线人数用 **/
	public String getGenOnlineLobby() {
		return genOnlineLobby;
	}

	/** 设置模拟在线人数用 **/
	public void setGenOnlineLobby(String genOnlineLobby) {
		if (this.genOnlineLobby != null && this.genOnlineLobby.equals(genOnlineLobby)) {
			return;
		}
		this.genOnlineLobby = genOnlineLobby;
	}

	/** 获取规则 **/
	public String getRule() {
		return rule;
	}

	/** 设置规则 **/
	public void setRule(String rule) {
		if (this.rule != null && this.rule.equals(rule)) {
			return;
		}
		this.rule = rule;
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("LobbyTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("name=").append(name);
		strBdr.append(",");
		strBdr.append("gameType=").append(gameType);
		strBdr.append(",");
		strBdr.append("isOpen=").append(isOpen);
		strBdr.append(",");
		strBdr.append("expend=").append(expend);
		strBdr.append(",");
		strBdr.append("currencyLimitMin=").append(currencyLimitMin);
		strBdr.append(",");
		strBdr.append("currencyLimitMax=").append(currencyLimitMax);
		strBdr.append(",");
		strBdr.append("baseScore=").append(baseScore);
		strBdr.append(",");
		strBdr.append("currencyId=").append(currencyId);
		strBdr.append(",");
		strBdr.append("kindType=").append(kindType);
		strBdr.append(",");
		strBdr.append("awards=").append(awards);
		strBdr.append(",");
		strBdr.append("spriteId=").append(spriteId);
		strBdr.append(",");
		strBdr.append("winExp=").append(winExp);
		strBdr.append(",");
		strBdr.append("lostExp=").append(lostExp);
		strBdr.append(",");
		strBdr.append("color=").append(color);
		strBdr.append(",");
		strBdr.append("fancyEffect=").append(fancyEffect);
		strBdr.append(",");
		strBdr.append("starEffect=").append(starEffect);
		strBdr.append(",");
		strBdr.append("levelType=").append(levelType);
		strBdr.append(",");
		strBdr.append("genOnlineRoom=").append(genOnlineRoom);
		strBdr.append(",");
		strBdr.append("genOnlineLobby=").append(genOnlineLobby);
		strBdr.append(",");
		strBdr.append("rule=").append(rule);
		strBdr.append("]");
		return strBdr.toString();
	}
}
