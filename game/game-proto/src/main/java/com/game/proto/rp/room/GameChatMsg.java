package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GameChatMsg extends RpMessage {
	// 聊天类型, 1为聊天, 2为表情
	private int type;
	// 模板Id
	private int chatId;
	// 文本消息
	private String text;
	// 发送的玩家Id
	private long playerId;
	// 目标玩家Id
	private long targetId;

	/** 聊天类型, 1为聊天, 2为表情 */
	public int getType() {
		return type;
	}

	/** 聊天类型, 1为聊天, 2为表情 */
	public void setType(int value) {
		this.type = value;
	}

	/** 模板Id */
	public int getChatId() {
		return chatId;
	}

	/** 模板Id */
	public void setChatId(int value) {
		this.chatId = value;
	}

	/** 文本消息 */
	public String getText() {
		return text;
	}

	/** 文本消息 */
	public void setText(String value) {
		this.text = value;
	}

	/** 发送的玩家Id */
	public long getPlayerId() {
		return playerId;
	}

	/** 发送的玩家Id */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 目标玩家Id */
	public long getTargetId() {
		return targetId;
	}

	/** 目标玩家Id */
	public void setTargetId(long value) {
		this.targetId = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putInt(buffer, chatId);
		ByteBufferHelper.putString(buffer, text);
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putLong(buffer, targetId);
	}

	public static GameChatMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GameChatMsg deserialize(ByteBuffer buffer) {
		GameChatMsg messageInstance = new GameChatMsg();
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.chatId = ByteBufferHelper.getInt(buffer);
		messageInstance.text = ByteBufferHelper.getString(buffer);
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.targetId = ByteBufferHelper.getLong(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 24;
		length += ByteBufferHelper.calcStringLength(text);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GameChatMsg[");
		sb.append("type=" + type + ", ");
		sb.append("chatId=" + chatId + ", ");
		sb.append("text=" + text + ", ");
		sb.append("playerId=" + playerId + ", ");
		sb.append("targetId=" + targetId + ", ");
		sb.append("]");
		return sb.toString();
	}
}