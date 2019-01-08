package com.game.room.constant;

/**
 * 房间参数
 * RoomConst.java
 * @author JiangBangMing
 * 2019年1月8日下午5:12:19
 */
public class RoomConst {
	/** 房间等待超时 **/
	public static final int ROOM_WAIT_TIMEOUT = 10 * 60 * 1000;
	
	/** 炸金花是否可用 **/
	public static final boolean GAME_ZJH_IS_AVALIABLE = true;
	
	/** 游戏中广播赢得钻石消息，钻石下限 **/
	public static final int NOTICE_GAME_POINT_LIMIT = 10000; // 50钻石
	
	public static final int NOTICE_LANDLORD_PRIMARY_POINT_LIMIT = 5000; // 斗地主初级场钻石限制
	public static final int NOTICE_LANDLORD_MIDDLE_POINT_LIMIT = 30000; // 斗地主初级场钻石限制
	public static final int NOTICE_LANDLORD_SENIOR_POINT_LIMIT = 50000; // 斗地主初级场钻石限制
	public static final int NOTICE_LANDLORD_ROOM_CARD_POINT_LIMIT = 30000; // 斗地主房卡场钻石限制
	
	public static final int NOTICE_BULLGOLDFIGHT_PRIMARY_POINT_LIMIT = 5000; // 斗金牛初级场钻石限制
	public static final int NOTICE_BULLGOLDFIGHT_MIDDLE_POINT_LIMIT = 30000; // 斗金牛初级场钻石限制
	public static final int NOTICE_BULLGOLDFIGHT_SENIOR_POINT_LIMIT = 50000; // 斗金牛初级场钻石限制
	public static final int NOTICE_BULLGOLDFIGHT_ROOM_CARD_POINT_LIMIT = 30000; // 斗金牛房卡场钻石限制
	
	public static final int NOTICE_DAXUAN_PRIMARY_POINT_LIMIT = 5000; // 扯旋初级场钻石限制
	public static final int NOTICE_DAXUAN_MIDDLE_POINT_LIMIT = 30000; // 扯旋初级场钻石限制
	public static final int NOTICE_DAXUAN_SENIOR_POINT_LIMIT = 50000; // 扯旋初级场钻石限制
	public static final int NOTICE_DAXUAN_ROOM_CARD_POINT_LIMIT = 30000; // 扯旋房卡场钻石限制
	
	public static final int NOTICE_ZHAJINHUA_PRIMARY_POINT_LIMIT = 5000; // 诈金花初级场钻石限制
	public static final int NOTICE_ZHAJINHUA_MIDDLE_POINT_LIMIT = 30000; // 诈金花初级场钻石限制
	public static final int NOTICE_ZHAJINHUA_SENIOR_POINT_LIMIT = 50000; // 诈金花初级场钻石限制
	public static final int NOTICE_ZHAJINHUA_ROOM_CARD_POINT_LIMIT = 30000; // 诈金花房卡场钻石限制
	
	public static final int NOTICE_MAHJONG_PRIMARY_POINT_LIMIT = 5000; // 麻将初级场钻石限制
	public static final int NOTICE_MAHJONG_MIDDLE_POINT_LIMIT = 30000; // 麻将初级场钻石限制
	public static final int NOTICE_MAHJONG_SENIOR_POINT_LIMIT = 50000; // 麻将初级场钻石限制
	public static final int NOTICE_MAHJONG_ROOM_CARD_POINT_LIMIT = 30000; // 麻将房卡场钻石限制
	
	public static final int NOTICE_LHD_POINT_LIMIT = 75000; // 龙虎斗钻石限制
	
	

	/** 出牌类型, 玩家出牌 **/
	public static final int PLAY_TYPE_PLAYER = 0;
	/** 出牌类型, 玩家自动出牌(托管出牌) **/
	public static final int PLAY_TYPE_AUTO = 1;
	/** 出牌类型, 玩家超时出牌 **/
	public static final int PLAY_TYPE_TIMEOUT = 2;
	/** 出牌类型，玩家听牌出牌 **/
	public static final int PLAY_TYPE_TING = 3;
	
	/** 房间类型，自由场 **/
	public static final int ROOM_TYPE_FREE = 0;
	/** 房间类型，房卡场 **/
	public static final int ROOM_TYPE_TICKET = 1;
	
	/**
	 * 大厅
	 */
	public static final byte LOCATION_LOBBY = 0;
	/**
	 * 房卡
	 */
	public static final byte LOCATION_ROOM = 1;
	/**
	 * 茶馆
	 */
	public static final byte LOCATION_TEAHOUSE = 2;
	
	/**
	 * 房卡_钻石场
	 */
	public static final byte LOCATION_ROOM_DIAMOND = 3;
	/**
	 * 茶馆_钻石场
	 */
	public static final byte LOCATION_TEAHOUSE_DIAMOND = 4;
	
	/**
	 * 龙虎斗 (暂时GM 关闭AI使用)
	 */
	public static final byte LOCATION_LHD = 50;
}
