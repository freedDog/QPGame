package com.game.framework.framework.session.netty;


import com.game.framework.framework.session.Session;

import io.netty.channel.Channel;

/**
 * NettySession
 * NettySession.java
 * @author JiangBangMing
 * 2019年1月3日下午2:49:05
 */
public class NettySession extends Session {
	protected Channel channel;

	public NettySession() {
	}

	public NettySession(Channel channel) {
		this.channel = channel;
	}

	@Override
	public synchronized void stop() {
		if (channel == null) {
			return;
		}
		channel.close();
		channel = null;
	}

	@Override
	public boolean isConnect() {
		return (channel != null) ? channel.isActive() : false;
	}

	@Override
	public boolean write(Object msg) {
		try {
			channel.write(msg);
			channel.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
