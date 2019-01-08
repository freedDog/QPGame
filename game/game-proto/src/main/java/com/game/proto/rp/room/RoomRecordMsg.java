package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class RoomRecordMsg extends RpMessage {
	// 加分(加倍)
	private int score;
	// 当前分数
	private int nowScore;
	// 类型
	private int type;
	// 目标: 0本家, 1:下家, 2:对家, 3:上家
	private int target;

	/** 加分(加倍) */
	public int getScore() {
		return score;
	}

	/** 加分(加倍) */
	public void setScore(int value) {
		this.score = value;
	}

	/** 当前分数 */
	public int getNowScore() {
		return nowScore;
	}

	/** 当前分数 */
	public void setNowScore(int value) {
		this.nowScore = value;
	}

	/** 类型 */
	public int getType() {
		return type;
	}

	/** 类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 目标: 0本家, 1:下家, 2:对家, 3:上家 */
	public int getTarget() {
		return target;
	}

	/** 目标: 0本家, 1:下家, 2:对家, 3:上家 */
	public void setTarget(int value) {
		this.target = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, score);
		ByteBufferHelper.putInt(buffer, nowScore);
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, target);
	}

	public static RoomRecordMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RoomRecordMsg deserialize(ByteBuffer buffer) {
		RoomRecordMsg messageInstance = new RoomRecordMsg();
		messageInstance.score = ByteBufferHelper.getInt(buffer);
		messageInstance.nowScore = ByteBufferHelper.getInt(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.target = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RoomRecordMsg[");
		sb.append("score=" + score + ", ");
		sb.append("nowScore=" + nowScore + ", ");
		sb.append("type=" + type + ", ");
		sb.append("target=" + target + ", ");
		sb.append("]");
		return sb.toString();
	}
}