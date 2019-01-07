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
 * 协议安全加密器<br>
 * RuneProtoSecureEncoder.java
 * @author JiangBangMing
 * 2019年1月7日下午5:43:23
 */
public class RuneProtoSecureEncoder extends MessageToMessageEncoder<Message> {
	private static final int msgMaxLen = 1024 * 1000; // 消息最大长度
	private final byte[] encryptKey = { 77, 22, 34, 38, 68, 35, 25, 66 };

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

	/** 加密消息 **/
	private void encode0(ChannelHandlerContext ctx, Message pkg, List<Object> out) {
		Object message = pkg.getObject();
		int dataLength = calcDataLength(pkg.getCode(), message);
		// 判断消息大小
		if (dataLength > msgMaxLen || dataLength < 0) {
			Log.error("Packet send error, the data is too long, playerId : " + pkg.getPlayerId() + ", code : " + pkg.getCode() + ", length : " + dataLength);
			return;
		}
		// 写入消息
		ByteBuf buffer = ctx.alloc().buffer(CodecConstant.HEAD_SIZE + dataLength);
		buffer.writeShort(CodecConstant.HEAD);
		buffer.writeShort(pkg.getCode());
		buffer.writeInt(dataLength);
		writeData(buffer, message);

		// 输出二进制数据
		// ByteBuf byteBuf = buffer.copy();
		// byte[] temps = new byte[byteBuf.capacity()];
		// byteBuf.readBytes(temps);
		// // Log.info("buffer:" + Arrays.toString(temps));
		// String keyStr = Arrays.toString(encryptKey);

		// 加密
		encrypt(0, encryptKey[0], buffer);

		// 输出二进制数据
		// byteBuf = buffer.copy();
		// temps = new byte[byteBuf.capacity()];
		// byteBuf.readBytes(temps);
		// Log.info("encrypt buffer[code=" + pkg.getCode() + " key=" + keyStr + "]: " + Arrays.toString(temps));

		// 输出消息
		out.add(buffer);
	}

	/**
	 * <b>数据加密</b>
	 * 
	 * @param beginIndex
	 * @param lastCipher
	 * @param data
	 * @return
	 */
	private int encrypt(int beginIndex, byte lastCipher, ByteBuf buffer) {
		/*for (int i = 0, cap = buffer.capacity(); i < cap; i++) {
			int keyIndex = beginIndex & 7;
			encryptKey[keyIndex] = (byte) (((~(encryptKey[keyIndex] - lastCipher)) ^ beginIndex) & 0xff);
			byte b = (byte) (((~buffer.getByte(i)) ^ encryptKey[keyIndex]) + lastCipher);
			buffer.setByte(i, b);
			lastCipher = b;
			beginIndex++;
		}*/
		return beginIndex;
	}

	/** 写入消息体数据 **/
	private void writeData(ByteBuf buffer, Object message) {
		if (message == null) {
			return;
		}
		if (message instanceof RpMessage) {
			((RpMessage) message).serialize(buffer); // 二进制化数据
		} else if (message instanceof byte[]) {
			buffer.writeBytes((byte[]) message); // 直接写入二进制数据
		}
	}

	/** 消息大小 **/
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
