package com.game.room.constant;

/**
 * 房间重置码
 * RoomResetCode.java
 * @author JiangBangMing
 * 2019年1月8日下午5:15:35
 */
public class RoomResetCode {
	/** 正常关闭 **/
	public static final int NORMAL_CLOSE = 4;
	/** 等待超时 **/
	public static final int WAIT_TIMEOUT = 3;

	/** 完成下局重置 **/
	public static final int NEXT_CLOSE = 2;

	/** 完成下局重置 **/
	public static final int NEXT_RESET = 1;

	/** 房间卸载 **/
	public static final int UNLOAD = 0;

	/** 执行错误 **/
	public static final int ACTION_ERROR = -1;

	/** 下个玩家不存在 **/
	public static final int NEXT_PLAYER_ERROR = -2;

	/** 玩家已存在房间 **/
	public static final int PLAYE_HAS_ROOM = -3;

	/** 状态切换错误 **/
	public static final int STATE_CHANGE_ERROR = -4;

	/** 找不到胜利玩家 **/
	public static final int NO_WIN_PLAYER = -5;

	/** 玩家位置上为空 **/
	public static final int SEAT_EMPTY = -6;

	/** 加载玩家错误 **/
	public static final int LOAD_PLAYER_ERROR = -7;

}