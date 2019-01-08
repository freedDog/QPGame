package com.game.server.rpc;

import java.util.List;
import java.util.Map;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.IGameService;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.rpc.handler.ProxyHandler;
import com.game.proto.msg.Message;
import com.game.server.GameServer;
import com.game.server.mgr.GameChannelMgrX;

/**
 * 网关连接服务
 * 
 */
@Rpc
public class GameServiceHandler extends ProxyHandler implements IGameService {
	protected final GameServer server;

	public GameServiceHandler(GameServer server) {
		this.server = server;
	}

	@Override
	public boolean onConnect(ProxyChannel channel) {
		return true;
	}

	@Override
	public boolean onClose(ProxyChannel channel) {
		// 获取配置
		if (!GameChannelMgrX.getGateClientMgr().unregister(channel)) {
			return false;
		}
		Log.debug("网关服断开!");
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void registerGate(ProxyChannel channel, ServerConfig gateInfo, RpcCallback callback) {
		if (!GameChannelMgrX.getGateClientMgr().register(channel, gateInfo)) {
			callback.callBack(0, "注册失败!", null);
			return;
		}
		// 注册成功.
		callback.callBack(1, null, server.getConfig());
		Log.debug("网关服连接成功!");

//		 测试逻辑服间的通讯
//		if (ConfigMgr.isDebug()) {
//			LocalGameTest.testRpc();
//			LocalGameTest.testGateRpc(channel);
//		}
	}

	@Rpc.RpcFunc
	@Override
	public void onHttpRequest(final String url, final Map<String, String> params, final ProxyChannel channel, final RpcCallback callback) {
		// Log.debug("onHttpRequest: " + url + " " + params + " " + channel);
		// 根据url判断模块
		ModuleName moduleName = ModuleName.getModuleByUrl(url);
		if (moduleName == null) {
			Log.error("没有找到url获取对应的模块! url=" + url);
			return;
		}

		// 获取当前逻辑服对应模块
		Module module = server.getModule(moduleName);
		if (module == null) {
			Log.error("逻辑服模块没有初始化! url=" + url + " moduleName=" + moduleName);
			return;
		}
		// 执行
		module.onHttpRequest(url, params, channel, callback);
	}

	@Rpc.RpcFunc
	@Override
	public void onModuleMessage(final Message packet, final ProxyChannel channel) {
		// Log.debug("onModuleMessage: " + packet + " " + channel);

		// 根据url判断模块
		short code = packet.getCode();
		ModuleName moduleName = ModuleName.getModuleByCode(code);

		// 获取当前逻辑服对应模块
		Module module = server.getModule(moduleName);
		if (module == null) {
			Log.error("逻辑服不存在模块! code=" + code + " moduleName=" + moduleName);
			return;
		}
		// 执行
		module.onModuleMessage(packet, channel);
	}

	@Rpc.RpcFunc
	@Override
	public void playerConnectionVerified(ProxyChannel channel, long connectId, long playerId, ServerConfig gate) {
		// 遍历所有模块执行断线处理
		List<Module> modules = server.getModules();
		for (Module module : modules) {
			module.onPlayerVerified(channel, connectId, playerId);
		}
	}

	@Rpc.RpcFunc
	@Override
	public void playerLostConnection(ProxyChannel channel, long connectId, long playerId) {
		// 遍历所有模块执行断线处理
		List<Module> modules = server.getModules();
		for (Module module : modules) {
			module.onPlayerLost(channel, connectId, playerId);
		}
	}

	@Rpc.RpcFunc
	@Override
	public void onModuleCommon(ModuleName moduleName, String cmd, Map<String, String> params) {
		Module module = server.getModule(moduleName);
		if (module == null) {
			return;
		}
		// 执行处理
		module.onModuleCommon(cmd, params);
	}

	@Rpc.RpcFunc
	@Override
	public String shutdown(String key, int waitTime) {
		// 执行关闭
		App.getInstance().stop(waitTime);
		return "ok";
	}

	@Rpc.RpcFunc
	@Override
	public int testCall(ProxyChannel channel, int a, RpcCallback callback) {
		callback.callBack(1, a);
		return a;
	}
}
