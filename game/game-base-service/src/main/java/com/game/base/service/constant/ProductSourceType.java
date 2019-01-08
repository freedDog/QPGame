package com.game.base.service.constant;

/**
 * 资源产入产出类型<br>
 * 不再按照以前增加或者减少分ID, 增加或者减少看数量.
 */
public interface ProductSourceType {
	// 系统相关的 1 - 100
	/** 创建角色 **/
	short CREATE_ROLE = 1;

	/** GM发送 **/
	short GM = 2;

	/** API发送 **/
	short API = 3;

	/** 修改名称 **/
	short CHANGE_NAME = 4;

	/** 充值 **/
	short CHARGE = 5;

	/** 创建机器人角色 **/
	short CREATE_ROBET_ROLE = 6;

	/** 新手礼包 **/
	short NEWPLAYERAWARDS = 7;

	/** 首充礼包 **/
	short FIRSTPAY = 8;

	/** 分享奖励 **/
	short SHAREAWARD = 9;

	/** 绑定手机赠送奖励 **/
	short BINDPHONEAWARDS = 10;

	/** 首次登陆奖励 **/
	short LOGINAWARDS = 11;

	/** 修改房卡 **/
	short ROOMCARDS = 12;
	
	/** 玩家提现扣除 **/
	short WITHDRAW_REDUCE = 13;

	/** 玩家提现返还 **/
	short WITHDRAW_INCRESE = 14;
	
	/** 游戏测试阶段参与奖励 **/
	short TEST_PRISE = 15;
	
	/**邮件*/
	short MAIL = 16;
	
	/** 阿里订单*/
	short ALIPAYORDER = 17;
	
	/** 自动调整机器人货币*/
	short AUTOROBOTCURRENCY = 18;
	
	/** 代理提现到余额*/
	short PROXYWITHDRAWTOGAME = 19;
	// 任务 101~110

	/** 任务奖励 **/
	short TASK_REWARD = 101;

	/** 任务累计奖励 **/
	short TASK_TOTAL_REWARD = 102;

	// 签到 111~120
	/** 签到奖励 **/
	short SIGN_REWARD = 111;

	/** 破产领取 **/
	short BROKE_REWARD = 112;

	// 商城121~125
	/** 购买商品奖励 **/
	short BUY_REWARD = 121;

	/** 充值获得 **/
	short CHARGE_GET = 122;
	
	/** 获得排行榜奖励 **/
	short RANK_RWARD = 126;

	// 背包 131~140
	/** 打开宝箱 **/
	short BAG_OPEN_BOX = 131;
	/** 使用物品 **/
	short ITEM_USE = 132;

	// 背包 141~150
	/** 兑换物品 **/
	short EXCHANGE = 141;
	/**
	 * GM扣除钻石
	 */
	short GM_COST_POINT = 142;
	/**
	 * 排行榜奖励
	 */
	short RANK_REWARD = 143;

	// 夺宝赛 151~160

//	/** 夺宝赛胜利 **/
//	short CONTEST_WIN = 151;
//
//	/** 夺宝赛报名支付 **/
//	short CONTEST_JOIN = 152;
//
//	/** 夺宝赛取消报名 **/
//	short CONTEST_QUIT = 153;

	// 斗地主游戏 5001 ~ 6000

	/** 游戏开局消耗 **/
	short GAME_EXPEND = 5001;

	/** 斗地主结算 **/
	short GAME_RESULT = 5002;

	/** 聊天消耗 **/
	short GAME_CHAT_EXPEND = 5003;

	/** 麻将游戏消耗 **/
	short GAME_MJ_EXPEND = 5004;

	/** 麻将最终游戏消耗 **/
	short GAME_MJ_LAST_EXPEND = 5005;

	/** 抢宝消耗 **/
	short SNATCH_EXPEND = 6001;

	/** 斗地主开房消耗 **/
	short DDZ_KF_EXPEND = 6002;
	/** 斗地主公会房间解散返还 */
	short DDZ_KF_FAN = 6003;

	// 斗金牛游戏7001 ~ 8000

	/** 游戏开局消耗 **/
	short BGF_GAME_EXPEND = 7004;
	/** 跟注（加注）消耗 **/
	short BGF_FOLLOW = 7001;
	/** PK消耗 **/
	short BGF_PK = 7002;
	/** 斗金牛胜利 **/
	short BGF_WIN = 7003;
	/** 斗金牛开房消耗 **/
	short DJN_KF_EXPEND = 7004;
	/** 斗金牛失败 **/
	short BGF_LOSE = 7005;
	/** 斗金牛结束结算 **/
	short BGF_OVER = 7006;
	/** 斗金牛房卡*/
	short BGF_ROOM_PAY = 7007;
	// 老虎机 8001-9000
	/** 游戏开局消耗 **/
	short SLOTS_GAME_EXPEND = 8001;
	/** 游戏押注 **/
	short SLOTS_GAME_ADDBET = 8002;
	/** 游戏中奖 **/
	short SLOTS_GAME_LOTTERY = 8005;
	/** 庄家盈利情况 **/
	short SLOTS_GAME_BANKER = 8010;
	/** 老虎机开房消耗 **/
	short LHJ_KF_EXPEND = 8011;

