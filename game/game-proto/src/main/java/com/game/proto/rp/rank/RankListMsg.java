package com.game.proto.rp.rank;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;
import com.game.proto.rp.rank.RankMsg;

public class RankListMsg extends RpMessage {
	// 排行榜类型  1：赚钱榜  2：充值榜 3：夺宝榜 4：财富榜 5：钻石榜
	private int rankType;
	// 排行榜链表数据
	private List<RankMsg> ranks = new ArrayList<RankMsg>();
	// 玩家的排行榜的数据
	private RankMsg self;

	/** 排行榜类型  1：赚钱榜  2：充值榜 3：夺宝榜 4：财富榜 5：钻石榜 */
	public int getRankType() {
		return rankType;
	}

	/** 排行榜类型  1：赚钱榜  2：充值榜 3：夺宝榜 4：财富榜 5：钻石榜 */
	public void setRankType(int value) {
		this.rankType = value;
	}

	public List<RankMsg> getRanks() {
		return ranks;
	}
		
	public void addRanks(RankMsg value) {
		this.ranks.add(value);
	}
		
	public void addAllRanks(List<RankMsg> values) {
		this.ranks.addAll(values);
	}

	/** 玩家的排行榜的数据 */
	public RankMsg getSelf() {
		return self;
	}

	/** 玩家的排行榜的数据 */
	public void setSelf(RankMsg value) {
		this.self = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, rankType);
		ByteBufferHelper.putObjectArray(buffer, ranks);
		ByteBufferHelper.putObject(buffer, self);
	}

	public static RankListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RankListMsg deserialize(ByteBuffer buffer) {
		RankListMsg messageInstance = new RankListMsg();
		messageInstance.rankType = ByteBufferHelper.getInt(buffer);
		int ranksSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < ranksSize; i++) {
			messageInstance.addRanks(RankMsg.deserialize(buffer));
		}
		if (buffer.getShort() > 0) {
			messageInstance.self = RankMsg.deserialize(buffer);
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 4;
		length += ByteBufferHelper.calcObjectArrayLength(ranks);
		length += ByteBufferHelper.calcObjectLength(self);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RankListMsg[");
		sb.append("rankType=" + rankType + ", ");
		sb.append("ranks=" + ranks + ", ");
		sb.append("self=" + self + ", ");
		sb.append("]");
		return sb.toString();
	}
}