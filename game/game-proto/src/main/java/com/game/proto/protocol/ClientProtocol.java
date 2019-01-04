package com.game.proto.protocol;

/**
 * 客户端协议ID, 1~-100通用占用. 100~10000
 * ClientProtocol.java
 * @author JiangBangMing
 * 2019年1月4日下午3:00:45
 */
public interface ClientProtocol {

	/******** 基础通用 0~500 ****************/

	/** 登陆初始化 **/
	short C_LOGIN_RESP = 101;
	/** 玩家信息 **/
	short C_PLAYER_INFO = 102;
	/** 玩家收货信息 **/
	short C_PLAYER_SHIPPINGADDRESS = 103;
	/** 文本 **/
	short C_TEXT = 104;
	/** 错误消息 **/
	short C_ERROR = 105;
	/** 时间同步 **/
	short C_SYNC_TIME = 106;

	/** 错误重登消息 **/
	short C_RELOGIN = 107;
	/** 个人信息 **/
	short C_GET_PLAYERINFO = 108;

	/** 产品不足消息 **/
	short C_PRODUCT_ERROR = 109;

	/** 登录玩家的绑定的消息 **/
	short C_LOGIN_BIND = 110;

	/** 游客转正 **/
	short C_VISITOR_POSITIVEE = 111;

	/** 绑定手机 **/
	short C_BIND_MOBILEPHONE = 112;
	/** 绑定代理码失败文本 **/
	short C_BIND_AGENCYNUMBER = 113;

	/** 录像列表 **/
	short C_VIDEO_LIST = 114;

	/** 录像数据 **/
	short C_VIDEO = 115;
	
	/** 116 */
	short C_TEST_TIME = 116;
	
	/** 绑定银行卡信息 */
	short C_BIND_BANK_INFO = 117;
	
	/** 银行列表信息 **/
	short C_BANK_LIST = 118;
	
	/******** 设置 201~300 ****************/
	/** 反馈信息 **/
	short C_FEEDBACK = 201;
	/** 收货信息 **/
	short C_SHIPPINGADDRESS = 202;

	/******** VIP信息 301~310 ****************/
	/** VIP配置信息 **/
	short C_VIPMSG = 301;

	/******** 玩家奖励 311~320 ****************/
	/** 玩家奖励获得 **/
	short C_PLAYERWARD = 311;
	/** 新手礼包和首充状态 **/
	short C_PLAYERWARDSTATE = 312;

	/******** 活动 321~340 ****************/
	/** 活动列表 **/
	short C_ACTIVITYLIST = 321;
	/******** 排行版系统401~410 ***************/
	/** 排行版列表 **/
	short C_RANK_LIST = 401;
	/** 战绩*/
	short C_RANK_RESULT = 402;
	/******** 背包 501~600 ****************/

	/** 货币信息 **/
	short C_CURRENCY_INFO = 501;

	/** 物品列表信息 **/
	short C_ITEM_LIST_INFO = 502;

	/******** 时装601~650 ****************/
	/** 时装列表(更新或者处理) **/
	short C_FASHION_LIST = 601;

	/******** 战绩651~700 ****************/
	/** 战绩(更新或者处理) **/
	short C_EXPLOIT_LIST = 651;

	/******** 签到701~720 ***************/
	/** 签到协议 **/
	short C_SIGN_INFO = 701;

	/******** 公告721~750 **************/
	/** 获得公告消息 **/
	short C_NOTICE_INFO = 721;

	/******** 邮件 751~800 ****************/
	/** 邮件信息列表 **/
	short C_MAIL_LIST = 751;

	/******** 任务系统 801~820 ****************/
	/** 任务列表 **/
	short C_TASK_LIST = 801;
	/** 任务获得奖励 **/
	short C_TASK_REWARD = 802;

	/******** buff系统 821~840 ****************/
	/** buff列表 **/
	short C_BUFF_LIST = 821;

	/******** 信息系统 861~870 **************/
	/** 获得信息消息 **/
	short C_MSG_INFO = 861;

	/******** 大厅 5001~5100 ****************/
	/** 大厅列表 **/
	short C_LOBBY_LIST = 5001;

