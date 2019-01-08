package com.game.room.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.base.service.connector.Connector;
import com.game.base.service.constant.GameConst;
import com.game.base.service.constant.GameType;
import com.game.base.service.constant.MsgType;
import com.game.base.service.constant.RoomState;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.mgr.RobotIncomeExpenseMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.IControlService;
import com.game.base.service.tempmgr.GlobalGameConfigMgr;
import com.game.base.service.tempmgr.RobotWinRateTempMgr;
import com.game.base.utils.RoomLogger;
import com.game.entity.configuration.LobbyTempInfo;
import com.game.entity.entity.ForcedissolveroomInfo;
import com.game.framework.component.ChangeCounter;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.utils.collection.entity.IEntity;
import com.game.framework.utils.struct.result.Result;
import com.game.proto.msg.RpMessage;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.room.RoomMsg;
import com.game.proto.rp.text.TextMsg;
import com.game.room.constant.RoomConst;
import com.game.room.constant.RoomLeaveType;
import com.game.room.constant.RoomResetCode;
import com.game.room.player.GamePlayer;
import com.game.room.player.GamePlayerMgr;

/**
 * 游戏房间<br>
 * 先作为斗地主房间
 */
public abstract class GameRoom<P extends GameRoomPlayer<?>> implements IEntity {

	private static boolean roomTest = true;
	private boolean aiEnable; // AI是否激活

	private final long id; // 房间Id
	private final int gameType; // 游戏类型
	protected ActionQueue queue; // 执行队列
	protected int roomType; // 房间类型 0 金币房 1房卡房
	protected final AtomicInteger updateCounter; // 更新计时器
	public final AtomicInteger joinCounter; // 申请加入计时器

	private long activeTime; // 这个实体数据上使用时间.
	private int state; // 房间状态.
	public int gameState; // 游戏状态
	private boolean stateLock; // 状态锁, 锁定了就过滤这个状态

	protected ConcurrentMap<Long, P> players; // 房间内玩家
	private long waitStartTime; // 等待开始时间(准备状态)
	protected long playerJoinTime; // 最后一次玩家进入时间

	/** 更新计数器 **/
	public final ChangeCounter changeCounter;
	
	protected RoomLogger logger;
	
	/** 房间创建时间 */
	protected long createTime;
	/** 是否强制关闭房间 */
	protected boolean forceToClose;
	
