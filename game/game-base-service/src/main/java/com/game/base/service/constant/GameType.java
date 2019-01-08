package com.game.base.service.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏类型
 * GameType.java
 * @author JiangBangMing
 * 2019年1月8日下午5:03:39
 */
public class GameType {
	public static final List<Integer> gameTypes = new ArrayList<>();
	
	/** 斗地主 **/
	public static final int LANDLORDS = 1;
	/** 红中麻将 **/
	public static final int MAHJOIN = 2;
	/** 斗金牛 **/
	public static final int BULLGOLDFIGHT = 3;
	/** 老虎机 **/
	public static final int SLOTS = 4;
	/** 国标麻将房间 **/
	public static final int GBMAHJONG = 5;
	/** 百家乐 **/
	public static final int BJL = 6;
	/** 炸金花 **/
	public static final int ZHAJINHUA = 7;
	/** 德州扑克 **/
	public static final int TEXASHOLDEM = 8;
	/** 雷州麻将 **/
	public static final int LEIZHOU = 9;
	/** 成都麻将 **/
	public static final int MJ_CHENGDU = 10;
	/** 宜宾麻将 **/
	public static final int YIBINMJ = 11;
	/** 打旋 **/
	public static final int DAXUAN = 12;
	/** 龙虎斗 **/
	public static final int LHD = 13;
	
	/** 支付宝提现功能*/
	public static final int ALIPAY = -1;
	/** 代理金提取到余额*/
	public static final int WITHDRAW = -2;
	static {
		gameTypes.add(LANDLORDS);
//		gameTypes.add(MAHJOIN);
		gameTypes.add(BULLGOLDFIGHT);
//		gameTypes.add(SLOTS);
//		gameTypes.add(GBMAHJONG);
//		gameTypes.add(BJL);
		gameTypes.add(ZHAJINHUA);
//		gameTypes.add(TEXASHOLDEM);
//		gameTypes.add(LEIZHOU);
		gameTypes.add(MJ_CHENGDU);
//		gameTypes.add(YIBINMJ);
		gameTypes.add(DAXUAN);
		gameTypes.add(LHD);
	}
	
	
	public static String getGameName(int type) {
		String retStr = "游戏";
		switch (type) {
			case LANDLORDS:
				retStr = "斗地主";
				break;
			case BULLGOLDFIGHT:
				retStr = "牛牛";
				break;
			case ZHAJINHUA:
				retStr = "炸金花";
				break;
			case MJ_CHENGDU:
				retStr = "血战到底";
				break;
			case DAXUAN:
				retStr = "扯旋儿";
				break;
			case LHD:
				retStr = "龙虎斗";
				break;
			default:
				break;
		}
		return retStr;
	}
}
