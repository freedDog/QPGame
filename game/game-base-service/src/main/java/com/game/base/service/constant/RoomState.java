package com.game.base.service.constant;

/**
 * 房间状态
 * RoomState.java
 * @author JiangBangMing
 * 2019年1月8日下午5:08:10
 */
public class RoomState {

	/** 无 **/
	public static final int NONE = 0;

	/** 准备阶段(玩家进入, 准备) **/
	public static final int PREPARE = 1;

	/** 开始游戏 **/
	public static final int START = 2;

	/** 进行中 **/
	public static final int PLAYING = 3;

	/** 结束阶段 **/
	public static final int OVER = 4;

	/** 关闭阶段 **/
	public static final int CLOSE = 5;
}
