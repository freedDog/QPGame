package com.game.base.server.codec;

import java.util.List;

import com.game.framework.component.log.Log;
import com.game.proto.msg.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 协议解码器<br>
 * 考虑到DelimiterBasedFrameDecoder不能处理脏数据， 同时为了集成protobufdecoder所以自定义decoder <bt>
 *  特别注意:Decoder和Encoder不可共享，因为内涵bytebuf用来存放断包等数据
 * RuneProtoDecoder.java
 * @author JiangBangMing
 * 2019年1月7日下午5:41:22
 */
public class RuneProtoDecoder extends ByteToMessageDecoder {
	@Override
	protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Object decoded = decode(ctx, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	private Object decode(ChannelHandlerContext ctx, ByteBuf in) {
		if (in.readableBytes() < CodecConstant.INNER_HEAD_SIZE) {
			return null;
		}

		// 解密头
		ByteBuf buf = in.slice();

		short head = buf.readShort();
		if (head != CodecConstant.HEAD) {
			close(ctx);
			in.clear();
			return null;
		}
		long playerId = buf.readLong();
		short code = buf.readShort();
		int length = buf.readInt();
		if (code <= 0 || length < 0) // 协议号和长度异常
		{
			Log.error("Error code or length,code:" + code + ",length:" + length + ",from:" + ctx.channel().remoteAddress());
			in.skipBytes(1);// 只跳过一个字节，尽最大可能保证不丢包
			return null;
		}
		if (length > buf.readableBytes())// 数据包不完整
		{
			return null;
		}

		byte[] data = null;
		if (length > 0) {
			data = new byte[length];
			buf.readBytes(data);
		}
		in.readerIndex(in.readerIndex() + buf.readerIndex());
		return Message.buildMessage(playerId, code, data);
	}

	private void close(ChannelHandlerContext ctx) {
		ctx.channel().close();
	}

}
