package com.game.proto.rp.video;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class VideoItemMsg extends RpMessage {
	// 离开始距离时间
	private int time;
	// 消息码
	private short code;
	// 消息体
	private List<Byte> data = new ArrayList<Byte>();

	/** 离开始距离时间 */
	public int getTime() {
		return time;
	}

	/** 离开始距离时间 */
	public void setTime(int value) {
		this.time = value;
	}

	/** 消息码 */
	public short getCode() {
		return code;
	}

	/** 消息码 */
	public void setCode(short value) {
		this.code = value;
	}

	public List<Byte> getData() {
		return data;
	}
		
	public void addData(byte value) {
		this.data.add(value);
	}
		
	public void addAllData(List<Byte> values) {
		this.data.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, time);
		ByteBufferHelper.putShort(buffer, code);
		ByteBufferHelper.putByteArray(buffer, data);
	}

	public static VideoItemMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static VideoItemMsg deserialize(ByteBuffer buffer) {
		VideoItemMsg messageInstance = new VideoItemMsg();
		messageInstance.time = ByteBufferHelper.getInt(buffer);
		messageInstance.code = ByteBufferHelper.getShort(buffer);
		ByteBufferHelper.readByteArray(buffer, messageInstance.data);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 6;
		length += 2 + data.size() * 1;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VideoItemMsg[");
		sb.append("time=" + time + ", ");
		sb.append("code=" + code + ", ");
		sb.append("data=" + data + ", ");
		sb.append("]");
		return sb.toString();
	}
}