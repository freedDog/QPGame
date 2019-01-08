package com.game.entity.configuration;

import com.game.utils.DataUtils;

public class ItemTempInfo
{
	private int templateId; // Id
	private String name; // 物品名字
	private int spriteId; // 精灵Id
	private int signSpriteId; // 精灵Id
	private String rewardSpriteId; // 奖励精灵Id(id,count|id,count|...)
	private String desc; // 说明
	private int productType; // 商品类型
	private int masterType; // 主类型
	private int sonType; // 子类
	private float price; // 价钱
	private String param01; // 额外参数1
	private int param01_int; // 额外参数1-整形数据
	private String param02; // 额外参数2
	private int param02_int; // 额外参数2-整形数据
	private String param03; // 额外参数3
	private int param03_int; // 额外参数3-整形数据
	private String param04; // 额外参数4
	private int param04_int; // 额外参数4-整形数据
	private String param05; // 额外参数5
	private int param05_int; // 额外参数5-整形数据

	/** 获取Id **/
	public int getTemplateId()
	{
		return templateId;
	}

	/** 设置Id **/
	public void setTemplateId(int templateId)
	{
		if (this.templateId == templateId)
		{
			return;
		}
		this.templateId = templateId;
	}

	/** 获取物品名字 **/
	public String getName()
	{
		return name;
	}

	/** 设置物品名字 **/
	public void setName(String name)
	{
		if (this.name != null && this.name.equals(name))
		{
			return;
		}
		this.name = name;
	}

	/** 获取精灵Id **/
	public int getSpriteId()
	{
		return spriteId;
	}

	/** 设置精灵Id **/
	public void setSpriteId(int spriteId)
	{
		if (this.spriteId == spriteId)
		{
			return;
		}
		this.spriteId = spriteId;
	}

	/** 获取精灵Id **/
	public int getSignSpriteId()
	{
		return signSpriteId;
	}

	/** 设置精灵Id **/
	public void setSignSpriteId(int signSpriteId)
	{
		if (this.signSpriteId == signSpriteId)
		{
			return;
		}
		this.signSpriteId = signSpriteId;
	}

	/** 获取奖励精灵Id(id,count|id,count|...) **/
	public String getRewardSpriteId()
	{
		return rewardSpriteId;
	}

	/** 设置奖励精灵Id(id,count|id,count|...) **/
	public void setRewardSpriteId(String rewardSpriteId)
	{
		if (this.rewardSpriteId != null && this.rewardSpriteId.equals(rewardSpriteId))
		{
			return;
		}
		this.rewardSpriteId = rewardSpriteId;
	}

	/** 获取说明 **/
	public String getDesc()
	{
		return desc;
	}

	/** 设置说明 **/
	public void setDesc(String desc)
	{
		if (this.desc != null && this.desc.equals(desc))
		{
			return;
		}
		this.desc = desc;
	}

	/** 获取商品类型 **/
	public int getProductType()
	{
		return productType;
	}

	/** 设置商品类型 **/
	public void setProductType(int productType)
	{
		if (this.productType == productType)
		{
			return;
		}
		this.productType = productType;
	}

	/** 获取主类型 **/
	public int getMasterType()
	{
		return masterType;
	}

	/** 设置主类型 **/
	public void setMasterType(int masterType)
	{
		if (this.masterType == masterType)
		{
			return;
		}
		this.masterType = masterType;
	}

	/** 获取子类 **/
	public int getSonType()
	{
		return sonType;
	}

	/** 设置子类 **/
	public void setSonType(int sonType)
	{
		if (this.sonType == sonType)
		{
			return;
		}
		this.sonType = sonType;
	}

	/** 获取价钱 **/
	public float getPrice()
	{
		return price;
	}

	/** 设置价钱 **/
	public void setPrice(float price)
	{
		if (this.price == price)
		{
			return;
		}
		this.price = price;
	}

