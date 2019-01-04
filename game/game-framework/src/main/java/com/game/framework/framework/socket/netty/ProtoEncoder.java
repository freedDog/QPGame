package com.game.framework.framework.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.framework.msg.BufferMessage;
import com.game.framework.framework.msg.Message;



/**
 * 协议加密器<br>
 * ProtoEncoder.java
 * @author JiangBangMing
 * 2019年1月3日下午4:13:04
 */
public class ProtoEncoder extends MessageToMessageEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		if (msg == null) {
			return;
		}

		byte[] data = null;
		if (Message.class.isInstance(msg)) {
			Message msg0 = (Message) msg;
			data = msg0.serialize(); // 加密数据
		} else if (msg.getClass() == byte[].class) {
			byte[] data0 = (byte[]) msg;
			Message msg0 = new BufferMessage(data0);
			data = msg0.serialize(); // 加密数据
		} else {
			Log.error("错误消息类型: msg=" + msg);
			return;
		}

		// 编码
		encode0(ctx, data, out);
	}

	private void encode0(ChannelHandlerContext ctx, byte[] date, List<Object> out) {
		int length = (date != null) ? date.length : 0;

		// 转化成二进制数据
		ByteBuf buffer = ctx.alloc().buffer(Message.HEADSIZE + length);
		buffer.writeShort(Message.HEAD);
		buffer.writeInt(length);
		if (date != null) {
			buffer.writeBytes(date);
		}

		// 输出消息
		out.add(buffer);
	}

}