	/******** 斗地主 5101~5200 ****************/
	/** 房间信息 **/
	short C_ROOM_INFO = 5101;

	/** 发牌 **/
	short C_DEAL_CARDS = 5151;

	/** 出牌 **/
	short C_PLAY_CARD = 5153;

	/** 房间初始化消息(进入房间) **/
	short C_ROOM_INIT = 5102;

	/** 房间内玩家事件 **/
	short C_ROOM_PLAYER_EVENT = 5103;

	/** 提示出牌 **/
	short C_ROOM_PLAY_HINT = 5104;

	/** 聊天消息 **/
	short C_ROOM_CHAT = 5105;

	/** 随机任务 **/
	short C_ROOM_RANDOM_TASK = 5106;

	/** 胜负结果 **/
	short C_ACCOUNT_RESULT = 5154;

	/** 斗地主排名消息 **/
	short C_DDZ_RANK = 5107;

	/** 房间投票消息 **/
	short C_ROOM_VOTE = 5108;

	/** 房间投票结果 **/
	short C_ROOM_VOTE_RESULT = 5109;
	/**
	 * 查看战绩
	 */
	short C_ROOM_ZHANJI = 5110;
	
	/**
	 * 总结算
	 */
	short C_ROOM_ZONGJIESUAN = 5111;
	/**
	 * 推荐出牌
	 */
	short C_ROOM_TUIJIANCHUPAI = 5112;

	/******* 商城5201~5250 ****************/

	/** 购买商品消息 **/
	short C_BUY_PRODUCT = 5201;
	/** 获取商品列表 **/
	short C_GET_SHOPLIST = 5202;

	/** 充值订单 **/
	short C_ORDER_INFO = 5203;

	/******* 兑换5251~5300 ****************/
	/** 获取实物列表 **/
	short C_GET_GOOD_LIST = 5251;
	/** 获取实物库存列表 **/
	short C_GET_GOODCOUNT_LIST = 5256;
	/** 兑换实物 **/
	short C_GOOD_LIST = 5252;
	/** 兑换实物反馈 **/
	short C_BUY_GOOD = 5253;
	/** 填写收货地址 **/
	short C_SET_ADDRESS = 5254;
	/** 收到订单列表 **/
	short C_GET_ORDER_LIST = 5255;
	/** 兑换结果 **/
	short C_BUYGOODS_STATE = 5257;

	/******* 抢宝5300~5400 ****************/
	/** 发送抢宝列表 **/
	short C_SNATCH_LIST = 5301;

	/** 发送抢宝消息 **/
	short C_SNATCH_INFO = 5302;

	/** 获得抢宝模型的消息 **/
	short C_GET_SNATCHTEMP_INFO = 5303;

	/** 发送抢宝开宝倒计时数据 **/
	short C_SNATCH_COUNTDOWNTIME = 5304;

	/** 发送抢宝开宝结果 **/
	short C_SNATCH_OPENAWARD = 5305;

	/** 发送抢宝的玩家所有参与记录 **/
	short C_SNATCH_PART_RECORD = 5306;

	/** 发送您当前的抢宝号 **/
	short C_GET_PLAYERSNATCHID = 5307;

	/** 发送抢宝模板的往期结果揭晓 **/
	short C_SNATCHTEMP_RECORD = 5308;

	/** 获得我的夺宝 **/
	short C_SELF_SNATCH = 5309;

	/** 获得其他玩家的夺宝 **/
	short C_USER_SNATCH = 5310;

	/** 获得该抢宝期号的所有抢宝号 **/
	short C_GET_ALL_SNATCHID = 5311;

	/** 获得抢宝的数值A,B,C的详情数据 **/
	short C_GET_SNATCH_ABC = 5312;

	/** 更新抢宝状态 **/
	short C_UPDATE_SNATCHSTATE = 5313;

	/** 获得抢宝的数值A或B详情 **/
	short C_GET_SNATCH_AORB = 5314;

	/** 获得玩家参与抢宝的抢宝ID **/
	short C_UPDATE_PLAYERINFO = 5315;

