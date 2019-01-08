package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameRoomMsg extends RpMessage {
	// 房间Id
	private long id;
	// 房间类型: 0斗地主
	private int type;
	// 房间状态: 1准备阶段, 2开始, 3游戏进行中, 4结束, 5关闭
	private int state;
	// 游戏状态
	private int gameState;
	// 底牌倍率
	private float landlordDoubleRate;
	// 地主牌(底牌)
	private List<Integer> landlordCards = new ArrayList<Integer>();
	// 货币Id
	private int currencyId;
	// 开局消耗货币
	private int expend;
	// 大厅模板Id
	private int lobbyId;
	// 大厅类型(游戏类型)
	private int kindType;

	/** 房间Id */
	public long getId() {
		return id;
	}

	/** 房间Id */
	public void setId(long value) {
		this.id = value;
	}

	/** 房间类型: 0斗地主 */
	public int getType() {
		return type;
	}

	/** 房间类型: 0斗地主 */
	public void setType(int value) {
		this.type = value;
	}

	/** 房间状态: 1准备阶段, 2开始, 3游戏进行中, 4结束, 5关闭 */
	public int getState() {
		return state;
	}

	/** 房间状态: 1准备阶段, 2开始, 3游戏进行中, 4结束, 5关闭 */
	public void setState(int value) {
		this.state = value;
	}

	/** 游戏状态 */
	public int getGameState() {
		return gameState;
	}

	/** 游戏状态 */
	public void setGameState(int value) {
		this.gameState = value;
	}

	/** 底牌倍率 */
	public float getLandlordDoubleRate() {
		return landlordDoubleRate;
	}

	/** 底牌倍率 */
	public void setLandlordDoubleRate(float value) {
		this.landlordDoubleRate = value;
	}

	public List<Integer> getLandlordCards() {
		return landlordCards;
	}
		
	public void addLandlordCards(int value) {
		this.landlordCards.add(value);
	}
		
	public void addAllLandlordCards(List<Integer> values) {
		this.landlordCards.addAll(values);
	}

	/** 货币Id */
	public int getCurrencyId() {
		return currencyId;
	}

	/** 货币Id */
	public void setCurrencyId(int value) {
		this.currencyId = value;
	}

	/** 开局消耗货币 */
	public int getExpend() {
		return expend;
	}

	/** 开局消耗货币 */
	public void setExpend(int value) {
		this.expend = value;
	}

	/** 大厅模板Id */
	public int getLobbyId() {
		return lobbyId;
	}

	/** 大厅模板Id */
	public void setLobbyId(int value) {
		this.lobbyId = value;
	}

	/** 大厅类型(游戏类型) */
	public int getKindType() {
		return kindType;
	}

	/** 大厅类型(游戏类型) */
	public void setKindType(int value) {
		this.kindType = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, id);
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, state);
		ByteBufferHelper.putInt(buffer, gameState);
		ByteBufferHelper.putFloat(buffer, landlordDoubleRate);
		ByteBufferHelper.putIntArray(buffer, landlordCards);
		ByteBufferHelper.putInt(buffer, currencyId);
		ByteBufferHelper.putInt(buffer, expend);
		ByteBufferHelper.putInt(buffer, lobbyId);
		ByteBufferHelper.putInt(buffer, kindType);
	}

	public static GameRoomMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameRoomMsg deserialize(ByteBuffer buffer) {
		GameRoomMsg messageInstance = new GameRoomMsg();
		messageInstance.id = ByteBufferHelper.getLong(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.state = ByteBufferHelper.getInt(buffer);
		messageInstance.gameState = ByteBufferHelper.getInt(buffer);
		messageInstance.landlordDoubleRate = ByteBufferHelper.getFloat(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.landlordCards);
		messageInstance.currencyId = ByteBufferHelper.getInt(buffer);
		messageInstance.expend = ByteBufferHelper.getInt(buffer);
		messageInstance.lobbyId = ByteBufferHelper.getInt(buffer);
		messageInstance.kindType = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 40;
		length += 2 + landlordCards.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameRoomMsg[");
		sb.append("id=" + id + ", ");
		sb.append("type=" + type + ", ");
		sb.append("state=" + state + ", ");
		sb.append("gameState=" + gameState + ", ");
		sb.append("landlordDoubleRate=" + landlordDoubleRate + ", ");
		sb.append("landlordCards=" + landlordCards + ", ");
		sb.append("currencyId=" + currencyId + ", ");
		sb.append("expend=" + expend + ", ");
		sb.append("lobbyId=" + lobbyId + ", ");
		sb.append("kindType=" + kindType + ", ");
		sb.append("]");
		return sb.toString();
	}
}