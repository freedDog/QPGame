package com.game.gate.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.mgr.CountMgr;
import com.game.base.service.module.ModuleMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.base.service.rpc.handler.IGameService;
import com.game.base.service.server.App;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.session.Session;
import com.game.framework.framework.session.netty.NettySession;
import com.game.gate.mgr.GateChannelMgr;
import com.game.proto.msg.Message;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.base.LongMsg;
import com.game.proto.rp.login.LoginReqMsg;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 客户端管理器
 * 
 */
public class ClientMgr {
	private static ClientMgr instance = new ClientMgr();
	private static final AttributeKey<ClientSession> SESSION = AttributeKey.<ClientSession> valueOf("SESSION");

	private final ConcurrentMap<Long, ClientSession> connects = new ConcurrentHashMap<>(); // 连接列表
	private final ConcurrentMap<Long, ClientSession> players = new ConcurrentHashMap<>(); // 玩家Id列表

	/** 获取句柄 **/
	public static ClientMgr getInstance() {
		return instance;
	}

	/** 连接激活 **/
	protected Session channelActive(Channel channel) {
		// 尝试插入
		ClientSession session = new ClientSession(channel);
		ClientSession old = connects.putIfAbsent(session.getConnectId(), session);
		if (old != null) {
			channel.close(); // 不可能.
			return null;
		}
		// 绑定
		channel.attr(SESSION).set(session);

		// 数量统计
		CountMgr.changeCount(CountMgr.Key_ConnectClient, 1);

		return session;
	}

	/** 连接关闭 **/
	protected void channelInactive(Channel channel) {
		// 获取连接
		ClientSession session = channel.attr(SESSION).get();
		if (session == null) {
			return;
		}
		// 执行移除
		long connectId = session.getConnectId(); // 连接ID
		if (!connects.remove(connectId, session)) {
			return; // 其他线程处理了吧
		}
		Log.info("连接断开, 清除绑定, 通知掉线! " + session);

		// 数量统计
		CountMgr.changeCount(CountMgr.Key_ConnectClient, -1);

		// 玩家掉线处理
		long playerId = session.getPlayerId();
		if (playerId <= 0) {
			return;
		}
		// 移除player
		if (!players.remove(playerId, session)) {
			return; // 已经被移除了.
		}

		// 处理玩家掉线处理
		MailBox.removeByGate(playerId);

		// 遍历所有逻辑服, 通知业务关闭.
		List<ProxyChannel> channels = GateChannelMgr.getGameClientMgr().getChannels();
		for (ProxyChannel channel0 : channels) {
			IGameService gameService = channel0.createImpl(IGameService.class);
			gameService.playerLostConnection(channel0, connectId, playerId);
		}
	}

	/** 根据playerId获取session **/
	public ClientSession getSession(long playerId) {
		return players.get(playerId);
	}

	/** 根据连接Id获取session **/
	public ClientSession getSessionByConnectId(long id) {
		return connects.get(id);
	}

	/** 根据连接Id获取session **/
	public List<ClientSession> getAllSession() {
		return new ArrayList<>(connects.values());
	}

	/** 根据区服获取配置, 区服可以为0. **/
	public static ServerConfig getServerConfigByGameZoneId(ModuleName moduleName, int gameZoneId) {
		// 随机GameZone对应的获取一个连接
		ServerConfig config = ModuleMgr.getServerConfig(moduleName, gameZoneId);
		// 有些运行在gamezoneid=0的模块
		if (config == null) {
			config = ModuleMgr.getServerConfig(moduleName); // 不设置区服限制, 随机一个给他即可.
		}
		return config;
	}

	/** 消息读取 **/
	protected void channelRead(Channel channel, final Message packet) {
		// 查找session
		final ClientSession session = channel.attr(SESSION).get();
		if (session == null) {
			Log.error("没有找到绑定的session!");
			channel.close();
			return;
		}

		// 检测session
		if (!session.isAlive()) {
			Log.warn("session已经失效!" + session);
			channel.close();
			return;
		}

		// 按连接提交任务
		session.enqueue(new Runnable() {
			@Override
			public void run() {
				channelRead0(session, packet);
			}
		});
	}

