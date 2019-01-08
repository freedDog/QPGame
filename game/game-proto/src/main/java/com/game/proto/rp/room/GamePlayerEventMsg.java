package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.room.GamePlayerMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GamePlayerEventMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 事件类型, 1进入房间, 2离开房间, 3叫地主.
	private int type;
	// 玩家信息, 根据消息类型有些类型事件没有
	private GamePlayerMsg info;
	// 额外整形参数
	private int intVal;
	// 额外整形列表参数
	private List<Integer> intVals = new ArrayList<Integer>();
	// 倒计时, 0为没有(不过也不可能)
	private int cdTime;

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 事件类型, 1进入房间, 2离开房间, 3叫地主. */
	public int getType() {
		return type;
	}

	/** 事件类型, 1进入房间, 2离开房间, 3叫地主. */
	public void setType(int value) {
		this.type = value;
	}

	/** 玩家信息, 根据消息类型有些类型事件没有 */
	public GamePlayerMsg getInfo() {
		return info;
	}

	/** 玩家信息, 根据消息类型有些类型事件没有 */
	public void setInfo(GamePlayerMsg value) {
		this.info = value;
	}

	/** 额外整形参数 */
	public int getIntVal() {
		return intVal;
	}

	/** 额外整形参数 */
	public void setIntVal(int value) {
		this.intVal = value;
	}

	public List<Integer> getIntVals() {
		return intVals;
	}
		
	public void addIntVals(int value) {
		this.intVals.add(value);
	}
		
	public void addAllIntVals(List<Integer> values) {
		this.intVals.addAll(values);
	}

	/** 倒计时, 0为没有(不过也不可能) */
	public int getCdTime() {
		return cdTime;
	}

	/** 倒计时, 0为没有(不过也不可能) */
	public void setCdTime(int value) {
		this.cdTime = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putObject(buffer, info);
		ByteBufferHelper.putInt(buffer, intVal);
		ByteBufferHelper.putIntArray(buffer, intVals);
		ByteBufferHelper.putInt(buffer, cdTime);
	}

	public static GamePlayerEventMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GamePlayerEventMsg deserialize(ByteBuffer buffer) {
		GamePlayerEventMsg messageInstance = new GamePlayerEventMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		if (buffer.getShort() > 0) {
			messageInstance.info = GamePlayerMsg.deserialize(buffer);
		}
		messageInstance.intVal = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.intVals);
		messageInstance.cdTime = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 20;
		length += ByteBufferHelper.calcObjectLength(info);
		length += 2 + intVals.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GamePlayerEventMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("type=" + type + ", ");
		sb.append("info=" + info + ", ");
		sb.append("intVal=" + intVal + ", ");
		sb.append("intVals=" + intVals + ", ");
		sb.append("cdTime=" + cdTime + ", ");
		sb.append("]");
		return sb.toString();
	}
}