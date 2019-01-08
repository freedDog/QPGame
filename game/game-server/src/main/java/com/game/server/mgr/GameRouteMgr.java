package com.game.server.mgr;

import java.util.List;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mgr.RouteMgr;
import com.game.base.service.rpc.handler.IGameClient;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;

/**
 * 游戏服消息转发
 * GameRouteMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:25:46
 */
public class GameRouteMgr extends RouteMgr {
	public GameRouteMgr() {
		instance = this;
	}

	/**
	 * 格式化消息, 把message中的消息对象转成二进制流<br>
	 * 这样的方式会增加Game的负荷, 但是更新协议可以不用更新网关服
	 **/
	protected static boolean formatMessage(Message msg) {
		Object msgObj = msg.getObject();
		if (msgObj instanceof RpMessage) {
			// 转化并设置二进制流
			byte[] byteBuf = RpMessage.serialize((RpMessage) msgObj);
			msg.setObject(byteBuf);
			return true;
		} else if (msgObj instanceof byte[]) {
			// 已经是二进制数据流了.
			return true;
		} else if (msgObj == null) {
			return true;
		}
		Log.error("未知消息内容: " + msg, true);
		return false;
	}

	@Override
	public boolean sendPacket(IRouter router, Message msg) {
		// 检测消息
		if (!checkMessage(msg)) {
			return false;
		}

		// 发送前格式化消息先
		if (!formatMessage(msg)) {
			Log.error("格式化消息失败! msg=" + msg, true);
			return false;
		}

		// 获取玩家Id
		long playerId = msg.getPlayerId();
		if (playerId <= 0) {
			Log.error("没有玩家信息! msg=" + msg.toSimpleString(), true);
			return false;
		}

		// 获取绑定信息
		ServerConfig config = router.getByGate(playerId);
		if (config == null) {
			Log.debug("找不到玩家绑定的网关! playerId=" + playerId + " msg=" + msg.toSimpleString());
			return false;
		}

		// 获取连接
		ProxyChannel channel = GameChannelMgrX.getGateClientMgr().getChannel(config);
		if (channel == null) {
			Log.warn("没有连接网关! playerId=" + playerId + " msg=" + msg.toSimpleString() + " config=" + config);
			return false;
		}
		// Log.debug("send:Msg:" + msg.toString());

		// 发送消息
		IGameClient gameClient = channel.createImpl(IGameClient.class);
		gameClient.sendMessageToClient(channel, msg, IGameClient.CLIENTACTION_NULL);
		return true;
	}

	@Override
	public boolean sendPacketToAll(int[] gameZoneIds, Message msg) {
		// 检测消息
		if (!checkMessage(msg)) {
			return false;
		}
		// 发送前格式化消息先
		if (!formatMessage(msg)) {
			Log.error("格式化消息失败! msg=" + msg);
			return false;
		}

		// 群发给所有服务器
		List<ProxyChannel> channels = GameChannelMgrX.getGateClientMgr().getChannels();
		for (ProxyChannel channel : channels) {
			IGameClient gameClient = channel.createImpl(IGameClient.class);
			gameClient.sendMessageToAll(channel, gameZoneIds, msg);
		}
		return true;
	}

	@Override
	public byte[] serialize(RpMessage rpMsg) {
		return RpMessage.serialize(rpMsg);
	}

}

