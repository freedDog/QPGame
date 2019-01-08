package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.player.SimplePlayerMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GamePlayerMsg extends RpMessage {
	// 玩家基本信息
	private SimplePlayerMsg info;
	// 是否准备
	private boolean prepare;
	// 是否是地主
	private boolean landlord;
	// 手上牌数
	private int cardNum;
	// 座位号, 0~2, -1为没座位
	private int seat;
	// 是否明牌
	private boolean showCard;
	// 是否托管
	private boolean deposit;
	// 货币Id
	private int currencyId;
	// 当前货币数量
	private int currencyValue;
	// 分数倍率
	private int scoreRate;
	// 胜利获取封顶
	private int maxGetCount;
	// 使用的道具Id
	private List<Integer> useItemIds = new ArrayList<Integer>();

	/** 玩家基本信息 */
	public SimplePlayerMsg getInfo() {
		return info;
	}

	/** 玩家基本信息 */
	public void setInfo(SimplePlayerMsg value) {
		this.info = value;
	}

	/** 是否准备 */
	public boolean getPrepare() {
		return prepare;
	}

	/** 是否准备 */
	public void setPrepare(boolean value) {
		this.prepare = value;
	}

	/** 是否是地主 */
	public boolean getLandlord() {
		return landlord;
	}

	/** 是否是地主 */
	public void setLandlord(boolean value) {
		this.landlord = value;
	}

	/** 手上牌数 */
	public int getCardNum() {
		return cardNum;
	}

	/** 手上牌数 */
	public void setCardNum(int value) {
		this.cardNum = value;
	}

	/** 座位号, 0~2, -1为没座位 */
	public int getSeat() {
		return seat;
	}

	/** 座位号, 0~2, -1为没座位 */
	public void setSeat(int value) {
		this.seat = value;
	}

	/** 是否明牌 */
	public boolean getShowCard() {
		return showCard;
	}

	/** 是否明牌 */
	public void setShowCard(boolean value) {
		this.showCard = value;
	}

	/** 是否托管 */
	public boolean getDeposit() {
		return deposit;
	}

	/** 是否托管 */
	public void setDeposit(boolean value) {
		this.deposit = value;
	}

	/** 货币Id */
	public int getCurrencyId() {
		return currencyId;
	}

	/** 货币Id */
	public void setCurrencyId(int value) {
		this.currencyId = value;
	}

	/** 当前货币数量 */
	public int getCurrencyValue() {
		return currencyValue;
	}

	/** 当前货币数量 */
	public void setCurrencyValue(int value) {
		this.currencyValue = value;
	}

	/** 分数倍率 */
	public int getScoreRate() {
		return scoreRate;
	}

	/** 分数倍率 */
	public void setScoreRate(int value) {
		this.scoreRate = value;
	}

	/** 胜利获取封顶 */
	public int getMaxGetCount() {
		return maxGetCount;
	}

	/** 胜利获取封顶 */
	public void setMaxGetCount(int value) {
		this.maxGetCount = value;
	}

	public List<Integer> getUseItemIds() {
		return useItemIds;
	}
		
	public void addUseItemIds(int value) {
		this.useItemIds.add(value);
	}
		
	public void addAllUseItemIds(List<Integer> values) {
		this.useItemIds.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObject(buffer, info);
		ByteBufferHelper.putBoolean(buffer, prepare);
		ByteBufferHelper.putBoolean(buffer, landlord);
		ByteBufferHelper.putInt(buffer, cardNum);
		ByteBufferHelper.putInt(buffer, seat);
		ByteBufferHelper.putBoolean(buffer, showCard);
		ByteBufferHelper.putBoolean(buffer, deposit);
		ByteBufferHelper.putInt(buffer, currencyId);
		ByteBufferHelper.putInt(buffer, currencyValue);
		ByteBufferHelper.putInt(buffer, scoreRate);
		ByteBufferHelper.putInt(buffer, maxGetCount);
		ByteBufferHelper.putIntArray(buffer, useItemIds);
	}

	public static GamePlayerMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GamePlayerMsg deserialize(ByteBuffer buffer) {
		GamePlayerMsg messageInstance = new GamePlayerMsg();
		if (buffer.getShort() > 0) {
			messageInstance.info = SimplePlayerMsg.deserialize(buffer);
		}
		messageInstance.prepare = ByteBufferHelper.getBoolean(buffer);
		messageInstance.landlord = ByteBufferHelper.getBoolean(buffer);
		messageInstance.cardNum = ByteBufferHelper.getInt(buffer);
		messageInstance.seat = ByteBufferHelper.getInt(buffer);
		messageInstance.showCard = ByteBufferHelper.getBoolean(buffer);
		messageInstance.deposit = ByteBufferHelper.getBoolean(buffer);
		messageInstance.currencyId = ByteBufferHelper.getInt(buffer);
		messageInstance.currencyValue = ByteBufferHelper.getInt(buffer);
		messageInstance.scoreRate = ByteBufferHelper.getInt(buffer);
		messageInstance.maxGetCount = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.useItemIds);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 28;
		length += ByteBufferHelper.calcObjectLength(info);
		length += 2 + useItemIds.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GamePlayerMsg[");
		sb.append("info=" + info + ", ");
		sb.append("prepare=" + prepare + ", ");
		sb.append("landlord=" + landlord + ", ");
		sb.append("cardNum=" + cardNum + ", ");
		sb.append("seat=" + seat + ", ");
		sb.append("showCard=" + showCard + ", ");
		sb.append("deposit=" + deposit + ", ");
		sb.append("currencyId=" + currencyId + ", ");
		sb.append("currencyValue=" + currencyValue + ", ");
		sb.append("scoreRate=" + scoreRate + ", ");
		sb.append("maxGetCount=" + maxGetCount + ", ");
		sb.append("useItemIds=" + useItemIds + ", ");
		sb.append("]");
		return sb.toString();
	}
}