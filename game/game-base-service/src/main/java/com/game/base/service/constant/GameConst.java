package com.game.base.service.constant;

import com.game.framework.utils.TimeUtils;

/**
 * 游戏中使用的一些公共的变量
 * GameConst.java
 * @author JiangBangMing
 * 2019年1月8日下午2:34:30
 */
public interface GameConst {
	/** 玩家空闲数据卸载时间 **/
	long PLAYER_UNLOAD_INTERVAL = 10 * 60 * 1000;
	/** 房间空闲数据卸载时间 **/
	long ROOM_UNLOAD_INTERVAL = 10 * 1000;

	/** 货币最大数量 **/
	int CURRENCY_MAX = 2100000000;

	/** 角色名字长度限制 **/
	short PLAYER_NAME_MIN_LEN = 1;

	/** 角色名字长度限制 **/
	short PLAYER_NAME_MAX_LEN = 8;

	/** 邮件格子数量 **/
	short MAIL_ATTACH_COUNT = 5;

	/** 系统邮件持续时间 **/
	int MAIL_DEFAULT_TIME = TimeUtils.oneDayTime * 7;

//	/** 基础时装-男孩 **/
//	int FASHION_BASE_BOY = 2;
//
//	/** 基础时装-女孩 **/
//	int FASHION_BASE_GIRL = 1;

	/** 麻将房间Id范围 **/
	int MAHJOING_ROOMID_MAX = 1000000;

}