	public GameRoom(long id, int gameType, ActionExecutor executor) {
		this.id = id;
		this.gameType = gameType;
		queue = new ActionQueue(executor);
		players = new ConcurrentHashMap<>();

		// 计数器
		updateCounter = new AtomicInteger();
		joinCounter = new AtomicInteger();
		// 房间机器人是否激活
		aiEnable = true;

		// 更新计数器
		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				onChangeCounter();
			}
		};
		createTime = System.currentTimeMillis();
		logger = new RoomLogger(id, gameType);
	}
	
	public GameRoom(long id, int gameType, int roomType, ActionExecutor executor) {
		this.id = id;
		this.gameType = gameType;
		queue = new ActionQueue(executor);
		players = new ConcurrentHashMap<>();

		// 计数器
		updateCounter = new AtomicInteger();
		joinCounter = new AtomicInteger();
		// 房间机器人是否激活
		aiEnable = true;
		this.roomType = roomType;

		// 更新计数器
		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				onChangeCounter();
			}
		};
		createTime = System.currentTimeMillis();
		logger = new RoomLogger(id, gameType);
	}

	@Override
	public boolean load() {
		return true;
	}

	@Override
	public void unload() {
	}

	public abstract void sendRoomInitMsgToPlayer(GamePlayer player);

	/** 房间更新时 **/
	protected void onChangeCounter() {
	}

	/** 玩家信息更新 **/
	public void onPlayerUpdate(GamePlayer player) {
	};

	/** 状态修改 **/
	protected boolean setState(int state) {
		if (this.state == state) {
			return false; // 状态没变化
		}
		// logger.debug("房间状态切换: " + this + " " + this.state + " -> " + state);
		// 触发修改事件
		onStateDisenable(this.state);
		this.state = state;
		onStateEnable(this.state);
		return true;
	}
	
	/**
	 * 更新机器人收入和支出数量
	 * @param income 收入增量
	 * @param expense 支出增量
	 */
	protected void changeRobotIncomeAndExpense(boolean isRobot, long income, long expense) {
		if (!isRobot || isRoomCard()) // 非机器人/房卡场不计算
			return;
		logger.info("changeRobotIncomeAndExpense, income:" + income + ", expense:" + expense);
		RobotIncomeExpenseMgr.changeIncomeAndExpense(getGameType(), income, expense);
	}
	
	/**
	 * 更新机器人收入和支出数量
	 * @param income 收入增量
	 */
	protected void changeRobotIncomeAndExpense(boolean isRobot, long income) {
		if (!isRobot || isRoomCard()) // 非机器人/房卡场不计算
			return;
		
		if(income > 0){
			changeRobotIncomeAndExpense(isRobot, income, 0);
		}else if(income < 0){
			changeRobotIncomeAndExpense(isRobot, 0, income);
		}
	}
	
	/**
	 * 获取机器人的胜负类型
	 * @return -1 必输模式 0 随机模式 1 必赢模式
	 */
	protected int getRobotWinType() {
		return RobotWinRateTempMgr.getRobotWinType(getGameType());
	}

	/** 游戏状态修改, 自动清除状态锁 **/
	protected boolean setGameState(int gameState,boolean enable) {
		if (this.gameState == gameState) {
			return false; // 状态没变化
		}
		// logger.debug("游戏状态切换: " + this + " " + this.gameState + " -> " + gameState);
		// 触发修改事件
		onGameStateDisenable(this.gameState);
		this.gameState = gameState;
		if(enable)
			onGameStateEnable(this.gameState);

		// 清除锁
		resetStateLock();
		return true;
	}
	/** 游戏状态修改, 自动清除状态锁 **/
	protected boolean setGameState(int gameState) {
		return setGameState(gameState, true);
	}

	/** 状态开始 **/
	protected void onStateEnable(int state) {
		// 每次切换状态, 重新计算等待时间.
		updateWaitTime();

		// switch (state) {
		// case RoomState.PREPARE:
		// this.waitStartTime = System.currentTimeMillis(); // 准备状态, 标记开始等待时间
		// break;
		// default:
		// waitStartTime = 0; // 清除时间
		// break;
		// }
	}
	/** 强制解散房间*/
	public abstract ForcedissolveroomInfo forceDissolveRoom();

	/** 更新等待时间 **/
	protected void updateWaitTime() {
		this.waitStartTime = System.currentTimeMillis();
	}

	/** 获取房间等待时间(等了多久) **/
	public int getWaitTime() {
		return (int) (System.currentTimeMillis() - waitStartTime);
	}

	/** 状态結束 **/
	protected void onStateDisenable(int state) {
	}

	/** 游戏状态开始 **/
	protected void onGameStateEnable(int state) {
	}

	/** 游戏状态結束 **/
	protected void onGameStateDisenable(int state) {
	}

	/** 玩家进入事件 **/
	protected void onPlayerEnter(P player) {
		updateWaitTime();
		playerJoinTime = System.currentTimeMillis();
	}

	/** 玩家离开事件 **/
	protected void onPlayerLeave(P player) {
	}

	/** 是否能进入房间 **/
	public abstract Result checkCanEnter(GamePlayer player);

	/** 判断是否能离开房间 **/
	public Result checkCanLeave(GamePlayer player) {
		// 判断状态, 非知准备状态和回合锁定不能进入
		if (getState() != RoomState.PREPARE) {
			return Result.error("游戏进行中, 不能离开!"); // 不是准备阶段不能退出
		}
		// 可以进入
		return Result.succeed();
	}

	/** 执行进入房间 **/
	public abstract Result playerEnter(GamePlayer player);

	/** 执行玩家进入房间(提交任务执行) **/
	public boolean doPlayerEnter(GamePlayer player) {
		// 执行进入
		Result reuslt = playerEnter(player);
		if (!reuslt.isSucceed()) {
			// 发送错误消息
			player.sendText(reuslt.getMsg());
			return false; // 进入失败
		}
		// 成功处理, 添加玩家.
		if (!GamePlayerMgr.getInstance().add(player)) {
			// 处理离开
			playerLeave(player);
			// 发送
			player.sendText(LanguageSet.get(TextTempId.ID_7, "进入房间错误!"));
			return false;
		}
		return true;
	}

	/** 玩家重连 **/
	protected abstract boolean doPlayerReload(GamePlayer player);

	/** 离开房间(强制) **/
	public abstract boolean playerLeave(GamePlayer player, int leaveType, boolean force);
	
	/** 获取房间位置 房卡房/自由房/公会房 **/
	public byte getRoomLocationType(){
		if(isRoomCard()){
			if(getRoomType() == RoomConst.ROOM_TYPE_FREE){
				if(isRoomGuild()){
					return RoomConst.LOCATION_TEAHOUSE_DIAMOND;
				}
				return RoomConst.LOCATION_ROOM_DIAMOND;
			}
			//下面是积分房卡场
			if(isRoomGuild()){
				return RoomConst.LOCATION_TEAHOUSE;
			}
			return RoomConst.LOCATION_ROOM;
		}
		return RoomConst.LOCATION_LOBBY;
	}
	
	/**
	 * 是否是房卡房间
	 * @return
	 */
	public boolean isRoomCard() {
		return getInviteCode() > 0;
	}
	
	/**
	 * 机器人是否可进入
	 * @return
	 */
	public boolean robotCanEnter() {
		return true;
	}
	
	/**
	 * 是否是茶馆
	 * @return
	 */
	public boolean isRoomGuild(){
		return getGuildId() > 0;
	}
	
	/**
	 * 获取最大局数
	 * @return
	 */
	public int getMaxGameRound(){
		return -1;
	}
	
	/**
	 * 获取当前局数
	 * @return
	 */
	public abstract int getCurGameRound();
	
	/**
	 * 获取房间模板
	 * @return
	 */
	public abstract LobbyTempInfo getTempInfo();
	/** 离开房间 **/
	public boolean playerLeave(GamePlayer player) {
		return playerLeave(player, RoomLeaveType.NORMAL, false);
	}

	/** 所有玩家离开 **/
	protected void playerLeaveAll(int leaveType, boolean force) {
		logger.info("playerLeaveAll");
		// 遍历玩家执行离开
		for (P rplayer : players.values()) {
			// 判断是否还在房间(如果更新时把其他人踢出就尴尬了)
			if (rplayer.getRoom() != this //
					|| rplayer.getPlayer().getRoom() != this //
			) {
				continue; // 跳过, 这个玩家可能已经出去了.
			}

			// 执行离开
			playerLeave(rplayer.getPlayer(), leaveType, force);
		}
	}

	/** 是否有人(机器人不算) **/
	public boolean hashHuman() {
		// 遍历判断
		Collection<P> players = getPlayers();
		for (P player : players) {
			if (player.isRobot()) {
				continue;
			}
			return true; // 有玩家
		}
		return false;
	}

	/** 提交任务 **/
	public void enqueue(Runnable runnable) {
		queue.enqueue(runnable);
	}

	/** 获取玩家 **/
	public P getPlayer(long playerId) {
		return (players != null) ? players.get(playerId) : null;
	}

	public Collection<P> getPlayers() {
		return players.values();
	}

	public int getPlayerCount() {
		return (players != null) ? players.size() : 0;
	}

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public boolean isAlive() {
		// 判断是否有玩家
		int psize = (players != null) ? players.size() : 0;
		if (psize > 0) {
			return true;
		}
		return (System.currentTimeMillis() - activeTime) < GameConst.ROOM_UNLOAD_INTERVAL;
	}

	@Override
	public void updateActiveTime() {
		activeTime = System.currentTimeMillis();
	}

	/** 玩家重登 **/
	public void onPlayerRelogin(GamePlayer player) {
		logger.debug("玩家重登:" + player);
	}

	/** 玩家离线 **/
	public void onPlayerLogout(GamePlayer player) {
		logger.debug("玩家下线:" + player);
	}

	/** 是否在房间里 **/
	public boolean isInRoom(long playerId) {
		return players.containsKey(playerId);
	}

	/** 发送多语言文本消息给所有人 **/
	public void sendLanguageTextToAll(Object key, Object... params) {
		// 遍历发送
		for (GameRoomPlayer<?> rplayer : players.values()) {
			GamePlayer player = rplayer.getPlayer();
			player.sendLanguageText(key, params);
		}
	}

	/** 发送文本消息给所有人 **/
	public void sendTextToAll(String text) {
		sendTextToAll(text, 0);
	}

	/** 发送文本消息给所有人 **/
	public void sendTextToAll(String text, long filtrId) {
		// 遍历发送
		for (GameRoomPlayer<?> rplayer : players.values()) {
			if (filtrId != 0 && filtrId == rplayer.getPlayerId()) {
				continue; // 过滤掉
			}
			rplayer.getPlayer().sendText(text);
		}
	}

	/** 发送信息给所有人 **/
	public void sendPacketToAll(short code, RpMessage rpMsg, long filtrId) {
		// 检测玩家数量
		int psize = (players != null) ? players.size() : 0;
		if (psize <= 0) {
			return;
		}
		// 遍历执行
		for (GameRoomPlayer<?> rplayer : players.values()) {
			if (filtrId != 0 && filtrId == rplayer.getPlayerId()) {
				continue; // 过滤掉
			}
			rplayer.sendPacket(code, rpMsg);
		}
	}

	/** 创建基础的房间消息(用于展示的) **/
	public RoomMsg createBaseMsg() {
		RoomMsg msg = new RoomMsg();
		msg.setId(id);
		return msg;
	}

	public int getState() {
		return state;
	}

	public int getGameState() {
		return gameState;
	}

	/** 判断游戏状态 **/
	public boolean checkGameState(int gameState) {
		return checkGameState(gameState, false);
	}

	/** 判断游戏状态, 如果状态不对, 是否输出错误日志 **/
	public boolean checkGameState(int gameState, boolean showError) {
		// 判断状态是否相符
		if (this.gameState != gameState) {
			if (showError) {
				logger.error("状态错误! " + this.gameState);
			}
			return false;
		}
		// 判断是否被锁定了
		if (isStateLock()) {
			if (showError) {
				logger.error("已经被执行了!");
			}
			return false;
		}
		return true;
	}

	/** 是否被锁定了 **/
	protected boolean isStateLock() {
		return stateLock;
	}

	/** 重置执行锁 **/
	protected void resetStateLock() {
		stateLock = false;
	}

	/** 设置状态锁, 控制 checkGameState,表示这个状态结束了 **/
	protected void setStateLock() {
		stateLock = true;
	}

	public long getId() {
		return id;
	}

	public int getGameType() {
		return gameType;
	}

	/** 触发更新 **/
	protected void onUpdate(long dt) {
		// 更新玩家
		if (players != null && !players.isEmpty()) {
			// 遍历更新玩家
			List<P> temps = new ArrayList<>(players.values());
			for (P rplayer : temps) {
				// 判断是否还在房间(如果更新时把其他人踢出就尴尬了)
				if (rplayer.getRoom() != this //
						|| rplayer.getPlayer().getRoom() != this //
				) {
					continue; // 跳过, 这个玩家可能已经出去了.
				}
				// 执行更新
				rplayer.onUpdate();
			}
		}

		// 检测开始
		int state = this.getState();
		if (state == RoomState.PREPARE) {
			// 检测开启
			if (this.tryToGameStart()) {
				return;
			}
			// 检测关闭
			if (this.tryToClose()) {
				return;
			}
		}

	}

	/** 检测是否自动关闭房间 **/
	protected boolean tryToClose() {
		// 检测是否还在这个状态
		if (this.checkGameState(RoomState.PREPARE)) {
			return false;
		}

		// 判断等待时间
		int waitTime = getWaitTime();
		if (waitTime < RoomConst.ROOM_WAIT_TIMEOUT) {
			return false; // 等待得不够
		}

		// 判断是否都下线了
		for (P rplayer : players.values()) {
			// 检测玩家是否在座位上
			if (rplayer == null) {
				continue;
			}
			// 机器人
			if (rplayer.isRobot()) {
				continue; // 机器人在不在线不管
			}
			// 判断是否在线
			if (rplayer.isOnline()) {
				return false; // 在线不关闭
			}
		}
		// logger.info("房间超时关闭:" + this + " " + Arrays.toString(seats));

		// 提示
		TextMsg textMsg = Connector.createTextMsg(MsgType.TIP, LanguageSet.get(TextTempId.ID_7, "房间等待超时, 自动关闭."));
		this.sendPacketToAll(Protocol.C_TEXT, textMsg, 0);

		// 执行关闭
		playerLeaveAll(RoomLeaveType.WAIT_TIMEOUT, true); // 再清除剩余玩家
		this.toClose(RoomResetCode.WAIT_TIMEOUT, "等待超时"); // 先结束房间
		return true;
	}

	/** 执行关闭 **/
	protected void toClose(int code, String errStr) {
		if (code < 0) {
			logger.error("房间非正常关闭!" + code + " " + errStr);
		}
		// 切换为关闭状态
		// this.roundIndex++; // 回合标记
		if (!setState(RoomState.CLOSE)) {
			// logger.error("房间已经关闭, 重复操作!" + this, true);
			return; // 已经是关闭状态
		}
		// 切换游戏状态

		// setGameState(SlotsGameState.NONE);
		// logger.debug("房间关闭! code=" + code + " err=" + errStr + " " + this);

		// 遍历检测

		for (P rplayer : players.values()) {
			// 检测玩家是否在座位上
			if (rplayer == null) {
				continue;
			}
			rplayer.onClose(code, errStr);
		}

		// 触发关闭事件
		onClose(code, errStr);
	}

	/** 房间重置前的关闭 **/
	protected void onClose(int code, String errStr) {
		List<P> temps = new ArrayList<>(players.values());
		for (P rplayer : temps) {
			onClose(rplayer, code, errStr);
		}
	}

	/** 房间重置前的关闭 **/
	protected void onClose(P player, int code, String errStr) {

	}

	/** 检测是否能开始游戏, 能开始就进入开始(准备模式下调用 ) **/
	protected boolean tryToGameStart() {
		// 检测是否还在这个状态
		if (this.checkGameState(RoomState.PREPARE)) {
			return false;
		}

		// 检测是否能开始
		if (!checkGameStart()) {
			return false;
		}

		// 执行开始
		this.setStateLock(); // 锁定状态, 防止重复执行
		// 直接开始, 不要延迟, 避免玩家突然离开.(如果要延迟应该在检测上处理.)
		toGameStart();
		return true;
	}
	
	/**
	 * 获取房间号
	 * @return
	 */
	public abstract int getInviteCode();
	
	/**
	*工会ID
	*/
	public abstract long getGuildId();
	/**
	 * 添加概率事件日志（默认事件次数为1、无额外数据）
	 * @param playerId 玩家ID
	 * @param type 事件类型
	 */
	protected void addProbabilityLog(long playerId, int type) {
		addProbabilityLog(playerId, type, 1, null);
	}
	
	/**
	 * 添加概率事件日志（无玩家ID、默认事件次数为1、无额外数据）
	 * @param type 事件类型
	 */
	protected void addProbabilityLog(int type) {
		addProbabilityLog(0, type, 1, null);
	}
	
	/**
	 * 添加概率事件日志（无玩家ID、无额外数据）
	 * @param type 事件类型
	 * @param num 事件次数
	 */
	protected void addProbabilityLog(int type, int num) {
		addProbabilityLog(0, type, num, null);
	}
	
	/**
	 * 添加概率事件日志（无额外数据）
	 * @param playerId 玩家ID
	 * @param type 事件类型
	 * @param num 事件次数
	 */
	protected void addProbabilityLog(long playerId, int type, int num) {
		addProbabilityLog(playerId, type, num, null);
	}
	
	/**
	 * 添加概率事件日志（无额外数据）
	 * @param playerId 玩家ID
	 * @param type 事件类型
	 * @param num 事件次数
	 */
	protected void addProbabilityLog(long playerId, int type, String extr) {
		addProbabilityLog(playerId, type, 1, extr);
	}
	
	/**
	 * 添加概率事件日志
	 * @param playerId 玩家ID
	 * @param type 事件类型
	 * @param num 事件次数
	 * @param extr 额外数据（各个游戏类型的自己的扩展数据，长度为64位的String, 可为null，）
	 */
	protected void addProbabilityLog(long playerId, int type, int num, String extr) {
//		switch (gameType) {
//		case GameType.ZHAJINHUA:{
//			ZJHProbabilityLogInfo info = new ZJHProbabilityLogInfo();
//			info.setRoomId(getId());
//			info.setRoomType(getRoomType());
//			info.setLocationType(getRoomLocationType());
//			info.setPlayerId(playerId);
//			info.setType(type);
//			info.setNum(num);
//			info.setInviteCode(getInviteCode());
//			info.setUpdateTime(new Date());
//			if (StringUtils.isEmpty(extr))
//				info.getExtr();
//			OpLogMgr.instance.get(ZJHProbabilityLogInfo.class).write(info);
//			break;
//		}
//		case GameType.MJ_CHENGDU: {
//			MahjongProbabilityLogInfo info = new MahjongProbabilityLogInfo();
//			info.setRoomId(getId());
//			info.setRoomType(getRoomType());
//			info.setLocationType(getRoomLocationType());
//			info.setPlayerId(playerId);
//			info.setType(type);
//			info.setNum(num);
//			info.setInviteCode(getInviteCode());
//			info.setUpdateTime(new Date());
//			if (StringUtils.isEmpty(extr))
//				info.getExtr();
//			OpLogMgr.instance.get(MahjongProbabilityLogInfo.class).write(info);
//			break;
//		}
//		case GameType.BULLGOLDFIGHT: {
//			BGFProbabilityLogInfo info = new BGFProbabilityLogInfo();
//			info.setRoomId(getId());
//			info.setRoomType(getRoomType());
//			info.setLocationType(getRoomLocationType());
//			info.setPlayerId(playerId);
//			info.setType(type);
//			info.setNum(num);
//			info.setInviteCode(getInviteCode());
//			info.setUpdateTime(new Date());
//			if (StringUtils.isEmpty(extr))
//				info.getExtr();
//			OpLogMgr.instance.get(BGFProbabilityLogInfo.class).write(info);
//			break;
//		}
//		case GameType.DAXUAN: {
//			DaXuanProbabilityLogInfo info = new DaXuanProbabilityLogInfo();
//			info.setRoomId(getId());
//			info.setRoomType(getRoomType());
//			info.setLocationType(getRoomLocationType());
//			info.setPlayerId(playerId);
//			info.setType(type);
//			info.setNum(num);
//			info.setInviteCode(getInviteCode());
//			info.setUpdateTime(new Date());
//			if (StringUtils.isEmpty(extr))
//				info.getExtr();
//			OpLogMgr.instance.get(DaXuanProbabilityLogInfo.class).write(info);
//			break;
//		}
//		case GameType.LANDLORDS: {
//			LandlordProbabilityLogInfo info = new LandlordProbabilityLogInfo();
//			info.setRoomId(getId());
//			info.setRoomType(getRoomType());
//			info.setLocationType(getRoomLocationType());
//			info.setPlayerId(playerId);
//			info.setType(type);
//			info.setNum(num);
//			info.setInviteCode(getInviteCode());
//			info.setUpdateTime(new Date());
//			if (StringUtils.isEmpty(extr))
//				info.getExtr();
//			OpLogMgr.instance.get(LandlordProbabilityLogInfo.class).write(info);
//			break;
//		}
//
//		default:
//			logger.error("未知游戏类型概率日志");
//			break;
//		}
	}
	
	/**
	 * 发送游戏赢得钻石公告
	 * @param pname 玩家名字
	 * @param lobbyTemplateId 大厅模板ID
	 * @param point 钻石数量
	 */
	protected void sendGameNotice(String pname, int lobbyTemplateId, int point) {
		if (!checkNoticePoint(point, getGameType(), lobbyTemplateId))
			return;
		IControlService service = GameChannelMgr.getRandomServiceByModule(ModuleName.CONTROL, IControlService.class);
		service.sendNotice("系统", 0, getNoticeText(pname, lobbyTemplateId, point), 0, 0, 0, 1, 1);
	}

	/**
	 * 发送游戏赢得钻石公告
	 * @param pname 玩家名字
	 * @param point 钻石数量
	 */
	protected void sendGameNotice(String pname, int point) {
		if (!checkNoticePoint(point, getGameType(), -1))
			return;
		IControlService service = GameChannelMgr.getRandomServiceByModule(ModuleName.CONTROL, IControlService.class);
		service.sendNotice("系统", 0, getNoticeText(pname, -1, point), 0, 0, 0, 1, 1);
	}
	
	protected String getNoticeText(String pname, int lobbyTemplateId, int point) {
		StringBuilder sb = new StringBuilder();
		sb.append("恭喜").append("<color=#FFFFFF>").append(pname).append("</color>").append("玩家在").append("<color=#FFFF00>").append(GameType.getGameName(getGameType()));
		switch (getRoomLocationType()) {
			case RoomConst.LOCATION_LOBBY:
//				sb.append(LobbyTempMgr.getLobbyName(lobbyTemplateId));
				break;
			case RoomConst.LOCATION_ROOM:
				sb.append("房卡场");
				break;
			case RoomConst.LOCATION_TEAHOUSE:
				sb.append("俱乐部");
				break;
			case RoomConst.LOCATION_ROOM_DIAMOND:
				sb.append("房卡场");
				break;
			case RoomConst.LOCATION_TEAHOUSE_DIAMOND:
				sb.append("俱乐部");
				break;
			default:
				break;
		}
		sb.append("</color>").append("赢得").append("<color=#FF0000>").append(point / 100).append("</color>").append("钻石");
		return sb.toString();
	}
	
	private boolean checkNoticePoint(int point, int gameType, int lobbyTemplateId) {
//		switch (gameType) {
//			case GameType.LANDLORDS:
//				if (getRoomLocationType() == RoomConst.LOCATION_ROOM_DIAMOND 
//					|| getRoomLocationType() == RoomConst.LOCATION_TEAHOUSE_DIAMOND) { // 钻石俱乐部或者钻石房卡
//					return point >= RoomConst.NOTICE_LANDLORD_ROOM_CARD_POINT_LIMIT;
//				} else if (getRoomLocationType() == RoomConst.LOCATION_LOBBY) {
//					if (lobbyTemplateId == LobbyTempMgr.landlord_primary_templateId) { // 初级场
//						return point >= RoomConst.NOTICE_LANDLORD_PRIMARY_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.landlord_middle_templateId) { // 中级场
//						return point >= RoomConst.NOTICE_LANDLORD_MIDDLE_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.landlord_senoir_templateId) { // 高级场
//						return point >= RoomConst.NOTICE_LANDLORD_SENIOR_POINT_LIMIT;
//					}
//				}
//				break;
//			case GameType.BULLGOLDFIGHT:
//				if (getRoomLocationType() == RoomConst.LOCATION_ROOM_DIAMOND 
//					|| getRoomLocationType() == RoomConst.LOCATION_TEAHOUSE_DIAMOND) { // 钻石俱乐部或者钻石房卡
//					return point >= RoomConst.NOTICE_BULLGOLDFIGHT_ROOM_CARD_POINT_LIMIT;
//				} else if (getRoomLocationType() == RoomConst.LOCATION_LOBBY) {
//					if (lobbyTemplateId == LobbyTempMgr.bullgoldfight_primary_templateId) { // 初级场
//						return point >= RoomConst.NOTICE_BULLGOLDFIGHT_PRIMARY_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.bullgoldfight_middle_templateId) { // 中级场
//						return point >= RoomConst.NOTICE_BULLGOLDFIGHT_MIDDLE_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.bullgoldfight_senoir_templateId) { // 高级场
//						return point >= RoomConst.NOTICE_BULLGOLDFIGHT_SENIOR_POINT_LIMIT;
//					}
//				}
//				break;
//			case GameType.ZHAJINHUA:
//				if (getRoomLocationType() == RoomConst.LOCATION_ROOM_DIAMOND 
//					|| getRoomLocationType() == RoomConst.LOCATION_TEAHOUSE_DIAMOND) { // 钻石俱乐部或者钻石房卡
//					return point >= RoomConst.NOTICE_ZHAJINHUA_ROOM_CARD_POINT_LIMIT;
//				} else if (getRoomLocationType() == RoomConst.LOCATION_LOBBY) {
//					if (lobbyTemplateId == LobbyTempMgr.zhajinhua_primary_templateId) { // 初级场
//						return point >= RoomConst.NOTICE_ZHAJINHUA_PRIMARY_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.zhajinhua_middle_templateId) { // 中级场
//						return point >= RoomConst.NOTICE_ZHAJINHUA_MIDDLE_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.zhajinhua_senoir_templateId) { // 高级场
//						return point >= RoomConst.NOTICE_ZHAJINHUA_SENIOR_POINT_LIMIT;
//					}
//				}
//				break;
//			case GameType.MJ_CHENGDU:
//				if (getRoomLocationType() == RoomConst.LOCATION_ROOM_DIAMOND 
//					|| getRoomLocationType() == RoomConst.LOCATION_TEAHOUSE_DIAMOND) { // 钻石俱乐部或者钻石房卡
//					return point >= RoomConst.NOTICE_MAHJONG_ROOM_CARD_POINT_LIMIT;
//				} else if (getRoomLocationType() == RoomConst.LOCATION_LOBBY) {
//					if (lobbyTemplateId == LobbyTempMgr.mahjong_primary_templateId) { // 初级场
//						return point >= RoomConst.NOTICE_MAHJONG_PRIMARY_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.mahjong_middle_templateId) { // 中级场
//						return point >= RoomConst.NOTICE_MAHJONG_MIDDLE_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.mahjong_senoir_templateId) { // 高级场
//						return point >= RoomConst.NOTICE_MAHJONG_SENIOR_POINT_LIMIT;
//					}
//				}
//				break;
//			case GameType.DAXUAN:
//				if (getRoomLocationType() == RoomConst.LOCATION_ROOM_DIAMOND 
//					|| getRoomLocationType() == RoomConst.LOCATION_TEAHOUSE_DIAMOND) { // 钻石俱乐部或者钻石房卡
//					return point >= RoomConst.NOTICE_DAXUAN_ROOM_CARD_POINT_LIMIT;
//				} else if (getRoomLocationType() == RoomConst.LOCATION_LOBBY) {
//					if (lobbyTemplateId == LobbyTempMgr.daxuan_primary_templateId) { // 初级场
//						return point >= RoomConst.NOTICE_DAXUAN_PRIMARY_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.daxuan_middle_templateId) { // 中级场
//						return point >= RoomConst.NOTICE_DAXUAN_MIDDLE_POINT_LIMIT;
//					} else if (lobbyTemplateId == LobbyTempMgr.daxuan_senoir_templateId) { // 高级场
//						return point >= RoomConst.NOTICE_DAXUAN_SENIOR_POINT_LIMIT;
//					}
//				}
//				break;
//			case GameType.LHD:
//				return point >= RoomConst.NOTICE_LHD_POINT_LIMIT;
//			default:
//				break;
//		}
		return false;
	}
	
	/**
	 * 进行抽成处理
	 * @param money 需要抽成的数量
	 * @return 抽成的数量
	 */
	protected long commissionsFilter(boolean isRobot, long money) {
		if (money <= 0 || isRobot)
			return 0;
		return (long)Math.ceil(money * GlobalGameConfigMgr.globalConfig.getCommissions());
	}
	
	protected boolean checkGameStart() {
		return true;
	}

	protected void toGameStart() {

	}
	
	protected int getRobotCount() {
		int i = 0;
		for(P p : this.players.values()) {
			if (p.isRobot())
				i++;
		}
		return i;
	}

	@Override
	public String toString() {
		return "GameRoom [id=" + id + " state=" + state + " gameState=" + gameState + "]";
	}

	/** 是否允许房间测试 **/
	public static boolean isRoomTest() {
		return roomTest;
	}

	public static void setRoomTest(boolean roomTest) {
		GameRoom.roomTest = roomTest;
	}

	public boolean isAiEnable() {
		return aiEnable;
	}

	public void setAiEnable(boolean aiEnable) {
		this.aiEnable = aiEnable;
	}

	public int getRoomType() {
		return roomType;
	}

	public long getCreateTime() {
		return createTime;
	}
	
}
