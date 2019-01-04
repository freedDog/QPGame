package com.game.framework.framework.drop;

/**
 * 掉落对象
 * IDropElement.java
 * @author JiangBangMing
 * 2019年1月3日下午2:54:28
 */
public interface IDropElement
{
	/** 对应掉落组 **/
	int getGroupId();

	/** 获取掉落次数 **/
	int getTime();

	/** 获取掉落比率 **/
	int getRate();
}
