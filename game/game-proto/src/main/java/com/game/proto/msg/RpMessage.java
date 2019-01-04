package com.game.proto.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 客户端消息对象
 * RpMessage.java
 * @author JiangBangMing
 * 2019年1月4日下午2:57:50
 */
public abstract class RpMessage {
	/** 数据流大小 **/
	public abstract int calcLength();

	/** 数据流转化 **/
	public abstract void serialize(ByteBuf buffer);

	/** 消息二进制化 **/
	public static byte[] serialize(RpMessage rpMsg) {
		// 创建数据流
		int bufSize = rpMsg.calcLength();
		ByteBuf byteBuf = Unpooled.buffer(bufSize);
		byteBuf.writerIndex(0); // 归位
		// 转化数据
		rpMsg.serialize(byteBuf);
		return byteBuf.array();
	}

}