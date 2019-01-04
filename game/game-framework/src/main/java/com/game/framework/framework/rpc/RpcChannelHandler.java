package com.game.framework.framework.rpc;


import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcMsg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * rpc基础处理逻辑
 * RpcChannelHandler.java
 * @author JiangBangMing
 * 2019年1月3日下午3:36:54
 */
public abstract class RpcChannelHandler<T extends RpcChannel> extends SimpleChannelInboundHandler<RpcMsg> {
	protected final AttributeKey<T> ckey;

	protected RpcChannelHandler(AttributeKey<T> ckey) {
		this.ckey = ckey;
	}

	@Override
	public abstract void channelActive(ChannelHandlerContext ctx) throws Exception;

	/** 连接激活时, 绑定RpcChannel **/
	protected boolean channelActive(ChannelHandlerContext ctx, T c) throws Exception {
		if (c == null) {
			Log.error("没有初始化RpcChannel!");
			ctx.channel().close();
			return false;
		}
		// 绑定和触发消息
		ctx.channel().attr(ckey).set(c);
		c.onConnect();
		return true;
	}

	/** 连接闲置时(断线也会调用) **/
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		T c = ctx.channel().attr(ckey).get();
		if (c != null) {
			c.onClose();
		}
	}

	/** 产生错误时 **/
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		if (e.getClass() == java.io.IOException.class) {
			Log.warn("rpc连接错误: " + ctx.channel().remoteAddress() + " " + e.toString());
		} else {
			Log.error("rpc连接错误: " + ctx.channel().remoteAddress(), e);
		}
		ctx.channel().close();
	}

	/** 接受消息时 **/
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMsg packet) throws Exception {
		// 获取连接
		T c = ctx.channel().attr(ckey).get();
		if (c == null) {
			Log.error("rpc初始化失败!");
			ctx.channel().close();
			return;
		}

		// 处理消息
		if (!c.revc(packet)) {
			ctx.channel().close(); // 处理失败
			return;
		}
	}
}
