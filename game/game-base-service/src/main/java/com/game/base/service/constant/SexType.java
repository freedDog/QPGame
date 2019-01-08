package com.game.base.service.constant;

/**
 * 性别类型
 * SexType.java
 * @author JiangBangMing
 * 2019年1月8日下午2:25:17
 */
public class SexType {
	/** 男人 **/
	public static final short BOY = 1;
	/** 女人 **/
	public static final short GIRL = 2;

	/** 检测性别类型是否正确 **/
	public static boolean isSex(short sex) {
		if (sex != BOY && sex != GIRL) {
			return false;
		}
		return true;
	}

	/** 性别筛选, 0为通用 **/
	public static boolean checkSex(short sex, short filtrater) {
		// 检测是否有筛选规则.
		if (filtrater == 0) {
			return true; // 没有限制
		}
		return sex == filtrater;
	}
}