package com.game.proto.rp.video;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.video.VideoPlayerInfoMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class VideoRecordMsg extends RpMessage {
	// 记录Id
	private long recordId;
	// 游戏类型
	private int gameType;
	// 玩家信息
	private List<VideoPlayerInfoMsg> info = new ArrayList<VideoPlayerInfoMsg>();
	// 记录时间戳
	private int recordTime;

	/** 记录Id */
	public long getRecordId() {
		return recordId;
	}

	/** 记录Id */
	public void setRecordId(long value) {
		this.recordId = value;
	}

	/** 游戏类型 */
	public int getGameType() {
		return gameType;
	}

	/** 游戏类型 */
	public void setGameType(int value) {
		this.gameType = value;
	}

	public List<VideoPlayerInfoMsg> getInfo() {
		return info;
	}
		
	public void addInfo(VideoPlayerInfoMsg value) {
		this.info.add(value);
	}
		
	public void addAllInfo(List<VideoPlayerInfoMsg> values) {
		this.info.addAll(values);
	}

	/** 记录时间戳 */
	public int getRecordTime() {
		return recordTime;
	}

	/** 记录时间戳 */
	public void setRecordTime(int value) {
		this.recordTime = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, recordId);
		ByteBufferHelper.putInt(buffer, gameType);
		ByteBufferHelper.putObjectArray(buffer, info);
		ByteBufferHelper.putInt(buffer, recordTime);
	}

	public static VideoRecordMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static VideoRecordMsg deserialize(ByteBuffer buffer) {
		VideoRecordMsg messageInstance = new VideoRecordMsg();
		messageInstance.recordId = ByteBufferHelper.getLong(buffer);
		messageInstance.gameType = ByteBufferHelper.getInt(buffer);
		int infoSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < infoSize; i++) {
			messageInstance.addInfo(VideoPlayerInfoMsg.deserialize(buffer));
		}
		messageInstance.recordTime = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 16;
		length += ByteBufferHelper.calcObjectArrayLength(info);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VideoRecordMsg[");
		sb.append("recordId=" + recordId + ", ");
		sb.append("gameType=" + gameType + ", ");
		sb.append("info=" + info + ", ");
		sb.append("recordTime=" + recordTime + ", ");
		sb.append("]");
		return sb.toString();
	}
}