package com.game.proto.rp.video;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.video.VideoRecordMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class VideoRecordListMsg extends RpMessage {
	// 记录
	private List<VideoRecordMsg> record = new ArrayList<VideoRecordMsg>();

	public List<VideoRecordMsg> getRecord() {
		return record;
	}
		
	public void addRecord(VideoRecordMsg value) {
		this.record.add(value);
	}
		
	public void addAllRecord(List<VideoRecordMsg> values) {
		this.record.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, record);
	}

	public static VideoRecordListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static VideoRecordListMsg deserialize(ByteBuffer buffer) {
		VideoRecordListMsg messageInstance = new VideoRecordListMsg();
		int recordSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < recordSize; i++) {
			messageInstance.addRecord(VideoRecordMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(record);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VideoRecordListMsg[");
		sb.append("record=" + record + ", ");
		sb.append("]");
		return sb.toString();
	}
}