	/** 获得玩家参与抢宝的消息数据 **/
	short C_USER_SNATCH_INFO = 5316;

	/** 获得抢宝参与的分期抢宝号 **/
	short C_GET_PART_SNATCHID = 5317;

	/** 获得抢宝奖励消息 **/
	short C_GET_SNATCH_AWARD = 5318;

	/** 获取抢宝奖励列表消息 **/
	short C_GET_SNATCH_AWARD_LIST = 5319;

	/** 获得抢宝订单模块中的消息 **/
	short C_GET_SNATCHORDER = 5320;

	/** 获得获胜玩家抢宝记录 **/
	short C_GET_USER_WIN_SNATCH = 5321;

	/******* 夺宝赛5401~5500 ****************/

	/** 正在报名中的夺宝赛 **/
	short C_CONTEST_LIST = 5401;

	/** 参与的夺宝赛信息 **/
	short C_CONTEST_INFO = 5402;

	/** 退赛消息 **/
	short C_CONTEST_QUIT = 5403;

	/** 排行榜消息 **/
	short C_CONTEST_RANK = 5404;

	/** 自己参加的夺宝赛数据 **/
	short C_CONTEST_SELFPLAY = 5405;

	/** 指定夺宝赛数据 **/
	short C_CONTEST_GET = 5406;

	/** 夺宝赛结果 **/
	short C_CONTEST_RESULT = 5407;

	/** 夺宝赛个人信息 **/
	short C_CONTEST_PLAYEMSG = 5408;

	/** 参与的夺宝赛信息更新 **/
	short C_CONTEST_UPDATE = 5409;

	/** 夺宝赛记录 **/
	short C_CONTEST_RECORD = 5410;

	/** 夺宝赛胜利记录 **/
	short C_CONTEST_WIN_RECORD = 5411;

	/** 检测当前所在的房间Id **/
	short C_GAME_ROOM_ID = 5441;

	/******** 麻将 5501~5600 ****************/

	/** 麻将房间初始化消息(进入房间) **/
	short C_MJ_ROOM_INIT = 5501;

	/** 麻将房间信息 **/
	short C_MJ_ROOM_INFO = 5502;

	/** 房间内玩家事件 **/
	short C_MJ_ROOM_PLAYER_EVENT = 5503;

	/** 发牌 **/
	short C_MJ_DEAL_CARDS = 5504;

	/** 出牌 **/
	short C_MJ_PLAY_CARD = 5505;

	/** 结算 **/
	short C_MJ_ACCOUNT_RESULT = 5506;

	/** 聊天消息 **/
	short C_MJ_ROOM_CHAT = 5507;

	/** 排名消息 **/
	short C_MJ_RANK = 5509;

	/** 听牌消息 **/
	short C_MJ_TING_CARD = 5510;

	/** 胡牌消息 **/
	short C_MJ_HU_CARD = 5511;

	/** 麻将投票消息 **/
	short C_MJ_VOTE = 5512;

	/** 麻将投票结果 **/
	short C_MJ_VOTE_RESULT = 5513;

	/** 重置手牌 **/
	short C_MJ_RESET_CARD = 5514;

	/** 玩家胡牌信息 **/
	short C_MJ_PLAYER_HU = 5515;
	
	/** 麻将游戏模式 **/
	short C_MJ_ROOM_MODE = 5516;

	/** 麻将胡牌提示张数 **/
	short C_MJ_ROOM_GET_HU_CARD = 5517;
	
	/** 设置庄家结果 **/
	short C_MJ_CHEAT_BANKER = 5594;
	
	/** 开启或者关闭机器人 **/
	short C_MJ_AI_ENABLE = 5595;
	
	/** 麻将游戏状态更新 **/
	short C_MJ_UPDATE_GAME_STATUS = 5596;
	
	/** 结算亮牌 **/
	short C_MJ_SHOW_CARD = 5597;

	/** 设置下一张发什么牌 返回结果 **/
	short C_MJ_DRAW_CHEAT = 5598;
	
