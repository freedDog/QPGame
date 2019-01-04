package com.game.proto.protocol;

/**
 * 服务器协议ID 10001~N, 1个模块1000个消息, 参考ModuleName<br>
 * core: 10001~13000<br>
 * control: 13001~14000<br>
 * room : 14001~15000<br>
 * ServerProtocol.java
 * @author JiangBangMing
 * 2019年1月4日下午3:01:12
 */
public interface ServerProtocol {
	/******************** code 10001 ~ 13000 ***************/
	/******************** 基础消息 10001 ~ 10500 ***************/
	/** 登陆初始化 **/
	short S_LOGIN_RESP = 10001;

	/** 登陆加载 **/
	short S_LOGIN_LOAD = 10002;

	/** 断线重连(最小限度的处理加载) **/
	short S_LOGIN_RELOAD = 10003;

	/** 时间同步 **/
	short S_SYNC_TIME = 10004;

	/** 修改信息 **/
	short S_CHANGE_INFO = 10006;

	/** 修改收货地址 **/
	short S_CHANGE_SHIPPINGADDRESS = 10007;

	/** 绑定账号 **/
	short S_BIND = 10008;

	/** 获取收货地址 **/
	short S_GET_SHIPPINGADDRESS = 10009;

	/************ 设置信息 11000 ~ 11500 *************/
	/** 保存玩家反馈信息 **/
	short S_SET_FEEDBACK = 11000;
	/** 绑定代理码 **/
	short S_SET_AGENCYNUMBER = 11001;
	
	/** 绑定银行卡信息**/
	short S_BIND_BANK = 11002;
	
	/** 获取绑定银行卡信息**/
	short S_BANK_INFO = 11003;

	/** 修改开户行地址 **/
	short S_REST_BRANCH_BANK_NAME = 11004;

	/** 提现 **/
	short S_WITHDRAW_ORDER = 11005;
	
	/** 绑定阿里账户*/
	short S_BIND_ALIPAYACCOUNT = 11006;//绑定阿里账户
	
	/** 绑定身份证信息**/
	short S_BIND_ID = 11007;
	/************ 签到10010 ~ 10020 *************/
	/** 进行签到 **/
	short S_DO_SIGN = 10010;

	/** 进行补签 **/
	short S_DO_RESIGN = 10011;

	/** 获得连续签到奖励 **/
	short S_DO_SIGNRWD = 10013;

	/** 领取破产补助 **/
	short S_BROKE_AWARD_GET = 10014;

	/** 游客转正 **/
	short S_VISITOR_POSITIVEE = 10015;

	/** 绑定手机 **/
	short S_BIND_MOBILEPHONE = 10016;

	/******************** 玩家奖励: 10021~10100 ***************/
	/** 新手礼包 **/
	short S_NEWPLAYERAWARDS = 10021;

	/** 首充礼包 **/
	short S_FIRSTPAY = 10022;

	/** 分享奖励 **/
	short S_SHAREAWRAD = 10023;
	
	short S_TEST_TIME = 10024;

	/******************** 兑换 10100~10200 *****************/
	/** 获取实物列表 **/
	short S_GET_GOOD_LIST = 10100;
	/** 获取实物列表库存 **/
	short S_GET_GOODCOUNT_LIST = 10104;
	/** 兑换实物 **/
	short S_BUY_GOOD = 10101;
	/** 收到收货地址，修改订单信息 **/
	short S_ADD_ORDER = 10102;
	/** 获取所有订单 **/
	short S_GET_ORDER_LIST = 10103;
	/** 完成收货 **/
	short S_DO_DONEORDER = 10105;
	/** 完成晒单 **/
	short S_DO_SHAREORDER = 10106;

	/******************** 商城 10201~10300 *****************/

	/** 购买商品 **/
	short S_BUY_PRODUCT = 10201;
	/** 购买商品列表 **/
	short S_GET_SHOPLIST = 10202;
	/** 充值创建订单 **/
	short S_CREATE_ORDER = 10203;
	/** 阿里订单申请 */
	short S_ALIPAY_ORDER = 10204;
	/** 代理金提取到余额*/
	short S_PROXY_WITHDRAW = 10205;
	/******************** 公告 10301~10320 *****************/
	/** 公告消息 **/
	short S_SEND_NOTICE = 10301;

