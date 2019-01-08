package com.game.core;

import java.lang.reflect.Method;

import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.base.service.rpc.handler.IGameClient;
import com.game.base.service.tempmgr.GameConfigMgr;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.core.login.LoginInventory;
import com.game.core.player.CorePlayer;
import com.game.core.player.CorePlayerMgr;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.xml.XmlNode;
import com.game.proto.msg.Message;
import com.game.proto.protocol.Protocol;

/**
 * core模块
 * CoreModule.java
 * @author JiangBangMing
 * 2019年1月8日上午11:37:49
 */
public class CoreModule extends Module {
	private static ActionExecutor executor; // 线程池

	protected CoreModule(XmlNode moduleNode) {
		super(moduleNode);

		// 初始化线程池
		int thread = Runtime.getRuntime().availableProcessors();
		executor = new ActionExecutor("ActionExecutor-Core", thread, thread * 2);
	}

	@Override
	public boolean init() {
		// 初始化通用功能
		if (!super.init()) {
			return false;
		}
		// 初始化组件
		if (!initStatic(GameConfigMgr.class)) {
			return false;
		}
		if (!initStatic(ItemTempMgr.class)) {
			return false;
		}
//		if (!initStatic(FashionTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(MailMgr.class)) {
//			return false;
//		}
//		if (!initStatic(SignTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(LobbyTempMgr.class)) {
//			return false;
//		}
//		// if (!initStatic(BullGoldFightTempMgr.class)) {
//		// return false;
//		// }
//		if (!initStatic(TaskTempMgr.class)) {
//			return false;
//		}
//
//		if (!initStatic(ShopTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(RankTempMgr.class)) {
//			return false;
//		}
//		if (!init(LoginMgr.class, LoginMgr.getInstance())) {
//			return false;
//		}
//		if (!init(PayMgr.class, PayMgr.getInstance())) {
//			return false;
//		}
//		if (!init(AgencyMgr.class, AgencyMgr.getInstance())) {
//			return false;
//		}
//		if (!initStatic(TextTempMgr.class)) {
//			return false;
//		}
////		if (!initStatic(VipTempMgr.class)) {
////			return false;
////		}
//		if (!initStatic(DirtyTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(IPDataTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(DropTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(GameLobbyOffMgr.class)) {
//			return false;
//		}
		// 注册定时器
//		TimeMgr.register(new SaveDataTimer());
//		TimeMgr.register(new DayResetTimer());
//		TimeMgr.register(new HourUpdateTimer());
//		TimeMgr.register(new SaveLogTimer());

		Log.info("定时器创建完毕！");
		return true;
	}

	@Override
	public void destroy() {
		// 保存数据
		CorePlayerMgr playerMgr = CorePlayerMgr.getInstance();
		if (playerMgr != null) {
			Log.info("移除保存玩家数据...");
			// playerMgr.removeAll();
			playerMgr.removeAllByThread();
			Log.info("移除保存玩家数据完成");
		}

		// 关闭线程池
		if (executor != null) {
			executor.stop();
			executor = null;
			Log.debug("关闭core线程池...");
		}
	}

	@Override
	public ModuleName getModuleName() {
		return ModuleName.CORE;
	}

	@Override
	public void onPlayerLost(ProxyChannel channel, long connectId, long playerId) {
		// 玩家掉线处理
		final CorePlayer player = CorePlayerMgr.getInstance().getFromCache(playerId);
		if (player == null) {
			return; // 玩家不在
		}

		// 检测是否绑定这个连接(避免切换网络, 之前的连接尚未断开就开始新连接)
		LoginInventory loginInventory = player.getInventory(LoginInventory.class);
		if (!loginInventory.checkConnect(channel, connectId)) {
			return; // 连接过时
		}

		// 提交离线事件
		player.enqueue(new Runnable() {
			@Override
			public void run() {
				player.logout();
			}
		});
	}

	@Override
	protected Object getMessageParam(Method method, int index, Class<?> paramType, Message packet, ProxyChannel channel, RpcCallback callback) throws Exception {
		// 根据类型补充数据
		if (Player.class.isAssignableFrom(paramType)) {
			// 获取玩家
			long playerId = packet.getPlayerId();
			CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
			if (player == null) {
				throw new Exception("找不到玩家数据! playerId=" + playerId);
			}
			// 除了登陆消息外, 其他消息必须验证在线情况
			if (packet.getCode() != Protocol.S_LOGIN_RESP) {
				// 如果不在线, 说明逻辑服可能重启过了.
				if (!player.isOnline()) {
					// Log.warn("玩家连接状态已经断开, 不能接受消息." + packet);
					// 创建错误消息
					// ErrorMsg errMsg = new ErrorMsg();
					// errMsg.setCode(ErrorCode.NOLOGIN);
					// errMsg.setMsg(LanguageSet.get(TextTempId.ID_1007));
					// 发送重新登录协议
					// RouteMgr.sendPacketByConnectId(channel, packet.getConnectId(), Protocol.C_ERROR, errMsg);
					
					// 断开连接(这里就直接迫使客户端重连咯)
					IGameClient client = channel.createImpl(IGameClient.class);
					client.closeConnectByConnectId(packet.getConnectId());
					return PARAM_INTERRUPT;
				}
			}
			return player;
		}
		return null;
	}

	@Override
	protected void enqueue(long id, Runnable r) {
		if (id > 0) {
			CorePlayerMgr.enqueue(id, r);
			return;
		}
		// 直接放入线程池
		executor.execute(r);
	}

	public static ActionExecutor getExecutor() {
		return executor;
	}
}
