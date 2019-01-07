package com.game.base.server.codec;

import java.util.List;

import com.game.framework.component.log.Log;
import com.game.proto.msg.Message;
import com.game.proto.msg.MessageBuffer;
import com.game.proto.msg.RpMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * 协议加密器<br>
 * RuneProtoEncoder.java
 * 
 * @author JiangBangMing 2019年1月7日下午5:28:01
 */
public class RuneProtoEncoder extends MessageToMessageEncoder<Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Message pkg, List<Object> out) throws Exception {
		if (pkg.getObject() instanceof MessageBuffer) {
			MessageBuffer messageBuffer = pkg.getObject();
			for (Message message : messageBuffer) {
				encode0(ctx, message, out);
			}
		} else {
			encode0(ctx, pkg, out);
		}
	}

	private void encode0(ChannelHandlerContext ctx, Message pkg, List<Object> out) {
		Object message = pkg.getObject();
		int dataLength = calcDataLength(pkg.getCode(), message);

		if (dataLength > (Short.MAX_VALUE * 10) || dataLength < 0) {
			Log.error("Packet send error, the data is too long, playerId : " + pkg.getPlayerId() + ", code : "
					+ pkg.getCode() + ", length : " + dataLength);
			return;
		}

		ByteBuf buffer = ctx.alloc().buffer(CodecConstant.INNER_HEAD_SIZE + dataLength);

		buffer.writeShort(CodecConstant.HEAD);
		buffer.writeLong(pkg.getPlayerId());
		buffer.writeShort(pkg.getCode());
		buffer.writeInt(dataLength);
		writeData(buffer, message);

		out.add(buffer);
	}

	private void writeData(ByteBuf buffer, Object message) {
		if (message == null) {
			return;
		}
		if (message instanceof RpMessage) {
			((RpMessage) message).serialize(buffer);
		} else if (message instanceof byte[]) {
			buffer.writeBytes((byte[]) message);
		}
	}

	private int calcDataLength(short code, Object message) {
		if (message == null) {
			return 0;
		}
		if (message instanceof RpMessage) {
			return ((RpMessage) message).calcLength();
		} else if (message instanceof byte[]) {
			return ((byte[]) message).length;
		} else {
			Log.error("Message error ! Message type is : " + message.getClass().getName() + ", code : " + code);
			return 0;
		}
	}
}