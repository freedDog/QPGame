package com.game.base.server.codec;

import java.util.List;

import com.game.framework.component.log.Log;
import com.game.proto.msg.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 协议安全解密器<br>
 * 考虑到DelimiterBasedFrameDecoder不能处理脏数据， 同时为了集成protobufdecoder所以自定义decoder <bt> 
 * 特别注意:Decoder和Encoder不可共享，因为内涵bytebuf用来存放断包等数据
 * RuneProtoSecureDecoder.java
 * @author JiangBangMing
 * 2019年1月7日下午5:44:00
 */
public class RuneProtoSecureDecoder extends ByteToMessageDecoder {
	private byte[] decryptKey = { 77, 22, 34, 38, 68, 35, 25, 66 };
	private byte[] bakKey = new byte[decryptKey.length]; // 备份数据
	private byte[] shortByte = new byte[2];
	private byte[] intByte = new byte[4];

	@Override
	protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Object decoded = decode(ctx, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	/** 解析消息 **/
	private Object decode(ChannelHandlerContext ctx, ByteBuf in) {
		if (in.readableBytes() < CodecConstant.HEAD_SIZE) {
			return null;
		}

		ByteBuf buf = in.slice();
		backupKey();

		int dataIndex = 0;
		byte lastCipher = decryptKey[0];

		// 解密头
		buf.readBytes(shortByte);
		lastCipher = decrypt(dataIndex, lastCipher, shortByte);
		short head = (short) (((shortByte[0] & 0xff) << 8) | (shortByte[1] & 0xff));
		if (head != CodecConstant.HEAD) {
			close(ctx);
			in.clear();
			return null;
		}

		// 解密协议号
		dataIndex += 2;
		buf.readBytes(shortByte);
		lastCipher = decrypt(dataIndex, lastCipher, shortByte);
		short code = (short) (((shortByte[0] & 0xff) << 8) | (shortByte[1] & 0xff));

		// 解密长度
		dataIndex += 2;

		buf.readBytes(intByte);
		lastCipher = decrypt(dataIndex, lastCipher, intByte);
		int length = (((intByte[0] & 0x000000ff) << 24) | ((intByte[1] & 0x000000ff) << 16) | ((intByte[2] & 0x000000ff) << 8) | (intByte[3] & 0x000000ff));

		if (code <= 0 || length < 0) // 协议号和长度异常
		{
			Log.error("Error code or length,code:" + code + ",length:" + length + ",from:" + ctx.channel().remoteAddress());
			in.skipBytes(1);// 只跳过一个字节，尽最大可能保证不丢包
			revertKey();
			return null;
		}
		if (length > buf.readableBytes())// 数据包不完整
		{
			revertKey();
			return null;
		}

		byte[] data = null;
		if (length > 0) {
			// 解密数据
			data = new byte[length];
			buf.readBytes(data);
			dataIndex += 4;
			decrypt(dataIndex, lastCipher, data);
		}

		in.readerIndex(in.readerIndex() + buf.readerIndex());
	

		return Message.buildMessage(code, data);
	}

	private void close(ChannelHandlerContext ctx) {
		ctx.channel().close();
	}

	/** 解析消息, 返回当前最后用到的key **/
	private byte decrypt(int beginIndex, byte lastCipher, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			int keyIndex = beginIndex & 7;
			byte tempCipher = data[i];
			decryptKey[keyIndex] = (byte) (((~(decryptKey[keyIndex] - lastCipher)) ^ beginIndex) & 0xff);
			data[i] = (byte) (~((data[i] - lastCipher) ^ decryptKey[keyIndex]) & 0xff);
			lastCipher = tempCipher;
			beginIndex++;
		}
		return lastCipher;
	}

	/** 备份密钥 **/
	private void backupKey() {
		System.arraycopy(decryptKey, 0, bakKey, 0, bakKey.length);
	}

	/** 重设密钥 **/
	private void revertKey() {
		System.arraycopy(bakKey, 0, decryptKey, 0, decryptKey.length);

	}

}
