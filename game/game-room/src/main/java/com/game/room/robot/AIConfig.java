package com.game.room.robot;

import com.game.base.utils.RandomUtils;
import com.game.framework.component.log.Log;

/**
 * AI配置
 * 
 */
public class AIConfig {
	public static final AIConfig config = new AIConfig();

	/** 叫地主模式 **/
	public static final int MODE_CALLDZ = 0;
	/** 明牌模式 **/
	public static final int MODE_SHOWCARD = 1;
	/** 加倍模式 **/
	public static final int MODE_DOUBLERATE = 2;
	/** 出牌模式 **/
	public static final int MODE_PLAYCARD = 3;

	/** 不控制模式 **/
	public static final int MODEVALUE_NORMAL = 0;
	/** 必然false **/
	public static final int MODEVALUE_FALSE = 1;
	/** 必然true **/
	public static final int MODEVALUE_TRUE = 2;

	protected boolean enable; // 是否激活
	private int[] modes; // 模式

	public AIConfig() {
		enable = true;
		modes = new int[10];
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/** 获取模式 **/
	public int getMode(int type) {
		if (type < 0 || type > modes.length) {
			Log.error("模式超出上限!? " + type);
			return 0;
		}
		return modes[type];
	}

	/** 设置模式 **/
	public void setMode(int type, int mode) {
		if (type < 0 || type >= modes.length) {
			Log.error("模式超出上限!? " + type);
			return;
		}
		modes[type] = mode;
	}

	/** ai执行结果 **/
	public boolean toResult(int type) {
		return toResult(type, 50);
	}

	/**
	 * ai执行结果
	 * 
	 * @param n
	 *            随机情况下 n/100
	 * **/
	public boolean toResult(int type, int n) {
		return getModeResult(getMode(type), n);
	}

	/**
	 * 计算结果<br>
	 * 模式: 0为随机, 1必然false, 2必然true
	 * 
	 * @param n
	 *            N/100概率true
	 * **/
	private static boolean getModeResult(int mode, int n) {
		if (mode == MODEVALUE_FALSE) {
			return false; // 必然不处理
		} else if (mode == MODEVALUE_TRUE) {
			return true; // 必然true
		}
		// 根据模式
		return RandomUtils.randomBoolean(n, 100);
	}

	/** 随机人品模式 **/
	public static boolean randomVipRp(int vipLv) {
		int rate = getRpRate(vipLv);
		int r = RandomUtils.randomInt(100);
		return r < rate;
	}

	/** 获取人品模式概率 **/
	private static int getRpRate(int vipLv) {
		int[] rs = new int[] { 0, 10, 30, 50 };
		if (vipLv <= 0) {
			return rs[0];
		} else if (vipLv >= rs.length - 1) {
			return rs[rs.length - 1];
		}
		return rs[vipLv];
	}

	/** 随机人品模式 **/
	public static boolean randomBGFVipRp(int vipLv) {
		int rate = getBGFRpRate(vipLv);
		int r = RandomUtils.randomInt(100);
		return r < rate;
	}

	/** 获取人品模式概率 **/
	private static int getBGFRpRate(int vipLv) {
		int[] rs = new int[] { 0, 2, 6, 10 };
		if (vipLv <= 0) {
			return rs[0];
		} else if (vipLv >= rs.length - 1) {
			return rs[rs.length - 1];
		}
		return rs[vipLv];
	}
}

