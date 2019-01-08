package com.game.room.constant;

/**
 * 房间离开事件
 * RoomLeaveType.java
 * @author JiangBangMing
 * 2019年1月8日下午5:14:21
 */
public class RoomLeaveType {
	/** 常规(正常的离开操作) **/
	public static final int NORMAL = 0;

	/** 货币不足了 **/
	public static final int CURRENCY_MIN = -1;

	/** 货币超过 **/
	public static final int CURRENCY_MAX = -2;

	/** 强制关闭 **/
	public static final int CLOSE = -3;

	/** 离线关闭 **/
	public static final int OFFLINE = -4;

	/** 换桌 **/
	public static final int CHANGE = -5;

	/** 卸载 **/
	public static final int UNLOAD = -6;

	/** 等待超时 **/
	public static final int WAIT_TIMEOUT = -7;
	
	/** 长时间未操作 **/
	public static final int IDLE = -8;
	
	/** 房间正常结束 **/
	public static final int ROOM_OVER = -9;
	
	/** 服务器维护 **/
	public static final int MAINTENANCE = -10;
	
	/** 后台强制解散 **/
	public static final int GM_FORCE_DISSOLVE = -11;
	
	/** 玩的局数达到最大(AI) **/
	public static final int MAX_PLAY_COUNT = -12;
	
}
