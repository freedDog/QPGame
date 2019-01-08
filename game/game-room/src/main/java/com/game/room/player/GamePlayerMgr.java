package com.game.room.player;

import java.util.ArrayList;
import java.util.List;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.base.service.server.App;
import com.game.entity.shared.RoomPlayerInfo;
import com.game.framework.component.SyncObject;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.action.LoopAction;
import com.game.framework.component.data.manager.ElementManager0;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.room.base.GameRoom;

/**
 * 玩家管理器
 * GamePlayerMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午5:09:40
 */
public class GamePlayerMgr extends ElementManager0<Long, GamePlayer> {
	private static GamePlayerMgr instance = new GamePlayerMgr();
	protected final ActionQueue queue;

	private GamePlayerMgr() {
		// 创建公会默认队列
		queue = new ActionQueue(ServiceMgr.getExecutor());
		// 添加定时器
		queue.enqueue(new UpdateAction(1000));
	}

	/** 读取玩家信息(阻塞调用) **/
	public static RoomPlayerInfo getRoomPlayerInfo(long playerId) {
		final SyncObject<RoomPlayerInfo> syncObj = new SyncObject<>();
		syncObj.start();

		// 获取玩家所在进程上
		ICoreService service = GameChannelMgr.getChannelServiceByPlayerId(playerId, ModuleName.CORE, ICoreService.class);
		service.loadRoomPlayerInfo(playerId, new RpcCallback() {
			@SuppressWarnings("unused")
			void onCallBack(final int code, final String msg, final RoomPlayerInfo info) {
				syncObj.complete(code > 0, msg, info);
			}

			@Override
			protected void onTimeOut(final long timeout) {
				syncObj.complete(false, "超时:" + timeout, null);
			}
		});

		// 循环等待
		while (syncObj.waiting(13 * 1000)) {
			Thread.yield();
		}
		// 判断结果
		if (!syncObj.isSucceed()) {
			Log.warn("读取玩家RoomPlayerInfo失败, playerId=" + playerId + " msg=" + syncObj.getMsg());
			return null;
		}
		return syncObj.getObj();
	}
	
	/**
	 * 获取房间人数
	 * int[0] 总房间人数 int[1] 房间真实玩家数量
	 */
	public static int[] getRoomPlayerCount() {
		int[] count = new int[]{0, 0};
		for(GamePlayer p : GamePlayerMgr.getInstance().elements.values()) {
			if (p == null || p.getRoom() == null)
				continue;
			count[0]++;
			if (!p.isRobet())
				count[1]++;
		}
		return count;
	}

	/** 提交玩家任务 **/
	public static void enqueue(long playerId, Runnable runnable) {
		// 获取玩家是否在内存
		GamePlayer player = getInstance().getFromCache(playerId);
		if (player != null) {
			player.enqueue(runnable);
			return;
		}

		// 玩家不在内存, 按照ID分配到系统队列
		ServiceMgr.enqueue(playerId, runnable);
	}

	@Override
	protected GamePlayer create(Long id) {
		return null; // 不自动创建
	}

	/** 添加玩家 **/
	public synchronized boolean add(GamePlayer player) {
		long playerId = player.getPlayerId();
		// 判断当前是否存在数据
		if (super.isExist(playerId)) {
			return false; // 已经存在了
		}
		// 检测玩家数据绑定
		ServerConfig selfConfig = App.getInstance().getConfig();
		if (!MailBox.setIfNull(playerId, ModuleName.ROOM, selfConfig)) {
			// 检测是否是同个地址
			ServerConfig config = MailBox.get(playerId, ModuleName.ROOM);
			if (config == null || !config.equals(selfConfig)) {
				Log.warn("绑定玩家mailbox失败, 已经存在别的进程了! playerId=" + playerId + " nowConfig=" + config + " selfConfig=" + selfConfig);
				return false;
			}
		}

		// 创建数据
		GamePlayer now = super.addByCreate(playerId, player);
		if (player != now) {
			return false; // 存在数据
		}
//		Log.debug("创建玩家对象: " + player);
		return true;
	}

	@Override
	protected void onRemove(Long playerId, GamePlayer player) {
		super.onRemove(playerId, player);
		// 绑定数据绑定
		MailBox.set(playerId, ModuleName.ROOM, null);
//		Log.debug("卸载玩家对象: " + player);
	}

	/** 获取玩家 **/
	public GamePlayer get(long id) {
		// 空过滤
		if (id <= 0) {
			return null;
		}
		// 获取玩家数据
		GamePlayer player = super.getFromCache(id);
		return player;
	}

	/** 获取玩家 **/
	public GamePlayer getFromCache(long playerId) {
		return super.getFromCache(playerId);
	}

	@Override
	public boolean isExist(Long key) {
		return super.isExist(key);
	}

	/** 移除玩家 **/
	public GamePlayer remove(long key) {
		return super.remove(key);
	}


	/** 检测是否能移除 **/
	public boolean tryRemove(final GamePlayer player) {
		GameRoom<?> room = player.getRoom();
		if (room != null) {
			return false;
		}

		// 提交玩家任务
		player.enqueue(new Runnable() {
			@Override
			public void run() {
				// 再次检测是否离开房间
				GameRoom<?> room = player.getRoom();
				if (room != null) {
					Log.warn("玩家又有房间了! " + player);
					return;
				}
				// TODO 测试房间是否关闭和断开连接!

				// 移除玩家
				GamePlayerMgr.super.remove(player.getPlayerId(), player);
				Log.debug("清除玩家:" + player);
			}

		});
		return true;
	}

	/** 清除没在的玩家 **/
	protected void onClear() {
		List<GamePlayer> players = new ArrayList<>(super.elements.values());
		for (GamePlayer player : players) {
			tryRemove(player);
		}
	}

	/** 获取句柄 **/
	public static GamePlayerMgr getInstance() {
		return instance;
	}

	/** 定时器 **/
	protected class UpdateAction extends LoopAction {
		public UpdateAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(long now, long prev, long dt, int index) {
			onClear();
		}

	}

	// @Override
	// protected void removeAll() {
	// super.removeAll();
	// }
}