package com.game.proto.rp.text;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class ResultMsg extends RpMessage {
	// 结果
	private boolean result;
	// 消息
	private String value;

	/** 结果 */
	public boolean getResult() {
		return result;
	}

	/** 结果 */
	public void setResult(boolean value) {
		this.result = value;
	}

	/** 消息 */
	public String getValue() {
		return value;
	}

	/** 消息 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putBoolean(buffer, result);
		ByteBufferHelper.putString(buffer, value);
	}

	public static ResultMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ResultMsg deserialize(ByteBuffer buffer) {
		ResultMsg messageInstance = new ResultMsg();
		messageInstance.result = ByteBufferHelper.getBoolean(buffer);
		messageInstance.value = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 1;
		length += ByteBufferHelper.calcStringLength(value);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ResultMsg[");
		sb.append("result=" + result + ", ");
		sb.append("value=" + value + ", ");
		sb.append("]");
		return sb.toString();
	}
}