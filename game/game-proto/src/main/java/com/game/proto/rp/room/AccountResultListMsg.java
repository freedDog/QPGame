package com.game.proto.rp.room;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;
import com.game.proto.rp.room.AccountResultMsg;

public class AccountResultListMsg extends RpMessage {
	// 结果
	private List<AccountResultMsg> result = new ArrayList<AccountResultMsg>();

	public List<AccountResultMsg> getResult() {
		return result;
	}
		
	public void addResult(AccountResultMsg value) {
		this.result.add(value);
	}
		
	public void addAllResult(List<AccountResultMsg> values) {
		this.result.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, result);
	}

	public static AccountResultListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static AccountResultListMsg deserialize(ByteBuffer buffer) {
		AccountResultListMsg messageInstance = new AccountResultListMsg();
		int resultSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < resultSize; i++) {
			messageInstance.addResult(AccountResultMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(result);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AccountResultListMsg[");
		sb.append("result=" + result + ", ");
		sb.append("]");
		return sb.toString();
	}
}