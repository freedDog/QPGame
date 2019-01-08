package com.game.room.base;

import com.game.base.service.connector.Connector;
import com.game.base.utils.RoomLogger;
import com.game.framework.component.ChangeCounter;
import com.game.proto.msg.RpMessage;
import com.game.room.player.GamePlayer;

/**
 * 房间内玩家对象
 * GameRoomPlayer.java
 * @author JiangBangMing
 * 2019年1月8日下午4:51:00
 */
public class GameRoomPlayer<T extends GameRoom<?>> extends Connector {
	protected final GamePlayer player; // 玩家对象
	protected T room;
	protected RoomLogger logger;

	public final ChangeCounter changeCounter;

	public GameRoomPlayer(GamePlayer player, T room) {
		this.player = player;
		this.room = room;
		this.logger = room.logger;

		// 计时器发送更新消息
		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				onChangeCounter();
			}
		};
	}

	/** 是否准备 **/
	public boolean isPrepare() {
		return false;
	}

	/** 数据更新 **/
	protected void onChangeCounter() {

	}

	/** 是否开启透视 **/
	public boolean isPrespective() {
		return player.isPrespective();
	}

	/** 玩家进入(在房间内已经进入了) **/
	protected void onPlayerEnter(T room) {
		logger.debug("玩家加入房间! " + this + " -> " + room);
	}

	/** 玩家离开(在房间内已经离开了) **/
	protected void onPlayerLeave(T room) {
		logger.debug("玩家离开房间! " + this + " <- " + room);
	}

	/** 房间重置 **/
	protected void onRoomReset(int code, String errStr) {
	}

	/** 房间重置前的关闭 **/
	public void onClose(int code, String errStr) {
	}

	/** 更新处理(在房间内才会更新) **/
	protected void onUpdate() {
	}

	@Override
	public long getPlayerId() {
		return (player != null) ? player.getPlayerId() : 0L;
	}
	
	/** 获取玩家名 **/
	public String getName() {
		return (player != null) ? player.getName() : null;
	}

	/** 获取玩家 **/
	public GamePlayer getPlayer() {
		return player;
	}

	/** 获取房间 **/
	public <R extends GameRoom<?>> R getRoom() {
		return player.getRoom();
	}

	/** 获取房间 **/
	public <R extends GameRoom<?>> R getRoom(Class<R> clazz) {
		return player.getRoom(clazz);
	}

	/** 是否是内置机器人 **/
	public boolean isLocalRobot() {
		// 通过Id处理
		if (player.getPlayerId() <= 0) {
			return true;
		}
		// 判断类型
		if (player.isLocalRobet()) {
			return true;
		}
		return false;
	}

	/** 是否是机器人 **/
	public boolean isRobot() {
		// 通过Id处理
		if (player.getPlayerId() <= 0) {
			return true;
		}
		// 判断类型
		if (player.isRobet()) {
			return true;
		}
		return false;
	}

	/** 是否断线(离开) **/
	public boolean isOnline() {
		return (player != null) ? player.isOnline() : false;
	}

	@Override
	public void sendPacket(short code, RpMessage rpMsg) {
		// 玩家消息发送
		player.sendPacket(code, rpMsg);
	}

	@Override
	public String toString() {
		return "RoomPlayer [playerId=" + getPlayerId() + " name=" + getName() +  " robot=" + isRobot() + "]";
	}

}
