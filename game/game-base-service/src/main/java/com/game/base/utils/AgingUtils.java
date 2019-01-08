package com.game.base.utils;

import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.entity.bean.ProductResult;
import com.game.framework.utils.TimeUtils;

/**
 * 时效性工具<br>
 * 用于检测物品的时效性.
 * AgingUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午3:49:34
 */
public class AgingUtils {
	/** 永久时间 **/
	public static final int TIME_FOREVER = 0;
	/** 最大时间 **/
	public static final int TIME_MAX = Integer.MAX_VALUE;

	/** 获取续时错误文本, -1:超过上限不能添加, -2:时间不够扣除 **/
	public static String getErrorText(int code, String itemName) {
		switch (code) {
		case -1:
			return LanguageSet.get(TextTempId.ID_2001, itemName);
		case -2:
			return LanguageSet.get(TextTempId.ID_2002, itemName);
		}
		return null;
	}

	/**
	 * 检测可否进行时间添加<br>
	 * 
	 * @return code<0为失败, -1:超过上限不能添加, -2:时间不够扣除
	 **/
	public static ProductResult checkChangeTime(int nowActiveTime, int changeTime) {
		// 判断当前自身是否已经是永久时间了, 无法进行修改.
		if (isForeverTime(nowActiveTime)) {
			// 永久扣除时间
			if (changeTime < 0) {
				long setTime = TIME_MAX + changeTime;
				return ProductResult.succeed(setTime);
			}
			return ProductResult.create(-1, null, 0, 0, 0);
		}

		// 检测是否转为永久时间?
		boolean forever = isForeverTime(changeTime);
		if (forever) {
			return ProductResult.succeed(TIME_FOREVER); // 可修改为永久
		}

		// 添加时间
		int nowTime = TimeUtils.getCurrentTime();
		long lastTime = nowActiveTime - nowTime;
		if (lastTime <= 0) {
			nowActiveTime = nowTime; // 这个东西之前已经失效了.
		}

		// 扣除时间处理
		if (changeTime < 0) {
			// 检测时间是否够扣除
			if (lastTime + changeTime <= 0) {
				return ProductResult.create(-2, null, 0, 0, 0);
			}
			long setTime = nowActiveTime + changeTime;
			return ProductResult.succeed(setTime); // 可以设置时间
		}

		// 添加时间
		long setTime = (long) nowActiveTime + changeTime;
		if (setTime >= TIME_MAX) {
			return ProductResult.create(-1, null, 0, 0, 0);
		}
		return ProductResult.succeed(setTime); // 可以设置时间
	}

	/** 是否有效, 检测是否过时(有有效时间时检测). **/
	public static boolean checkActive(int activeTime) {
		// 判断时效
		if (isForeverTime(activeTime)) {
			return true; // 永久
		}
		// 检测时间
		int nowTime = TimeUtils.getCurrentTime();
		return nowTime <= activeTime;
	}

	/** 是否永久有效时间 **/
	public static boolean isForeverTime(int activeTime) {
		return activeTime == TIME_FOREVER || activeTime == Integer.MAX_VALUE;
	}

	/**
	 * 更新检测时间<br>
	 * 
	 * @return 0:永久, 1:有效, -1:无效 -2:刚刚无效
	 **/
	public static int checkActiveTime(long prevTime, long nowTime, int activeTime) {
		if (isForeverTime(activeTime)) {
			return 0; // 永久
		}
		// 检测是否失效
		int prevTime0 = TimeUtils.time(prevTime);
		boolean penable = prevTime0 <= activeTime; // 之前是否激活
		if (!penable) {
			return -1; // 已经失效了
		}
		// 之前还是有效的, 检测现在失效了吗
		int nowTime0 = TimeUtils.time(nowTime);
		boolean nenable = nowTime0 <= activeTime; // 当前是否还激活
		if (nenable) {
			return 1; // 还没失效
		}
		return -2;
	}
}
