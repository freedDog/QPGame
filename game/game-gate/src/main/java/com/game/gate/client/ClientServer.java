package com.game.gate.client;

import java.util.concurrent.TimeUnit;

import com.game.base.server.codec.RuneProtoSecureDecoder;
import com.game.base.server.codec.RuneProtoSecureEncoder;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.NettyServiceMgr;
import com.game.framework.framework.server.netty.NettyServer;
import com.game.proto.msg.Message;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * 客户端服务器连接
 * ClientServer.java
 * @author JiangBangMing
 * 2019年1月7日下午6:22:57
 */
public class ClientServer extends NettyServer {
	private static final int IDLE_TIME = 3 * 60; // 闲置超时(s)

	@Override
	public ChannelHandler createHandler() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new IdleStateHandler(IDLE_TIME, 0, IDLE_TIME, TimeUnit.SECONDS)); // 读写闲置时间
				pipeline.addLast("decoder", new RuneProtoSecureDecoder());
				pipeline.addLast("encoder", new RuneProtoSecureEncoder());
				pipeline.addLast("handler", new GateServerHandler());
				
			}
		};
	}

	@Override
	protected boolean initOption(ServerBootstrap bootstrap) {
		// TIME_WAIT时可重用端口，服务器关闭后可立即重启，此时任何非期
		// 望数据到达，都可能导致服务程序反应混乱，不过这只是一种可能，事实上很不可能
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		// 设置了ServerSocket类的SO_RCVBUF选项，就相当于设置了Socket对象的接收缓冲区大小，4KB
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 8);
		// 请求连接的最大队列长度，如果backlog参数的值大于操作系统限定的队列的最大长度，那么backlog参数无效
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		// 使用内存池的缓冲区重用机制
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// 当客户端发生断网或断电等非正常断开的现象，如果服务器没有设置SO_KEEPALIVE选项，则会一直不关闭SOCKET。具体的时间由OS配置
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		// 在调用close方法后，将阻塞n秒，让未完成发送的数据尽量发出，netty中这部分操作调用方法异步进行。我们的游戏业务没有这种需要，所以设置为0
		bootstrap.childOption(ChannelOption.SO_LINGER, 0);
		// 数据包不缓冲,立即发出
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		// 发送缓冲大小，默认8192
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024 * 8);
		// 接收缓存大小 总结: 通常情况下, 我个人经验是不建议设置rcv_buf, linux内核会对每一个连接做动态的调整, 一般情况下足够智能, 如果设置死了, 就失去了这个特性, 尤其是大量长连接的应用,
		// bootstrap.childOption(ChannelOption.SO_RCVBUF, RpcUtils.BUFFSIZE);
		// 使用内存池的缓冲区重用机制
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// WRITE_BUFFER_HIGH_WATER_MARK 与 WRITE_BUFFER_LOW_WATER_MARK是两个流控的参数，默认值分别为32*2K与32K.。
		// 如果在writer buffet里排队准备输出的字节超过上限，Channel就不是writable的，NIO的事件轮询里就会把它摘掉，直到它低于32k才重新变回writable。
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1024 * 128);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 1024 * 64);
		return true;
	}

	@Override
	protected void onStart(int port) {
		Log.info("socket服务启动完成, port=" + port);
	}

	@Override
	protected void onStop() {
	}

	@Override
	protected NioEventLoopGroup createParentGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	@Override
	protected NioEventLoopGroup createChildGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	/** 服务器接口 **/
	class GateServerHandler extends SimpleChannelInboundHandler<Message> {
		/** 连接激活时 **/
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			ClientMgr.getInstance().channelActive(ctx.channel());
		}

		/** 连接断开 **/
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			ClientMgr.getInstance().channelInactive(ctx.channel());
		}

		/** 产生错误时 **/
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
			if (e.getClass() == java.io.IOException.class) {
				// Log.debug("客户端错误: " + ctx.channel().remoteAddress() + " " + e.toString());
			} else {
				Log.error("客户端错误: " + ctx.channel().remoteAddress(), e);
			}
			// 错误关闭
			ctx.channel().close();
		}

		/** 接受消息时 **/
		@Override
		protected void channelRead0(final ChannelHandlerContext ctx, final Message packet) throws Exception {
			ClientMgr.getInstance().channelRead(ctx.channel(), packet);
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			// 检测事件类型
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent) evt;
				if (event.state() == IdleState.READER_IDLE) {
					// type = "read idle";
				} else if (event.state() == IdleState.WRITER_IDLE) {
					// type = "write idle";
				} else if (event.state() == IdleState.ALL_IDLE) {
					// type = "all idle";
					Log.warn(ctx.channel().remoteAddress() + "闲置超时断开! type=" + event.state());
					ctx.close();
				}

				// ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE); // 3
				return;
			}

			// 其他事件
			super.userEventTriggered(ctx, evt);

		}
	}

}
