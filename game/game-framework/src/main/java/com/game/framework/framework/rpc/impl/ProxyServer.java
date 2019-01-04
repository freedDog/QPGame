package com.game.framework.framework.rpc.impl;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.SocketAddress;

import com.game.framework.component.action.LoopAction;
import com.game.framework.framework.mgr.NettyServiceMgr;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcDevice;
import com.game.framework.framework.rpc.RpcServer;
import com.game.framework.framework.rpc.msg.RpcMsg;



/**
 * 客户端服务器连接
 * ProxyServer.java
 * @author JiangBangMing
 * 2019年1月3日下午4:00:04
 */
public class ProxyServer extends RpcServer<ProxyChannel> {
	public ProxyServer(RpcDevice<ProxyChannel> device) {
		super(device);
	}

	@Override
	protected void onStart(int port) {
		// 启动绑定监听
		ServiceMgr.enqueue(new UpdataAction(1000));
		// Log.info("服务器启动成功, port=" + port);
	}

	@Override
	protected NioEventLoopGroup createParentGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	@Override
	protected NioEventLoopGroup createChildGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	@Override
	protected ProxyChannel createChannel(final Channel channel) {
		final ProxyServer server = this;
		return new ProxyChannel() {
			@Override
			public boolean isConnect() {
				return (channel != null) && channel.isActive();
			}

			@Override
			public void write(RpcMsg msg) {
				if (channel != null) {
					channel.writeAndFlush(msg);
				}
			}

			@Override
			public RpcDevice<?> getDevice() {
				return server.getDevice();
			}

			@Override
			public void close() {
				if (channel != null) {
					channel.close();
				}
			}

			@Override
			public String toString() {
				SocketAddress addr = (channel != null) ? channel.remoteAddress() : null;
				return "[" + config + " " + addr + ", " + this.getClass() + "]";
			}
		};
	}

	/** 定时更新器 **/
	class UpdataAction extends LoopAction {
		public UpdataAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(long now, long prev, long dt, int index) {
			ProxyServer.this.getDevice().checkTimeOut(1000);
		}
	}

}
