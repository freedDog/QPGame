package com.game.room.robot;

/**
 * 斗地主常量
 * RobotConst.java
 * @author JiangBangMing
 * 2019年1月8日下午6:12:49
 */
public class RobotConst {

	/************************ 机器人逻辑 *****************************/

	/** 机器人最快出牌时间 **/
	public static final int ROBET_PLAY_MIN_TIME = 1000;
	/** 机器人最慢出牌时间 **/
	public static final int ROBET_PLAY_MAX_TIME = 6000;

	/** 机器人等待操作时间(离开或者催促) **/
	public static final int ROBET_WAIT_TIME = 20 * 1000;

	/** 机器人等待操作时间(催促), 游玩中 **/
	public static final int ROBET_WAIT_TIME_INPLAY = 15 * 1000;

	/** 房间重置机器人离开概率 **/
	public static final int ROBET_RATE_LEAVE_RESET = 50;
	/** 机器人等待超时离开 **/
	public static final int ROBET_RATE_WAIT_LEAVE = 50;
	/** 机器人等待超时催促 **/
	public static final int ROBET_RATE_WAIT_URAGE = 0;

	/** 机器人炸聊天 **/
	public static final int ROBET_RATE_CHAT_BOOM = 3;
	/** 机器人不要表情 **/
	public static final int ROBET_RATE_CHAT_NOPLAY = 5;

	/** 机器人最多玩的盘数 **/
	public static final int ROBET_ROUND_COUNT = 10;
	/** 机器人休息时间 **/
	public static final int ROBET_BAN_TIME = 600;
	
	/** 机器人最快进入时间 **/
	public static final int ROBOT_MIN_ENTER_TIME = 1;
	/** 机器人最慢进入时间 **/
	public static final int ROBOT_MAX_ENTER_TIME = 7;

}
