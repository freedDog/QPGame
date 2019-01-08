package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class PlayCardMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 当前牌数
	private int nowCardCount;
	// 出的牌
	private List<Integer> card = new ArrayList<Integer>();
	// 出的牌组成的类型
	private int cardType;

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 当前牌数 */
	public int getNowCardCount() {
		return nowCardCount;
	}

	/** 当前牌数 */
	public void setNowCardCount(int value) {
		this.nowCardCount = value;
	}

	public List<Integer> getCard() {
		return card;
	}
		
	public void addCard(int value) {
		this.card.add(value);
	}
		
	public void addAllCard(List<Integer> values) {
		this.card.addAll(values);
	}

	/** 出的牌组成的类型 */
	public int getCardType() {
		return cardType;
	}

	/** 出的牌组成的类型 */
	public void setCardType(int value) {
		this.cardType = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putInt(buffer, nowCardCount);
		ByteBufferHelper.putIntArray(buffer, card);
		ByteBufferHelper.putInt(buffer, cardType);
	}

	public static PlayCardMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static PlayCardMsg deserialize(ByteBuffer buffer) {
		PlayCardMsg messageInstance = new PlayCardMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.nowCardCount = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.card);
		messageInstance.cardType = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		length += 2 + card.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PlayCardMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("nowCardCount=" + nowCardCount + ", ");
		sb.append("card=" + card + ", ");
		sb.append("cardType=" + cardType + ", ");
		sb.append("]");
		return sb.toString();
	}
}