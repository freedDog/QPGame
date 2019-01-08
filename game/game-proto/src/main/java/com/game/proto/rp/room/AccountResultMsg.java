package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class AccountResultMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 是否获胜
	private boolean isWin;
	// 底分
	private int baseScore;
	// 倍率
	private float doubleRate;
	// 金钱变化
	private int money;
	// 结算货币Id
	private int currencyId;
	// 限制, 0为正常, -1为不够扣, 1为封顶增加
	private int moneyLimit;
	// 是否双倍
	private boolean isDouble;
	// 任务加倍, 正常是0
	private int taskRate;
	// 剩余的手牌
	private List<Integer> cards = new ArrayList<Integer>();
	// 是否春天啦
	private boolean isSpring;

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 是否获胜 */
	public boolean getIsWin() {
		return isWin;
	}

	/** 是否获胜 */
	public void setIsWin(boolean value) {
		this.isWin = value;
	}

	/** 底分 */
	public int getBaseScore() {
		return baseScore;
	}

	/** 底分 */
	public void setBaseScore(int value) {
		this.baseScore = value;
	}

	/** 倍率 */
	public float getDoubleRate() {
		return doubleRate;
	}

	/** 倍率 */
	public void setDoubleRate(float value) {
		this.doubleRate = value;
	}

	/** 金钱变化 */
	public int getMoney() {
		return money;
	}

	/** 金钱变化 */
	public void setMoney(int value) {
		this.money = value;
	}

	/** 结算货币Id */
	public int getCurrencyId() {
		return currencyId;
	}

	/** 结算货币Id */
	public void setCurrencyId(int value) {
		this.currencyId = value;
	}

	/** 限制, 0为正常, -1为不够扣, 1为封顶增加 */
	public int getMoneyLimit() {
		return moneyLimit;
	}

	/** 限制, 0为正常, -1为不够扣, 1为封顶增加 */
	public void setMoneyLimit(int value) {
		this.moneyLimit = value;
	}

	/** 是否双倍 */
	public boolean getIsDouble() {
		return isDouble;
	}

	/** 是否双倍 */
	public void setIsDouble(boolean value) {
		this.isDouble = value;
	}

	/** 任务加倍, 正常是0 */
	public int getTaskRate() {
		return taskRate;
	}

	/** 任务加倍, 正常是0 */
	public void setTaskRate(int value) {
		this.taskRate = value;
	}

	public List<Integer> getCards() {
		return cards;
	}
		
	public void addCards(int value) {
		this.cards.add(value);
	}
		
	public void addAllCards(List<Integer> values) {
		this.cards.addAll(values);
	}

	/** 是否春天啦 */
	public boolean getIsSpring() {
		return isSpring;
	}

	/** 是否春天啦 */
	public void setIsSpring(boolean value) {
		this.isSpring = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putBoolean(buffer, isWin);
		ByteBufferHelper.putInt(buffer, baseScore);
		ByteBufferHelper.putFloat(buffer, doubleRate);
		ByteBufferHelper.putInt(buffer, money);
		ByteBufferHelper.putInt(buffer, currencyId);
		ByteBufferHelper.putInt(buffer, moneyLimit);
		ByteBufferHelper.putBoolean(buffer, isDouble);
		ByteBufferHelper.putInt(buffer, taskRate);
		ByteBufferHelper.putIntArray(buffer, cards);
		ByteBufferHelper.putBoolean(buffer, isSpring);
	}

	public static AccountResultMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static AccountResultMsg deserialize(ByteBuffer buffer) {
		AccountResultMsg messageInstance = new AccountResultMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.isWin = ByteBufferHelper.getBoolean(buffer);
		messageInstance.baseScore = ByteBufferHelper.getInt(buffer);
		messageInstance.doubleRate = ByteBufferHelper.getFloat(buffer);
		messageInstance.money = ByteBufferHelper.getInt(buffer);
		messageInstance.currencyId = ByteBufferHelper.getInt(buffer);
		messageInstance.moneyLimit = ByteBufferHelper.getInt(buffer);
		messageInstance.isDouble = ByteBufferHelper.getBoolean(buffer);
		messageInstance.taskRate = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.cards);
		messageInstance.isSpring = ByteBufferHelper.getBoolean(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 35;
		length += 2 + cards.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AccountResultMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("isWin=" + isWin + ", ");
		sb.append("baseScore=" + baseScore + ", ");
		sb.append("doubleRate=" + doubleRate + ", ");
		sb.append("money=" + money + ", ");
		sb.append("currencyId=" + currencyId + ", ");
		sb.append("moneyLimit=" + moneyLimit + ", ");
		sb.append("isDouble=" + isDouble + ", ");
		sb.append("taskRate=" + taskRate + ", ");
		sb.append("cards=" + cards + ", ");
		sb.append("isSpring=" + isSpring + ", ");
		sb.append("]");
		return sb.toString();
	}
}