package com.game.gate.mgr;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.rpc.GameProxyServiceService;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.handler.IProxyHandler;
import com.game.framework.framework.rpc.impl.ProxyClientMgr;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.gate.GateServer;
import com.game.gate.rpc.GameClinetHandler;

/**
 * 网关连接管理器<br>
 * 只用来管理逻辑服连接
 * GateChannelMgr.java
 * @author JiangBangMing
 * 2019年1月7日下午6:03:56
 */
public class GateChannelMgr extends GameChannelMgr {
	protected static GateService rpcService;

	protected static boolean init(GateServer server) {
		// 游戏服rpc服务处理器
		rpcService = new GateService(new GameClinetHandler(server));
		rpcService.start();

		// 绑定连接管理器
		mgrs.put(ServerConfig.TYPE_GAME, new ProxyClientMgr<ServerConfig>(rpcService));
		return true;
	}

	protected static void destroy() {
		if (rpcService != null) {
			rpcService.stop();
			rpcService = null;
		}
	}

	public static ProxyClientMgr<ServerConfig> getGameClientMgr() {
		return getChannelMgr(ServerConfig.TYPE_GAME);
	}

	/** 官网rpc服务 **/
	protected static class GateService extends GameProxyServiceService {

		public GateService(IProxyHandler handler) {
			super(handler);
		}

		@Override
		protected boolean checkRoute(ProxyChannel channel, RpcMsg packet) {
			// 转发处理
			byte[] to = packet.getTo();
			if (to == null) {
				return true; // 本地执行
			}

			// 检测去处
			ServerConfig toConfig = packet.getTo(ServerConfig.class);
			if (toConfig == null || toConfig.equals(App.getInstance().getConfig())) {
				return true; // 本地执行
			}

			// 获取连接
			ProxyChannel toChannel = getGameClientMgr().getChannel(toConfig);
			if (toChannel == null || !toChannel.isConnect()) {
				ServerConfig fromConfig = packet.getFrom(ServerConfig.class);
				Log.warn("逻辑服尚未连接, 转发失败! packet=" + packet + " to=" + toConfig + " from=" + fromConfig);
				return false;
			}

			// 转发
			toChannel.write(packet);
			// Log.debug("转发消息! packet=" + packet + " to=" + toConfig);
			return false;
		}
	}
}