	// 百家乐 8001-9000
	/** 游戏开局消耗 **/
	short BJL_GAME_EXPEND = 9001;
	/** 游戏押注 **/
	short BJL_GAME_ADDBET = 9002;
	/** 游戏中奖 **/
	short BJL_GAME_LOTTERY = 9005;
	/** 庄家盈利情况 **/
	short BJL_GAME_BANKER = 9010;
	/** 斗地主开房消耗 **/
	short BJL_KF_EXPEND = 9011;

	// 炸金花游戏10001 ~ 10100

	/** 游戏开局下底分消耗 **/
	short ZJH_GAME_EXPEND_BASESCORE = 10001;
	/** 游戏开局消耗 **/
	short ZJH_GAME_EXPEND = 10002;
	/** 跟注（加注）消耗 **/
	short ZJH_FOLLOW = 10003;
	/** PK消耗 **/
	short ZJH_PK = 10004;
	/** 扎金花胜利 **/
	short ZJH_WIN = 10005;
	/** 扎金花返还房卡 **/
	short ZJH_KF_RETURN = 10006;
	/** 喜钱 **/
	short ZJH_XQ_EXPEND = 10007;
	/** 炸金花开房消耗 **/
	short ZJH_KF_EXPEND = 10008;
	/** 炸金花全下 **/
	short ZJH_ALL_IN = 10009;

	// 德州扑克游戏10101 ~ 10200

	/** 游戏开局消耗 **/
	short TH_GAME_EXPEND = 10104;
	/** 跟注（加注）消耗 **/
	short TH_FOLLOW = 10101;
	/** PK消耗 **/
	short TH_PK = 10102;
	/** 德州扑克胜利 **/
	short TH_WIN = 10103;
	/** 德州扑克开房消耗 **/
	short DZPK_KF_EXPEND = 10104;

	// 打旋游戏10201 ~ 10300
	/** 游戏开局消耗 **/
	short DX_GAME_EXPEND = 10204;
	/** 跟注（加注）消耗 **/
	short DX_FOLLOW = 10201;
	/** PK消耗 **/
	short DX_PK = 10202;
	/** 打旋胜利 **/
	short DX_WIN = 10203;
	/** 德州扑克开房消耗 **/
	short DX_KF_EXPEND = 10204;
	/** 底注消耗 **/
	short DX_DIZHU = 10205;
	/** 休退钱 **/
	short DX_XIU = 10206;
	/** 打旋房卡*/
	short DX_ROOM_PAY = 10207;
	//抽奖 10401-10450
	/** 登陆抽奖 */
	short L_LORRERY_LOGIN = 10401;
	/** 任务抽奖 */
	short L_LORRERY_TASK = 10402;
	/**保险箱*/
	short SAFE_BOX = 10403;
	
	// 麻将 10451-10500
	/** 麻将点杠 */
	short MJ_DG = 10451;
	/** 麻将巴杠 */
	short MJ_BG = 10452;
	/** 麻将暗杠 */
	short MJ_AG = 10453;
	/** 麻将炮胡 */
	short MJ_PH = 10454;
	/** 麻将自摸 */
	short MJ_ZM = 10455;
	/** 麻将转雨 */
	short MJ_ZY = 10456;
	/** 麻将转雨补扣 */
	short MJ_ZYBK = 10457;
	/** 麻将退雨 */
	short MJ_TY = 10458;
	/** 麻将查大叫 */
	short MJ_CDJ = 10459;
	/** 麻将查花猪 */
	short MJ_CHZ = 10460;
	/** 麻将抽成 */
	short MJ_CC = 10461;
	/** 麻将开房消耗 **/
	short MJ_KF_EXPEND = 10462;
	/** 麻将开放返还房卡 **/
	short MJ_KF_RETURN = 10463;
	
	
	// 麻将 10501-10600
	/** 龙虎斗 下注*/
	short LHD_BET = 10501;
	/** 龙虎斗 胜利*/
	short LHD_WIN = 10502;
	/** 龙虎斗 当庄输赢*/
	short LHD_BANKER_WIN = 10503;
	/** 龙虎斗 自动调整机器人金币*/
	short LHD_AUTO_CAHNGE_ROBOT_CURRENCY = 10504;
	
	
	
	
}
