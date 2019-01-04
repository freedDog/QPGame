package com.game.framework.framework.drop;

/**
 * 掉落类型
 * 
 */
public class DropType
{
	/** 单独掉落(每个物品单独概率算掉落) **/
	public static final int SIMGLE = 1;
	/** 组合掉落-M中抽N个(不重复) **/
	public static final int COMBINATION = 2;
	/** 固定掉落 **/
	public static final int REGULAR = 3;
	/** 组合掉落-M中抽N个(可重复) **/
	public static final int COMBINATION_REPEATABLE = 4;

}