	/******************* 信息 10321~10340 *****************/

	/** 发送信息消息 **/
	short S_SEND_MSG = 10321;

	/******************** 时装消息 10601 ~ 10650 ***************/
	/** 时装穿戴 **/
	short S_FASHION_INSTALL = 10601;

	/**
	 * 更改头像
	 */
	short S_CHANGE_HEAD_IMG = 10602;

	/******************** 房间消息 10651 ~ 10800 ***************/
	/** 快速进入房间 **/
	short S_QUICK_JOIN_ROOM = 10651;
	/** 换桌房间 **/
	short S_CHANGE_ROOM = 10652;

	/** 创建麻将房间 **/
	short S_MJ_CREATE_ROOM = 10653;

	/** 进入麻将房间 **/
	short S_MJ_JOIN_ROOM = 10654;

	/** 加入老虎机房间 **/
	short S_SLOTS_ROOM_JOIN = 10655;

	/** 快速加入成都麻将房间 **/
	short S_QUICK_MJ_ROOM_JOIN = 10656;

	/** 加入百家乐房间 **/
	short S_BJL_ROOM_JOIN = 10657;

	/** 加入扎金花房间 **/
	short S_ZJH_ROOM_JOIN = 10658;

	/** 加入德州扑克房间 **/
	short S_TH_ROOM_JOIN = 10659;

	/** 加入打旋游戏房间 **/
	short S_DX_ROOM_JOIN = 10660;
	
	/** 创建打旋游戏房间 **/
	short S_DX_CREATE_ROOM = 10661;
	
	/** 创建斗金牛游戏房间 **/
	short S_BG_CREATE_ROOM = 10662;
	
	/** 麻将房间换桌 **/
	short S_MJ_CHANGE_ROOM = 10663;

	/** 创建打旋钻石房间 **/
	short S_DX_CREATE_DIAMOND_ROOM = 10664;
	
	/** 创建斗金钻石房间 **/
	short S_BG_CREATE_DIAMOND_ROOM = 10665;
	
	/** 创建炸金花房间 **/
	short S_ZJH_CREATE_ROOM = 10666;
	
	/** 进入炸金花房间 **/
	short S_ZJH_JOIN_ROOM = 10667;
	
	/**
	 * 创建斗地主房间
	 */
	short S_DDZ_ROOM_CREATE = 10680;
	/**
	 * 进入斗地主房间
	 */
	short S_DDZ_ROOM_JOIN = 10681;
	
	/** 进入龙虎斗房间 **/
	short S_LHD_JOIN_ROOM = 10682;
	
	/** 进入房卡房间（进入房卡房统一接口） **/
	short S_JOIN_TICKET_ROOM = 10683;
	
	/** 获取龙虎斗房间列表 **/
	short S_LHD_ROOM_LIST = 10684;
	
	/******************** 邮件 10801 ~ 10850 ***************/
	/** 获取邮件附件 **/
	short S_MAIL_GET_ATTACH = 10801;
	/** 删除邮件 **/
	short S_MAIL_REMOVE = 10802;

	/******************** 任务 10901 ~ 10920 ***************/
	/** 领取任务奖励 **/
	short S_TASK_GET_REWARD = 10901;
	/** 领取累计任务奖励 **/
	short S_TASK_GET_TOTAL_REWARD = 10902;

	/******************** 背包物品 10851 ~ 10900 ***************/
	/** 使用物品 **/
	short S_ITEM_USE = 10851;

	/******************** 抽奖: 10921~10940 ***************/
	/**任务抽奖请求*/
	short S_LOTTERY_TASK = 10921;
	/**登陆抽奖请求*/
	short S_LOTTERY_LOGIN = 10922;
	/**登陆抽奖请求*/
	short S_LOTTERY_LOGIN_STATUS = 10923;
	
