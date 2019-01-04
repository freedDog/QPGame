package com.game.framework.framework.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import com.game.framework.component.log.Log;
import com.game.framework.framework.session.Session;


/**
 * Netty客户端(单连接)
 * NettyClient.java
 * @author JiangBangMing
 * 2019年1月3日下午2:44:37
 */
public abstract class NettyClient extends Session {
	protected Bootstrap bootstrap;
	protected EventLoopGroup workerGroup;
	protected Channel channel;

	/** 初始化参数 **/
	protected abstract boolean initOption(Bootstrap bootstrap);

	/** 这个是用于处理accept到的channel的eventloop **/
	protected NioEventLoopGroup createChildGroup() {
		return new NioEventLoopGroup();
	}

	/** 创建处理接口 **/
	protected abstract ChannelHandler createHandler();

	public boolean start(String host, int port) {
		return connect0(host, port);
	}

	/** 开始连接 **/
	protected synchronized boolean connect0(String host, int port) {
		try {
			// 连接地址
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);
			// 创建处理线程
			EventLoopGroup workerGroup = createChildGroup();
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workerGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 32);
			bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 32);
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
			if (!initOption(bootstrap)) {
				Log.error("初始化NettyClient配置失败!", true);
				return false;
			}
			bootstrap.handler(createHandler());
			// 进行连接
			ChannelFuture future = bootstrap.connect(socketAddress).syncUninterruptibly();
			if (!future.isSuccess() || !future.channel().isActive()) {
				Log.error("连接服务器失败! host=" + host + " port=" + port);
				return false;
			}
			synchronized (this) {
				this.channel = future.channel();
				this.bootstrap = bootstrap;
				this.workerGroup = workerGroup;
			}
			return true;
		} catch (Exception e) {
			Log.error("连接服务器错误! host=" + host + " port=" + port, e);
		}
		return false;
	}

	@Override
	public synchronized boolean isConnect() {
		return (channel != null) ? channel.isActive() : false;
	}

	@Override
	public synchronized void stop() {
		if (bootstrap != null && channel != null && workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
			bootstrap = null;
			channel = null;
		}
	}

	@Override
	public boolean write(Object msg) {
		channel.write(msg);
		channel.flush();
		return true;
	}
}
