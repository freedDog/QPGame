package com.game.proto.rp.struct;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class PosMsg extends RpMessage {
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int value) {
		this.x = value;
	}

	public int getY() {
		return y;
	}

	public void setY(int value) {
		this.y = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, x);
		ByteBufferHelper.putInt(buffer, y);
	}

	public static PosMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static PosMsg deserialize(ByteBuffer buffer) {
		PosMsg messageInstance = new PosMsg();
		messageInstance.x = ByteBufferHelper.getInt(buffer);
		messageInstance.y = ByteBufferHelper.getInt(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PosMsg[");
		sb.append("x=" + x + ", ");
		sb.append("y=" + y + ", ");
		sb.append("]");
		return sb.toString();
	}
}