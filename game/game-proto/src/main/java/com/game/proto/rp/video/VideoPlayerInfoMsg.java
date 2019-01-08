package com.game.proto.rp.video;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class VideoPlayerInfoMsg extends RpMessage {
	// 玩家Id
	private long playerId;
	// 玩家名
	private String name;
	// 头像
	private String icon;
	// 分数
	private int score;
	// 总分
	private int totalScore;
	// 形象模板
	private int fashionId;

	/** 玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 玩家名 */
	public String getName() {
		return name;
	}

	/** 玩家名 */
	public void setName(String value) {
		this.name = value;
	}

	/** 头像 */
	public String getIcon() {
		return icon;
	}

	/** 头像 */
	public void setIcon(String value) {
		this.icon = value;
	}

	/** 分数 */
	public int getScore() {
		return score;
	}

	/** 分数 */
	public void setScore(int value) {
		this.score = value;
	}

	/** 总分 */
	public int getTotalScore() {
		return totalScore;
	}

	/** 总分 */
	public void setTotalScore(int value) {
		this.totalScore = value;
	}

	/** 形象模板 */
	public int getFashionId() {
		return fashionId;
	}

	/** 形象模板 */
	public void setFashionId(int value) {
		this.fashionId = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putString(buffer, name);
		ByteBufferHelper.putString(buffer, icon);
		ByteBufferHelper.putInt(buffer, score);
		ByteBufferHelper.putInt(buffer, totalScore);
		ByteBufferHelper.putInt(buffer, fashionId);
	}

	public static VideoPlayerInfoMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static VideoPlayerInfoMsg deserialize(ByteBuffer buffer) {
		VideoPlayerInfoMsg messageInstance = new VideoPlayerInfoMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.name = ByteBufferHelper.getString(buffer);
		messageInstance.icon = ByteBufferHelper.getString(buffer);
		messageInstance.score = ByteBufferHelper.getInt(buffer);
		messageInstance.totalScore = ByteBufferHelper.getInt(buffer);
		messageInstance.fashionId = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 20;
		length += ByteBufferHelper.calcStringLength(name);
		length += ByteBufferHelper.calcStringLength(icon);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VideoPlayerInfoMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("name=" + name + ", ");
		sb.append("icon=" + icon + ", ");
		sb.append("score=" + score + ", ");
		sb.append("totalScore=" + totalScore + ", ");
		sb.append("fashionId=" + fashionId + ", ");
		sb.append("]");
		return sb.toString();
	}
}