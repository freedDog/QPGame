package com.game.framework.framework.rpc;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.codec.RpcDecoder;
import com.game.framework.framework.rpc.codec.RpcEncoder;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.framework.framework.rpc.msg.RpcProtocol;
import com.game.framework.framework.server.netty.NettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;

/**
 * 客户端服务器连接
 * RpcServer.java
 * @author JiangBangMing
 * 2019年1月3日下午3:40:43
 */
public abstract class RpcServer<T extends RpcChannel> extends NettyServer {
	protected final static int BUFF_SIZE = 1024 * 1024 * 8; // rpc消息缓存大小
	protected final AttributeKey<T> ckey = AttributeKey.<T> valueOf("Channel");
	protected final RpcDevice<T> device;

	public RpcServer(RpcDevice<T> device) {
		this.device = device;
	}

	public boolean start(int port) {
		try {
			if (!this.startSync(port)) {
				Log.error("rpc服务器启动失败! port=" + port);
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected ChannelHandler createHandler() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder", new RpcDecoder());
				pipeline.addLast("encoder", new RpcEncoder());
				pipeline.addLast("handler", new RpcServerHandler(ckey));
			}
		};
	}

	@Override
	protected boolean initOption(ServerBootstrap bootstrap) {

		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, RpcServer.BUFF_SIZE);
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.SO_LINGER, 0);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		// 接收缓存大小 总结: 通常情况下, 我个人经验是不建议设置rcv_buf, linux内核会对每一个连接做动态的调整, 一般情况下足够智能, 如果设置死了, 就失去了这个特性, 尤其是大量长连接的应用,
		// bootstrap.childOption(ChannelOption.SO_RCVBUF, RpcUtils.BUFFSIZE);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, RpcServer.BUFF_SIZE);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// 高水位线和低水位线，当buffer的大小超过高水位线的时候对应channel的isWritable就会变成false，当buffer的大小低于低水位线的时候，isWritable就会变成true。
		// 所以应用应该判断isWritable，如果是false就不要再写数据了。高水位线和低水位线是字节数，默认高水位是64K，低水位是32K，我们可以根据我们的应用需要支持多少连接数和系统资源进行合理规划
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, (int) (RpcServer.BUFF_SIZE * 0.8));
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, (int) (RpcServer.BUFF_SIZE * 0.5));
		return true;
	}

	public RpcDevice<T> getDevice() {
		return device;
	}

	/** 创建连接 **/
	protected abstract T createChannel(Channel channel);

	/** rpc服务器连接 **/
	public static class RpcChannelImpl<T extends RpcChannel> extends RpcChannel {
		protected Channel channel;
		protected RpcServer<T> server;

		public RpcChannelImpl(RpcServer<T> server, Channel channel) {
			this.channel = channel;
			this.server = server;
		}

		@Override
		public boolean isConnect() {
			return channel != null && channel.isActive();
		}

		@Override
		protected void write(RpcMsg msg) {
			channel.writeAndFlush(msg);
		}

		@Override
		public RpcDevice<T> getDevice() {
			return server.device;
		}

		@Override
		public void close() {
			channel.close();
		}
	}

	class RpcServerHandler extends RpcChannelHandler<T> {
		protected RpcServerHandler(AttributeKey<T> ckey) {
			super(ckey);
		}

		/** 连接激活时 **/
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			T c = RpcServer.this.createChannel(ctx.channel());
			if (!super.channelActive(ctx, c)) {
				return;
			}

			// 启动验证功能
			if (device != null && c.getDevice().isVerify()) {
				c.write(RpcMsg.create(RpcProtocol.RPC_INFO, RpcUtils.toByte(device.createInfoMsg())));
			}
		}

	}
}