	/** 设置初始发牌 返回结果 **/
	short C_MJ_INIT_CARDS_CHEAT = 5599;
	
	/** 设置最后几张牌 返回结果 **/
	short C_MJ_LAST_CARDS_CHEAT = 5600;

	/******** 斗金牛 5601~5700 ****************/
	/** 斗金牛房间初始化消息(进入房间) **/
	short C_BGF_ROOM_INIT = 5601;

	/** 斗金牛房间信息 **/
	short C_BGF_ROOM_INFO = 5602;

	/** 斗金牛内玩家事件 **/
	short C_BGF_ROOM_PLAYER_EVENT = 5603;

	/** 房主变更 **/
	 short C_BGF_ROOM_HOUSE = 5604;

	 /** 斗金牛聊天消息 **/
	short C_BGF_ROOM_CHAT = 5605;
	
	/** 斗金牛准备 **/
	short C_BGF_ROOM_READY = 5606;
	
	/** 斗金牛位置检查*/
	short C_BGF_ROOM_CHECK_LOCATION = 5607;
	
	/** 结算显示所有应该显示的牌 **/
	short C_BGF_GAMEOVER_SHOW = 5608;

	/** PK输了要显示牌 **/
	short C_BGF_PKOVER_SHOW = 5609;
	
	/** 斗金牛排名消息 **/
	short C_DJN_RANK = 5610;
	
	/** 分牌转发 **/
	short C_FENGPAI_RANK = 5611;

	/******** 老虎机 5701~5800 ****************/
	/** 房间初始化消息(进入房间) **/
	short C_SLOTS_ROOM_INIT = 5701;
	/** 房间消息 **/
	short C_SLOTS_ROOM = 5702;
	/** 老虎机内玩家事件 **/

	/** 玩家事件 **/
	short C_SLOTS_ROOM_PLAYER_EVENT = 5703;
	/** 上庄倒计时 **/
	short C_SLOTS_ROOM_BANKERSTATE = 5704;
	/** 游戏开始倒计时 **/
	short C_SLOTS_ROOM_GAMESTART = 5705;
	/** 更新下注面板消息 **/
	short C_SLOTS_ROOM_UPDATABET = 5706;

	/** 开奖 ***/
	short C_SLOTS_ROOM_LORRERY = 5710;
	/** 结算 ***/
	short C_SLOTS_ROOM_CLEAN = 5715;
	/** 获取上庄列表 ***/
	short C_SLOTS_ROOM_GET_BANKERS = 5720;
	/** 获取玩家列表 ***/
	short C_SLOTS_ROOM_GET_PLAYERS = 5721;
	/** 玩家上庄 ***/
	short C_SLOTS_ROOM_PLAYER_BANKER = 5730;
	/** 聊天消息 **/
	short C_SLOTS_ROOM_CHAT = 5735;

	/** 老虎机排名消息 **/
	short C_LHJ_RANK = 5736;

	/******** 百家乐 5801~500 ****************/
	/** 房间初始化消息(进入房间) **/
	short C_BJL_ROOM_INIT = 5801;
	/** 房间消息 **/
	short C_BJL_ROOM = 5802;

	/** 玩家事件 **/
	short C_BJL_ROOM_PLAYER_EVENT = 5803;
	/** 游戏开始倒计时 **/
	short C_BJL_ROOM_GAMESTART = 5805;
	/** 更新下注面板消息 **/
	short C_BJL_ROOM_UPDATABET = 5806;

	/** 开奖 ***/
	short C_BJL_ROOM_LORRERY = 5810;
	/** 结算 ***/
	short C_BJL_ROOM_CLEAN = 5815;
	/** 获取上庄列表 ***/
	short C_BJL_ROOM_GET_BANKERS = 5820;
	/** 获取玩家列表 ***/
	short C_BJL_ROOM_GET_PLAYERS = 5821;
	/** 玩家上庄 ***/
	short C_BJL_ROOM_PLAYER_BANKER = 5830;
	/** 聊天消息 **/
	short C_BJL_ROOM_CHAT = 5835;

	/** 百家乐排名消息 **/
	short C_BJL_RANK = 5836;

