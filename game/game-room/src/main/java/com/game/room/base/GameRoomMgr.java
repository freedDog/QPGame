package com.game.room.base;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.RoomState;
import com.game.base.service.key.KeyGenerateEnum;
import com.game.framework.component.action.Action;
import com.game.framework.component.action.ActionExecutor;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.action.LoopAction;
import com.game.framework.utils.collection.entity.EntityMgr0;
import com.game.framework.utils.struct.result.Result;
import com.game.room.player.GamePlayer;
import com.game.framework.utils.collections.MapUtils;


/**
 * 游戏房间管理器<br>
 * 
 */
public class GameRoomMgr<T extends GameRoom<?>> extends EntityMgr0<Long, T> {
//	protected RoomId roomId;
	protected ActionQueue queue; // 任务队列

	/** 初始化 **/
	protected boolean init() {
//		int appId = App.getInstance().getAppId();
		// roomId = new RoomId(appId, GameConst.MAHJOING_ROOMID_MAX);
//		roomId = new RoomId(appId, 0);

		// 创建队列
		// queue = new ActionQueue(RoomModule.getExecutor());
		//
		// // 添加任务
		// queue.enqueue(new UpdateAction(3 * 1000));
		return true;
	}

	public void initActionQueue(ActionExecutor executor) {
		// 创建队列
		queue = new ActionQueue(executor);

		// 添加任务
		queue.enqueue(new UpdateAction(3 * 1000));
	}

	/** 销毁 **/
	protected void destroy() {
		this.removeAll();
	}

	/** 获取房间 **/
	public T getRoom(long roomId) {
		return super.get(roomId);
	}

	/** 获取符合条件的房间 **/
	public T getRoom(IRoomFiltr<? super T> filter) {
		return super.get(filter);
	}

	/** 获取符合条件的房间 **/
	public List<T> getRooms(IRoomFiltr<? super T> filter, int maxSize) {
		return super.getAll(filter, maxSize);
	}

	public void enqueue(Runnable runnable) {
		queue.enqueue(runnable);
	}

	@Override
	protected T addByCreate(Long key, T element) {
		return super.addByCreate(key, element);
	}

	/** 获取一个新的房间Id **/
	protected long newId() {
//		return roomId.getId();
		return KeyGenerateEnum.RedisKey.keyLong();
	}

	@Override
	protected T create(Long key) {
		return null; // 不自动创建
	}

	@Override
	public void removeAll() {
		super.removeAll();
	}

	@Override
	protected void onRemove(Long key, T room) {
		super.onRemove(key, room);
		room.logger.info("房间销毁: " + room);
//		Log.debug("房间销毁: " + room);
	}

	@Override
	protected void onCreate(Long key, T data) {
		super.onCreate(key, data);
	}
	
	/**
	 * 房间数量
	 * int[0] 房卡房间数量  int[1] 自由房间数量
	 */
	public int[] roomSize() {
		int[] count = new int[]{0, 0};
		for(T t : this.elements.values()) {
			if (t == null)
				continue;
			if (t.isRoomCard()) {
				count[0]++;
			} else {
				count[1]++;
			}
		}
		return count;
	}

	// /** 执行玩家进入房间(需要提交到对应房间中执行) **/
	// private Result playerEnter(GamePlayer player, GameRoom<?> room) {
	// // 执行进入
	// Result reuslt = room.playerEnter(player);
	// if (!reuslt.isSucceed()) {
	// return reuslt;
	// }
	//
	// // 成功处理, 添加玩家.
	// if (!GamePlayerMgr.getInstance().add(player)) {
	// // 处理离开
	// room.playerLeave(player);
	// // 发送
	// return Result.error(LanguageSet.get(TextTempId.ID_7, "绑定玩家失败!"));
	// }
	//
	// return Result.error(msg);
	// }

