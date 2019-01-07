package com.game.proto.rp.login;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class LoginRespMsg extends RpMessage {
	// 用户ID
	private long playerId;
	// 登陆结果
	private short state;

	/** 用户ID */
	public long getPlayerId() {
		return playerId;
	}

	/** 用户ID */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 登陆结果 */
	public short getState() {
		return state;
	}

	/** 登陆结果 */
	public void setState(short value) {
		this.state = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putShort(buffer, state);
	}

	public static LoginRespMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static LoginRespMsg deserialize(ByteBuffer buffer) {
		LoginRespMsg messageInstance = new LoginRespMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.state = ByteBufferHelper.getShort(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 10;
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LoginRespMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("state=" + state + ", ");
		sb.append("]");
		return sb.toString();
	}
}