package com.game.proto.rp.text;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ErrorMsg extends RpMessage {
	// 错误源
	private short code;
	// 消息
	private String msg;

	/** 错误源 */
	public short getCode() {
		return code;
	}

	/** 错误源 */
	public void setCode(short value) {
		this.code = value;
	}

	/** 消息 */
	public String getMsg() {
		return msg;
	}

	/** 消息 */
	public void setMsg(String value) {
		this.msg = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putShort(buffer, code);
		ByteBufferHelper.putString(buffer, msg);
	}

	public static ErrorMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ErrorMsg deserialize(ByteBuffer buffer) {
		ErrorMsg messageInstance = new ErrorMsg();
		messageInstance.code = ByteBufferHelper.getShort(buffer);
		messageInstance.msg = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 2;
		length += ByteBufferHelper.calcStringLength(msg);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ErrorMsg[");
		sb.append("code=" + code + ", ");
		sb.append("msg=" + msg + ", ");
		sb.append("]");
		return sb.toString();
	}
}