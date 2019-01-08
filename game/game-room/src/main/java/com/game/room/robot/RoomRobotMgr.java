package com.game.room.robot;

import java.util.ArrayList;
import java.util.List;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.CurrencyId;
import com.game.base.service.constant.ProductSourceType;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.mgr.RobotOffMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.base.utils.RandomUtils;
import com.game.entity.bean.ProductResult;
import com.game.entity.shared.RoomPlayerInfo;
import com.game.framework.component.action.Action;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.action.LoopAction;
import com.game.framework.component.collections.map.ExpireMap;
import com.game.framework.component.log.Log;
import com.game.framework.utils.TimeUtils;
import com.game.room.RoomModule;
import com.game.room.base.GameRoom;
import com.game.room.base.GameRoomHelp;
import com.game.room.player.GamePlayer;
import com.game.room.player.GamePlayerMgr;
import com.game.room.base.GameRoomMgr.RobetRoomFiltr;

/**
 * 机器人玩家管理器<br>
 * RoomRobotMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午6:03:51
 */
public class RoomRobotMgr {
	private static ActionExecutor executor; // 线程池
	private static ActionQueue queue;

	protected static ExpireMap<Long, GamePlayer> cache; // 缓存数据
	
	protected static boolean init() {
		cache = new ExpireMap<>(5 * 1000);
		// 创建队列
		executor = new ActionExecutor("ActionExecutor-Room", 1, 1);
		queue = new ActionQueue(executor);
		// 添加定时器
		queue.enqueue(new UpdateAction(3 * 1000));
		return true;
	}

	protected static void destroy() {
		// 关闭线程池
		if (executor != null) {
			executor.stop();
			executor = null;
		}
	}

	/** 提交任务 **/
	public static void enqueue(final Action action) {
		queue.enqueue(action);
	}

	/** 更新机器人 **/
	public static void updateByPlayerId(long playerId) {
		if (!AIConfig.config.isEnable()) {
			return; // 关闭机器人了, 不管了
		}

		// 判断玩家是否存在
		GamePlayer player = GamePlayerMgr.getInstance().get(playerId);
		if (player != null) {
			return; // 已经存在玩家了
		}

		// 获取玩家数据
		player = getPlayerByCache(playerId);
		if (player == null) {
			return; // 读取玩家数据失败
		}

		// 执行更新
		updateByPlayerId(playerId, player);
	}

	/** 从缓存中获取玩家数据 **/
	private static GamePlayer getPlayerByCache(long playerId) {
		// 获取玩家
		GamePlayer player = cache.get(playerId);
		if (player != null) {
			return player;
		}
		// 读取玩家信息
		RoomPlayerInfo playerInfo = GamePlayerMgr.getRoomPlayerInfo(playerId);
		if (playerInfo == null) {
			return null; // 读取数据错误
		}
		// 创建玩家
		player = new GamePlayer(playerInfo);
		cache.put(playerId, player);

		// 标记为登陆
		player.setOnline(true);
		return player;
	}

