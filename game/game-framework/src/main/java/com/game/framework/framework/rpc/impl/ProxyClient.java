package com.game.framework.framework.rpc.impl;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.SocketAddress;

import com.game.framework.component.action.LoopAction;
import com.game.framework.framework.mgr.NettyServiceMgr;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcClient;
import com.game.framework.framework.rpc.RpcDevice;
import com.game.framework.framework.rpc.msg.RpcMsg;



/**
 * rpc 代理客户端
 * ProxyClient.java
 * @author JiangBangMing
 * 2019年1月3日下午3:56:44
 */
public class ProxyClient extends RpcClient<ProxyChannel> {
	public ProxyClient(RpcDevice<ProxyChannel> device) {
		super(device);
	}

	/** 开始连接 **/
	public boolean start(String host, int port) {
		// 启动
		if (!super.start(host, port, false)) {
			return false;
		}

		// 第一次更新
		update();

		// 增加定时器
		ServiceMgr.enqueue(new UpdateAction(3 * 1000));
		return true;
	}

	@Override
	protected NioEventLoopGroup createEventGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	/** 停止连接 **/
	@Override
	public synchronized void stop() {
		if (bootstrap != null) {
			// bootstrap.group().shutdownGracefully(); 这个循环是公用的不停止
			bootstrap = null;
		}
		// 停止连接
		if (future != null) {
			future.channel().close();
			future = null;
		}
	}

	@Override
	public String toString() {
		return "[host=" + host + ", port=" + port + "]";
	}

	@Override
	protected ProxyChannel createChannel() {
		final ProxyClient client = this;
		return new ProxyChannel() {
			@Override
			public boolean isConnect() {
				return client.isConnect();
			}

			@Override
			public void write(RpcMsg msg) {
				client.write(msg);
			}

			@Override
			public RpcDevice<?> getDevice() {
				return client.getDevice();
			}

			@Override
			public void close() {
				Channel channel = client.getNettyChannel();
				if (channel != null) {
					channel.close();
				}
			}

			@Override
			public String toString() {
				Channel channel = client.getNettyChannel();
				SocketAddress addr = (channel != null) ? channel.remoteAddress() : null;
				return "[" + config + " " + addr + ", " + this.getClass() + "]";
			}
		};
	}

	/** 定时更新器 **/
	class UpdateAction extends LoopAction {
		public UpdateAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(long now, long prev, long dt, int index) {
			getDevice().checkTimeOut(1000);
			ProxyClient.this.update();
			// 判断状态
			setLoop(isRunning());
			// Log.info(SGRpcClient.this + " loop: " + isRunning());
		}
	}

}
