package com.game.proto.rp.text;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class TextMsg extends RpMessage {
	// 文本
	private String text;
	// 类型(多种类型)
	private List<Short> type = new ArrayList<Short>();

	/** 文本 */
	public String getText() {
		return text;
	}

	/** 文本 */
	public void setText(String value) {
		this.text = value;
	}

	public List<Short> getType() {
		return type;
	}
		
	public void addType(short value) {
		this.type.add(value);
	}
		
	public void addAllType(List<Short> values) {
		this.type.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putString(buffer, text);
		ByteBufferHelper.putShortArray(buffer, type);
	}

	public static TextMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static TextMsg deserialize(ByteBuffer buffer) {
		TextMsg messageInstance = new TextMsg();
		messageInstance.text = ByteBufferHelper.getString(buffer);
		ByteBufferHelper.readShortArray(buffer, messageInstance.type);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcStringLength(text);
		length += 2 + type.size() * 2;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TextMsg[");
		sb.append("text=" + text + ", ");
		sb.append("type=" + type + ", ");
		sb.append("]");
		return sb.toString();
	}
}