	/******************** 保险箱: 10941~10945 ***************/
	/** 保险箱密码设置喝修改*/
	short S_AFE_ATM_PWD = 10941;
	
	/** 保险柜显示*/
	short S_AFE_SHOW_INFO = 10942;
	
	/** 保险柜存储*/
	short S_AFE_ATM = 10943;
	
	/**状态*/
	short S_AFE_STATE = 10944;
	
	/******************** BUFF 12001 ~ 12100 ***************/
	/** 获取Buff列表 **/
	short S_GET_BUFFLIST = 12010;
	/** 获取Buff列表 **/
	short S_GET_BUFF = 12012;
	/** 增加Buff **/
	short S_ADD_BUFF = 12011;

	/******************** 录像 12101 ~ 12110 ***************/
	/** 获取录像列表 **/
	short S_GET_VIDEO_LIST = 12101;

	/** 获取录像内容 **/
	short S_READ_VIDEO = 12102;

	/** 删除录像内容 **/
	short S_REMOVE_VIDEO = 12103;

	/******************** control: 13001~14000 ***************/

	/** 获取所有大厅的列表 **/
	short S_LOBBY_LIST = 13001;

	/** 获得玩家的个人信息 **/
	short S_GET_PLAYERINFO = 13002;

	/** 获得排行版的列表 **/
	short S_RANK_LIST = 13003;
	
	/** 获取战斗结果*/
	short S_ROOMCARDRESULT = 13004;

	/** 领取积分榜奖励*/
	short S_RANK_REWARD = 13005;

	/******************** room : 14001~15000 ***************/
	/******************** 斗地主 : 14001~14100 ***************/

	/** 房间准备 **/
	short S_ROOM_PREPARE = 14001;

	/** 叫地主 **/
	short S_ROOM_CALL_DZ = 14002;

	/** 叫加倍 **/
	short S_ROOM_CALL_DOUBLE = 14003;

	/** 出牌 **/
	short S_ROOM_PLAY_CARD = 14004;

	/** 离开房间 **/
	short S_ROOM_LEAVE = 14005;

	/** 明牌 **/
	short S_ROOM_SHOW_CARDS = 14006;

	/** 提示出牌 **/
	short S_ROOM_PLAY_HINT = 14007;

	/** 重连房间 **/
	short S_ROOM_RELOAD = 14008;

	/** 游戏托管 **/
	short S_ROOM_DEPOSIT = 14009;

	/** 发送房间聊天 **/
	short S_ROOM_CHAT = 14010;

	/** 发送准备使用道具消息 **/
	short S_ROOM_USE_ITEM = 14011;

	/** 读取玩家正在参与的房间 **/
	short S_ROOM_GETROOM = 14012;

	/** 执行投票 **/
	short S_DDZ_ROOM_VOTE = 14013;
	/** 发起解散投票 **/
	short S_DDZ_ROOM_VOTE_CLOSE = 14014;
	/**
	 * 发牌工具
	 */
	short S_DDZ_ROOM_TOTLE = 14015;
	/**
	 * 闷抓或看牌
	 */
	short S_DDZ_MEN = 14016;
	/**
	 * 看牌抓或不抓
	 */
	short S_DDZ_KANPAI_ZHUA = 14017;
	/**
	 * 倒或不倒
	 */
	short S_DDZ_DAO = 14018;
	/**
	 * 拉或不拉
	 */
	short S_DDZ_LA = 14019;
	/**
	 * 查看战绩
	 */
	short S_DDZ_ZJ = 14020;
	/**
	 * 距离检查
	 */
	short S_DDZ_CHECK = 14021;
	/**
	 * 公会房间解散
	 */
	short S_DDZ_GUILD_DISSOLVE = 14022;

	/**
	 * 开关机器人
	 */
	short S_DDZ_OPEN_OR_CLOSE_AI = 14023;