	/** 更新机器人 **/
	private static void updateByPlayerId(long playerId, final GamePlayer player) {

		// 判断机器人冷却时间
		if (TimeUtils.getCurrentTime() < player.getBanTime()) {
			return;
		}

		// 判断类型
		final List<Class<?>> roomClasses = new ArrayList<>();
		if (GameRoom.isRoomTest()) {
			// 测试模式
			// roomClasses = Arrays.asList(new Class<?>[] {
			// LandlordLobbyRoom.class, MahjongRoom.class });
			if (RoomModule.getBullGoldFight() != null) {
				roomClasses.add(RoomModule.getBullGoldFight().getGameClass());
			}
			if (RoomModule.getLandlord() != null) {
				roomClasses.add(RoomModule.getLandlord().getGameClass());
			}
			if (RoomModule.getMahjong() != null) {
				roomClasses.add(RoomModule.getMahjong().getGameClass());
			}
			if (RoomModule.getZhaJinHua() != null) {
				roomClasses.add(RoomModule.getZhaJinHua().getGameClass());
			}
//			if (RoomModule.getTexasHoldem() != null) {
//				roomClasses.add(RoomModule.getTexasHoldem().getGameClass());
//			}
//			if (RoomModule.getSlots() != null) {
//				roomClasses.add(RoomModule.getSlots().getGameClass());
//			}
			if (RoomModule.getDaXuan() != null) {
				roomClasses.add(RoomModule.getDaXuan().getGameClass());
			}
			// Class<?> bgf = RoomModule.getBullGoldFight() != null ?
			// RoomModule.getBullGoldFight().getGameClass() : null;
			// Class<?> landlord = RoomModule.getLandlord() != null ?
			// RoomModule.getLandlord().getGameClass() : null;
			// Class<?> mahjong = RoomModule.getMahjong() != null ?
			// RoomModule.getMahjong().getGameClass() : null;
			// roomClasses = Arrays.asList(new Class<?>[] { landlord, bgf,
			// mahjong });
		} else {
			// 正常模式
			Class<?> landlord = RoomModule.getLandlord() != null ? RoomModule
					.getLandlord().getGameClass() : null;
			// roomClasses = Arrays.asList(new Class<?>[] { landlord });
			if (landlord != null) {
				roomClasses.add(landlord);
			}
			Class<?> bullgoldfight = RoomModule.getBullGoldFight() != null ? RoomModule
					.getBullGoldFight().getGameClass() : null;
			if (bullgoldfight != null) {
				roomClasses.add(bullgoldfight);
			}
			Class<?> mahjong = RoomModule.getMahjong() != null ? RoomModule
					.getMahjong().getGameClass() : null;
			if (mahjong != null) {
				roomClasses.add(mahjong);
			}

			Class<?> zhajinhua = RoomModule.getZhaJinHua() != null ? RoomModule
					.getZhaJinHua().getGameClass() : null;
			if (zhajinhua != null) {
				roomClasses.add(zhajinhua);
			}

//			Class<?> texasholdem = RoomModule.getTexasHoldem() != null ? RoomModule
//					.getTexasHoldem().getGameClass() : null;
//			if (texasholdem != null) {
//				roomClasses.add(texasholdem);
//			}
//
//			Class<?> slots = RoomModule.getSlots() != null ? RoomModule
//					.getSlots().getGameClass() : null;
//			if (slots != null) {
//				roomClasses.add(slots);
//			}

			Class<?> daxuan = RoomModule.getDaXuan() != null ? RoomModule
					.getDaXuan().getGameClass() : null;
			if (daxuan != null) {
				roomClasses.add(daxuan);
			}
		}

		// 先检测当前有能进入的房间否
		GameRoom<?> findRoom = null;
		findRoom = GameRoomHelp.getRoom(new RobetRoomFiltr<GameRoom<?>>(player,
				roomClasses));
 
		// 判断是否有符合的房间
		if (findRoom == null) {
			return; // 没有符合条件的房间
		}
		//RobetRoomFiltr 默认把房卡模式踢出了 所以这里只关注 钻石场逻辑
		if(isRoomRebootOff(findRoom)){
			return;
		}
		
		/*if(!findRoom.isRoomCard()){
			if(findRoom instanceof LandlordLobbyRoom){
				LandlordLobbyRoom landlordRoom = (LandlordLobbyRoom)findRoom;
				LobbyTempInfo tempInfo = landlordRoom.getTempInfo();
				if(tempInfo != null && (tempInfo.getTemplateId() == 2 || tempInfo.getTemplateId() == 3 || tempInfo.getTemplateId() == 1)){
					return ;
				}
			}else if(findRoom instanceof MahjongRoom){
				MahjongRoom mahjongRoom = (MahjongRoom)findRoom;
				LobbyTempInfo tempInfo = mahjongRoom.getTempInfo();
				if(tempInfo != null && (tempInfo.getTemplateId() == 52 || tempInfo.getTemplateId() == 53 || tempInfo.getTemplateId() == 51)){
					return ;
				}
			}
		}*/
		// 判断是否应该能冷却
		int playerCount = player.getPlayerCount();
		if (playerCount == RobotConst.ROBET_ROUND_COUNT) {
			ICoreService coreService = GameChannelMgr
					.getChannelServiceByPlayerId(playerId, ModuleName.CORE,
							ICoreService.class);
			coreService.onRobetJoinRoom(player.getPlayerId(), 0,
					TimeUtils.getCurrentTime() + RobotConst.ROBET_BAN_TIME);
			if (ConfigMgr.isDebug()) {
				Log.info(player.getName() + " ：连续玩了"
						+ RobotConst.ROBET_ROUND_COUNT + "盘 冷却"
						+ RobotConst.ROBET_BAN_TIME + "秒");
			}
			return;
		}

		// 处理进入
		final GameRoom<?> joinRoom = findRoom;
		joinRoom.joinCounter.incrementAndGet(); // 加入标记
		ICoreService coreService = GameChannelMgr.getChannelServiceByPlayerId(
				playerId, ModuleName.CORE, ICoreService.class);
		coreService.onRobetJoinRoom(player.getPlayerId(), ++playerCount, 0);
		updateRobotPoint(player);
		joinRoom.enqueue(new Runnable() {
			@Override
			public void run() {
				try {
					joinRoom.doPlayerEnter(player);
				} finally {
					joinRoom.joinCounter.decrementAndGet(); // 加入标记清除
				}
			}
		});
	}
	
