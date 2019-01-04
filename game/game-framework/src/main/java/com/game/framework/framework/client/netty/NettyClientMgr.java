package com.game.framework.framework.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import com.game.framework.component.log.Log;
import com.game.framework.framework.session.netty.NettySession;


/**
 * Netty管理器(管理多个客户端连接)
 * NettyClientMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午2:49:28
 */
public abstract class NettyClientMgr {
	public static final AttributeKey<NettySession> SESSION_KEY = AttributeKey.valueOf("SESSION");

	private Bootstrap bootstrap;

	public synchronized boolean start(int threads) {
		EventLoopGroup workerGroup = createChildGroup(threads);
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 32);
		bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 32);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		if (!initOption(bootstrap)) {
			Log.error("初始化NettyClientMgr配置失败!", true);
			return false;
		}
		bootstrap.handler(createHandler());
		return true;
	}

	public synchronized void stop() {
		if (bootstrap == null) {
			return;
		}
		bootstrap.group().shutdownGracefully().syncUninterruptibly();
		bootstrap = null;
	}

	/** 初始化参数 **/
	protected abstract boolean initOption(Bootstrap bootstrap);

	/** 创建处理接口 **/
	protected abstract ChannelHandler createHandler();

	/** 这个是用于处理accept到的channel的eventloop **/
	protected NioEventLoopGroup createChildGroup(int thread) {
		return new NioEventLoopGroup(thread);
	}

	/** 连接服务器 **/
	public NettyClientSession connect(String host, int port, boolean autoConnect) {
		NettyClientSession session = new NettyClientSession();
		if (autoConnect) {
			session.start(host, port);
		}
		return session;
	}

	/** 客户端会话 **/
	public class NettyClientSession extends NettySession {

		public boolean start(String host, int port) {
			// 检测是否连接
			if (isConnect()) {
				return false;
			}

			// 连接地址
			InetSocketAddress address = new InetSocketAddress(host, port);
			try {
				// 开始连接
				ChannelFuture future = bootstrap.connect(address).syncUninterruptibly();
				if (future.isSuccess() && future.channel().isActive()) {
					future.channel().attr(SESSION_KEY).set(this);
					this.channel = future.channel();
				} else {
					Log.error("连接完成但是连接不可用, isSuccess : " + future.isSuccess() + ", isAvtive : " + future.channel().isActive());
					return false;
				}
			} catch (Exception e) {
				Log.error("执行连接异常, socket : " + address, e);
				return false;
			}
			return true;
		}

	}

}
