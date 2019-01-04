package com.game.base.service.mgr;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.gamezone.GameZone;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.IGameClient;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;

/**
 * 转发管理器<br>
 * 用于转发消息:game->gate<br>
 * RouteMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午4:57:48
 */
public abstract class RouteMgr {
	protected static RouteMgr instance;

	@SuppressWarnings("unchecked")
	public static <T extends RouteMgr> T getInstance() {
		return (T) instance;
	}

	/** 消息二进制化 **/
	public abstract byte[] serialize(RpMessage rpMsg);

	/** 群发给所有玩家 **/
	public boolean sendPacketToAll(Message msg) {
		return sendPacketToAll((int[]) null, msg);
	}

	/** 群发给某个区服的玩家. **/
	public boolean sendPacketToAll(GameZone gameZone, Message msg) {
		return sendPacketToAll(gameZone.getIds(), msg);
	}

	/** 群发给某个区服的玩家, gameZoneId为0代表全部玩家. **/
	public abstract boolean sendPacketToAll(int[] gameZoneIds, Message msg);

	/** 发送消息 **/
	public abstract boolean sendPacket(IRouter router, Message msg);

	/** 通过连接Id发送 **/
	public static void sendPacketByConnectId(ProxyChannel channel, long connectId, Message msg) {
		// 检测消息
		if (!checkMessage(msg)) {
			return;
		}
		// 绑定连接Id
		msg.setConnectId(connectId);
		// 发送消息回去
		IGameClient client = channel.createImpl(IGameClient.class);
		client.sendMessageToClientByConnectId(channel, msg, 0);
	}

	/** 根据连接Id发送 **/
	public static void sendPacketByConnectId(ProxyChannel channel, long connectId, short code, Object data) {
		Message msg = Message.buildMessage(code, data);
		sendPacketByConnectId(channel, connectId, msg);
	}

	/** 检测消息 **/
	protected static boolean checkMessage(Message msg) {
		short code = msg.getCode();
		if (!ModuleName.CLIENT.checkCode(code)) {
			Log.error("不能发送非客户端消息!" + msg.toSimpleString(), true);
			return false;
		}
		return true;
	}

	/** 默认网关转发接口 **/
	public final static RouteMgr.IRouter mailboxRouter = new RouteMgr.IRouter() {
		@Override
		public ServerConfig getByGate(long playerId) {
			return MailBox.getByGate(playerId);
		}
	};

	/** 转发器接口 **/
	public interface IRouter {
		/** 获取转发网关 **/
		ServerConfig getByGate(long playerId);
	}
}

