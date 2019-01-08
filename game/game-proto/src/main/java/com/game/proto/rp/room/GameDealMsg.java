package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameDealMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 牌
	private List<Integer> card = new ArrayList<Integer>();
	// 地主牌
	private List<Integer> landlordCard = new ArrayList<Integer>();

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
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

	public List<Integer> getLandlordCard() {
		return landlordCard;
	}
		
	public void addLandlordCard(int value) {
		this.landlordCard.add(value);
	}
		
	public void addAllLandlordCard(List<Integer> values) {
		this.landlordCard.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putIntArray(buffer, card);
		ByteBufferHelper.putIntArray(buffer, landlordCard);
	}

	public static GameDealMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameDealMsg deserialize(ByteBuffer buffer) {
		GameDealMsg messageInstance = new GameDealMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.card);
		ByteBufferHelper.readIntArray(buffer, messageInstance.landlordCard);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		length += 2 + card.size() * 4;
		length += 2 + landlordCard.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameDealMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("card=" + card + ", ");
		sb.append("landlordCard=" + landlordCard + ", ");
		sb.append("]");
		return sb.toString();
	}
}