	/** 更新处理 **/
	private void onUpdate(final long dt) {
		// 保存处理
		super.clean(this);
		// 遍历提交更新
		for (final GameRoom<?> room : this.elements.values()) {
			// 判断更新绑定
			if (room.updateCounter.get() > 0) {
				continue; // 避免重复更新
			}
			room.updateCounter.incrementAndGet(); // 增加标记

			// 提交更新任务
			room.enqueue(new Action() {
				@Override
				public void execute() throws Exception {
					try {
						room.onUpdate(dt);
					} finally {
						room.updateCounter.decrementAndGet(); // 解除标记
					}
				}
			});
		}
	}

	/************************** 内嵌类 **********************/

	/** 定时器 **/
	protected class UpdateAction extends LoopAction {
		public UpdateAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(long now, long prev, long dt, int index) {
			onUpdate(dt);
		}

	}

	/** 房间过滤 **/
	public interface IRoomFiltr<T> extends MapUtils.IFilter<Long, T> {
		boolean check(Long roomId, T room);
	}

	/** 基础房间过滤 **/
	public static class RoomFiltr<T extends GameRoom<?>> implements IRoomFiltr<T> {
		protected long[] filtrIds;
		protected GamePlayer player; // 玩家

		public RoomFiltr(GamePlayer player, long[] filtrIds) {
			this.filtrIds = filtrIds;
			this.player = player;
		}

		@Override
		public boolean check(Long roomId, T room) {
			// 检测状态
			if (room.getState() != RoomState.PREPARE) {
				return false;
			}

			// 遍历检测
			int fsize = (filtrIds != null) ? filtrIds.length : 0;
			for (int i = 0; i < fsize; i++) {
				long filtrId = filtrIds[i];
				if (room.getId() == filtrId) {
					return false;
				}
			}

			// 判断是否能进入
			Result result = room.checkCanEnter(player);
			if (!result.isSucceed()) {
				return false;
			}

			return true;
		}
	}

	/** 机器人快速进入筛选 **/
	public static class RobetRoomFiltr<T extends GameRoom<?>> extends RoomFiltr<T> {
		private List<Class<?>> roomClasses; // 可以进入的房间类型

		public RobetRoomFiltr(GamePlayer player, List<Class<?>> roomClasses) {
			super(player, null);
			this.roomClasses = roomClasses;
		}

		@Override
		public boolean check(Long roomId, T room) {
			// 判断房间类型
			if (roomClasses != null) {
				// 遍历判断类型
				boolean enable = false;
				for (Class<?> roomClass : roomClasses) {
					if (!roomClass.isInstance(room)) {
						continue;
					}
					enable = true;
					break;
				}
				// 判断结果
				if (!enable) {
					return false; // 类型不符合
				}
			}
			
			if(room.isRoomCard()){ // 房卡场禁止机器人进入
				return false;
			}
			
			if (!room.robotCanEnter()) {
				return false;
			}


			// 检测加入申请(多线程申请中, 避免重复浪费时间, 直接先过滤掉)
			if (room.joinCounter.get() > 0) {
				return false; // 有人申请中啦
			}

			// 筛选是否有人 或者房间AI控制开关没有开启
			if (!room.hashHuman() || (!room.isAiEnable() && ConfigMgr.isDebug())) {
				return false; // 没有玩家
			}

			return super.check(roomId, room);
		}
	}

	/** 房间Id **/
	protected class RoomId {
		protected AtomicLong roomId;
		protected long startId;
		protected long range;

		public RoomId(int appId, int range) {
			this.startId = appId * range;
			this.range = range;
			this.roomId = new AtomicLong();
		}

		public long getId() {
			long index = roomId.incrementAndGet();
			long rindex = (range > 0) ? index % range : index;
			return startId + rindex;
		}

	}

	/************************** 静态 **********************/
	protected static GameRoomMgr<GameRoom<?>> instance = new GameRoomMgr<>(); // 单例

	public static GameRoomMgr<GameRoom<?>> getInstance() {
		return instance;
	}

}
