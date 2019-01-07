package com.game.proto.rp.login;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class LoginReqMsg extends RpMessage {
	// 用户ID
	private long playerId;
	// 登陆key
	private String key;

	/** 用户ID */
	public long getPlayerId() {
		return playerId;
	}

	/** 用户ID */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 登陆key */
	public String getKey() {
		return key;
	}

	/** 登陆key */
	public void setKey(String value) {
		this.key = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putString(buffer, key);
	}

	public static LoginReqMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static LoginReqMsg deserialize(ByteBuffer buffer) {
		LoginReqMsg messageInstance = new LoginReqMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.key = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 8;
		length += ByteBufferHelper.calcStringLength(key);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LoginReqMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("key=" + key + ", ");
		sb.append("]");
		return sb.toString();
	}
}