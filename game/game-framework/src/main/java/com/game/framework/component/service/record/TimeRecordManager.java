package com.game.framework.component.service.record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.framework.utils.FileUtils;
import com.game.framework.utils.StringUtils;



/**
 * <ul>
 * 时间记录管理器
 * <li>用于记录每个actionID执行的时间</li>
 * <li>关闭服务器或者间隔时间后自动保存到本地记录</li>
 * </ul>
 * TimeRecordManager.java
 * @author JiangBangMing
 * 2019年1月3日下午1:50:41
 */
public class TimeRecordManager<K> {
	private final ConcurrentMap<K, TimeRecord<K>> map;
	private final Comparator<TimeRecord<K>> comparator;

	public TimeRecordManager() {
		map = new ConcurrentHashMap<>();

		// 输出排序器
		comparator = new Comparator<TimeRecord<K>>() {
			@Override
			public int compare(TimeRecord<K> o1, TimeRecord<K> o2) {
				return -Integer.compare(o1.getUserAverageTime(), o2.getUserAverageTime());
			}
		};
	}

	/**
	 * 增加使用时间记录
	 * 
	 * @param actionId
	 * @param useTime
	 */
	public void addTime(K id, int useTime) {
		addTime(id, useTime, null);
	}

	/**
	 * 增加使用时间记录
	 * 
	 * @param actionId
	 * @param useTime
	 */
	public void addTime(K id, int useTime, String tag) {
		TimeRecord<K> record = this.get(id);
		record.add(useTime);
		// 设置备注
		if (!StringUtils.isEmpty(tag)) {
			record.setTag(tag);
		}
	}

	/**
	 * 增加警告记录
	 * 
	 * @param actionId
	 */
	public void addWarn(K id) {
		TimeRecord<K> record = this.get(id);
		record.addWarn();
	}

	/**
	 * 读取统计对象
	 * 
	 * @param actionId
	 * @return
	 */
	protected TimeRecord<K> get(K id) {
		TimeRecord<K> timeRecord = map.get(id);
		// 创建新的统计数据
		if (timeRecord == null) {
			timeRecord = new TimeRecord<K>(id);
			TimeRecord<K> old = map.putIfAbsent(id, timeRecord);
			timeRecord = (old != null) ? old : timeRecord;
		}
		return timeRecord;
	}

	/**
	 * 输出
	 * 
	 * @param clean
	 *            是否清除
	 * @return
	 */
	public String getLogString(boolean clean) {

		// 获取所有记录
		List<TimeRecord<K>> statistics = new ArrayList<>(map.values());
		int ssize = (statistics != null) ? statistics.size() : 0;
		if (ssize > 0) {
			Collections.sort(statistics, comparator);
		}

		// 生成文本
		StringBuilder logStr = new StringBuilder();
		// 写入日期
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		logStr.append(format.format(date) + "\r\n");

		// 读取统计数据
		for (TimeRecord<K> statistic : statistics) {
			logStr.append(this.recordString(statistic) + "\r\n");
		}
		logStr.append("\r\n");

		// 清除
		if (clean) {
			clear();
		}
		return logStr.toString();
	}

	/** 记录输出字符串 **/
	protected String recordString(TimeRecord<K> record) {
		return record.toString();
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * 保存到本地文件中
	 * 
	 * @param path
	 *            文件路径
	 * @param clear
	 *            是否清除
	 * @return 是否成功
	 */
	public boolean saveFile(String path, boolean clear) {
		if (map.isEmpty()) {
			return false; // 空数据
		}

		String logStr = this.getLogString(true);
		boolean result = FileUtils.saveFile(path, logStr, "UTF-8", true);
		System.out.println("records:\n" + logStr);
		return result;
	}

	/**
	 * 运行一次记录
	 * 
	 * @param id
	 * @param runnable
	 * @return 是否执行成功
	 */
	public boolean record(K id, Runnable runnable) {
		// 执行开始
		TimeMeter timeMeter = new TimeMeter();
		timeMeter.start();
		try {
			runnable.run(); // 运行
		} catch (Exception e) {
			e.printStackTrace();
			this.addWarn(id); // 添加错误记录
			return false;
		}
		// 添加时间
		int useTime = timeMeter.end();
		this.addTime(id, useTime, runnable.toString());
		return true;
	}

	private static final TimeRecordManager<Object> instance = new TimeRecordManager<>();

	public static TimeRecordManager<Object> getInstance() {
		return instance;
	}
}
