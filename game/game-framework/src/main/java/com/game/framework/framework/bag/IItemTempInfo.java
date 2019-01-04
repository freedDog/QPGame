package com.game.framework.framework.bag;

/**
 * 物品模板
 * IItemTempInfo.java
 * @author JiangBangMing
 * 2019年1月3日下午2:36:21
 */
public interface IItemTempInfo
{
	/** 模板Id **/
	int getTemplateId();

	/** 单个格子最大堆叠数 **/
	int getMaxCount();
}

