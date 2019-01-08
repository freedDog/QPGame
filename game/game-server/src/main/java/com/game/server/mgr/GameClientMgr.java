package com.game.server.mgr;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.base.service.config.ModuleConfig;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.server.App;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.RpcDevice;
import com.game.framework.framework.rpc.impl.ProxyConnectMgr;
import com.game.framework.framework.rpc.msg.RpcMsg;

/**
 * 逻辑服转发管理器<br>
 * 通过网关服转发消息用于实现访问其他逻辑服
 * GameClientMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:30:16
 */
public class GameClientMgr extends ProxyConnectMgr<ServerConfig> {

	/** 同步模块 **/
	public synchronized void syncModuleInfos(Map<String, List<ModuleConfig>> moduleInfos) {
		Set<ServerConfig> serverConfigs = new HashSet<>();
		// 遍历整理
		for (List<ModuleConfig> moduleConfigs : moduleInfos.values()) {
			for (ModuleConfig moduleConfig : moduleConfigs) {
				ServerConfig serverConfig = moduleConfig.getServerConfig();
				serverConfigs.add(serverConfig);
			}
		}

		// 清除不存在的连接
		Set<ServerConfig> nowConfigs = new HashSet<>(connects.keySet());
		for (ServerConfig nowConfig : nowConfigs) {
			if (!serverConfigs.contains(nowConfig)) {
				connects.remove(nowConfig); // 移除
			}
		}
		// 检测新增
		ServerConfig selfConfig = App.getInstance().getConfig();
		for (ServerConfig serverConfig : serverConfigs) {
			ProxyChannel channel = connects.get(serverConfig);
			if (channel != null) {
				continue; // 存在连接
			}
			// 判断是否是本进程的逻辑
			if (serverConfig.equals(selfConfig)) {
				channel = new LocalChannel(serverConfig);
			} else {
				channel = new GateForwardChannel(serverConfig);
			}

			// 插入对象
			ProxyChannel old = connects.putIfAbsent(serverConfig, channel);
			channel = (old != null) ? old : channel;
		}
	}

	/** 本地转发连接 **/
	public static class LocalChannel extends ProxyChannel {

		public LocalChannel(ServerConfig config) {
			super.setConfig(config);
		}

		@Override
		public void write(RpcMsg rpcMsg) {
			super.revc(rpcMsg); // 写入直接转为接收
		}

		@Override
		public boolean isConnect() {
			return true; // 本地一直保持连接
		}

		@Override
		public RpcDevice<?> getDevice() {
			return ProxyService.getInstance();
		}

	}

	/** rpc网关转发连接 **/
	public static class GateForwardChannel extends ProxyChannel {

		public GateForwardChannel(ServerConfig config) {
			this.config = config;
		}

		@Override
		public void close() {
		}

		@Override
		public boolean isConnect() {
			ProxyChannel channel = GameChannelMgr.getChannelMgr(ServerConfig.TYPE_GATE).getRandomChannel();
			return (channel != null) ? channel.isConnect() : false;
		}

		@Override
		public void write(RpcMsg msg) {
			// 随机获取个Gate转发
			ProxyChannel channel = GameChannelMgr.getChannelMgr(ServerConfig.TYPE_GATE).getRandomChannel();
			if (channel == null || !channel.isConnect()) {
				throw new RuntimeException("网关连接错误! channel=" + channel);
			}
			// 消息加上转发信息
			msg.setTo(config, ServerConfig.class);
			msg.setFrom(App.getInstance().getConfig(), ServerConfig.class);
			channel.write(msg);
		}

		@Override
		public RpcDevice<?> getDevice() {
			return ProxyService.getInstance();
		}
	}

}

