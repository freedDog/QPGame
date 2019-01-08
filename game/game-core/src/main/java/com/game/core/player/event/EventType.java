package com.game.core.player.event;

/**
 * 游戏事件类型<br>
 * EventType.java
 * @author JiangBangMing
 * 2019年1月8日下午2:22:03
 */
public interface EventType {
	/** 玩家等级事件: arg1:当前等级, arg2:升级前的等级 **/
	short PLAYER_LEVEL = 1;

	/** 商店购买: arg1:购买次数, arg2:购买的模板, arg3:模板类型 **/
	short SHOP_BUY = 2;

	/** 游戏参与:arg1 参与次数, arg2 游戏类型(0为不限制), arg3 是否胜利(1为必须胜利, 0为无限制)), arg4 大厅种类(0为不限制), arg5 游戏房间位置类型(-1不限制0自由场1房卡场2茶馆)**/
	short PLAY_GAME = 3;

	/** 游戏记录: arg1 达成次数, arg2 达成类型, arg4 大厅种类(0为不限制), arg4 游戏房间位置类型(-1不限制0自由场1房卡场2茶馆) **/
	short GAME_RECORD = 4;

//	/** 夺宝赛参与: arg1 达成次数, arg2 名次 **/
//	short GAME_CONTEST = 5;

	/** 玩家充值(Param1 充值金额) **/
	short PLAYE_PAY = 6;

	/** 发送公告(arg1 发送次数) **/
	short SEND_NOTICE = 7;

	/** 发送表情(Param1 发送次数) **/
	short SEND_BIAOQING = 8;

//	/** 抢宝赛参与: arg1 达成次数 **/
//	short GAME_SNATCH = 9;
	
	/** 登陆次数(Param1 登陆次数) **/
	short PLAYER_LOGIN = 10;

}
