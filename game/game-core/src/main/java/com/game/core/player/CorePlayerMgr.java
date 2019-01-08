package com.game.core.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.module.ModuleName;
import com.game.base.service.server.App;
import com.game.core.CoreModule;
import com.game.entity.dao.PlayerDAO;
import com.game.entity.entity.PlayerInfo;
import com.game.framework.component.action.Action;
import com.game.framework.component.action.ActionQueue;
import com.game.framework.component.action.LoopAction;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.utils.ThreadUtils;
import com.game.framework.utils.collection.entity.EntityMgr0;
import com.game.framework.utils.collections.MapUtils;

/**
 * 玩家管理器 CorePlayerMgr.java
 * 
 * @author JiangBangMing 2019年1月8日下午2:19:29
 */
public class CorePlayerMgr extends EntityMgr0<Long, CorePlayer> {
	private static CorePlayerMgr instance = new CorePlayerMgr();
	protected final ActionQueue queue;

	private CorePlayerMgr() {
		// 创建公会默认队列
		queue = new ActionQueue(CoreModule.getExecutor());
		// 绑定定时器
		queue.enqueue(new UpdateAction(5 * 1000));
	}

	/** 提交玩家任务 **/
	public static void enqueue(long playerId, Runnable runnable) {
		// 获取玩家是否在内存
		CorePlayer player = getInstance().getFromCache(playerId);
		if (player != null) {
			player.enqueue(runnable);
			return;
		}

		// 玩家不在内存, 按照ID分配到系统队列
		ServiceMgr.enqueue(playerId, runnable);
	}

	/** 获取句柄 **/
	public static CorePlayerMgr getInstance() {
		return instance;
	}

	@Override
	protected CorePlayer create(Long id) {
		// 判断ID
		if (id == null || id <= 0L) {
			return null;
		}
		long playerId = id;

		// 绑定数据
		ServerConfig selfConfig = App.getInstance().getConfig();
		if (!MailBox.setIfNull(playerId, ModuleName.CORE, selfConfig)) {
			ServerConfig config = MailBox.get(playerId, ModuleName.CORE);
			if (config == null || !config.equals(selfConfig)) {
				Log.warn("绑定玩家失败, 当前已存在别的内存中加载!" + playerId + " in " + config);
				return null;
			}
		}

		try {
			// 检测player数据
			PlayerDAO dao = DaoMgr.getInstance().getDao(PlayerDAO.class);
			PlayerInfo info = dao.get(playerId);
			if (info == null) {
				MailBox.set(playerId, ModuleName.CORE, null); // 清除标记
				Log.error("不存在玩家Id! playerId=" + playerId, true);
				return null;
			}

			// 创建玩家数据
			CorePlayer player = new CorePlayer(info);
			return player;
		} catch (Exception e) {
			Log.error("加载玩家错误! " + playerId, e);
		}
		return null;
	}

	@Override
	protected void onRemove(Long playerId, CorePlayer player) {
		super.onRemove(playerId, player);
		// 绑定数据绑定
		// MailBox.set(playerId, ModuleName.CORE, null);
	}

	/** 从缓存中获取玩家数据 **/
	public CorePlayer getFromCache(long id, boolean updateActive) {
		// 空过滤
		if (id <= 0) {
			return null;
		}
		return super.getFromCache(id, updateActive);
	}

	public CorePlayer getFromCache(long id) {
		return getFromCache(id, true);
	}

	public CorePlayer get(long id) {
		// 空过滤
		if (id <= 0) {
			return null;
		}
		// 获取玩家数据
		CorePlayer player = super.get(id);
		return player;
	}

	/** 是否存在这个玩家的Id **/
	public boolean checkExistPlayerId(long playerId) {
		// 先从内存找, 如果有代表存在.
		if (isExist(playerId)) {
			return true;
		}
		// 数据库查找
		PlayerDAO dao = DaoMgr.getInstance().getDao(PlayerDAO.class);
		return dao.get(playerId) != null;
	}

	/** 获取全部在内存的玩家 **/
	public Collection<CorePlayer> getPlayers() {
		return this.elements.values();
	}

	/** 获取全部在线玩家 **/
	public List<CorePlayer> getOnlinePlayers() {
		List<CorePlayer> onlines = new ArrayList<>();
		for (CorePlayer player : this.elements.values()) {
			if (!player.isOnline()) {
				continue;
			}
			onlines.add(player);
		}
		return onlines;
	}

