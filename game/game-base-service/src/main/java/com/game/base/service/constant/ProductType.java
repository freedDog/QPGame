package com.game.base.service.constant;

/**
 * 商品类型
 * ProductType.java
 * @author JiangBangMing
 * 2019年1月8日下午1:02:42
 */
public interface ProductType {
	/** 数值资源, 金币, 钻石等等, 货币ID与物品Id相通, 但是ID必须小于0 **/
	short CURRENCY = 1;

	/** 物品 **/
	short ITEM = 2;

	/** 时装 **/
	short FASHION = 3;

	/** buff **/
	short BUFF = 4;

//	/** 实物 **/
//	short GOODS = 5;

	/** 随机掉落 **/
	short DROP = 9;
}