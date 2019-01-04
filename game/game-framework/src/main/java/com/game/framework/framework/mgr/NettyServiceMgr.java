package com.game.framework.framework.mgr;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

import com.game.framework.component.log.Log;



/**
 * netty服务管理<br>
 * 统一管理服务器线程
 * NettyServiceMgr.java
 * @author JiangBangMing
 * 2019年1月3日下午3:01:40
 */
public class NettyServiceMgr {
	protected static NioEventLoopGroup eventLoopGroup; // netty线程池

	static {
		init();
	}

	/** 初始化 **/
	public static boolean init() {
		// 判断是否初始化过了
		if (eventLoopGroup != null) {
			return true;
		}
		// 创建线程
		int thread = Runtime.getRuntime().availableProcessors();
		thread = Math.max(thread, 4);
		return init(thread * 2);
	}

	/** 初始化, 生成线程队列和系统队列 **/
	protected synchronized static boolean init(int thread) {
		destroy();
		eventLoopGroup = new NioEventLoopGroup(thread);
		return true;
	}

	/** 关闭服务 **/
	public static synchronized void destroy() {
		try {
			if (eventLoopGroup != null) {
				eventLoopGroup.shutdownGracefully();
				eventLoopGroup.awaitTermination(2, TimeUnit.MINUTES);
				eventLoopGroup = null;
			}
		} catch (Exception e) {
			Log.error("关闭线程池异常", e);
		}
	}

	public static NioEventLoopGroup getEventLoopGroup() {
		return eventLoopGroup;
	}
}
