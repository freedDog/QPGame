package com.game.room;

import java.lang.reflect.Method;
import java.util.Map;

import com.game.base.service.constant.TextTempId;
import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.base.service.tempmgr.GameConfigMgr;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.ReflectUtils;
import com.game.proto.msg.Message;
import com.game.room.base.GameRoom;
import com.game.room.base.GameRoomMgr;
import com.game.room.base.IGameRoom;
import com.game.room.player.GamePlayer;
import com.game.room.player.GamePlayerMgr;

/**
 * 房间模块
 * 
 */
public class RoomModule extends Module {
	private static IGameRoom bullGoldFight;
	private static IGameRoom zhajinhua;
	private static IGameRoom texasholdem;
	private static IGameRoom landlord;
	private static IGameRoom mahjong;
	private static IGameRoom slots;
	private static IGameRoom bjl;
	private static IGameRoom daxuan;

	public static IGameRoom getBjl() {
		return bjl;
	}

	private static ActionExecutor executor; // 线程池

	protected RoomModule(XmlNode moduleNode) {
		super(moduleNode);

		// 初始化线程池
		int thread = Runtime.getRuntime().availableProcessors();
		executor = new ActionExecutor("ActionExecutor-Room", thread, thread * 2);
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
//		// 初始化配置
//		if (!initStatic(EffectTempMgr.class)) {
//			return false;
//		}
		if (!initStatic(ItemTempMgr.class)) {
			return false;
		}
//		if (!initStatic(LobbyTempMgr.class)) {
//			return false;
//		}
//
//		if (!initStatic(DirtyTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(RandomNameMgr.class)) {
//			return false;
//		}
//		
//		if (!initStatic(RebootRandomNameMgr.class)) {
//			return false;
//		}
//		
//		if (!initStatic(RoomRobotMgr.class)) {
//			return false;
//		}
//		
//		if (!initStatic(RobotWinRateTempMgr.class)) {
//			return false;
//		}
//		
//		if (!initStatic(GameLobbyOffMgr.class)) {
//			return false;
//		}
//		
//		if (!initStatic(RobotOffMgr.class)) {
//			return false;
//		}
		// if (!initStatic(LandlordCardTempMgr.class)) {
		// return false;
		// }
//		if (!initStatic(VipTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(ActivityTempMgr.class)) {
//			return false;
//		}

		// 尝试加载游戏
		bullGoldFight = getGame("com.tgt.uu.bullgoldfight.BullGoldFightGame");
		zhajinhua = getGame("com.tgt.uu.zhajinhua.ZhaJinHuaGame");
//		texasholdem = getGame("com.tgt.uu.texasholdem.TexasHoldemGame");
		landlord = getGame("com.tgt.uu.landlord.LandlordGame");
		mahjong = getGame("com.tgt.uu.mahjong.MahjongGame");
//		slots = getGame("com.tgt.uu.slots.SlotsGame");
//		bjl = getGame("com.tgt.uu.bjl.BjlGame");
		daxuan = getGame("com.tgt.uu.daxuan.DaXuanGame");
		// 初始化房间管理器
		if (!init(GameRoomMgr.class, GameRoomMgr.getInstance())) {
			return false;
		}
		GameRoomMgr.getInstance().initActionQueue(executor);

		// 房间测试代码
		GameRoom.setRoomTest(moduleNode.getAttr("roomTest", false));
		return true;
	}

	@Override
	public void destroy() {
		// 关闭所有房间
		GameRoomMgr.getInstance().removeAll();

		// 关闭线程池
		if (executor != null) {
			executor.stop();
			executor = null;
		}
	}

	@Override
	public ModuleName getModuleName() {
		return ModuleName.ROOM;
	}

