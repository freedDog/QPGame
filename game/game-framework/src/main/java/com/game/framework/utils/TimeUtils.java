package com.game.framework.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 * TimeUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午2:38:41
 */
public class TimeUtils {
	/** 默认时间格式: yyyy-MM-dd HH:mm:ss **/
	public static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";
	/** 默认日期格式: yyyy-MM-dd **/
	public static final String dayFormat = "yyyy-MM-dd";
	/** 默认时间格式: yyyy-MM-dd **/
	public static final String timeFormat = "HH:mm:ss";

	/** 毫秒转秒比例(直接把毫秒乘以这个值即可) **/
	public static final double secondRate = 0.001;
	/** 秒转毫秒比例(直接把秒乘以这个值即可) **/
	public static final long millisecondRate = 1000L;
	/** 1秒时间(s) **/
	public static final int oneSecondTime = 1;
	/** 1秒时间(ms) **/
	public static final long oneSecondTimeL = oneSecondTime * millisecondRate;
	public static final int oneMinuteTime = 60;
	public static final long oneMinuteTimeL = oneMinuteTime * millisecondRate;
	public static final int oneHourTime = 60 * oneMinuteTime;
	public static final long oneHourTimeL = oneHourTime * millisecondRate;
	public static final int oneDayTime = oneHourTime * 24;
	public static final long oneDayTimeL = oneDayTime * millisecondRate;
	public static final int oneWeekTime = oneDayTime * 7;
	public static final long oneWeekTimeL = oneWeekTime * millisecondRate;

	/** 离下个小时的时间 **/
	public static long getNextHourDelay() {
		// 计算下次更新时间
		long nowTime = System.currentTimeMillis();
		long nextTime = getHourDate(nowTime).getTime() + oneHourTimeL;
		// System.out.println("hour date: " + TimeUtils.toString(nextTime));
		return nextTime - nowTime;
	}

	/** 下个小时的时间 **/
	public static long getNextHourTime() {
		long nowTime = System.currentTimeMillis();
		long nextTime = getHourDate(nowTime).getTime() + oneHourTimeL;
		return nextTime;
	}

	/** 离下个分钟的时间 **/
	public static long getNextMinDelay() {
		// 计算下次更新时间
		long nowTime = System.currentTimeMillis();
		long nextTime = getMinuteDate(nowTime).getTime() + oneMinuteTimeL;
		return nextTime - nowTime;
	}

	/** 下个分钟的时间 **/
	public static long getNextMinTime() {
		// 计算下次更新时间
		long nowTime = System.currentTimeMillis();
		return getMinuteDate(nowTime).getTime() + oneMinuteTimeL;
	}

	/** 转成基础时间 <br> */
	public static String toShowString(int second) {
		return toShowString(second, "天", "小时", "分钟", "秒");
	}

	/** 转成基础时间 <br> */
	public static String toShowString(int second, String dstr, String hstr, String mstr, String sstr) {
		// 分析时间
		int stime = second;
		// 计算天数
		int day = second / oneDayTime;
		if (day > 0) {
			stime = stime - day * oneDayTime;
		}
		// 计算小时
		int hour = stime / oneHourTime;
		if (hour > 0) {
			stime = stime - hour * oneHourTime;
		}
		// 计算分钟
		int minute = stime / oneMinuteTime;
		if (minute > 0) {
			stime = stime - minute * oneMinuteTime;
		}

		// 拼装时间
		StringBuffer strBuf = new StringBuffer();
		if (day > 0) {
			strBuf.append(day);
			strBuf.append(dstr);
		}
		if (hour > 0) {
			strBuf.append(hour);
			strBuf.append(hstr);
		}
		if (minute > 0) {
			strBuf.append(minute);
			strBuf.append(mstr);
		}
		if (stime > 0 || (day <= 0 && hour <= 0 && minute <= 0)) {
			strBuf.append(stime);
			strBuf.append(sstr);
		}
		return strBuf.toString();
	}

	/** 获取时间截(int, 精确到s) **/
	public static int getCurrentTime() {
		long nowTimeL = System.currentTimeMillis();
		int nowTime = (int) (nowTimeL * 0.001);
		return nowTime;
	}

