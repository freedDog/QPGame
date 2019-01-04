package com.game.framework.framework.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.BindException;

import com.game.framework.component.log.Log;
import com.game.framework.utils.ThreadUtils;


/**
 * Netty服务器基类
 * NettyServer.java
 * @author JiangBangMing
 * 2019年1月3日下午3:42:25
 */
public abstract class NettyServer {
	private EventLoopGroup bossGroup;
	private Channel channel = null;
	private volatile boolean isRunStop = false;

	/** 启动(非阻塞, 必然true) **/
	public boolean startSync(final int port) throws Exception {
		return startSync(port, 1000);
	}

	/** 启动(非阻塞, 必然true) **/
	public boolean startSync(final int port, int waitTime) throws Exception {
		// 启用线程执行
		ThreadUtils.run("ServerSync", new Runnable() {
			@Override
			public void run() {
				try {
					start0(port);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		});

		// 检测启动
		long prev = System.currentTimeMillis();
		while (!isRunning()) {
			long now = System.currentTimeMillis();
			if ((now - prev) > waitTime) {
				return false; // 超时没成功
			}
			Thread.yield();
		}
		return true;
	}

	/** 启动(阻塞) **/
	public boolean start(int port) throws Exception {
		return start0(port);
	}

	/** 初始化参数 **/
	protected abstract boolean initOption(ServerBootstrap bootstrap);

	/** server socket channel的eventloop **/
	protected NioEventLoopGroup createParentGroup() {
		return new NioEventLoopGroup();
	}

	/** 这个是用于处理accept到的channel的eventloop **/
	protected NioEventLoopGroup createChildGroup() {
		return new NioEventLoopGroup();
	}

	/** 启动执行 **/
	protected boolean start0(int port) throws Exception {
		ServerBootstrap bootstrap = null;
		EventLoopGroup bossGroup = null;
		EventLoopGroup workerGroup = null;

		synchronized (this) {
			if (isRunning()) {
				throw new Exception("server is start");
			}
			// 创建
			bossGroup = createParentGroup(); // 这个是用于serversocketchannel的eventloop
			workerGroup = createChildGroup(); // 这个是用于处理accept到的channel
			bootstrap = new ServerBootstrap(); // 构建serverbootstrap对象
			bootstrap.group(bossGroup, workerGroup); // 设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
			bootstrap.channel(NioServerSocketChannel.class); // 用它来建立新accept的连接，用于构造serversocketchannel的工厂类
			bootstrap.childHandler(createHandler());
			// 初始化配置
			if (!initOption(bootstrap)) {
				Log.error("初始化NettyServer配置失败!", true);
				return false;
			}
		}

		// 开始启动
		Channel channel = null;
		try {
			synchronized (this) {
				// bind方法会创建一个serverchannel，并且会将当前的channel注册到eventloop上面，会为其绑定本地端口并对其进行初始化，为其的pipeline加一些默认的handler
				channel = bootstrap.bind(port).sync().channel();
				// System.out.println("server start");
				onStart(port); // 启动事件
				this.bossGroup = bossGroup;
				this.channel = channel;
			}

			// 添加紧急结束运行(jsvc 结束处理)
			isRunStop = false; // 标记运行关闭
			Runtime.getRuntime().addShutdownHook(new Thread() {
				// 结束检测
				@Override
				public void run() {
					// 检测是否执行过结束
					if (!isRunStop) {
						// 没处理过结束, 执行结束操作.
						synchronized (this) {
							isRunStop = true;
							onStop(); // 停止事件
						}
					}
				}
			});

			// 运行
			channel.closeFuture().sync(); // 相当于在这里阻塞，直到serverchannel关闭

			// 正常结束
			synchronized (this) {
				isRunStop = true;
				onStop(); // 停止事件
			}
		} catch (Exception e) {
			// 处理端口冲突
			if (BindException.class.isInstance(e)) {
				// throw new Exception("port conflicts:" + port);
				// System.err.println("port conflicts:" + port);
				Log.error("port conflicts: " + port, true);
				return false;
			}
			// throw e;
			e.printStackTrace();
		} finally {
			// System.out.println("server closing ...");
			// 关闭LoopGroup
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully().sync();

			// System.out.println("server close");
		}
		return true;
	}

	public synchronized void stop() throws Exception {
		if (!isRunning()) {
			// throw new Exception("server no start");
			return;
		}
		// 关闭服务器
		bossGroup.shutdownGracefully();
		bossGroup = null;
	}

	protected abstract ChannelHandler createHandler();

	public boolean isRunning() {
		return (bossGroup != null && !bossGroup.isShutdown()) && (channel != null && channel.isActive());
	}

	protected void onStart(int port) {
		Log.info("server start " + port);
	}

	protected void onStop() {
		Log.info("server stop ");
	}
}

// socket
// // 望数据到达，都可能导致服务程序反应混乱，不过这只是一种可能，事实上很不可能
// bootstrap.option(ChannelOption.SO_REUSEADDR, true);
// // 设置了ServerSocket类的SO_RCVBUF选项，就相当于设置了Socket对象的接收缓冲区大小，4KB
// bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 8);
// // 请求连接的最大队列长度，如果backlog参数的值大于操作系统限定的队列的最大长度，那么backlog参数无效
// bootstrap.option(ChannelOption.SO_BACKLOG, 128);
// // 使用内存池的缓冲区重用机制
// bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
// // 当客户端发生断网或断电等非正常断开的现象，如果服务器没有设置SO_KEEPALIVE选项，则会一直不关闭SOCKET。具体的时间由OS配置
// bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
// // 在调用close方法后，将阻塞n秒，让未完成发送的数据尽量发出，netty中这部分操作调用方法异步进行。我们的游戏业务没有这种需要，所以设置为0
// bootstrap.childOption(ChannelOption.SO_LINGER, 0);
// // 数据包不缓冲,立即发出
// bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
// // 发送缓冲大小，默认8192
// bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024 * 8);
// // 使用内存池的缓冲区重用机制
// bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
// // 写入缓存设置
// bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1024 * 128);
// bootstrap.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 1024 * 64);

// http
// bootstrap.childOption(ChannelOption.TCP_NODELAY, true); // tcp无延迟
// bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 访问保持连接
// bootstrap.childOption(ChannelOption.SO_REUSEADDR, true); // 重用端口