	/******************** 麻将 : 14101~14200 ***************/
	/** 房间准备 **/
	short S_MJ_ROOM_PREPARE = 14101;
	/** 出牌 **/
	short S_MJ_ROOM_PLAY_CARD = 14102;
	/** 胡碰杠吃(1:碰 2:杠 3:吃 4:胡) **/
	short S_MJ_ROOM_HPGC = 14103;
	/** 聊天 **/
	short S_MJ_ROOM_CHAT = 14104;
	/** 房间离开 **/
	short S_MJ_ROOM_LEAVE = 14105;
	/** 房间分数排名 **/
	short S_MJ_ROOM_RANK = 14106;
	/** 执行投票 **/
	short S_MJ_ROOM_VOTE = 14107;
	/** 发起解散投票 **/
	short S_MJ_ROOM_VOTE_CLOSE = 14108;
	/** 选择缺一门的牌类型 **/
	short S_MJ_ROOM_QYM = 14109;
	/** 选择换三张的牌 **/
	short S_MJ_ROOM_HSZ = 14110;
	/** 听牌 **/
	short S_MJ_ROOM_TING = 14111;
	/** 飞 **/
	short S_MJ_ROOM_FEI = 14112;
	/** 提 **/
	short S_MJ_ROOM_TI = 14113;
	/** 指定下一张发什么牌 **/
	short S_MJ_ROOM_NEXT_DRAW = 14114;
	/** 指定初始牌 **/
	short S_MJ_ROOM_INIT_CARDS = 14115;
	/** 指定最后几张牌 **/
	short S_MJ_ROOM_LAST_CARDS = 14116;
	/** 开启或关闭房间机器人 **/
	short S_MJ_ROOM_AI_ENABLE = 14117;
	/** 指定下一局庄家 **/
	short S_MJ_ROOM_CHEAT_BANKER = 14118;
	/** 解散公会房间 **/
	short S_MJ_ROOM_DISSOLVE = 14119;
	/** 取消托管 **/
	short S_MJ_ROOM_DEPOSIT = 14120;
	/** 获取麻将胡牌张数提示 **/
	short S_MJ_ROOM_GET_HU_CARD = 14121;
	

	/******************** 斗金牛 : 14201~14301 ***************/
	/** 准备**/
	short S_BGF_ROOM_READY = 14201;
	/** 加注 **/
	short S_BGF_ROOM_EXFOLLOW = 14202;
	/** 开始游戏 前一个步骤 **/
	short S_BGF_ROOM_START_READY = 14203;
	/** 关闭茶馆房间 **/
	short S_BGF_CLOSE_GUILD = 14204;
	/** 开始游戏 **/
	short S_BGF_ROOM_STARTGAME = 14205;
	/** 看牌 X**/
//	short S_BGF_ROOM_LOOK = 14213;
	/** 重连房间 **/
	short S_BGF_ROOM_RELOAD = 14208;
	/** 游戏托管 **/
	short S_BGF_ROOM_DEPOSIT = 14209;
	/** 发送房间聊天 **/
	short S_BGF_ROOM_CHAT = 14210;
	/** 发送准备使用道具消息 X**/
//	short S_BGF_ROOM_USE_ITEM = 14211;
	/** 离开房间 **/
	short S_BGF_ROOM_LEAVE = 14212;
	/** 开关机器人 debug **/
	short S_BGF_AIPLAY = 14214;
	/** 抢庄 **/
	short S_BGF_BANKER = 14215;
	/** 投注 **/
	short S_BGF_BET = 14216;
	/** 获得牌组中剩余的牌 **/
	short S_BGF_GET_CARDS = 14217;
	/** 修改玩家的牌组 **/
	short S_BGF_CHANG_CARD = 14218;
	/** 分牌通知 **/
	short S_BGF_FENGPAI_CARD = 14219;
	/** 定制牌 debug*/
	short S_BGF_DEBUG_CARDS = 14301;

	/******************** 老虎机 : 14401 ~14500 ***************/

