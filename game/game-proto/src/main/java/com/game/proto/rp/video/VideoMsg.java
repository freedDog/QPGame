package com.game.proto.rp.video;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.video.VideoItemMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class VideoMsg extends RpMessage {
	// 记录Id
	private long recordId;
	// 游戏类型
	private int gameType;
	// 内容列表
	private List<VideoItemMsg> item = new ArrayList<VideoItemMsg>();

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

	public List<VideoItemMsg> getItem() {
		return item;
	}
		
	public void addItem(VideoItemMsg value) {
		this.item.add(value);
	}
		
	public void addAllItem(List<VideoItemMsg> values) {
		this.item.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, recordId);
		ByteBufferHelper.putInt(buffer, gameType);
		ByteBufferHelper.putObjectArray(buffer, item);
	}

	public static VideoMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static VideoMsg deserialize(ByteBuffer buffer) {
		VideoMsg messageInstance = new VideoMsg();
		messageInstance.recordId = ByteBufferHelper.getLong(buffer);
		messageInstance.gameType = ByteBufferHelper.getInt(buffer);
		int itemSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < itemSize; i++) {
			messageInstance.addItem(VideoItemMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		length += ByteBufferHelper.calcObjectArrayLength(item);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VideoMsg[");
		sb.append("recordId=" + recordId + ", ");
		sb.append("gameType=" + gameType + ", ");
		sb.append("item=" + item + ", ");
		sb.append("]");
		return sb.toString();
	}
}