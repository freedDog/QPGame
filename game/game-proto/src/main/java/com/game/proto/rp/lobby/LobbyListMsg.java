package com.game.proto.rp.lobby;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;
import com.game.proto.rp.lobby.LobbyMsg;

public class LobbyListMsg extends RpMessage {
	// 大厅数据
	private List<LobbyMsg> lobby = new ArrayList<LobbyMsg>();

	public List<LobbyMsg> getLobby() {
		return lobby;
	}
		
	public void addLobby(LobbyMsg value) {
		this.lobby.add(value);
	}
		
	public void addAllLobby(List<LobbyMsg> values) {
		this.lobby.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, lobby);
	}

	public static LobbyListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static LobbyListMsg deserialize(ByteBuffer buffer) {
		LobbyListMsg messageInstance = new LobbyListMsg();
		int lobbySize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < lobbySize; i++) {
			messageInstance.addLobby(LobbyMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(lobby);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LobbyListMsg[");
		sb.append("lobby=" + lobby + ", ");
		sb.append("]");
		return sb.toString();
	}
}