	/** 重连房间 **/
	short S_SLOTS_ROOM_RELOAD = 14401;
	/** 发送房间聊天 **/
	short S_SLOTS_ROOM_CHAT = 14405;
	/** 发送准备使用道具消息 **/
	short S_SLOTS_ROOM_USE_ITEM = 14410;
	/** 离开房间 **/
	short S_SLOTS_ROOM_LEAVE = 14415;
	/** 更新上庄列表 **/
	short S_SLOTS_ROOM_WAITS = 14420;
	/** 玩家下注 **/
	short S_SLOTS_ROOM_ADDBET = 14430;
	/** 玩家上庄 **/
	short S_SLOTS_ROOM_BEBANKER = 14435;
	/** 玩家下庄 **/
	short S_SLOTS_ROOM_UNBANKER = 14440;
	/** 查看上庄列表 **/
	short S_SLOTS_ROOM_GET_BANKERS = 14445;
	/** 查看玩家列表 **/
	short S_SLOTS_ROOM_GET_PLAYERS = 14450;
	/** 玩家续住 **/
	short S_SLOTS_ROOM_PLAYER_DOLAST = 14451;
	/** 玩家自动下注 **/
	short S_SLOTS_ROOM_PLAYER_AUTODO = 14453;

	/******************** 百家乐 : 14501 ~14600 ***************/

	/** 发送房间聊天 **/
	short S_BJL_ROOM_CHAT = 14505;
	/** 发送准备使用道具消息 **/
	short S_BJL_ROOM_USE_ITEM = 14510;
	/** 离开房间 **/
	short S_BJL_ROOM_LEAVE = 14515;
	/** 玩家下注 **/
	short S_BJL_ROOM_ADDBET = 14530;
	/** 玩家上庄 **/
	short S_BJL_ROOM_BEBANKER = 14535;
	/** 玩家下庄 **/
	short S_BJL_ROOM_UNBANKER = 14540;
	/** 查看上庄列表 **/
	short S_BJL_ROOM_GET_BANKERS = 14545;
	/** 查看玩家列表 **/
	short S_BJL_ROOM_GET_PLAYERS = 14550;
	/** 玩家续住 **/
	short S_BJL_ROOM_PLAYER_DOLAST = 14551;

	/******************** 炸金花 : 14601~14700 ***************/
	/** 跟注 **/
	short S_ZJH_ROOM_FOLLOW = 14601;
	/** 加注 **/
	short S_ZJH_ROOM_EXFOLLOW = 14602;
	/** 弃牌 **/
	short S_ZJH_ROOM_ABANDON = 14603;
	/** 亮牌 **/
	short S_ZJH_ROOM_SHOWCARD = 14604;
	/** 比牌 **/
	short S_ZJH_ROOM_PK = 14605;
	/** 看牌 **/
	short S_ZJH_ROOM_LOOK = 14613;
	/** 重连房间 **/
	short S_ZJH_ROOM_RELOAD = 14608;
	/** 游戏跟任何注 **/
	short S_ZJH_ROOM_FOLLOW_END = 14609;
	/** 发送房间聊天 **/
	short S_ZJH_ROOM_CHAT = 14610;
	/** 发送准备使用道具消息 **/
	short S_ZJH_ROOM_USE_ITEM = 14611;
	/** 离开房间 **/
	short S_ZJH_ROOM_LEAVE = 14612;
	/** 智能操作（测试用） **/
	short S_ZJH_AIPLAY = 14614;
	/** 抢庄 **/
	short S_ZJH_BANKER = 14615;
	/** 投注 **/
	short S_ZJH_BET = 14616;
	/** 工具选牌 **/
	short S_ZJH_TOOL = 14617;
	/** 房主开始游戏 **/
	short S_ZJH_TO_START = 14618;
	/** 托管设置 **/
	short S_ZJH_TO_SETDEPOSIT = 14619;
	/** 确定位置 **/
	short S_ZJH_TO_CONFIRM_LOCATION = 14620;
	/** 解散公会房间 **/
	short S_ZJH_ROOM_DISSOLVE = 14621;