	/**
	 * 跟新机器人 货币
	 * 2份 另外一份在 LHD 机器管理里面
	 * @param player
	 */
	private static void updateRobotPoint(final GamePlayer player){
		//强行补贴
		long cur = player.getCurrency(CurrencyId.POINT);
		ProductResult  result = null;
		if(ConfigMgr.isDebug()){
			if(cur <= 0){
				Log.warn("机器人强行补贴");
				player.changeCurrency(CurrencyId.POINT, RandomUtils.randomInt(1,800) * 100, ProductSourceType.AUTOROBOTCURRENCY);
			}
		}else{
			if(cur <= 0){
				int newCur = 1000;
				Log.warn("自动调整机器人金额==>pid:"+player.getPlayerId()+"old:"+cur+" new:"+newCur);
				result = player.changeCurrency(CurrencyId.POINT, newCur, ProductSourceType.AUTOROBOTCURRENCY);
			}else if(cur > 60000){
				int newCur = RandomUtils.randomInt(5,600) * 100;
				Log.warn("自动调整机器人金额==>pid:"+player.getPlayerId()+"old:"+cur+" new:"+newCur);
				result = player.changeCurrency(CurrencyId.POINT, -(cur - newCur), ProductSourceType.AUTOROBOTCURRENCY);
			}
		}
		if(result != null && !result.isSucceed()){
			Log.error("机器人自动修改货币失败");
		}
	}
	
	/** 更新 **/
	protected static void onUpdate(long dt) {
		cache.expire(); // 数据超时清除
	}
	
	/**
	 * 场次机器人是否关闭
	 * 如果模板没有 就不拦截 机器人
	 * @return true off false on
	 */
	protected static boolean isRoomRebootOff(GameRoom<?> findRoom){
		//如果没有模板 就不拦截
		if(findRoom.getTempInfo() == null){
			return false;
		}
		return RobotOffMgr.isRoomRobootOff(findRoom.getRoomLocationType(),findRoom.getTempInfo().getTemplateId());
	}
	
	/** 定时器 **/
	protected static class UpdateAction extends LoopAction {
		public UpdateAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(long now, long prev, long dt, int index) {
			onUpdate(dt);
		}
	}

}

