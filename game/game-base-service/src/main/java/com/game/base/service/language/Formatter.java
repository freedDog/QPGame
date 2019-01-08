package com.game.base.service.language;

import com.game.base.service.constant.ProductType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.entity.configuration.ItemTempInfo;

/**
 * 字段格式化<br>
 * 包括星魂输出字段格式化和物品格式化
 * Formatter.java
 * @author JiangBangMing
 * 2019年1月8日下午1:03:49
 */
public final class Formatter {
	protected static final String color_red = "BC0101";
	protected static final String color_yellow = "FFFFB9";
	protected static final String colorFormat = "<color=#%s>%s</color>";

	/**
	 * 创建物品名称字符串
	 */
	public static String createItemString(int tempId) {
		ItemTempInfo tempInfo = ItemTempMgr.getTempInfo(tempId);
		if (tempInfo == null) {
			String nameStr = LanguageSet.get(TextTempId.ID_2004, ProductType.ITEM, tempId);
			return nameStr;
		}
		// 常规物品文本
		return String.format(colorFormat, color_yellow, tempInfo.getName());
		// return tempInfo.getName(); // 不要颜色了
	}
//
//	/**
//	 * 创建Fashion名称字符串
//	 */
//	public static String createFashionString(int tempId) {
//		FashionTempInfo tempInfo = FashionTempMgr.getTempInfo(tempId);
//		if (tempInfo == null) {
//			String nameStr = LanguageSet.get(TextTempId.ID_2004, ProductType.FASHION, tempId);
//			return nameStr;
//		}
//		// 常规物品文本
//		return createFashionString(tempInfo);
//	}
//
//	/**
//	 * 创建Fashion名称字符串
//	 */
//	public static String createFashionString(FashionTempInfo tempInfo) {
//		return String.format(colorFormat, color_yellow, tempInfo.getName());
//	}

//	/**
//	 * 创建buff名称字符串
//	 */
//	public static String createBuffString(int tempId) {
//		EffectTempInfo tempInfo = EffectTempMgr.getBuffTempInfo(tempId);
//		if (tempInfo == null) {
//			String nameStr = LanguageSet.get(TextTempId.ID_2004, ProductType.BUFF, tempId);
//			return nameStr;
//		}
//		// 常规物品文本
//		// return String.format(colorFormat, color_yellow, tempInfo.getName());
//		return tempInfo.getName(); // 不要颜色了
//	}
	
	/** 创建雷州麻将字符串 **/
	public static String createLZString(String text) {
		return String.format(colorFormat, color_yellow, text);
	}

}
