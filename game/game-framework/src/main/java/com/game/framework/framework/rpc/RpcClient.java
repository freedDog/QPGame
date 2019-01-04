package com.game.framework.framework.rpc;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.codec.RpcDecoder;
import com.game.framework.framework.rpc.codec.RpcEncoder;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.framework.framework.rpc.msg.RpcProtocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/**
 * rpc客户端
 * RpcClient.java
 * @author JiangBangMing
 * 2019年1月3日下午3:39:40
 */
public abstract class RpcClient<T extends RpcChannel> {
	protected final AttributeKey<T> ckey = AttributeKey.<T> valueOf("Channel");
	protected final RpcDevice<T> device; // rpc设备
	protected T channel;

	protected String host;
	protected int port;
	protected Bootstrap bootstrap;
	protected ChannelFuture future;

	public RpcClient(RpcDevice<T> device) {
		this.device = device;
		channel = this.createChannel();
	}

	/** 开始连接 **/
	public boolean start(String host, int port) {
		return start(host, port, true);
	}

	/** 开始连接 **/
	protected boolean start(String host, int port, boolean connect) {
		this.host = host;
		this.port = port;
		// 初始化
		if (!init()) {
			return false;
		}

		// 判断是否立马连接
		if (connect) {
			// 启动连接
			if (!connect(host, port)) {
				// Log.info("服务器连接失败!");
				return false;
			}
		}
		// 增加定时器
		// ActionQueueMgr.enqueue(new RpcUpdataAction(1000));
		return true;
	}

	/** 初始化客户端 **/
	protected boolean init() {
		try {
			// 创建处理线程
			bootstrap = new Bootstrap();
			bootstrap.group(createEventGroup());
			bootstrap.channel(NioSocketChannel.class);
			// 初始化配置
			if (!initOption(bootstrap)) {
				return false;
			}
			bootstrap.handler(createHandler());

			return true;
		} catch (Exception e) {
			Log.error("初始化rpc client错误!", e);
		}
		return false;
	}

	/** 初始化配置 **/
	protected boolean initOption(Bootstrap bootstrap) {
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		// bootstrap.option(ChannelOption.SO_RCVBUF, RpcServer.BUFFSIZE);
		bootstrap.option(ChannelOption.SO_SNDBUF, RpcServer.BUFF_SIZE);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);
		bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, (int) (RpcServer.BUFF_SIZE * 0.8));
		bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, (int) (RpcServer.BUFF_SIZE * 0.5));
		return true;
	}

	/** server socket channel的eventloop **/
	protected NioEventLoopGroup createEventGroup() {
		return new NioEventLoopGroup();
	}

	/** 开启连接 **/
	protected synchronized boolean connect(String host, int port) {
		if (isConnect()) {
			return false;
		}
		if (bootstrap == null || bootstrap.group().isShutdown()) {
			return false;
		}
		// 清除
		if (future != null) {
			future.channel().close();
		}

		try {
			// 连接
			future = bootstrap.connect(host, port);
			return isConnect();
		} catch (Exception e) {
			Log.error("连接地址错误!" + host + " " + port, e);
		}
		return false;
	}

	/** 停止连接 **/
	public synchronized void stop() {
		if (bootstrap != null) {
			bootstrap.group().shutdownGracefully();
			bootstrap = null;
		}
	}

	/** 获取连接 **/
	public T getChannel() {
		return channel;
	}

	/** 创建连接 **/
	protected abstract T createChannel();

	/** 写入消息 **/
	public void write(RpcMsg msg) {
		Channel channel = (future != null) ? future.channel() : null;
		if (channel != null) {
			channel.writeAndFlush(msg);
		}
	}

	/** 获取连接 **/
	public Channel getNettyChannel() {
		return (future != null) ? future.channel() : null;
	}

	public synchronized boolean isConnect() {
		return (future != null) ? future.channel().isActive() : false;
	}

	/** 是否运行中 **/
	public boolean isRunning() {
		return (bootstrap != null) ? !bootstrap.group().isShutdown() : false;
	}

	public RpcDevice<T> getDevice() {
		return device;
	}

	/** 保持连接 **/
	protected synchronized void update() {
		// 判断是否还在运行中
		if (!isRunning()) {
			return; // 客户端停止连接了
		}

		// 检测是否还连接
		if (isConnect()) {
			this.write(RpcMsg.create(RpcProtocol.RPC_KEEP, null));
			return;
		}

		// 重新连接
		if (!connect(host, port)) {
			Log.warn("连接rpc服务器失败! host=" + host + " port=" + port);
		}
	}

	/** 创建句柄 **/
	protected ChannelHandler createHandler() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder", new RpcDecoder());
				pipeline.addLast("encoder", new RpcEncoder());
				pipeline.addLast("handler", new RpcClientHandler(ckey));
			}
		};
	}

	/** rpc服务器连接 **/
	public static class RpcChannelImpl<T extends RpcChannel> extends RpcChannel {
		protected RpcClient<T> client;

		public RpcChannelImpl(RpcClient<T> client) {
			this.client = client;
		}

		@Override
		public boolean isConnect() {
			return client.isConnect();
		}

		@Override
		protected void write(RpcMsg msg) {
			client.write(msg);
		}

		@Override
		public RpcDevice<T> getDevice() {
			return client.device;
		}

		@Override
		public void close() {
			Channel channel = client.getNettyChannel();
			if (channel != null) {
				channel.close();
			}
		}
	}

	/** rpc客戶端处理接口 **/
	class RpcClientHandler extends RpcChannelHandler<T> {
		protected RpcClientHandler(AttributeKey<T> ckey) {
			super(ckey);
		}

		/** 连接激活时 **/
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			T c = getChannel();
			if (!super.channelActive(ctx, c)) {
				return;
			}

			// 启动验证功能
			if (c.getDevice().isVerify() && device != null) {
				write(RpcMsg.create(RpcProtocol.RPC_INFO, RpcUtils.toByte(device.createInfoMsg())));
			}
		}

	}

}
