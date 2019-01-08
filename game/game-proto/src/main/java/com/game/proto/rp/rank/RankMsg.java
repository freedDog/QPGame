package com.game.proto.rp.rank;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.player.SimplePlayerMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;
import com.game.proto.rp.rank.RankMsg;

public class RankMsg extends RpMessage {
	// 玩家ID
	private long playerId;
	// 玩家状态数据
	private SimplePlayerMsg player;
	// 排行名次
	private int rankIndex;
	// 数值数组
	private List<Integer> rankVals = new ArrayList<Integer>();

	/** 玩家ID */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家ID */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 玩家状态数据 */
	public SimplePlayerMsg getPlayer() {
		return player;
	}

	/** 玩家状态数据 */
	public void setPlayer(SimplePlayerMsg value) {
		this.player = value;
	}

	/** 排行名次 */
	public int getRankIndex() {
		return rankIndex;
	}

	/** 排行名次 */
	public void setRankIndex(int value) {
		this.rankIndex = value;
	}

	public List<Integer> getRankVals() {
		return rankVals;
	}
		
	public void addRankVals(int value) {
		this.rankVals.add(value);
	}
		
	public void addAllRankVals(List<Integer> values) {
		this.rankVals.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putObject(buffer, player);
		ByteBufferHelper.putInt(buffer, rankIndex);
		ByteBufferHelper.putIntArray(buffer, rankVals);
	}

	public static RankMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static RankMsg deserialize(ByteBuffer buffer) {
		RankMsg messageInstance = new RankMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		if (buffer.getShort() > 0) {
			messageInstance.player = SimplePlayerMsg.deserialize(buffer);
		}
		messageInstance.rankIndex = ByteBufferHelper.getInt(buffer);
		ByteBufferHelper.readIntArray(buffer, messageInstance.rankVals);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		length += ByteBufferHelper.calcObjectLength(player);
		length += 2 + rankVals.size() * 4;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RankMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("player=" + player + ", ");
		sb.append("rankIndex=" + rankIndex + ", ");
		sb.append("rankVals=" + rankVals + ", ");
		sb.append("]");
		return sb.toString();
	}
}