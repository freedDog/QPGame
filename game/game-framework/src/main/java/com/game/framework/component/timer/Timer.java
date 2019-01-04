package com.game.framework.component.timer;

import com.game.framework.component.trigger.TriggerEventMgr;

/**
 * 定时器
 * Timer.java
 * @author JiangBangMing
 * 2019年1月3日下午1:53:37
 */
public class Timer implements ITimer {
	protected int id; // 计时器ID
	protected String name; // 计时器名称
	// 特殊参数(延迟、定时和间隔发送)
	protected long startTime; // 开始时间(毫秒)
	protected int intervalTime; // 间隔时间, 必须大于0(毫秒计算)
	protected int resetCount; // 间隔重置次数, 0 为永久

	/**
	 * @param id
	 *            定时器ID
	 * @param name
	 *            定时器名称
	 * @param startTime
	 *            开始时间(ms)
	 * @param intervalTime
	 *            间隔时间(ms)
	 * @param resetCount
	 *            重复次数(0为永久)
	 */
	public Timer(int id, String name, long startTime, int intervalTime, int resetCount) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.intervalTime = Math.max(intervalTime, 1);
		this.resetCount = resetCount;
	}

	/**
	 * @param id
	 *            定时器ID
	 * @param name
	 *            定时器名称
	 * @param intervalTime
	 *            间隔时间(ms)
	 */
	public Timer(int id, String name, int intervalTime) {
		this(id, name, System.currentTimeMillis(), intervalTime, 0);
	}

	/**
	 * @param id
	 *            定时器ID
	 * 
	 * @param name
	 *            定时器名称
	 * 
	 * @param delay
	 *            延迟时间(ms)
	 * 
	 * @param intervalTime
	 *            间隔时间(ms)
	 * **/
	public Timer(int id, String name, int delay, int intervalTime) {
		this(id, name, System.currentTimeMillis() + delay, intervalTime, 0);
	}

	@Override
	public int check(long prevTimeL, long nowTimeL) {
		return check(prevTimeL, nowTimeL, startTime, intervalTime, (long) resetCount);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * 计算出2段时间内, 定时器激活了多少次.
	 * 
	 * @param prevTime
	 *            上次计算的时间
	 * @param nowTime
	 *            现在计算的时间
	 * @param startTime
	 *            定时器开始时间(毫秒)
	 * @param intervalTime
	 *            间隔时间(ms), 必须大于0.
	 * @param resetCount
	 *            重复次数, 0为无限次
	 * @return
	 */
	protected static int check(long prevTime, long nowTime, long startTime, int intervalTime, int resetCount) {
		return check(prevTime, nowTime, startTime, intervalTime, (long) resetCount);
	}

	/**
	 * 计算出2段时间内, 定时器激活了多少次.
	 * 
	 * @param prevTime
	 *            上次计算时间
	 * @param nowTime
	 *            现在的时间
	 * @param startTime
	 *            定时器开始时间
	 * @param endTime
	 *            定时器结束时间
	 * @param intervalTime
	 *            间隔时间(毫秒)
	 * @return
	 */
	protected static int check(long prevTime, long nowTime, long startTime, long endTime, int intervalTime) {
		long liveTime = endTime - startTime + 1; // 计算定时器存活时间, 加1秒确保最后一次临界值符合条件.
		long resetCount = liveTime / intervalTime; // 重复次数
		return check(prevTime, nowTime, startTime, intervalTime, resetCount);
	}

	protected static int check(long prevTime, long nowTime, long startTime, int intervalTime, long resetCount) {
		// // 检测是否开始
		// if (nowTime < startTime)
		// {
		// return 0; // 尚未开始
		// }
		// prevTime = Math.max(prevTime, startTime);
		//
		// // 检测最长时间限制
		// if (resetCount > 0)
		// {
		// long endTime = startTime + ((long) resetCount * intervalTime);
		// if (prevTime > endTime)
		// {
		// return 0; // 上次计算的时间已经超过最大时间, 无需计算了
		// }
		//
		// // 控制检测时间
		// nowTime = Math.min(nowTime, endTime);
		// }
		//
		// // 计算各自对应的间隔时间
		// intervalTime = Math.max(intervalTime, 1);
		// int prevIndex = (int) (prevTime - startTime) / intervalTime;
		// int nowIndex = (int) (nowTime - startTime) / intervalTime;
		// int count = nowIndex - prevIndex;
		//
		// // 这段时间应该触发了N次
		// return count;

		// 检测时间
		int[] indexs = TriggerEventMgr.checkLoopExec(prevTime, nowTime, startTime, intervalTime, (int) resetCount);
		if (indexs == null || indexs.length < 2) {
			return 0;
		}
		return indexs[1] - indexs[0];
	}

	/** 是否有效 **/
	@Override
	public boolean isAlive() {
		// 判断是否永久
		if (resetCount <= 0) {
			return true;
		}
		long nowTime = System.currentTimeMillis();
		long endTime = startTime + ((long) resetCount * intervalTime);
		return nowTime <= endTime;
	}
}
