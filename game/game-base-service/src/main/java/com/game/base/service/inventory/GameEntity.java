package com.game.base.service.inventory;

import com.game.base.service.config.ConfigMgr;
import com.game.framework.component.log.Log;
import com.game.framework.utils.TimeUtils;
import com.game.framework.utils.collections.MapUtils;

/**
 * 游戏个体<br>
 * 代表一个玩家或者一个公会等对象处理集合<br>
 * 
 */
public class GameEntity<T extends GameEntityInventory> extends AbstractInventoryMgr<T> {
	protected long updateTime;

	public GameEntity() {
		updateTime = System.currentTimeMillis();
	}

	/** 定时更新 **/
	protected void onTimeUpdate(final long prevTime, final long nowTime, final long dt) {
		forech(new MapUtils.Foreach<Class<?>, T>() {
			@Override
			public void action(Class<?> clazz, T inventory) {
				try {
					inventory.onTimeUpdate(prevTime, nowTime, dt);
				} catch (Exception e) {
					Log.error("数据更新失败!? " + inventory, e);
				}
			}
		});
	}

	/** 每日重置 **/
	protected void onDayReset(int day) {
		forech(new MapUtils.Foreach<Class<?>, T>() {
			@Override
			public void action(Class<?> clazz, T inventory) {
				try {
					inventory.onDayReset();
				} catch (Exception e) {
					Log.error("数据每日重置失败!? " + inventory, e);
				}
			}
		});
	}

	/** 每周重置 **/
	protected void onWeekReset() {
		forech(new MapUtils.Foreach<Class<?>, T>() {
			@Override
			public void action(Class<?> clazz, T inventory) {
				try {
					inventory.onWeekReset();
				} catch (Exception e) {
					Log.error("数据每周重置失败!? " + inventory, e);
				}
			}
		});
	}

	/** 每月重置 **/
	protected void onMonthReset() {
		forech(new MapUtils.Foreach<Class<?>, T>() {
			@Override
			public void action(Class<?> clazz, T inventory) {
				try {
					inventory.onMonthReset();
				} catch (Exception e) {
					Log.error("数据每日重置失败!? " + inventory, e);
				}
			}
		});
	}

	/**
	 * 处理数据更新<br>
	 * 
	 * @param minIntervalTime
	 *            间隔时间, 用于过滤2次处理时间间隔, 0为强制更新.
	 * 
	 * **/
	public void update(long minIntervalTime) {
		long prevTime = updateTime;
		long nowTime = System.currentTimeMillis();
		// 检测更新时间, 这个线上不可能出现, 只存在于被人调时间才有.
		if (prevTime > nowTime) {
			// 测试模式下, 调时间自动重置时间
			if (ConfigMgr.isDebug()) {
				updateTime = nowTime;
			}
		}

		// 避免过于频繁检测
		long dt = nowTime - prevTime;
		if (dt < minIntervalTime) {
			return;
		}
		updateTime = nowTime;

		// 更新个人消息
		onTimeUpdate(prevTime, nowTime, (int) dt);
	}

	/** 更新检测每日重置(每周, 每月), 这个最好不要放到update中, 因为还是有点运算量的. **/
	public boolean updateByDay(int minIntervalTime) {
		long prevTime = getUpdateTime();
		long nowTime = System.currentTimeMillis();

		// 检测更新时间, 这个线上不可能出现, 只存在于被人调时间才有.
		if (prevTime > nowTime) {
			// 测试模式下, 调时间自动重置时间
			if (ConfigMgr.isDebug()) {
				setUpdateTime(nowTime); // 重置时间, 这轮保持更新.
			}
		}

		// 避免过于频繁检测
		long dt = nowTime - prevTime;
		if (dt < minIntervalTime) {
			return false;
		}
		// 更新记录时间
		setUpdateTime(nowTime);

		// 更新每日更新
		long prevDayTime = TimeUtils.getDayTime(prevTime); // 转成当天0点时间
		long nowDayTime = TimeUtils.getDayTime(nowTime);
		long dayTime = nowDayTime - prevDayTime;
		if (dayTime < TimeUtils.oneDayTimeL) {
			return false; // 在同一天.
		}
		// 确实不在同一天, 计算相隔几天.
		try {
			int day = (int) (dayTime / TimeUtils.oneDayTimeL);
			onDayReset(day);
		} catch (Exception e) {
			Log.error("玩家每日重置错误!", e);
		}

		// 获取2个时间的周一时间点.
		long prevWeekTime = TimeUtils.getWeekTime(prevTime, 1, 0, 0, 0);
		long nowWeekTime = TimeUtils.getWeekTime(nowTime, 1, 0, 0, 0);
		if (prevWeekTime < nowWeekTime) {
			// 周一刷新每周数据(跟策划说好的, 别搞其他潜规则)
			try {
				onWeekReset();
			} catch (Exception e) {
				Log.error("玩家每周重置错误!", e);
			}
		}

		// 检测每月
		long prevMonthTime = TimeUtils.getMonthTime(prevTime, 1, 0, 0, 0);
		long nowMonthTime = TimeUtils.getMonthTime(nowTime, 1, 0, 0, 0);
		if (prevMonthTime < nowMonthTime) {
			try {
				onMonthReset();
			} catch (Exception e) {
				Log.error("玩家每月重置错误!", e);
			}
		}
		return true;
	}

	/** 获取更新时间, 用于计算更新在线时间 **/
	protected long getUpdateTime() {
		return System.currentTimeMillis();
	}

	/** 保存更新时间 **/
	protected void setUpdateTime(long updateTime) {
	}

}

