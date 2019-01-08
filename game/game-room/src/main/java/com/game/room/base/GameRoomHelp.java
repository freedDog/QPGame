package com.game.room.base;

import com.game.room.base.GameRoomMgr.IRoomFiltr;

/**
 * 游戏房间助手<br>
 * GameRoomHelp.java
 * @author JiangBangMing
 * 2019年1月8日下午6:06:50
 */
public class GameRoomHelp {

	/** 提交任务 **/
	public static void enqueue(Runnable runnable) {
		GameRoomMgr.getInstance().enqueue(runnable);
	}

	/** 筛选获取房间 **/
	public static GameRoom<?> getRoom(IRoomFiltr<? super GameRoom<?>> filter) {
		return getRoom(filter, GameRoom.class);
	}

	/** 筛选获取房间 **/
	@SuppressWarnings("unchecked")
	public static <T extends GameRoom<?>> T getRoom(IRoomFiltr<? super GameRoom<?>> filter, Class<T> clazz) {
		GameRoom<?> room = GameRoomMgr.getInstance().getRoom(filter);
		if (!clazz.isInstance(room)) {
			return null;
		}
		return (T) room;
	}

	/** 根据房间Id获取房间 **/
	@SuppressWarnings("unchecked")
	public static <R> R getRoom(long roomId, Class<R> clazz) {
		GameRoom<?> room = GameRoomMgr.getInstance().getRoom(roomId);
		if (!clazz.isInstance(room)) {
			return null;
		}
		return (R) room;
	}

	/** 获取新Id **/
	protected static long newRoomId() {
		return GameRoomMgr.getInstance().newId();
	}

	/** 添加创建房间 **/
	protected static boolean addRoom(long newId, GameRoom<?> room) {
		GameRoom<?> old = GameRoomMgr.getInstance().addByCreate(newId, room);
		return old == room;
	}
}
