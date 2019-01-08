package com.game.proto.rp.lobby;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class LobbyMsg extends RpMessage {
	// 大厅Id
	private int id;
	// 大厅名字
	private String name;
	// 当前玩家数量
	private int playerCount;
	// 底分
	private int baseScore;
	// 货币类型
	private int currencyId;
	// 每局消耗
	private int expend;
	// 资源最小限制
	private int currencyLimitMin;
	// 资源最大限制
	private int currencyLimitMax;
	// 大厅类型
	private int kindType;

	/** 大厅Id */
	public int getId() {
		return id;
	}

	/** 大厅Id */
	public void setId(int value) {
		this.id = value;
	}

	/** 大厅名字 */
	public String getName() {
		return name;
	}

	/** 大厅名字 */
	public void setName(String value) {
		this.name = value;
	}

	/** 当前玩家数量 */
	public int getPlayerCount() {
		return playerCount;
	}

	/** 当前玩家数量 */
	public void setPlayerCount(int value) {
		this.playerCount = value;
	}

	/** 底分 */
	public int getBaseScore() {
		return baseScore;
	}

	/** 底分 */
	public void setBaseScore(int value) {
		this.baseScore = value;
	}

	/** 货币类型 */
	public int getCurrencyId() {
		return currencyId;
	}

	/** 货币类型 */
	public void setCurrencyId(int value) {
		this.currencyId = value;
	}

	/** 每局消耗 */
	public int getExpend() {
		return expend;
	}

	/** 每局消耗 */
	public void setExpend(int value) {
		this.expend = value;
	}

	/** 资源最小限制 */
	public int getCurrencyLimitMin() {
		return currencyLimitMin;
	}

	/** 资源最小限制 */
	public void setCurrencyLimitMin(int value) {
		this.currencyLimitMin = value;
	}

	/** 资源最大限制 */
	public int getCurrencyLimitMax() {
		return currencyLimitMax;
	}

	/** 资源最大限制 */
	public void setCurrencyLimitMax(int value) {
		this.currencyLimitMax = value;
	}

	/** 大厅类型 */
	public int getKindType() {
		return kindType;
	}

	/** 大厅类型 */
	public void setKindType(int value) {
		this.kindType = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, id);
		ByteBufferHelper.putString(buffer, name);
		ByteBufferHelper.putInt(buffer, playerCount);
		ByteBufferHelper.putInt(buffer, baseScore);
		ByteBufferHelper.putInt(buffer, currencyId);
		ByteBufferHelper.putInt(buffer, expend);
		ByteBufferHelper.putInt(buffer, currencyLimitMin);
		ByteBufferHelper.putInt(buffer, currencyLimitMax);
		ByteBufferHelper.putInt(buffer, kindType);
	}

	public static LobbyMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static LobbyMsg deserialize(ByteBuffer buffer) {
		LobbyMsg messageInstance = new LobbyMsg();
		messageInstance.id = ByteBufferHelper.getInt(buffer);
		messageInstance.name = ByteBufferHelper.getString(buffer);
		messageInstance.playerCount = ByteBufferHelper.getInt(buffer);
		messageInstance.baseScore = ByteBufferHelper.getInt(buffer);
		messageInstance.currencyId = ByteBufferHelper.getInt(buffer);
		messageInstance.expend = ByteBufferHelper.getInt(buffer);
		messageInstance.currencyLimitMin = ByteBufferHelper.getInt(buffer);
		messageInstance.currencyLimitMax = ByteBufferHelper.getInt(buffer);
		messageInstance.kindType = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 32;
		length += ByteBufferHelper.calcStringLength(name);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LobbyMsg[");
		sb.append("id=" + id + ", ");
		sb.append("name=" + name + ", ");
		sb.append("playerCount=" + playerCount + ", ");
		sb.append("baseScore=" + baseScore + ", ");
		sb.append("currencyId=" + currencyId + ", ");
		sb.append("expend=" + expend + ", ");
		sb.append("currencyLimitMin=" + currencyLimitMin + ", ");
		sb.append("currencyLimitMax=" + currencyLimitMax + ", ");
		sb.append("kindType=" + kindType + ", ");
		sb.append("]");
		return sb.toString();
	}
}