package com.game.framework.framework.bean;

/**
 * 商品模板
 * IProduct.java
 * @author JiangBangMing
 * 2019年1月3日下午2:40:22
 */
public interface IProduct
{
	/** 商品类型 **/
	int getType();

	/** 商品Id **/
	int getId();

	/** 商品数量 **/
	long getCount();
}

