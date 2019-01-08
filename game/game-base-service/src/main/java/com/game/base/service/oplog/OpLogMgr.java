package com.game.base.service.oplog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.utils.ReflectUtils;
import com.game.framework.utils.ThreadUtils;

/**
 * 日志管理器
 * OpLogMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午3:35:59
 */
public class OpLogMgr {
	public final static OpLogMgr instance = new OpLogMgr();

	protected ConcurrentMap<Class<?>, LogWriter<?>> map;

	public OpLogMgr() {
		map = new ConcurrentHashMap<Class<?>, LogWriter<?>>();

//		create(ProductLogDAO.class, ProductLogInfo.class);
//		create(MatchLogDAO.class, MatchLogInfo.class);
//		create(RevenueLogDAO.class, RevenueLogInfo.class);
//		create(VipLogDAO.class, VipLogInfo.class);
//		create(ContestLogDAO.class, ContestLogInfo.class);
//		create(ExchangeLogDAO.class, ExchangeLogInfo.class);
//		create(GoodsLogDAO.class, GoodsLogInfo.class);
//		create(PlayerMatchLogDAO.class, PlayerMatchLogInfo.class);
//		create(ActivityLogDAO.class, ActivityLogInfo.class);
//		create(RoomLogDAO.class, RoomLogInfo.class);
//		create(SnatchLogDAO.class, SnatchLogInfo.class);
//		create(RechargeLogDAO.class, RechargeLogInfo.class);
//		create(PayLogDAO.class, PayLogInfo.class);
//		create(ProxyLogDAO.class, ProxyLogInfo.class);
//		create(MahjongProbabilityLogDAO.class, MahjongProbabilityLogInfo.class);
//		create(ZJHProbabilityLogDAO.class, ZJHProbabilityLogInfo.class);
//		create(BGFProbabilityLogDAO.class, BGFProbabilityLogInfo.class);
//		create(DaXuanProbabilityLogDAO.class, DaXuanProbabilityLogInfo.class);
//		create(LandlordProbabilityLogDAO.class, LandlordProbabilityLogInfo.class);
//		create(GameResultRoomLogDAO.class, GameResultRoomLogInfo.class);
//		create(GameResultRoomPlayerLogDAO.class, GameResultRoomPlayerLogInfo.class); 
//		create(SafeBoxLogDAO.class, SafeBoxLogInfo.class);
//		create(ProxyOrderLogDAO.class, ProxyOrderLogInfo.class);
	}

	/** 写入日志 **/
	public static void write(Object info) {
		if (info == null) {
			Log.error("不能写入空日志!", true);
			return;
		}
		// 获取写入器
		@SuppressWarnings("unchecked")
		LogWriter<Object> writer = (LogWriter<Object>) instance.get(info.getClass());
		if (writer == null) {
			Log.error("获取不到对应日志写入器!" + info.getClass(), true);
			return;
		}
		writer.write(info);
	}

	/** 获取对应的日志写入器 **/
	@SuppressWarnings("unchecked")
	public <I> LogWriter<I> get(Class<I> icls) {
		LogWriter<?> writer = map.get(icls);
		return (LogWriter<I>) writer;

	}

	/** 将Info与对应的DAO写入ConcurrentHashmap表 **/
	private void create(Class<?> dcls, Class<?> icls) {
		LogWriter<?> writer = (LogWriter<?>) ReflectUtils.createInstance(LogWriter.class, true, dcls, icls);
		map.putIfAbsent(icls, writer);
	}

	/** 保存日志 **/
	public void save() {
		if (map.isEmpty()) {
			return;
		}
		// 遍历保存
		for (LogWriter<?> t : map.values()) {
			t.save();
		}
	}

	/** 多线程保存数据 **/
	public void saveByThread() {
		// 检测数据
		if (map.isEmpty()) {
			return;
		}

		// 计数器
		final AtomicInteger counter = new AtomicInteger();

		// 遍历保存
		for (final LogWriter<?> t : map.values()) {
			counter.incrementAndGet();
			// 提交释放任务
			ServiceMgr.execute(new Runnable() {
				@Override
				public void run() {
					// 执行保存
					t.save();
					// 删除完成
					counter.decrementAndGet();
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
			ThreadUtils.sleep(100);
			// 输出信息
			// Log.debug("save all log last: " + count);
		}
	}

}