	/******************** 德州扑克 : 14701~14800 ***************/
	/** 跟注 **/
	short S_TH_ROOM_FOLLOW = 14701;
	/** 加注 **/
	short S_TH_ROOM_EXFOLLOW = 14702;
	/** 弃牌 **/
	short S_TH_ROOM_ABANDON = 14703;
	/** 亮牌 **/
	short S_TH_ROOM_SHOWCARD = 14704;
	/** PK **/
	short S_TH_ROOM_PK = 14705;
	/** 让牌 **/
	short S_TH_ROOM_LET = 14706;
	/** 全下 **/
	short S_TH_ROOM_ALLIN = 14713;
	/** 重连房间 **/
	short S_TH_ROOM_RELOAD = 14708;
	/** 游戏托管 **/
	short S_TH_ROOM_DEPOSIT = 14709;
	/** 发送房间聊天 **/
	short S_TH_ROOM_CHAT = 14710;
	/** 发送准备使用道具消息 **/
	short S_TH_ROOM_USE_ITEM = 14711;
	/** 离开房间 **/
	short S_TH_ROOM_LEAVE = 14712;
	/** 智能操作（测试用） **/
	short S_TH_AIPLAY = 14714;

	/******************** 扯旋 : 14801~14900 ***************/
	/** 跟注 **/
	short S_DX_ROOM_FOLLOW = 14801;
	/**房主开始游戏 1*/
	short S_DX_ROOM_STARTGAME = 14802;
	/** 弃牌 **/
	short S_DX_ROOM_ABANDON = 14803;
	/** 开始游戏 2 **/
	short S_DX_ROOM_STARTGAME_CHECK = 14804;
	/** 准备 **/
	short S_DX_ROOM_READY = 14808;
	/** 游戏托管 **/
	short S_DX_ROOM_DEPOSIT = 14809;
	/** 发送房间聊天 **/
	short S_DX_ROOM_CHAT = 14810;
	/** 离开房间 **/
	short S_DX_ROOM_LEAVE = 14812;
	/** 智能操作（测试用） **/
	short S_DX_AIPLAY = 14814;
	/** 进行起钵钵 **/
	short S_DX_QIBOBO = 14815;
	/** 进行敲 **/
	short S_DX_QIAO = 14816;
	/** 进行扯牌 **/
	short S_DX_CHEPAI = 14817;
	/** 求出牌值 **/
	short S_DX_CARTTYPE = 14818;
	/** 发牌Debug**/
	short S_DX_CARDDEBUG = 14819;
	/** 休*/
	short S_DX_XIUPAI = 14820;
	/** 亮牌*/
	short S_DX_LIANGPAI = 14821;
	/** 关闭茶馆房间*/
	short S_DX_CLOSE_GUILD = 14822;

	/******************** snatch : 15001~16000 ***************/

	/** 该模板的往期揭晓 **/
	short S_SNATCHTEMP_RECORD = 15001;

	/** 该模板当前期号的参与玩家的所有记录 **/
	short S_SNATCH_JOIN_RECORD = 15002;

	/** 获得我的夺宝记录 **/
	short S_SELF_SNATCH = 15003;

	/** 获得其他用户夺宝 **/
	short S_USER_SNATCH = 15004;

	/** 获得该抢宝期号的所有抢宝号 **/
	short S_GET_ALL_SNATCHID = 15005;

	/** 获得抢宝的数值A,B,C的详情数据 **/
	short S_GET_SNATCH_ABC = 15006;

	/** 获得抢宝的数值A Or B的详情数据 **/
	short S_GET_SNATCH_AORB = 15007;

	/** 获得抢宝列表 **/
	short S_SNATCH_LIST = 15008;

	/** 进行抢宝 **/
	short S_SNATCH_JOIN = 15009;

	/** 进入抢宝模块 **/
	short S_IN_SNATCH_MODULE = 15010;

	/** 退出抢宝模块 **/
	short S_OUT_SNATCH_MODULE = 15011;

	/** 获得抢宝参与的分期抢宝号 **/
	short S_GET_PART_SNATCHID = 15012;

	/** 获得抢宝消息 **/
	short S_GET_SNATCH_INFO = 15013;

