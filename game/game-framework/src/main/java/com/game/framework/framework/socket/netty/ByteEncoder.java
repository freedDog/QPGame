package com.game.framework.framework.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.game.framework.component.log.Log;


/**
 * 协议加密器<br>
 * ByteEncoder.java
 * @author JiangBangMing
 * 2019年1月3日下午4:12:29
 */
public class ByteEncoder extends MessageToMessageEncoder<byte[]> {

	protected static final int MSG_HEAD = 3076;
	protected static final int MSG_HEAD_SIZE = 4;

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] data, List<Object> out)
			throws Exception {
		if (data == null) {
			Log.error("空数据发送错误!");
			return;
		}
		// 编码
		encode0(ctx, data, out);
	}

	private void encode0(ChannelHandlerContext ctx, byte[] date, List<Object> out) {
		int length = (date != null) ? date.length : 0;

		// 转化成二进制数据
		ByteBuf buffer = ctx.alloc().buffer(MSG_HEAD_SIZE + length);
		buffer.writeShort(MSG_HEAD);
		buffer.writeInt(length);
		if (length > 0 && date != null) {
			buffer.writeBytes(date);
		}
		// 输出消息
		out.add(buffer);
	}

}
