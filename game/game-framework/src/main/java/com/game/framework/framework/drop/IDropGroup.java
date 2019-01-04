package com.game.framework.framework.drop;

/**
 * 掉落组
 * IDropGroup.java
 * @author JiangBangMing
 * 2019年1月3日下午2:54:52
 */
public interface IDropGroup
{
	/** 获取对应掉落Id **/
	int getDropId();

	/** 获取组ID **/
	int getGroupId();

	/** 获取掉落类型 **/
	int getType();

	/** 掉落数量(只对M抽N类有用) **/
	int getCount();
}
