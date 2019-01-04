package com.game.base.service.inventory;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.component.log.Log;
import com.game.framework.component.service.record.TimeRecordManager;
import com.game.framework.utils.collections.MapUtils;

/**
 * Inventory管理器
 * AbstractInventoryMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午5:05:20
 */
public abstract class AbstractInventoryMgr<T extends AbstractInventory> {
	/** 保存警告时间 **/
	private static final int SAVE_WAIT_TIME = 500;

	// 玩家数据处理集合
	protected final ConcurrentMap<Class<?>, T> inventorys;

	protected AbstractInventoryMgr() {
		inventorys = new ConcurrentHashMap<>();
	}

	/** 加载数据 **/
	protected synchronized boolean load() {
		return true;
	}

	/** 卸载所有数据体 **/
	protected synchronized void unload() {
		// 卸载所有玩家数据
		Iterator<Map.Entry<Class<?>, T>> iterator = inventorys.entrySet().iterator();
		while (iterator.hasNext()) {
			T inventory = iterator.next().getValue();
			if (inventory == null) {
				iterator.remove(); // 空的也移除掉
				continue;
			}
			// 保存数据
			save(inventory);
			try {
				inventory.unload();
			} catch (Exception e) {
				Log.error("卸载数据失败!? " + inventory, e);
			}
			// 移除
			iterator.remove();
		}
		inventorys.clear();
	}

	/** 保存数据 **/
	protected boolean save(AbstractInventory inventory) {
		try {
			long startTime = System.currentTimeMillis();
			boolean r = inventory.save();
			long endTime = System.currentTimeMillis();

			// 保存消耗时间输出
			long dt = endTime - startTime;
			if (dt > SAVE_WAIT_TIME) {
				Log.warn("save data long time:" + dt + " inventory=" + inventory + " " + this);
			}
			TimeRecordManager.getInstance().addTime("save_" + inventory.getClass(), (int) dt);
			// 保存结果检测
			if (!r) {
				Log.error("保存数据失败! " + inventory.getClass() + " " + inventory);
				return false;
			}
		} catch (Exception e) {
			Log.error("保存数据失败!? " + inventory, e);
			TimeRecordManager.getInstance().addWarn("save_" + inventory.getClass());
			return false;
		}
		return true;
	}

	/** 保存数据 **/
	protected synchronized boolean save() {
		boolean result = true;
		for (Map.Entry<Class<?>, T> entry : inventorys.entrySet()) {
			T inventory = entry.getValue();
			// 保存数据
			if (!save(inventory)) {
				result = false;
			}
		}
		return result;
	}

	/** 遍历处理 **/
	protected void forech(MapUtils.Foreach<Class<?>, T> foreach) {
		MapUtils.action(inventorys, foreach, 0);
	}

	/** 创建Inventory **/
	@SuppressWarnings("unchecked")
	protected T createInventory(Class<?> clazz) {
		// 获取符合条件的构造函数
		Constructor<T> c = null;
		Constructor<?>[] cs = clazz.getDeclaredConstructors();
		for (Constructor<?> c0 : cs) {
			Class<?>[] paramTypes = c0.getParameterTypes();
			int psize = (paramTypes != null) ? paramTypes.length : 0;
			if (psize != 1) {
				continue;
			}
			// 检测参数是否符合
			Class<?> paramType = paramTypes[0];
			if (!paramType.isAssignableFrom(this.getClass())) {
				continue;
			}
			c = (Constructor<T>) c0;
			break;
		}

		// 判断构造函数
		if (c == null) {
			Log.error("找不到符合条件自动创建的构造函数! clazz=" + clazz, true);
			return null;
		}

		// 创建数据
		T inventory = null;
		try {
			c.setAccessible(true); // 启动权限获取
			inventory = (T) c.newInstance(this);
		} catch (Exception e) {
			Log.error("创建数据失败! clazz=" + clazz + " c=" + c, e);
			return null;
		}
		return (T) inventory;
	}

	@SuppressWarnings("unchecked")
	public synchronized <S extends T> S getInventory(Class<S> clazz) {
		T inventory = inventorys.get(clazz);
		if (inventory == null) {
			// 创建数据
			inventory = createInventory(clazz);
			if (inventory == null) {
				Log.error("创建数据失败! clazz=" + clazz, true);
				return null;
			}

			// 尝试插入
			T old = inventorys.putIfAbsent(clazz, inventory); // 先设置, 失败了再删除.
			inventory = (old != null) ? old : inventory;
			if (old == null) {
				boolean result = false;
				try {
					result = inventory.load();
				} catch (Exception e) {
					Log.error("玩家数据加载失败!? " + inventory, e);
					result = false;
				}

				// 加载数据
				if (!result) {
					inventorys.remove(clazz, inventory); // 移除.
					Log.error("加载数据失败! clazz=" + clazz, true);
					return null;
				}
			}

		}
		return (S) inventory;
	}

}