	/******** 炸金花 5901~6000 ****************/
	/** 炸金花房间初始化消息(进入房间) **/
	short C_ZJH_ROOM_INIT = 5901;

	/** 炸金花房间信息 **/
	short C_ZJH_ROOM_INFO = 5902;

	/** 炸金花内玩家事件 **/
	short C_ZJH_ROOM_PLAYER_EVENT = 5903;

	/** 聊天消息 **/
	// short C_ZJH_OOM_CHAT = 5904;
	
	/** 炸金花聊天消息 **/
	short C_ZJH_ROOM_CHAT = 5905;
	
	/** 炸金花房间距离检查 */
	short C_ZJH_ROOM_CHECK_LOCATION = 5906;

	/** 结算显示所有应该显示的牌 **/
	short C_ZJH_GAMEOVER_SHOW = 5908;

	/** PK输了要显示牌 **/
	short C_ZJH_PKOVER_SHOW = 5909;

	/** 炸金花排名消息 **/
	short C_ZJH_RANK = 5910;
	
	/** 通知客户端开始播放开场动画 **/
	short C_ZJH_START = 5911;
	
	/******** 德州扑克 6001~6100 ****************/
	/** 德州扑克房间初始化消息(进入房间) **/
	short C_TH_ROOM_INIT = 6001;

	/** 德州扑克房间信息 **/
	short C_TH_ROOM_INFO = 6002;

	/** 德州扑克内玩家事件 **/
	short C_TH_ROOM_PLAYER_EVENT = 6003;

	/** 聊天消息 **/
	// short C_TH_OOM_CHAT = 6004;

	/** 结算显示所有应该显示的牌 **/
	short C_TH_GAMEOVER_SHOW = 6008;

	/** PK输了要显示牌 **/
	short C_TH_PKOVER_SHOW = 6009;

	/** 德州扑克聊天消息 **/
	short C_TH_ROOM_CHAT = 6005;

	/** 德州扑克排名消息 **/
	short C_DZPK_RANK = 6010;

	/******** 打旋 6101~6200 ****************/
	/** 打旋房间初始化消息(进入房间) **/
	short C_DX_ROOM_INIT = 6101;

	/** 打旋房间信息 **/
	short C_DX_ROOM_INFO = 6102;

	/** 打旋内玩家事件 **/
	short C_DX_ROOM_PLAYER_EVENT = 6103;

	/** 新得房主通知 **/
	short C_DX_ROOM_NEWHOUSE = 6104;
	
	/** 打旋聊天消息 **/
	short C_DX_ROOM_CHAT = 6105;

	/** 检查打旋位置 **/
	short C_DX_ROOM_CHECK_LOCATION = 6106;
	
	/** 结算显示所有应该显示的牌 **/
	short C_DX_GAMEOVER_SHOW = 6108;

	/** 准备通知*/
	short C_DX_PLAYER_READY = 6109;
	
	/** 打旋排名消息 **/
	short C_DX_RANK = 6110;

	/** 打旋牌型类型 **/
	short C_DX_CARDTYPE = 6111;
	
	/******** 抽奖 6201~6203 ****************/
	
	/**奖品*/
	short C_LOTTERY_REWARD = 6201;
	/** 登陆抽奖验证*/
	short C_LOTTERY_LOGIN_STATUS = 6202;
	
	/******** 保险柜 6204~6207 ****************/
	/**保险柜信息*/
	short C_SAFE_INFO = 6204;
	/**保险柜开启信息*/
	short C_SAFE_OPEN = 6205;
	/**保险柜开启状态*/
	short C_SAFE_STATE = 6206;
	
