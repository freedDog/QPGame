package com.game.framework.framework.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.game.framework.framework.rpc.RpcUtils;
import com.game.framework.framework.rpc.msg.RpcMsg;


/**
 * 协议加密器<br>
 * RpcEncoder.java
 * @author JiangBangMing
 * 2019年1月3日下午3:46:03
 */
public class RpcEncoder extends MessageToMessageEncoder<RpcMsg>
{
	protected static final short MSG_HEAD = 3076;
	protected static final int MSG_HEAD_SIZE = 2 + 4; // head len

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMsg obj, List<Object> out) throws Exception
	{
		// 生成二进制数据
		byte[] data = RpcUtils.toByte(obj, RpcMsg.class);
		int length = (data != null) ? data.length : 0;

		// 转化成二进制数据
		ByteBuf buffer = ctx.alloc().buffer(MSG_HEAD_SIZE + length);
		buffer.writeShort(MSG_HEAD);
		buffer.writeInt(length);
		if (length > 0 && data != null)
		{
			buffer.writeBytes(data);
		}
		// 输出消息
		out.add(buffer);
	}

	// /** 获取泛型接口第一个类型 **/
	// protected Class<?> getTClass()
	// {
	// return (Class<?>) GenericityUtils.getGenericType(this.getClass(), 0);
	// }
}
