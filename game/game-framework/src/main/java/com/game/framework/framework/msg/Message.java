package com.game.framework.framework.msg;

/**
 * 消息
 * 
 */
public abstract class Message {
	public static final short HEAD = 0x0C03; // 消息头数据
	public static final int HEADSIZE = 2 + 4; // 消息头大小: head+len

	public Message() {

	}

	/** 获取数据流 **/
	public abstract byte[] getData();

	/** 加密数据 **/
	public abstract byte[] serialize();

	// /** 加密数据 **/
	// protected boolean serialize(IByteBuf byteBuf) {
	// return true;
	// }

	/** 解析数据 **/
	public abstract boolean deserialize(byte[] data);

	// /** 解析数据 **/
	// protected boolean deserialize(IByteBuf byteBuf) {
	// return true;
	// }

	/** 计算二进制数据长度 **/
	public abstract int calclength();

}

// public class Message {
// public static final int HEADSIZE = 2 + 4; // 消息头大小
// public static final short HEAD = 0x0C03; // 消息头数据
//
// protected byte[] data;
// protected int clientId;
//
// public Message() {
//
// }
//
// public Message(byte[] data) {
// this.data = data;
// }
//
// public byte[] getData() {
// return data;
// }
//
// @Override
// public String toString() {
// return "Message [clientId=" + clientId + " date=" + Arrays.toString(data) + "]";
// }
//
// public byte[] serialize() {
// int size = (data != null) ? data.length : 0;
// // 转化成二进制数据
// ByteBuf byteBuf = Unpooled.buffer(4 + size);
// byteBuf.writerIndex(0); // 归位
// byteBuf.writeInt(clientId);
// if (data != null) {
// byteBuf.writeBytes(data);
// }
// return byteBuf.array();
// }
//
// public boolean deserialize(byte[] data) {
// return deserialize(Unpooled.copiedBuffer(data));
// }
//
// public boolean deserialize(ByteBuf byteBuf) {
// int size = byteBuf.capacity();
// clientId = byteBuf.readInt();
// byte[] data = new byte[size - 4];
// byteBuf.readBytes(data);
// this.data = data;
// return true;
// }
//
// public int getClientId() {
// return clientId;
// }
//
// public void setClientId(int clientId) {
// this.clientId = clientId;
// }
//
// }
