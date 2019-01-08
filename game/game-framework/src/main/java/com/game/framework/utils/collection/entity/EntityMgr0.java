package com.game.framework.utils.collection.entity;

import java.util.Iterator;
import java.util.Map;

import com.game.framework.component.data.manager.ElementManager0;
import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.MapUtils;

/**
 * 实体管理器<br>
 * 实体带初始化, 卸载和有效时间管理.
 * EntityMgr0.java
 * @author JiangBangMing
 * 2019年1月8日上午10:11:09
 */
public abstract class EntityMgr0<K, T extends IEntity> extends ElementManager0<K, T> {
	/** 加载创建玩家数据 **/
	protected abstract T create(K key);

	/** 保存, 根据index和mod实现部分储存. **/
	protected boolean save(int index, int mod) {
		return false;
	}

	/** 保存, 根据index和mod实现部分储存. **/
	protected static <T extends IEntity> void forech(EntityMgr0<Long, T> mgr, int index, int mod, MapUtils.Foreach<Long, T> foreach) {
		mod = Math.max(mod, 1);
		index = index % mod;
		// 遍历玩家列表
		Iterator<Map.Entry<Long, T>> iter = mgr.elements.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Long, T> entry = iter.next();
			// 根据取模筛选出此次保存的玩家
			Long idObj = entry.getKey();
			final long id = (idObj != null) ? idObj : 0L;
			if (id % mod != index) {
				continue;
			}

			// 保存玩家玩家数据, 如果玩家保存失败, 继续保存其他玩家.
			T entity = entry.getValue();
			try {
				foreach.action(idObj, entity);
			} catch (Exception e) {
				Log.error("遍历数据错误, key:" + id + " entity=" + entity, e);
			}
		}
	}

	/** 保存, 根据index和mod实现部分储存.保存完毕后清除房间 **/
	protected static <T extends IEntity> boolean save(final EntityMgr0<Long, T> mgr, int index, int mod) {
		forech(mgr, index, mod, new MapUtils.Foreach<Long, T>() {
			@Override
			public void action(Long id, T entity) {
				// 保存成功才能卸载
				boolean s = entity.save();
				if (!s) {
					return; // 保存失败, 不检测删除.
				}
				// 判断能否卸载.
				if (!entity.isAlive()) {
					mgr.remove(id, entity);
				}
			}
		});
		return true;
	}

	/** 清理需要移除的数据 **/
	protected static <T extends IEntity> boolean clean(final EntityMgr0<Long, T> mgr) {
		// 遍历玩家列表
		Iterator<Map.Entry<Long, T>> iter = mgr.elements.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Long, T> entry = iter.next();
			// 根据取模筛选出此次保存的玩家
			Long idObj = entry.getKey();
			final long id = (idObj != null) ? idObj : 0L;
			// 遍历检测清除
			T entity = entry.getValue();
			try {
				// 判断能否卸载.
				if (!entity.isAlive()) {
					mgr.remove(id, entity);
				}
			} catch (Exception e) {
				Log.error("遍历数据错误, key:" + id + " entity=" + entity, e);
			}
		}
		return true;
	}

	@Override
	protected void onGet(K key, T data) {
		data.updateActiveTime();
	}

	@Override
	protected void onCreate(K key, T data) {
		data.updateActiveTime();
	}

	@Override
	protected void onRemove(K key, T data) {
		data.save();
	}

	@Override
	protected T getFromCache(K key) {
		return getFromCache(key, true);
	}

	/**
	 * 从缓存中获取数据 <br>
	 * 
	 * @param updateActive
	 *            是否更新时间
	 **/
	protected T getFromCache(K key, boolean updateActive) {
		T entity = elements.get(key);
		if (entity != null) {
			if (updateActive) {
				entity.updateActiveTime();
			}
		}
		return entity;
	}

}
