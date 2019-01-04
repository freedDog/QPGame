package com.game.base.service.player;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.connector.Connector;
import com.game.base.service.connector.IConnector;
import com.game.base.service.constant.OnlineState;
import com.game.base.service.constant.PlayerType;
import com.game.base.service.gamezone.GameZone;
import com.game.base.service.gamezone.GameZoneMgr;
import com.game.base.service.inventory.GameEntity;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.mgr.RouteMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.utils.DataUtils;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.utils.collections.MapUtils;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;

/**
 * 游戏玩家对象
 * Player.java
 * @author JiangBangMing
 * 2019年1月4日下午5:02:38
 */
public abstract class Player extends GameEntity<PlayerInventory<? extends Player>> implements IConnector {
	private volatile short onlineState; // 登陆状态,用于过滤消息发送 (volatile是必要).
	protected Connector connector; // 连接器, 用于发送消息.
	protected ActionQueue queue; // 玩家任务队列
	public final MailBox mailBox; // mailbox对象
	private final RouteMgr.IRouter router; // 消息转发器

	protected Player() {
		queue = new ActionQueue(ServiceMgr.getExecutor());

		// 创建mailbox
		this.mailBox = new MailBox() {
			@Override
			public int getType() {
				return MailBox.TYPE_PLAYER;
			}

			@Override
			public long getId() {
				return Player.this.getPlayerId();
			}
		};

		// 创建转发器
		router = new RouteMgr.IRouter() {
			@Override
			public ServerConfig getByGate(long playerId) {
				if (playerId != Player.this.mailBox.getId()) {
					return RouteMgr.mailboxRouter.getByGate(playerId);
				}
				return Player.this.mailBox.getByGate();
			}
		};

		// 创建连接器
		connector = new Connector() {
			@Override
			public long getPlayerId() {
				return Player.this.getPlayerId();
			}

			@Override
			public void sendPacket(short code, RpMessage rpMsg) {
				Player.this.sendPacket(code, rpMsg);
			}
		};

	}

	public Connector getConnector() {
		return this.connector;
	}

	/** 获取账号ID **/
	public abstract long getUserId();

	/** 获取玩家ID **/
	public abstract long getPlayerId();

	/** 获取角色的名字 **/
	public abstract String getName();
	
	/** 是否是超级账号 **/
	public abstract boolean isSuperAccount();

	/** 获取角色的等级 **/
	public abstract int getLevel();
	public abstract long getRankScore();
	/** 获取玩家类型 **/
	public int getType() {
		return PlayerType.NORMAL;
	}

	/** 是否是机器人 **/
	public boolean isRobet() {
		return getType() <= 0;
	}

	/** 获取区服Id **/
	public int getGameZoneId() {
		return getGameZoneId(getPlayerId());
	}

	/** 获取区服 **/
	public GameZone getGameZone(ModuleName moduleName) {
		return GameZoneMgr.getGameZone(moduleName, getGameZoneId());
	}

	/** 登陆操作 **/
	protected void login() {
		// 遍历触发PlayerInventory
		super.forech(new MapUtils.Foreach<Class<?>, PlayerInventory<?>>() {
			@Override
			public void action(Class<?> clazz, PlayerInventory<?> inventory) {
				try {
					inventory.onLogin();
				} catch (Exception e) {
					Log.error("登陆处理失败!? " + inventory, e);
				}
			}
		});
	}

	/** 登出操作 **/
	protected void logout() {
		// 遍历触发PlayerInventory
		super.forech(new MapUtils.Foreach<Class<?>, PlayerInventory<?>>() {
			@Override
			public void action(Class<?> clazz, PlayerInventory<?> inventory) {
				try {
					inventory.onLogout();
				} catch (Exception e) {
					Log.error("登出处理失败!? " + inventory, e);
				}
			}
		});
	}

	/** 获取游戏服模块连接 **/
	public ProxyChannel getGameChannel(ModuleName moduleName) {
		return getGameChannel(getPlayerId(), moduleName);
	}

	/** 获取网关连接 **/
	public ProxyChannel getGateChannel() {
		return getGateChannel(getPlayerId());
	}

	/** 发送消息给玩家客户端 */
	public void sendPacketByOnline(short code, RpMessage rpMsg) {
		sendPacket(code, rpMsg, true);
	}

	/** 发送消息给玩家客户端 */
	@Override
	public void sendPacket(short code, RpMessage rpMsg) {
		sendPacket(code, rpMsg, true);
	}

