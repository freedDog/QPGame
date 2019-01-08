package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameRoomTaskMsg extends RpMessage {
	// 任务类型, 1最后出的手牌类型
	private int type;
	// 牌型
	private int cardType;
	// 牌值
	private int cardNum;
	// 完成倍率
	private int rate;

	/** 任务类型, 1最后出的手牌类型 */
	public int getType() {
		return type;
	}

	/** 任务类型, 1最后出的手牌类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 牌型 */
	public int getCardType() {
		return cardType;
	}

	/** 牌型 */
	public void setCardType(int value) {
		this.cardType = value;
	}

	/** 牌值 */
	public int getCardNum() {
		return cardNum;
	}

	/** 牌值 */
	public void setCardNum(int value) {
		this.cardNum = value;
	}

	/** 完成倍率 */
	public int getRate() {
		return rate;
	}

	/** 完成倍率 */
	public void setRate(int value) {
		this.rate = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, cardType);
		ByteBufferHelper.putInt(buffer, cardNum);
		ByteBufferHelper.putInt(buffer, rate);
	}

	public static GameRoomTaskMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameRoomTaskMsg deserialize(ByteBuffer buffer) {
		GameRoomTaskMsg messageInstance = new GameRoomTaskMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.cardType = ByteBufferHelper.getInt(buffer);
		messageInstance.cardNum = ByteBufferHelper.getInt(buffer);
		messageInstance.rate = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameRoomTaskMsg[");
		sb.append("type=" + type + ", ");
		sb.append("cardType=" + cardType + ", ");
		sb.append("cardNum=" + cardNum + ", ");
		sb.append("rate=" + rate + ", ");
		sb.append("]");
		return sb.toString();
	}
}