	/** 获取时间截(int, 精确到s) **/
	public static int time(long timeL) {
		return (int) (timeL * 0.001);
	}

	/** 获取时间截(int, 精确到s) **/
	public static int time(Date date) {
		long timeL = (date != null) ? date.getTime() : 0L;
		return time(timeL);
	}

	/** 获取这个时间当天某点的时间 **/
	public static long getDayTime(Date source, int hour, int min, int second) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(source);
		cale.set(Calendar.HOUR_OF_DAY, hour);
		cale.set(Calendar.MINUTE, min);
		cale.set(Calendar.SECOND, second);
		cale.set(Calendar.MILLISECOND, 0);
		return cale.getTimeInMillis();
	}

	/**
	 * 获取对应周时间
	 * 
	 * @param timeL
	 *            标准时间, 通常是当前时间(用于确定是哪个周)
	 * @param day
	 *            周几, 周日为1, 周一为2 如此.
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getWeekTime(long timeL, int day, int hour, int minute, int second) {
		// 计算这个星期的天数
		Calendar cale = Calendar.getInstance();
		cale.setTime(new Date(timeL)); // 设置当前时间
		cale.set(Calendar.HOUR_OF_DAY, hour);
		cale.set(Calendar.MINUTE, minute);
		cale.set(Calendar.SECOND, second);
		cale.set(Calendar.MILLISECOND, 0);
		cale.set(Calendar.DAY_OF_WEEK, day);
		// cale.setFirstDayOfWeek(Calendar.MONDAY);
		return cale.getTimeInMillis();
	}

	public static long getMonthTime(long timeL, int day, int hour, int minute, int second) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(new Date(timeL)); // 设置当前时间
		cale.setFirstDayOfWeek(Calendar.MONDAY);
		cale.set(Calendar.DAY_OF_MONTH, day);
		cale.set(Calendar.HOUR_OF_DAY, hour);
		cale.set(Calendar.MINUTE, minute);
		cale.set(Calendar.SECOND, second);
		cale.set(Calendar.MILLISECOND, 0);
		return cale.getTimeInMillis();
	}

	/** 获取时间对应的小时时间点(0分0秒) **/
	public static Date getHourDate(long timeL) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(timeL)); // 设置当前时间
		// cal.set(Calendar.HOUR, cal.get(Calendar.HOUR));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// System.out.println("hour date: " +
		// TimeUtils.toString(cal.getTime()));
		return cal.getTime();
	}

	/** 获取当前时间 **/
	public static Date getMinuteDate(long timeL) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(timeL)); // 设置当前时间
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/** 把时间转到当日的某一点 **/
	public static long getDayTime(long timeL, int hour) {
		return getDayTime(timeL, hour, 0, 0);
	}

	/** 把时间转到当日的某一点 **/
	public static long getDayTime(long timeL, int hour, int minute, int second) {
		return getDayTime(new Date(timeL), hour, minute, second);
	}

	/** 把时间控制到当日的0点 **/
	public static long getDayTime(long timeL) {
		return getDayTime(timeL, 0);
	}

	/** 获取今天0点的时间 **/
	public static long getTodayTime() {
		return getDayTime(System.currentTimeMillis(), 0);
	}

	public static int getTodayTimeBySecond() {
		long todayTimeL = getDayTime(System.currentTimeMillis(), 0);
		return (int) (todayTimeL * 0.001);
	}

	/** 输出时间文本 **/
	public static String toString(long time, String format) {
		DateFormat format0 = new SimpleDateFormat(format);
		Date date = new Date(time);
		return format0.format(date);
	}

	/** 输出时间文本 **/
	public static String toString(int time, String format) {
		return toString((long) (time * 1000L), format);
	}

	/** 输出时间文本(yyyy-MM-dd HH:mm:ss) **/
	public static String toString(int time) {
		return toString((long) (time * 1000L), defaultFormat);
	}

	/** 输出时间文本(yyyy-MM-dd HH:mm:ss) **/
	public static String toString(long time) {
		return toString(time, defaultFormat);
	}

	/**
	 * 转化成日期整数<br>
	 * 例如:2016-01-01 -> 20160101<br>
	 **/
	public static int toTimeInt(long time) {
		String dayStr = TimeUtils.toString(time, "yyyyMMdd");
		Integer day0 = Integer.valueOf(dayStr);
		int day = (day0 != null) ? day0 : 0;
		return day;
	}

	/**
	 * 转化成日期整数<br>
	 * 例如:2016-01-01 -> 20160101<br>
	 **/
	public static int toTimeInt(int time) {
		return toTimeInt(time * 1000L);
	}

	/** 从文本中转化成时间(毫秒) 格式: yyyy-MM-dd HH:mm:ss **/
	public static Date toDate(String dateStr) {
		return toDate(dateStr, defaultFormat);
	}

	/** 转化成Date, 如果格式不对解析错误, 返回null.(timeZone: TimeZone.getTimeZone("GMT+8")) **/
	public static Date toDate(String dateStr, String format, TimeZone timeZone) {
		if (dateStr == null || dateStr.length() <= 0) {
			return null;
		}

		// 格式转化器
		DateFormat format0 = new SimpleDateFormat(format);
		if (timeZone != null) {
			format0.setTimeZone(timeZone);
		}

		try {
			return format0.parse(dateStr);
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return null;
	}

	/** 转化成Date, 如果格式不对解析错误, 返回null. **/
	public static Date toDate(String dateStr, String format) {
		return toDate(dateStr, format, null);
	}

	/** 从文本中转化成时间段 **/
	public static long toTime(String dateStr, String format) {
		DateFormat format0 = new SimpleDateFormat(format);
		try {
			Date date = format0.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/** 从文本中转化成时间(毫秒) 格式: yyyy-MM-dd HH:mm:ss **/
	public static long toTime(String dateStr) {
		return toTime(dateStr, defaultFormat);
	}

	/** 从文本中转化成时间(秒) **/
	public static int toTimeBySecond(String dateStr, String format) {
		long timeL = toTime(dateStr, format);
		int time = (int) (timeL * 0.001);
		return time;
	}

	/** 获取当天时间字符串 **/
	public static String toTodayString() {
		return toString(new Date(), dayFormat);
	}

	/** 获取时间字符串, 格式为yyyy-MM-dd HH:mm:dd */
	public static String toString(Date date, String format) {
		if (date == null) {
			return "null";
		}
		DateFormat format0 = new SimpleDateFormat(format);
		return format0.format(date);
	}

	/** 获取时间字符串, 格式为yyyy-MM-dd HH:mm:dd */
	public static String toString(Date date) {
		return toString(date, defaultFormat);
	}

	/**
	 * 检测等待时间
	 * 
	 * @param nowTime
	 *            当前时间
	 * @param prevTime
	 *            开始时间
	 * @param waiteTime
	 *            等待时间
	 * @return 是否已经达到时间了
	 */
	public static boolean checkWaiteTime(long nowTime, long prevTime, long waiteTime) {
		// long nowTime = System.currentTimeMillis();
		long dt = nowTime - prevTime;
		return dt >= waiteTime;
	}

	/**
	 * 获取时间差
	 * 
	 * @param prevTime
	 * @return
	 */
	public static long getIntervalTime(long prevTime) {
		long nowTime = System.currentTimeMillis();
		return nowTime - prevTime;
	}

	/** 是否是同个小时 **/
	public static boolean isSameHour(long nowTime, long prevTime) {
		nowTime = nowTime / oneHourTimeL;
		prevTime = prevTime / oneHourTimeL;
		return nowTime == prevTime;
	}

	/** 是否是同一分钟 **/
	public static boolean isSameMinute(long nowTime, long prevTime) {
		nowTime = nowTime / oneMinuteTimeL;
		prevTime = prevTime / oneMinuteTimeL;
		return nowTime == prevTime;
	}

	/** 是否是同一天 **/
	public static boolean isSameDay(long nowTime, long prevTime) {
		nowTime = nowTime / oneDayTimeL;
		prevTime = prevTime / oneDayTimeL;
		return nowTime == prevTime;
	}

	public static boolean isSameSecond(long nowTime, long prevTime) {
		nowTime = nowTime / oneSecondTimeL;
		prevTime = prevTime / oneSecondTimeL;
		return nowTime == prevTime;
	}

	/** 是否是同一天 **/
	public static boolean isSameDay(Date source, Date target) {
		if (source == null || target == null) {
			return false;
		}
		// 目标时间
		Calendar targetCale = Calendar.getInstance();
		targetCale.setTime(target);
		targetCale.set(Calendar.HOUR_OF_DAY, 0);
		targetCale.set(Calendar.MINUTE, 0);
		targetCale.set(Calendar.SECOND, 0);
		targetCale.set(Calendar.MILLISECOND, 0);
		long ttime = targetCale.getTimeInMillis();
		// 源时间
		Calendar sourceCale = Calendar.getInstance();
		sourceCale.setTime(source);
		sourceCale.set(Calendar.HOUR_OF_DAY, 0);
		sourceCale.set(Calendar.MINUTE, 0);
		sourceCale.set(Calendar.SECOND, 0);
		sourceCale.set(Calendar.MILLISECOND, 0);
		long stime = sourceCale.getTimeInMillis();
		return stime == ttime;
	}

	/** 检测触发时间 **/
	public static boolean checkActionTime(long prevTime, long nowTime, long actionTime) {
		return prevTime < actionTime && actionTime <= nowTime;
	}

	/** 检测间隔触发 **/
	public static boolean checkLoopTime(long prevTime, long nowTime, int intervalTime) {
		int[] vs = checkLoopTime(prevTime, nowTime, 0L, intervalTime, 0);
		return vs != null;

	}

	/**
	 * 检测循环事件(精确计算出触发区间)<br>
	 * TriggerEventMgr.checkExec
	 * 
	 * @param startTime
	 *            事件开始时间
	 * @param intervalTime
	 *            事件间隔
	 * @param resetCount
	 *            事件重复次数, 0为永久
	 * @return 触发区间[N, N+1], null为无触发, 时间可通过startTime+N*intervalTime
	 */
	public static int[] checkLoopTime(long prevTime, long nowTime, long startTime, int intervalTime, int resetCount) {
		// 检测是否开始
		if (nowTime < startTime) {
			return null; // 尚未开始
		}
		// 控制开始时间, 避免过多执行.
		prevTime = Math.max(prevTime, startTime);

		// 检测最长时间限制
		if (resetCount != 0) {
			long endTime = startTime + ((long) resetCount * intervalTime);
			if (prevTime > endTime) {
				return null; // 上次计算的时间已经超过最大时间, 无需计算了
			}
			// 控制检测时间(避免操作了还继续)
			nowTime = Math.min(nowTime, endTime);
		}
		intervalTime = Math.max(intervalTime, 1);

		// 计算各自对应的间隔时间
		int startIndex = (int) Math.ceil((prevTime - startTime) / (double) intervalTime);
		int endIndex = (int) Math.ceil((nowTime - startTime) / (double) intervalTime);
		int count = endIndex - startIndex;
		// Log.debug(startIndex + " -> " + endIndex + " = " + count);
		if (count <= 0) {
			return null; // 无除触发
		}
		return new int[] { startIndex, endIndex };
	}

	// /** 获取当前时间是当年第几周 **/
	// public static int getWeekOfYear(long time)
	// {
	// Calendar cale = Calendar.getInstance();
	// cale.setTime(new Date(time));
	// return cale.get(Calendar.WEEK_OF_YEAR); // 时间是当月第几周
	// }
	//
	// /** 获取当前时间周几, 国际标准, 周日才是1, 记住啊. **/
	// public static int getWeek(long time)
	// {
	// Calendar cale = Calendar.getInstance();
	// cale.setTime(new Date(time));
	// return cale.get(Calendar.DAY_OF_WEEK);
	// }
	//
	// /** 获取时间是几号 **/
	// public static int getDayOfMonth(long time)
	// {
	// Calendar cale = Calendar.getInstance();
	// cale.setTime(new Date(time));
	// return cale.get(Calendar.DAY_OF_MONTH);
	// }
}
