package com.game.base.service.config;

import com.game.framework.framework.rpc.ProxyConfig;

/**
 *  * 服务器配置<br>
 * 标记一个进程的信息
 * ServerConfig.java
 * @author JiangBangMing
 * 2019年1月4日下午3:14:31
 */
public class ServerConfig extends ProxyConfig {
	/** 游戏服 **/
	public static final int TYPE_GAME = 1;
	/** 网关服 **/
	public static final int TYPE_GATE = 2;

	protected ServerConfig() {
	}

	public ServerConfig(int id, String name, String host, int port) {
		super(id, name, host, port);
	}

	/** 服务器类型名 **/
	public static String getServerTypeStr(int type) {
		switch (type) {
		case TYPE_GAME:
			return "游戏服";
		case TYPE_GATE:
			return "网关服";
		}
		return "未知类型";
	}
}
