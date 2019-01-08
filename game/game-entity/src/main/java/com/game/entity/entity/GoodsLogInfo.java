package com.game.entity.entity;

public class GoodsLogInfo  {
	private int id; // 记录ID
	private int templateId; // 实物ID
	private String templateName; // 实物名称
	private int type; // 类型
	private int addCount; // 
	private int subCount; // 减少
	private int nowCount; // 当前库存
	private java.util.Date changeTime; // 记录日期
	private float allMarket; // 总市场价
	private float allCost; // 总成本价
	private int byExchange; // 通过兑换
	private int bySnatch; // 通过抢宝
	private int byContest; // 通过夺宝

	/** 获取记录ID **/
	public int getId() {
		return id;
	}

	/** 设置记录ID **/
	public void setId(int id) {
		if (this.id == id) {
			return;
		}
		this.id = id;
		
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
		
	}

	/** 获取实物名称 **/
	public String getTemplateName() {
		return templateName;
	}

	/** 设置实物名称 **/
	public void setTemplateName(String templateName) {
		if (this.templateName != null && this.templateName.equals(templateName)) {
			return;
		}
		this.templateName = templateName;
		
	}

	/** 获取类型 **/
	public int getType() {
		return type;
	}

	/** 设置类型 **/
	public void setType(int type) {
		if (this.type == type) {
			return;
		}
		this.type = type;
		
	}

	/** 获取 **/
	public int getAddCount() {
		return addCount;
	}

	/** 设置 **/
	public void setAddCount(int addCount) {
		if (this.addCount == addCount) {
			return;
		}
		this.addCount = addCount;
		
	}

	/** 获取减少 **/
	public int getSubCount() {
		return subCount;
	}

	/** 设置减少 **/
	public void setSubCount(int subCount) {
		if (this.subCount == subCount) {
			return;
		}
		this.subCount = subCount;
		
	}

	/** 获取当前库存 **/
	public int getNowCount() {
		return nowCount;
	}

	/** 设置当前库存 **/
	public void setNowCount(int nowCount) {
		if (this.nowCount == nowCount) {
			return;
		}
		this.nowCount = nowCount;
		
	}

	/** 获取记录日期 **/
	public java.util.Date getChangeTime() {
		return changeTime;
	}

	/** 设置记录日期 **/
	public void setChangeTime(java.util.Date changeTime) {
		if (this.changeTime != null && this.changeTime.equals(changeTime)) {
			return;
		}
		this.changeTime = changeTime;
		
	}

	/** 获取总市场价 **/
	public float getAllMarket() {
		return allMarket;
	}

	/** 设置总市场价 **/
	public void setAllMarket(float allMarket) {
		if (this.allMarket == allMarket) {
			return;
		}
		this.allMarket = allMarket;
		
	}

	/** 获取总成本价 **/
	public float getAllCost() {
		return allCost;
	}

	/** 设置总成本价 **/
	public void setAllCost(float allCost) {
		if (this.allCost == allCost) {
			return;
		}
		this.allCost = allCost;
		
	}

	/** 获取通过兑换 **/
	public int getByExchange() {
		return byExchange;
	}

	/** 设置通过兑换 **/
	public void setByExchange(int byExchange) {
		if (this.byExchange == byExchange) {
			return;
		}
		this.byExchange = byExchange;
		
	}

	/** 获取通过抢宝 **/
	public int getBySnatch() {
		return bySnatch;
	}

	/** 设置通过抢宝 **/
	public void setBySnatch(int bySnatch) {
		if (this.bySnatch == bySnatch) {
			return;
		}
		this.bySnatch = bySnatch;
		
	}

	/** 获取通过夺宝 **/
	public int getByContest() {
		return byContest;
	}

	/** 设置通过夺宝 **/
	public void setByContest(int byContest) {
		if (this.byContest == byContest) {
			return;
		}
		this.byContest = byContest;
		
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("GoodsLogInfo[");
		strBdr.append("id=").append(id);
		strBdr.append(",");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("templateName=").append(templateName);
		strBdr.append(",");
		strBdr.append("type=").append(type);
		strBdr.append(",");
		strBdr.append("addCount=").append(addCount);
		strBdr.append(",");
		strBdr.append("subCount=").append(subCount);
		strBdr.append(",");
		strBdr.append("nowCount=").append(nowCount);
		strBdr.append(",");
		strBdr.append("changeTime=").append(changeTime);
		strBdr.append(",");
		strBdr.append("allMarket=").append(allMarket);
		strBdr.append(",");
		strBdr.append("allCost=").append(allCost);
		strBdr.append(",");
		strBdr.append("byExchange=").append(byExchange);
		strBdr.append(",");
		strBdr.append("bySnatch=").append(bySnatch);
		strBdr.append(",");
		strBdr.append("byContest=").append(byContest);
		strBdr.append("]");
		return strBdr.toString();
	}
}