	/******** 龙虎斗 6221~6250 ****************/
	/**房间初始化消息*/
	short C_LHD_ROOM_INIT = 6221;
	/**房间消息*/
	short C_LHD_ROOM = 6222;
	/**开始投注*/
	short C_LHD_START_BET = 6223;
	/** 房间玩家事件*/
	short C_LHD_ROOM_PLAYER_EVENT = 6224;
	/** 开牌*/
	short C_LHD_RESULT = 6225;
	/** 开始播放下注前动画*/
	short C_LHD_START = 6226;
	/** 大赢家信息*/
	short C_LHD_BIG_WINNER = 6227;
	/** 上庄列表*/
	short C_LHD_BANKER_LIST = 6228;
	/** 加入上庄队列成功*/
	short C_LHD_BANKER_LIST_UP_SUCCESS = 6229;
	/** 上庄确认消息*/
	short C_LHD_BANKER_UP_CONFIRM = 6230;
	/** 玩家上庄*/
	short C_LHD_BANKER_UP = 6231;
	/** 下庄成功*/
	short C_LHD_BANKER_LIST_DOWN_SUCCESS = 6232;
	/** 庄家更新*/
	short C_LHD_BANKER_UPDATE = 6233;
	/** 获取龙虎斗房间列表*/
	short C_LHD_ROOM_LIST = 6234;
	/** 获取龙虎斗玩家列表*/
	short C_LHD_PLAYER_LIST = 6235;
	/** 龙虎斗下注信息*/
	short C_LHD_BET_LIST = 6236;

	/******* TEST 9001~10000 ****************/
	/** TEST **/
	short C_GET_BUFF = 9001;
	
	/**************************************Guil 公会模块 : 6301~6400 ***********************/
	/**
	 * 打开公会面板
	 */
	short C_GUILD_OPEN_PANEL = 6301;
	
	/**
	 * 创建公会
	 */
	short C_GUILD_CREATE_GUILD = 6302;
	
	/**
	 * 加入公会
	 */
	short C_GUILD_JOIN_GUILD = 6303;
	

	/**
	 * 进入公会
	 */
	short C_GUILD_IN_GUILD = 6304;
	
	/**
	 * 打开消息面板
	 */
	short C_GUILD_OPEN_MESSAGE_PANEL = 6305;
	
	/**
	 * 处理消息
	 */
	short C_GUILD_DEAL_MESSAGE = 6306;
	
	/**
	 * 打开公告面板
	 */
	short C_GUILD_OPEN_NOTICE_PANEL = 6307;
	
	/**
	 * 修改公告
	 */
	short C_GUILD_CHANGE_NOTICE = 6308;
	
	/**
	 * 打开基金面板
	 */
	short C_GUILD_OPEN_FUND_PANEL = 6309;
	
	/**
	 * 充值基金
	 */
	short C_GUILD_PAY_FUND = 6310;
	
	/**
	 * 打开基金帐单面板
	 */
	short C_GUILD_OPEN_FUND_FLOW_PANEL = 6311;
	
	/**
	 * 打开成员列表
	 */
	short C_GUILD_OPEN_MEMBER_LIST = 6312;
	
	/**
	 * 成员权限设置
	 */
	short C_GUILD_MEMBER_SET_AUTH = 6313;
	
	/**
	 * 成员踢出公会
	 */
	short C_GUILD_MEMBER_REMOVE = 6314;
	
	/**
	 * 成员退出公会
	 */
	short C_GUILD_MEMBER_EXIT = 6315;
	
	/**
	 * 解散公会
	 */
	short C_GUILD_DISSOLVE = 6316;
	
	/**
	 * 修改公会名称
	 */
	short C_GUILD_CHANGE_NAME = 6317;
	
	/**
	 * 公会高级设置
	 */
	short C_GUILD_SET = 6318;
	/**
	 * 房间列表 斗地主
	 */
	short C_GUILD_ROOM_LIST_DDZ = 6319;
	/**
	 * 房间列表 扎金花
	 */
	short C_GUILD_ROOM_LIST_ZJH = 6320;
	/**
	 * 房间列表 扯旋
	 */
	short C_GUILD_ROOM_LIST_DX = 6321;
	/**
	 * 房间列表 斗牛
	 */
	short C_GUILD_ROOM_LIST_BGF = 6322;
	/**
	 * 房间列表 麻将
	 */
	short C_GUILD_ROOM_LIST_MJ = 6323;
	/**
	 * 公会红点推送
	 */
	short C_GUILD_RED_DOT_PUSH = 6324;
}