	@Override
	protected Object getMessageParam(Method method, int index,
			Class<?> paramType, Message packet, ProxyChannel channel,
			RpcCallback callback) throws Exception {
		// 根据类型补充数据
		if (Player.class.isAssignableFrom(paramType)) {
			// 获取玩家
			long playerId = packet.getPlayerId();
			GamePlayer player = GamePlayerMgr.getInstance().getFromCache(
					playerId);
			if (player == null) {
				// throw new Exception("找不到玩家数据! packet=" + packet + " method="
				// + method);
				// 发送离开消息给自己
//				MJPlayerEventMsg pmsg = new MJPlayerEventMsg();
//				pmsg.setPlayerId(playerId);
//				pmsg.setType(MahjongPlayerEventType.LEAVE);
//				pmsg.setIntVal(RoomLeaveType.NORMAL);
//				Player.sendPacket(playerId, Protocol.C_MJ_ROOM_PLAYER_EVENT,
//						pmsg); // 发送给自己
//				Player.sendLanguageTextT(playerId, TextTempId.ID_1101);
				return PARAM_INTERRUPT;
			}
			return player;
		} else if (GameRoom.class.isAssignableFrom(paramType)) {
			// 获取玩家
			long playerId = packet.getPlayerId();
			GamePlayer player = GamePlayerMgr.getInstance().getFromCache(
					playerId);
			if (player == null) {
				// throw new Exception("找不到房间玩家数据! packet=" + packet);
				Player.sendLanguageTextT(playerId, TextTempId.ID_1101);
				return PARAM_INTERRUPT;
			}
			// 读取房间
			GameRoom<?> room = player.getRoom();
			if (room == null || !paramType.isInstance(room)) {
				// throw new Exception("玩家不在房间内! room=" + room);
				Player.sendLanguageTextT(playerId, TextTempId.ID_1101);
				return PARAM_INTERRUPT;
			}
			return room;
		}
		return null;
	}

	@Override
	public void onPlayerVerified(ProxyChannel channel, long connectId,
			long playerId) {
		// 玩家登陆, 验证玩家是否有还在参与的夺宝赛

	}

	@Override
	public void onPlayerLost(ProxyChannel channel, long connectId, long playerId) {
		// 判断是否本服玩家
		GamePlayer gamePlayer = GamePlayerMgr.getInstance().getFromCache(
				playerId);
		if (gamePlayer == null) {
			return;
		}
		Log.debug("RoomModule onPlayerLost playerId:" + playerId);
		gamePlayer.setOnline(false);
	}

	@Override
	public void onModuleCommon(String cmd, Map<String, String> params) {
//		if (cmd.equals("ai")) {
//			boolean enable = DataUtils.toBoolean(params.get("enable"));
//			AIConfig.config.setEnable(enable);
//		} else if (cmd.equals("aidz")) {
//			int mode = DataUtils.toInt(params.get("mode"));
//			AIConfig.config.setMode(AIConfig.MODE_CALLDZ, mode);
//		} else if (cmd.equals("aimp")) {
//			int mode = DataUtils.toInt(params.get("mode"));
//			AIConfig.config.setMode(AIConfig.MODE_SHOWCARD, mode);
//		} else if (cmd.equals("aicp")) {
//			int mode = DataUtils.toInt(params.get("mode"));
//			AIConfig.config.setMode(AIConfig.MODE_PLAYCARD, mode);
//		} else if (cmd.equals("aidr")) {
//			int mode = DataUtils.toInt(params.get("mode"));
//			AIConfig.config.setMode(AIConfig.MODE_DOUBLERATE, mode);
//		} else if (cmd.equals("aireset")) {
//			AIConfig.config.setMode(AIConfig.MODE_CALLDZ, 0);
//			AIConfig.config.setMode(AIConfig.MODE_SHOWCARD, 0);
//			AIConfig.config.setMode(AIConfig.MODE_DOUBLERATE, 0);
//			AIConfig.config.setMode(AIConfig.MODE_PLAYCARD, 0);
//		}

	}

	public static ActionExecutor getExecutor() {
		return executor;
	}

	@Override
	protected void enqueue(long id, Runnable r) {
		executor.execute(r);
	}

	/**
	 * @return bullGoldFight
	 */
	public static IGameRoom getBullGoldFight() {
		return bullGoldFight;
	}

	/**
	 * @return landlord
	 */
	public static IGameRoom getLandlord() {
		return landlord;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IGameRoom getGame(String path) {
		Class moduleClass;
		try {
			moduleClass = Class.forName(path);
			if (moduleClass != null) {
				IGameRoom game = (IGameRoom) ReflectUtils.createInstance(
						moduleClass, true);
				if (game == null) {
					return null;
				}
				game.init();
				return game;
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 成都麻将
	 * @return mahjong
	 */
	public static IGameRoom getMahjong() {
		return mahjong;
	}

	/**
	 * @return slots
	 */
	public static IGameRoom getSlots() {
		return slots;
	}

	/**
	 * 获取炸金花房间
	 * 
	 * @return zhajinhua
	 */
	public static IGameRoom getZhaJinHua() {
		return zhajinhua;
	}

	/**
	 * 获取德州扑克房间
	 * 
	 * @return texasholdem
	 */
	public static IGameRoom getTexasHoldem() {
		return texasholdem;
	}
	
	/**
	 * 获取打旋扑克房间
	 * 
	 * @return daxuan
	 */
	public static IGameRoom getDaXuan() {
		return daxuan;
	}

}