	/** 处理接收消息 **/
	private void channelRead0(ClientSession session, final Message packet) {
		// 根据协议码找到对应模块
		final short code = packet.getCode();
		if(code == Protocol.S_TEST_TIME){
			LongMsg timemsg = new LongMsg();
			timemsg.setValue(System.currentTimeMillis());
			session.write(Message.buildMessage(Protocol.C_TEST_TIME, timemsg));
			/*ShortMsg shortV = ShortMsg.deserialize(packet.getByteArray());
			if(shortV.getValue() > 100){
				Log.debug("===>client ping:"+shortV.getValue()+"ms");
			}*/
			return;
		}
		ModuleName moduleName = ModuleName.getModuleByCode(code);
		if (moduleName == null) {
			Log.warn("消息未找到对应模块!" + packet.toSimpleString());
			return;
		}
		// 获取玩家Id
		ServerConfig config = null;
		long connectId = session.getConnectId();
		long playerId = 0L;
		if (packet.getCode() == Protocol.S_LOGIN_RESP) {
			// 判断是否验证过
			if (session.isVerify()) {
				Log.warn("该链接已经验证通过了." + session);
				return;
			}
			// 登陆接口验证playerId
			LoginReqMsg msg = LoginReqMsg.deserialize(packet.getByteArray());
			playerId = msg.getPlayerId();
			if (playerId <= 0) {
				Log.warn("验证函数玩家Id不能为0!" + msg.toString() + " " + session);
				return;
			}
			config = MailBox.get(playerId, moduleName);
			Log.info("连接申请验证! packet=" + packet.toSimpleString() + " " + session);
		} else if (session.getPlayerId() > 0L) {
			// 正常消息
			playerId = session.getPlayerId();
			config = session.mailBox.get(moduleName); // 通过session的mailbox获取
		} else {
			// session没有playerId, 并且也不是登陆消息
			Log.warn("没登陆验证前, 除了登陆初始化消息外不允许接受其他消息!: connectId=" + connectId + " packet=" + packet.toSimpleString());
			return;
		}

		// 如果没有config说明没有绑定, 通过区服绑定消息来源.
		if (config == null) {
			int gameZoneId = Player.getGameZoneId(playerId);
			gameZoneId = 0; // 随机区服
			config = getServerConfigByGameZoneId(moduleName, gameZoneId);
		}
		// 再次过滤
		if (config == null) {
			Log.debug("无法找到合适的配置! " + moduleName + " playerId=" + playerId + " packet=" + packet.toSimpleString());
			return;
		}

		// 获取模块对应连接
		ProxyChannel rpcChannel = GateChannelMgr.getGameClientMgr().getChannel(config);
		if (rpcChannel == null) {
			Log.debug("找不到对应的连接! " + moduleName + " config=" + config + " playerId=" + playerId + " packet=" + packet.toSimpleString());
			return;
		}

		// 客户端消息处理
		packet.setConnectId(connectId);
		packet.setPlayerId(playerId);

		// 执行调用
		IGameService gameService = rpcChannel.createImpl(IGameService.class);
		gameService.onModuleMessage(packet, rpcChannel);
	}

	/** 绑定玩家Id **/
	public void bindPlayerId(final ClientSession session, final long playerId) {
		if (playerId <= 0) {
			Log.error("绑定玩家连接错误! playerId错误!" + playerId, true);
			return;
		}

		// 按照连接提交队列(客户端发送消息上来倘若尚未绑定就悲剧了)
		long connectId = session.getConnectId();
		Log.debug("绑定玩家Id: connectId=" + connectId + " playerId=" + playerId);

		// 标记到玩家映射中, 冲突踢旧连接下线.(这一块也放入队列中)
		ClientSession prevSession = players.put(playerId, session);
		if (prevSession != null) {
			// 检测是否是同个连接
			if (prevSession.equals(session)) {
				return; // 相同连接, 不处理
			}
			// 断开关联
			prevSession.setPlayerId(0L); // 取消连接绑定
			players.remove(playerId, prevSession);
			prevSession.stop(); // 停止这个连接
		}

		// 绑定玩家Id
		session.setPlayerId(playerId);
		// 选择在gate上绑定playerId, mailBox也要绑定.
		MailBox.setByGate(playerId, App.getInstance().getConfig());

		// 遍历所有逻辑服, 玩家所对应的网关地址
		ServerConfig gate = App.getInstance().getConfig();
		List<ProxyChannel> channels = GateChannelMgr.getGameClientMgr().getChannels();
		for (ProxyChannel channel0 : channels) {
			IGameService gameService = channel0.createImpl(IGameService.class);
			gameService.playerConnectionVerified(channel0, connectId, playerId, gate);
		}
	}

	/** 客户端连接session **/
	public static class ClientSession extends NettySession {
		private static AtomicLong nextConnId = new AtomicLong(0);
		protected final long connectId; // 连接Id
		protected long playerId; // 绑定的playerId
		protected ActionQueue queue; // 队列

		protected long checkTime; // 检测时间
		protected long checkInterval;// 检测间隔时间
		public final MailBox mailBox; // mailbox

		public ClientSession(Channel channel) {
			super(channel);
			queue = new ActionQueue(ServiceMgr.getExecutor());
			connectId = nextConnId.incrementAndGet();
			checkTime = System.currentTimeMillis();
			checkInterval = (!ConfigMgr.isDebug()) ? 60 * 1000 : 10 * 1000; // 正常1分检测1次, 测试10s

			// mailbox
			mailBox = new MailBox() {
				@Override
				public int getType() {
					return MailBox.TYPE_PLAYER;
				}

				@Override
				public long getId() {
					return ClientSession.this.getPlayerId();
				}
			};
		}

		/** 是否验证过(绑定了playerId) **/
		public boolean isVerify() {
			return playerId > 0;
		}

		/** 判断是否需要重新检测 **/
		protected boolean needRecheck() {
			long nowTime = System.currentTimeMillis();
			long dt = nowTime - checkTime;
			if (dt < checkInterval) {
				return false; // 还没到间隔时间
			}
			checkTime = nowTime;
			return true;
		}

		/** 是否还激活 **/
		public boolean isAlive() {
			// 判断是否还在连接中
			if (!isConnect()) {
				return false;
			}

			// 检测玩家网关
			if (playerId > 0 && needRecheck()) {
				// 通过mailbox检测(maibox对象中有对数据进行缓存, 不必担心)
				ServerConfig serverConfig = mailBox.getByGate();
				if (!App.getInstance().getConfig().equals(serverConfig)) {
					Log.info("玩家网关检测断开: " + playerId);
					return false; // 网关不相同
				}
				// Log.debug("玩家网关检测成功: " + playerId);
			}
			return true;
		}

		public void enqueue(Runnable runnable) {
			queue.enqueue(runnable);
		}

		public long getConnectId() {
			return connectId;
		}

		public long getPlayerId() {
			return playerId;
		}

		protected void setPlayerId(long playerId) {
			this.playerId = playerId;
		}

		public int getGameZoneId() {
			return Player.getGameZoneId(playerId);
		}

		@Override
		public String toString() {
			return "ClientSession [connectId=" + connectId + ", playerId=" + playerId + "]";
		}

	}
}
