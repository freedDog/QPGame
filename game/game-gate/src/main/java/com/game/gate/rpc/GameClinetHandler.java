package com.game.gate.rpc;

import java.util.List;

import com.game.base.service.rpc.callback.ServerRegisterCallBack;
import com.game.base.service.rpc.handler.IGameClient;
import com.game.base.service.rpc.handler.IGameService;
import com.game.framework.component.action.Action;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.rpc.handler.ProxyHandler;
import com.game.framework.utils.collections.ArrayUtils;
import com.game.gate.GateServer;
import com.game.gate.client.ClientMgr;
import com.game.gate.client.ClientMgr.ClientSession;
import com.game.proto.msg.Message;

/**
 * 逻辑服连接处理句柄
 * 
 */
@Rpc
public class GameClinetHandler extends ProxyHandler implements IGameClient {
	protected final GateServer server;

	public GameClinetHandler(GateServer server) {
		this.server = server;
	}

	@Override
	public boolean onConnect(final ProxyChannel channel) {
		Log.debug("逻辑服连接成功!");
		IGameService gameService = channel.createImpl(IGameService.class);
		gameService.registerGate(channel, server.getConfig(), new ServerRegisterCallBack(channel));
		return true;
	}

	@Override
	public boolean onClose(ProxyChannel channel) {
		Log.debug("逻辑服连接断开!");
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void sendMessageToAll(ProxyChannel channel, int[] gameZoneIds, Message packet) {
		// Log.debug("群发消息: gameZoneIds=" + Arrays.toString(gameZoneIds) + " packet=" + packet);
		// 遍历客户端
		List<ClientSession> sessions = ClientMgr.getInstance().getAllSession();
		for (ClientSession session : sessions) {
			if (!session.isConnect()) {
				continue;
			}
			// 判断是否绑定了玩家信息
			long playerId = session.getPlayerId();
			if (playerId <= 0) {
				continue;
			}
			// 判断区服, 如果gameZoneIds为null代表全部通过.
			if (gameZoneIds != null && ArrayUtils.indexOf(gameZoneIds, session.getGameZoneId()) < 0) {
				continue;
			}
			// 发送消息
			session.write(packet);
		}
	}

	@Rpc.RpcFunc
	@Override
	public void sendMessageToClientByConnectId(ProxyChannel channel, final Message packet, final int action) {
		long connectId = packet.getConnectId();
		// 获取连接, 没有则为断开连接了.
		final ClientSession session = ClientMgr.getInstance().getSessionByConnectId(connectId);
		if (session == null) {
			Log.debug("找不到连接! connectId=" + connectId + " channel=" + channel + " packet=" + packet.toSimpleString());
			return;
		}
		// 塞入队列执行(保证相关逻辑处理顺序)
		session.enqueue(new SendMessageAction(session, packet, action));
	}

	@Rpc.RpcFunc
	@Override
	public boolean sendMessageToClient(final ProxyChannel channel, final Message packet, int action) {
		long playerId = packet.getPlayerId();
		// 查找玩家连接, 没有则为断开连接了.
		final ClientSession session = ClientMgr.getInstance().getSession(playerId);
		if (session == null) {
			Log.debug("找不到玩家连接! playerId=" + playerId + " channel=" + channel + " packet=" + packet.toSimpleString());
			return false;
		}
		// 塞入队列执行(保证相关逻辑处理顺序)
		session.enqueue(new SendMessageAction(session, packet, action));
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void bindPlayerId(final long connectId, final long playerId) {
		// 获取连接, 没有则为断开连接了.
		final ClientSession session = ClientMgr.getInstance().getSessionByConnectId(connectId);
		if (session == null) {
			Log.debug("找不到连接! connectId=" + connectId);
			return;
		}

		// 提交队列
		session.enqueue(new Runnable() {
			@Override
			public void run() {
				// 发送消息
				ClientMgr.getInstance().bindPlayerId(session, playerId);
			}
		});

	}

	@Rpc.RpcFunc
	@Override
	public void closeConnectByConnectId(long connectId) {
		final ClientSession session = ClientMgr.getInstance().getSessionByConnectId(connectId);
		if (session == null) {
			return;
		}
		// 提交关闭任务
		session.enqueue(new Runnable() {
			@Override
			public void run() {
				// 关闭
				session.stop();
			}
		});
	}

	@Rpc.RpcFunc
	@Override
	public void closeConnectByPlayerId(long playerId) {
		// 查找玩家连接, 没有则为断开连接了.
		final ClientSession session = ClientMgr.getInstance().getSession(playerId);
		if (session == null) {
			return;
		}

		// 提交关闭任务
		session.enqueue(new Runnable() {
			@Override
			public void run() {
				// 关闭
				session.stop();
			}
		});
	}

	@Rpc.RpcFunc
	@Override
	public int testCall(ProxyChannel channel, int a, RpcCallback callback) {
		callback.callBack(1, a);
		return a;
	}

	/** 发送任务 **/
	private class SendMessageAction extends Action {
		private ClientSession session;
		private Message packet;
		private int action;

		public SendMessageAction(ClientSession session, Message packet, int action) {
			this.session = session;
			this.action = action;
			this.packet = packet;
		}

		@Override
		public void execute() throws Exception {
			// 检测是否还在连接中
			if (!session.isConnect()) {
				return;
			}
			// 检测是否绑定
			if (action == IGameClient.CLIENTACTION_BIND) {
				// Log.debug("bindPlayerId By Msg: " + packet.toSimpleString());
				long playerId = packet.getPlayerId();
				ClientMgr.getInstance().bindPlayerId(session, playerId);
			}

			try {
				// 发送消息
				session.write(packet);
			} catch (Exception e) {
				Log.error("发送消息错误!" + packet.toSimpleString(), e);
			}

			// 检测是否关闭
			if (action == IGameClient.CLIENTACTION_CLOSE) {
				Log.info("发送消息并申请断开!" + session);
				session.stop();
			}
		}

	}

//	@Rpc.RpcFunc
//	@Override
//	public boolean sendClientActionMessageToClient(ProxyChannel channel,
//			Message packet, int action) {
//		long playerId = packet.getPlayerId();
//		// 查找玩家连接, 没有则为断开连接了.
//		final ClientSession session = ClientMgr.getInstance().getSession(playerId);
//		if (session == null) {
//			Log.debug("找不到玩家连接! playerId=" + playerId + " channel=" + channel + " packet=" + packet.toSimpleString());
//			return false;
//		}
//		// 塞入队列执行(保证相关逻辑处理顺序)
//		session.enqueue(new SendMessageAction(session, packet, action));
//		return true;
//	}
}

