package com.game.base.service.constant;

/**
 * 物品类型<br>
 * ItemType.java
 * @author JiangBangMing
 * 2019年1月8日下午1:00:02
 */
public interface ItemType {
	/** 货币 **/
	int CURRENCY = 0;
	/** 常规(不可用物品) **/
	int NORMAL = 1;
	/** 可用物品 **/
	int USEABLE = 2;
	/** 自动使用的物品，获得转为使用, 和使用物品相同子类 **/
	int AUTO_USE = 3;

	/** 常规物品分类 **/
	public interface NormalType {
		/** 常规 **/
		int NORMAL = 1;
	}

	/** 可用物品分类 **/
	public interface UseableType {
		/** 宝箱, 打开后获取资源.(参数1: 打开后获得的资源, 掉落的话直接配掉落资源, 参数2:开箱子所需花费) **/
		int BOX = 1;

		/** VIP权限获取 **/
		int VIP = 2;

		/** buff获取 **/
		int BUFF = 3;
	}

}