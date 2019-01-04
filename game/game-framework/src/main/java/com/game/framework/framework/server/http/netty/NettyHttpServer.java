package com.game.framework.framework.server.http.netty;

import com.game.framework.framework.server.netty.NettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

/**
 * 基于netty的http服务器
 * NettyHttpServer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:02:27
 */
public abstract class NettyHttpServer extends NettyServer {

	@Override
	protected boolean initOption(ServerBootstrap bootstrap) {
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true); // tcp无延迟
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 访问不保持连接
		bootstrap.childOption(ChannelOption.SO_REUSEADDR, true); // 重用端口

		return true;
	}

	@Override
	protected ChannelHandler createHandler() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// 读取写入 时间超时处理
				ch.pipeline().addLast(new ReadTimeoutHandler(10));
				ch.pipeline().addLast(new WriteTimeoutHandler(3));

				// 方案1, 简化的绑定.
				// ch.pipeline().addLast(new HttpServerCodec()); // 用于解析http报文的handler
				// ch.pipeline().addLast(new HttpObjectAggregator(Short.MAX_VALUE * 2));

				// 方案2, Encoder必须放在第一位, 否则C#的post有错误.
				ch.pipeline().addLast(new HttpResponseEncoder()); // 用于将response编码成httpresponse报文发送, http消息下发加密(不要超过HttpObjectAggregator)
				ch.pipeline().addLast(new HttpRequestDecoder()); // 用于解析http报文的handler
				ch.pipeline().addLast(new HttpObjectAggregator(Short.MAX_VALUE * 2)); // 用于将解析出来的数据封装成http对象，httprequest什么的
				ch.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
				// 逻辑处理
				ch.pipeline().addLast(createHttpHandler());
			}
		};
	}

	protected abstract SimpleChannelInboundHandler<? extends HttpObject> createHttpHandler();

}
