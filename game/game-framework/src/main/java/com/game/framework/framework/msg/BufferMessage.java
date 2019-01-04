package com.game.framework.framework.msg;

import java.util.Arrays;

/**
 * 消息
 * 
 */
public class BufferMessage extends Message {
	protected byte[] data;

	public BufferMessage() {
	}

	public BufferMessage(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	/** 加密数据 **/
	@Override
	public byte[] serialize() {
		return data;
	}

	/** 解析数据 **/
	@Override
	public boolean deserialize(byte[] data) {
		this.data = data;
		return true;
	}

	/** 计算二进制数据长度 **/
	@Override
	public int calclength() {
		return (data != null) ? data.length : 0;
	}

	@Override
	public String toString() {
		return "BufferMessage [date=" + Arrays.toString(data) + "]";
	}

}