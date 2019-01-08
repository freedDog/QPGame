package com.game.framework.utils.struct.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.game.framework.utils.StringUtils;
import com.game.framework.utils.TimeUtils;
import com.game.framework.utils.struct.result.Result;

/**
 * 时间范围<br>
 * 检测当前某个时间范围.<br>
 * startTime的时间如果在1天内, 默认为当天时间判断
 * TimeSlot.java
 * @author JiangBangMing
 * 2019年1月8日上午11:56:26
 */
public class TimeSlot {
	protected long startTime;
	protected long endTime;

	public TimeSlot(int[] startTime, int[] endTime) {
		this(toTime(startTime), toTime(endTime));
	}

	public TimeSlot(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		// 判断是否是当天时间判断, 如果是当天时间模式, 允许结束时间比开始时间大(相当于第二天), 例如: 7:00-2:00
		boolean isDayTime = (startTime < TimeUtils.oneDayTimeL);
		if (!isDayTime && startTime > endTime) {
			throw new RuntimeException("起始时间比结束时间大, 永远无法匹配成功! [" + toTimeString(startTime) + ", " + toTimeString(endTime) + "]");
		}
	}

	/** 小于0代表没到时间, 等于0代表刚好, 大于0代表过了时间, 单位为s **/
	public TimeSlotResult compare() {
		return compare(new Date());
	}

	/** 小于0代表没到时间, 等于0代表刚好, 大于0代表过了时间, 单位为s **/
	public TimeSlotResult compare(Date nowData) {
		return compare(nowData.getTime());
	}

	/** 小于0代表没到时间, 等于0代表刚好, 大于0代表过了时间, 单位为s **/
	public TimeSlotResult compare(long time) {
		// 当前记录的是不是一天内的时间(小于1天的时间), 否则代表记录的是确切的一个时间段.
		boolean isDayTime = (startTime < TimeUtils.oneDayTimeL);
		if (isDayTime) {
			return compareDay(time);
		}
		return compareNormal(time);
	}

	/** 当日时间比较 **/
	private TimeSlotResult compareDay(long time) {
		// 计算偏移时间
		long offset = TimeUtils.getDayTime(new Date(time), 0, 0, 0);
		long checkTime = time - offset; // 把时间改成当前时间

		// 检测结束时间, 判断结束时间是否是第二天, 是否超到第二天计算
		long endTime = this.endTime;
		if (endTime < startTime) {
			// 判断是否在结束时间之前
			long oed = checkTime - endTime;
			if (oed < 0) {
				return TimeSlotResult.success(0, -oed);
			}
			endTime += TimeUtils.oneDayTimeL; // 把统计的结束时间定为第二天
		}

		// 检测离开始时间还有多久
		long sd = checkTime - startTime;
		if (sd < 0) {
			return TimeSlotResult.error("尚未到时间", sd, -1);
		}

		// 检测离结束时间
		long ed = endTime - checkTime;
		if (ed <= 0) {
			return TimeSlotResult.error("已经超过时间", ed, -1);
		}
		return TimeSlotResult.success(0, ed);
	}

	/** 指定日期比较 **/
	private TimeSlotResult compareNormal(long time) {
		// 检测离开始时间还有多久
		long sd = time - startTime;
		if (sd < 0) {
			return TimeSlotResult.error("尚未到时间", sd, -1);
		}
		// 检测离结束时间
		long ed = endTime - time;
		if (ed < 0) {
			return TimeSlotResult.error("已经超过时间", ed, -1);
		}
		return TimeSlotResult.success(0, ed);
	}

	/** 把时分秒数组变成秒数 **/
	private static long toTime(int[] time) {
		int h = 0;
		int m = 0;
		int s = 0;
		if (time.length >= 1) {
			h = time[0];
		}
		if (time.length >= 2) {
			m = time[1];
		}
		if (time.length >= 3) {
			s = time[2];
		}
		return (h * 60 * 60 + m * 60 + s) * 1000L;
	}

	/** 把时间转化成符合的格式显示 **/
	public static String toTimeString(long time) {
		// 当天时间内, 不帶日期.
		if (time < TimeUtils.oneDayTimeL) {
			DateFormat format0 = new SimpleDateFormat(TimeUtils.timeFormat);
			format0.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
			// Date date = new Date(time * 1000L);
			Date date = new Date(time);
			return format0.format(date);
		}
		// 带日期输出
		return TimeUtils.toString(time);
	}

	@Override
	public String toString() {
		return "TimeSlot [" + toTimeString(startTime) + ", " + toTimeString(endTime) + "]";
	}

	/**
	 * 拆分成TimeSlot, 格式: h:m,h:m 至少要包含时和分<br>
	 * 例如:<br>
	 * 1. 7:00, 2:00<br>
	 * 2. 6:00, 23:00<br>
	 * **/
	public static TimeSlot toTimeSlot(String str) {
		TimeSlot[] timeSlots = toTimeSlots(str);
		int tsize = (timeSlots != null) ? timeSlots.length : 0;
		return (tsize > 0) ? timeSlots[0] : null;
	}

	/**
	 * 拆分成TimeSlot, 格式: h:m,h:m|h:m,h:m, 至少要包含时和分 例如:<br>
	 * 例如:<br>
	 * 1. 1:00, 3:00|4:00, 5:00<br>
	 * **/
	public static TimeSlot[] toTimeSlots(String str) {
		int[][][] obj = StringUtils.splitToInt3(str, "\\|", ",", ":");
		if (obj == null || obj.length <= 0 || obj[0].length <= 0) {
			return null; // 解析错误判断
		}
		try {
			// 遍历生成
			TimeSlot[] ts = new TimeSlot[obj.length];
			for (int i = 0; i < obj.length; i++) {
				int[][] times = obj[i];
				if (times == null || times.length != 2) {
					return null; // 时间段, 必须要2个.
				}
				// 每个至少包括时, 分
				if (times[0] == null || times[0].length < 2) {
					return null;
				}
				// 每个至少包括时, 分
				if (times[1] == null || times[1].length < 2) {
					return null;
				}
				ts[i] = new TimeSlot(times[0], times[1]);
			}
			return ts;
		} catch (Exception e) {
			// Log.error("解析时间错误! " + str + " " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/** 时间戳结果 **/
	public static class TimeSlotResult extends Result {
		protected long offset; // 偏移时间, 离时间范围的时间
		protected long last; // 剩余时间

		/** 偏移时间, 用于判断是否到时间范围内, 小于0代表没到时间, 等于0代表刚好, 大于0代表过了时间, 单位为ms **/
		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		/** 剩余时间, 用于判断离结束时间还剩多久, -1为还没到时间 **/
		public long getLast() {
			return last;
		}

		public void setLast(long last) {
			this.last = last;
		}

		/** 成功 **/
		public static TimeSlotResult error(String msg, long offset, long last) {
			TimeSlotResult result = error(TimeSlotResult.class, msg);
			result.setLast(last);
			result.setOffset(offset);
			return result;
		}

		/** 成功 **/
		public static TimeSlotResult success(long offset, long last) {
			TimeSlotResult result = create(TimeSlotResult.class, SUCCESS, null);
			result.setLast(last);
			result.setOffset(offset);
			return result;
		}

		@Override
		public String toString() {
			return "TimeSlotResult [offset=" + offset + ", last=" + last + ", code=" + code + ", msg=" + msg + "]";
		}
	}

}