	/** 获取额外参数1 **/
	public String getParam01()
	{
		return param01;
	}

	/** 设置额外参数1 **/
	public void setParam01(Object param01)
	{
		String pstr = (param01 != null) ? param01.toString() : null;
		if (this.param01 != null && this.param01.equals(param01))
		{
			return;
		}
		this.param01 = pstr;
		this.param01_int = DataUtils.toInt(this.param01);
	}

	/** 获取额外参数1-整形参数 **/
	public int getParam01Int()
	{
		return this.param01_int;
	}

	/** 获取额外参数2 **/
	public String getParam02()
	{
		return param02;
	}

	/** 设置额外参数2 **/
	public void setParam02(Object param02)
	{
		String pstr = (param02 != null) ? param02.toString() : null;
		if (this.param02 != null && this.param02.equals(param02))
		{
			return;
		}
		this.param02 = pstr;
		this.param02_int = DataUtils.toInt(this.param02);
	}

	/** 获取额外参数2-整形参数 **/
	public int getParam02Int()
	{
		return this.param02_int;
	}

	/** 获取额外参数3 **/
	public String getParam03()
	{
		return param03;
	}

	/** 设置额外参数3 **/
	public void setParam03(Object param03)
	{
		String pstr = (param03 != null) ? param03.toString() : null;
		if (this.param03 != null && this.param03.equals(param03))
		{
			return;
		}
		this.param03 = pstr;
		this.param03_int = DataUtils.toInt(this.param03);
	}

	/** 获取额外参数3-整形参数 **/
	public int getParam03Int()
	{
		return this.param03_int;
	}

	/** 获取额外参数4 **/
	public String getParam04()
	{
		return param04;
	}

	/** 设置额外参数4 **/
	public void setParam04(Object param04)
	{
		String pstr = (param04 != null) ? param04.toString() : null;
		if (this.param04 != null && this.param04.equals(param04))
		{
			return;
		}
		this.param04 = pstr;
		this.param04_int = DataUtils.toInt(this.param04);
	}

	/** 获取额外参数4-整形参数 **/
	public int getParam04Int()
	{
		return this.param04_int;
	}

	/** 获取额外参数5 **/
	public String getParam05()
	{
		return param05;
	}

	/** 设置额外参数5 **/
	public void setParam05(Object param05)
	{
		String pstr = (param05 != null) ? param05.toString() : null;
		if (this.param05 != null && this.param05.equals(param05))
		{
			return;
		}
		this.param05 = pstr;
		this.param05_int = DataUtils.toInt(this.param05);
	}

	/** 获取额外参数5-整形参数 **/
	public int getParam05Int()
	{
		return this.param05_int;
	}

	@Override
	public String toString()
	{
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("ItemTempInfo[");
		strBdr.append("templateId=").append(templateId);
		strBdr.append(",");
		strBdr.append("name=").append(name);
		strBdr.append(",");
		strBdr.append("spriteId=").append(spriteId);
		strBdr.append(",");
		strBdr.append("signSpriteId=").append(signSpriteId);
		strBdr.append(",");
		strBdr.append("rewardSpriteId=").append(rewardSpriteId);
		strBdr.append(",");
		strBdr.append("desc=").append(desc);
		strBdr.append(",");
		strBdr.append("productType=").append(productType);
		strBdr.append(",");
		strBdr.append("masterType=").append(masterType);
		strBdr.append(",");
		strBdr.append("sonType=").append(sonType);
		strBdr.append(",");
		strBdr.append("price=").append(price);
		strBdr.append(",");
		strBdr.append("param01=").append(param01);
		strBdr.append(",");
		strBdr.append("param02=").append(param02);
		strBdr.append(",");
		strBdr.append("param03=").append(param03);
		strBdr.append(",");
		strBdr.append("param04=").append(param04);
		strBdr.append(",");
		strBdr.append("param05=").append(param05);
		strBdr.append("]");
		return strBdr.toString();
	}
}