	/**
	 * 获取全部在线玩家
	 * 
	 * @return 数组 int[0]在线总人数, int[1] 真实玩家数量
	 */
	public int[] getPlayersCount() {
		int[] countArr = new int[] { 0, 0 };
		for (CorePlayer player : this.elements.values()) {
			if (player == null)
				continue;
			if (player.isOnline()) {
				countArr[0]++;
				if (!player.isRobet())
					countArr[1]++;
			}
		}
		return countArr;
	}

	@Override
	public boolean isExist(Long key) {
		return super.isExist(key);
	}

	/** 移除玩家数据 **/
	public boolean remove(long playerId) {
		CorePlayer player = super.remove(playerId);
		// Log.debug("卸载玩家数据: " + playerId + " " + player);
		return (player != null);
	}

	@Override
	protected boolean remove(Long key, CorePlayer player) {
		// Log.debug("卸载玩家数据: " + player);
		return super.remove(key, player);
	}

	/** 移除所有数据(多线程保存) **/
	public void removeAllByThread() {
		// 计数器
		final AtomicInteger counter = new AtomicInteger();

		// 遍历所有玩家, 提交移除任务.
		Collection<CorePlayer> players = getPlayers();
		for (final CorePlayer player : players) {
			counter.incrementAndGet();
			// 提交释放任务
			player.enqueue(new Action() {
				@Override
				public void execute() throws Exception {
					try {
						// 执行移除
						remove(player.getPlayerId(), player);
					} finally {
						// 删除完成
						counter.decrementAndGet();
					}
				}

				@Override
				public String toString() {
					return "remove player task: playerId=" + player.getPlayerId() + " " + this.getClass();
				}

				@Override
				public int getWarningTime() {
					return 3 * 1000;
				}

			});
		}

		// 遍历等待
		while (true) {
			// 检测结束时间
			int count = counter.get();
			if (count <= 0) {
				break;
			}
			// 等待结束
			ThreadUtils.sleep(500);
			// 输出信息
			Log.info("remove all player last: " + count);
		}
	}

	@Override
	public void removeAll() {
		super.removeAll();
	}

	/** 局部保存数据(多线程保存) **/
	public boolean saveByThread(int index, int mod) {
		// 计数器
		final AtomicInteger counter = new AtomicInteger();
		forech(this, index, mod, new MapUtils.Foreach<Long, CorePlayer>() {
			@Override
			public void action(final Long id, final CorePlayer player) {
				player.enqueue(new Action() {
					@Override
					public void execute() throws Exception {
						// 保存玩家玩家数据, 如果玩家保存失败, 继续保存其他玩家.
						try {
							// 保存成功才能卸载
							boolean s = player.save();
							if (s) {
								// 保存成功后, 判断能否卸载.
								if (!player.isAlive()) {
									remove(id, player);
								}
							}
						} catch (Exception e) {
							Log.error("数据保存异常, key:" + id + " entity=" + player, e);
						}
						// 删除完成
						counter.decrementAndGet();
					}

					@Override
					public String toString() {
						return "save player[" + player.getPlayerId() + "]" + this.getClass();
					}

					@Override
					public int getWarningTime() {
						return 3 * 1000;
					}
				});
			}
		});

		// 遍历等待
		while (true) {
			// 检测结束时间
			int count = counter.get();
			if (count <= 0) {
				break;
			}
			// 等待结束
			ThreadUtils.sleep(100);
			// 输出信息
			Log.debug("save all player last: " + count);
		}
		return true;
	}

	@Override
	public boolean save(int index, int mod) {
		return save(this, index, mod);
	}

	/** 保存所有数据(多线程保存) **/
	public void saveAll() {
		save(0, 1);
	}

	/** 定时更新 **/
	protected static class UpdateAction extends LoopAction {
		public UpdateAction(long delay) {
			super(delay);
		}

		@Override
		protected void update(final long now, final long prev, final long dt, final int index) {
			CorePlayerMgr.getInstance().forech(new MapUtils.Foreach<Long, CorePlayer>() {
				@Override
				public void action(Long playerId, final CorePlayer player) {
					// 只有在线才会更新
					if (!ConfigMgr.isDebug()) {
						if (!player.isOnline()) {
							return;
						}
					}

					// 提交事件
					player.enqueue(new Runnable() {
						@Override
						public void run() {
							player.updateByDay(60 * 1000);
							player.update(5000L);
						}

						@Override
						public String toString() {
							return "update player[" + player.getPlayerId() + "]";
						}
					});
				}
			});
		}
	}

}