	protected void sendPacket(short code, RpMessage rpMsg, boolean online) {
		// 过滤机器人等消息发送
		if (isRobet()) {
			return; // 机器人不发送, 类型小于0为机器人不能登录.
		}

		// 过滤在线情况
		if (online && !isOnline()) {
			return;
		}

		// debug测试消息下发数量
		if (ConfigMgr.isDebug()) {
			// String str = "player[" + this.getPlayerId() + "]send: " + protocolCode;
			// if (rpMsg != null)
			// {
			// str += " " + rpMsg.getClass() + " " + rpMsg;
			// }
			// str = str.replaceAll("\n", " ");
			// Log.debug(str);
		}

		// 发送消息
		// Connector.sendPacket(getPlayerId(), code, rpMsg);
		RouteMgr.getInstance().sendPacket(router, Message.buildMessage(getPlayerId(), code, rpMsg));
	}

	@Override
	public void sendLanguageText(Object key, Object... params) {
		this.connector.sendLanguageText(key, params);
	}

	@Override
	public void sendLanguageText(short type, Object key, Object... params) {
		this.connector.sendLanguageText(type, key, params);
	}

	@Override
	public void sendText(short type, String text) {
		this.connector.sendText(type, text);
	}

	@Override
	public void sendText(String text) {
		connector.sendText(text);
	}

	@Override
	public void sendLanguageError(short code, Object key, Object... params) {
		connector.sendLanguageError(code, key, params);
	}

	@Override
	public void sendError(short code, String msg) {
		connector.sendError(code, msg);
	}

	/** 提交任务 **/
	public void enqueue(Runnable runnable) {
		queue.enqueue(runnable);
	}

	/** 设置在线状态 **/
	protected boolean setOnlineState(short onlineState) {
		if (this.onlineState == onlineState) {
			return false;
		}
		this.onlineState = onlineState;
		return true;
	}

	/** 是否在线 **/
	public boolean isOnline() {
		return this.onlineState == OnlineState.ONLINE;
	}

	/******************** 静态方法 ********************/

	/** 发送多语言文本消息(加个T避免歧义调用) **/
	public static void sendLanguageTextT(long playerId, Object key, Object... params) {
		Connector.create(playerId).sendLanguageText(key, params);
	}

	/** 发送文本消息 **/
	public static void sendText(long playerId, String text) {
		Connector.create(playerId).sendText(text);
	}

	/** 发送文本消息 **/
	public static void sendText(long playerId, short type, String text) {
		Connector.create(playerId).sendText(type, text);
	}

	/** 发送错误消息 **/
	public static void sendError(long playerId, short code, String msg) {
		Connector.create(playerId).sendError(code, msg);
	}

	/** 发送消息给玩家 **/
	public static void sendPacket(long playerId, short code, RpMessage rpMsg) {
		Connector.sendPacket(playerId, code, rpMsg);
	}

	/** 发送消息给所有玩家 **/
	public static void sendPacketToAll(short code, RpMessage rpMsg) {
		Connector.sendPacketToAll(code, rpMsg);
	}

	/** 根据Id获得对应的区服Id **/
	public static int getGameZoneId(long id) {
		return DataUtils.getGameZoneId(id);
	}

	/** 获取游戏服模块连接 **/
	public static ProxyChannel getGameChannel(long playerId, ModuleName moduleName) {
		ServerConfig config = MailBox.get(playerId, moduleName.name());
		if (config == null) {
			return null;
		}
		return GameChannelMgr.getChannel(ServerConfig.TYPE_GAME, config);
	}

	/** 获取网关连接 **/
	public static ProxyChannel getGateChannel(long playerId) {
		ServerConfig config = MailBox.getByGate(playerId);
		if (config == null) {
			return null;
		}
		return GameChannelMgr.getChannel(ServerConfig.TYPE_GATE, config);
	}

	/** 判断玩家是否登录了(检测是否有对应网关) **/
	public static boolean isOnline(long playerId) {
		// 判断玩家Id
		if (playerId <= 0) {
			return false;
		}
		// 判断是否有网关
		ServerConfig config = MailBox.getByGate(playerId);
		return config != null;
	}

	@Override
	public String toString() {
		return "Player [" + this.getPlayerId() + " [" + this.getType() + "]" + this.getName() + "]";
	}
}