	/** 领取抢宝奖励 **/
	short S_GET_SNATCH_AWARD = 15014;

	/** 获得抢宝订单模块中的消息 **/
	short S_GET_SNATCHORDER = 15015;

	/******************** control: 16001~17000 ***************/
	/** 读取报名中的夺宝赛 **/
	short S_CONTEST_LIST = 16001;

	/** 报名参加夺宝赛 **/
	short S_CONTEST_JOIN = 16002;

	/** 夺宝赛报名取消 **/
	short S_CONTEST_QUIT = 16003;

	/** 读取夺宝赛排名 **/
	short S_CONTEST_RANK = 16004;

	/** 读取自己当前参加的夺宝赛 **/
	short S_CONTEST_SELFPLAY = 16005;

	/** 读取夺宝赛数据 **/
	short S_CONTEST_GET = 16006;

	/** 读取夺宝赛记录数据 **/
	short S_CONTEST_GET_RECORD = 16007;

	/** 读取夺宝赛胜利记录数据 **/
	short S_CONTEST_GET_WIN_RECORD = 16008;
	
	/**************************************Guil 公会模块 : 20001~18000 ***********************/
	/**
	 * 打开公会面板
	 */
	short S_GUILD_OPEN_PANEL = 20001;
	
	/**
	 * 创建公会
	 */
	short S_GUILD_CREATE_GUILD = 20002;
	
	/**
	 * 加入公会
	 */
	short S_GUILD_JOIN_GUILD = 20003;
	

	/**
	 * 进入公会
	 */
	short S_GUILD_IN_GUILD = 20004;
	
	/**
	 * 打开消息面板
	 */
	short S_GUILD_OPEN_MESSAGE_PANEL = 20005;
	
	/**
	 * 处理消息
	 */
	short S_GUILD_DEAL_MESSAGE = 20006;
	
	/**
	 * 打开公告面板
	 */
	short S_GUILD_OPEN_NOTICE_PANEL = 20007;
	
	/**
	 * 修改公告
	 */
	short S_GUILD_CHANGE_NOTICE = 20008;
	
	/**
	 * 打开基金面板
	 */
	short S_GUILD_OPEN_FUND_PANEL = 20009;
	
	/**
	 * 充值基金
	 */
	short S_GUILD_PAY_FUND = 20010;
	
	/**
	 * 打开基金帐单面板
	 */
	short S_GUILD_OPEN_FUND_FLOW_PANEL = 20011;
	
	/**
	 * 打开成员列表
	 */
	short S_GUILD_OPEN_MEMBER_LIST = 20012;
	
	/**
	 * 成员权限设置
	 */
	short S_GUILD_MEMBER_SET_AUTH = 20013;
	
	/**
	 * 成员踢出公会
	 */
	short S_GUILD_MEMBER_REMOVE = 20014;
	
	/**
	 * 成员退出公会
	 */
	short S_GUILD_MEMBER_EXIT = 20015;
	
	/**
	 * 解散公会
	 */
	short S_GUILD_DISSOLVE = 20016;
	
	/**
	 * 修改公会名称
	 */
	short S_GUILD_CHANGE_NAME = 20017;
	
	/**
	 * 公会高级设置
	 */
	short S_GUILD_SET = 20018;
	/**
	 * 房间列表
	 */
	short S_GUILD_ROOM_LIST = 20019;
	
	/******************** 龙虎斗: 21001~22000 ***************/
	/** 龙虎斗下注 **/
	short S_LHD_ROOM_BET = 21001;
	/** 龙虎斗离开房间 **/
	short S_LHD_ROOM_LEAVE = 21002;
	/** 获取龙虎斗上庄列表 **/
	short S_LHD_BANKER_LIST = 21003;
	/** 上庄 **/
	short S_LHD_BANKER_UP = 21004;
	/** 下庄 **/
	short S_LHD_BANKER_DOWN = 21005;
	/** 确认上庄 **/
	short S_LHD_BANKER_CONFIRM = 21006;
	/** 玩家列表 **/
	short S_LHD_PLAYER_LIST = 21007;
	
	
}
