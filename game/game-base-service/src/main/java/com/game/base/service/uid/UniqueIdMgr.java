package com.game.base.service.uid;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.base.service.db.DaoMgr;
import com.game.entity.dao.UniqueIdDAO;
import com.game.framework.component.log.Log;
import com.game.framework.utils.ThreadUtils;
import com.game.utils.DataUtils;

/**
 * 唯一ID管理器
 * 
 */
public class UniqueIdMgr {
	protected static final int UNIQUE_ID_ADD = 500; // 一次获取的区间
	protected static ConcurrentMap<Integer, ConcurrentMap<UniqueId, UniqueIdObj>> uniqueIdMap = new ConcurrentHashMap<>();

	/** 获取Id **/
	public static long getUniqueId(UniqueId uniqueId, int gameZoneId) {
		// 获取各个区服的id列表
		ConcurrentMap<UniqueId, UniqueIdObj> uniqueIds = uniqueIdMap.get(gameZoneId);
		if (uniqueIds == null) {
			uniqueIds = new ConcurrentHashMap<>();
			ConcurrentMap<UniqueId, UniqueIdObj> old = uniqueIdMap.putIfAbsent(gameZoneId, uniqueIds);
			uniqueIds = (old != null) ? old : uniqueIds;
		}

		// 检测id是否创建了
		UniqueIdObj uobj = uniqueIds.get(uniqueId);
		if (uobj == null) {
			uobj = new UniqueIdObj(uniqueId, gameZoneId);
			UniqueIdObj old = uniqueIds.putIfAbsent(uniqueId, uobj);
			uobj = (old != null) ? old : uobj;
		}
		return uobj.incrementAndGet();
	}

	/** 获取ID区间 **/
	private static long getCurrentId(UniqueId uniqueId, int gameZoneId) {
		UniqueIdDAO udao = DaoMgr.getInstance().getDao(UniqueIdDAO.class);
		return udao.get(uniqueId.getType(), gameZoneId, (long) UNIQUE_ID_ADD, DataUtils.getGameZoneStartId(uniqueId.getType(), gameZoneId));
	}

	/** 唯一ID对象, 1个对象管理1个数据. **/
	static class UniqueIdObj {
		private UniqueId uniqueId;
		private int gameZoneId;
		private long id; // 当前Id
		private long upperLimit; // 上限

		private UniqueIdObj(UniqueId uniqueId, int gameZoneId) {
			this.uniqueId = uniqueId;
			this.gameZoneId = gameZoneId;
			id = -1;
			update();
		}

		/**
		 * <b>为什么不用原子计数器?</b></br> 计数器增加和查库更新是一段完整的逻辑，无法分割，所以这段既然加了锁，那么直接加1的效率要高于原子计数器<br>
		 * PS:如果你有更好的解决方法，欢迎拿出来一起讨论一下
		 * 
		 * @return
		 */
		public synchronized long incrementAndGet() {
			// 检测初始化是否成功
			if (id < 0) {
				update(); // 更新一次
				if (id < 0) {
					throw new RuntimeException("更新获取唯一Id错误! ");
				}
			}

			// 增长并且返回
			long value = ++id;
			if (value >= upperLimit) {
				update();
			}
			return value;
		}

		/** 申请新区间 **/
		private void update() {
			id = getCurrentId(uniqueId, gameZoneId); // 获取提交新区间
			upperLimit = id + UNIQUE_ID_ADD;
		}
	}

	public static void main(String[] args) throws Exception {
		final ConcurrentMap<Long, Long> keys = new ConcurrentHashMap<>();

		for (int i = 0; i < 10; i++) {
			ThreadUtils.run(new Runnable() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();

					UniqueIdObj uobj = new UniqueIdObj(UniqueId.PLAYER, 3);
					Log.info("start!");
					for (int i = 0; i < 30000; i++) {
						long id = uobj.incrementAndGet();
						Long old = keys.putIfAbsent(id, id);
						if (old != null) {
							Log.error("冲突id=" + id);
						}
					}

					long endTime = System.currentTimeMillis();
					long dt = endTime - startTime;
					Log.info("ok! " + keys.size() + " dt=" + dt);
				}
			});
		}
	}
}