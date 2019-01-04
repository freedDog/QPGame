package com.game.framework.framework.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

/**
 * netty工具类
 * 
 */
public class NettyUtils {
	/**
	 * 获取客户端ip
	 * 
	 * @param ctx
	 * @param msg
	 *            消息(可空)
	 * @return
	 */
	public static String getIp(ChannelHandlerContext ctx, Object msg) {
		String ip = null;
		// 判断是否是http访问
		if (msg != null && (msg instanceof HttpRequest)) {
			HttpRequest request = (HttpRequest) msg;
			ip = request.headers().get("X-Forwarded-For");
		}

		// 常规获取方式
		if (ip == null) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			ip = insocket.getAddress().getHostAddress();
		}
		return (ip != null) ? ip : "";
	}

}
