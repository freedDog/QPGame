package com.game.framework.component.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.game.framework.utils.StringUtils;


/**
 * 日志输出<br>
 * 可通过句柄处理. 准备改成针对对象的处理.
 * Log.java
 * @author JiangBangMing
 * 2019年1月4日上午11:52:44
 */
public class Log {
	protected static ILogHandler handler = new ConsoleLogHandler(LogType.DEBUG);

	public static ILogHandler getHandler() {
		return handler;
	}

	/** 设置处理句柄 **/
	public static void setHandler(ILogHandler handler) {
		if (handler == null) {
			error("不能设置空Log句柄.");
			return;
		}
		Log.handler = handler;
	}

	public static void debug(Object msg) {
		writeLog(LogType.DEBUG, msg, 4, false);
	}

	public static void debug(Object msg, boolean showStackTrace) {
		writeLog(LogType.DEBUG, msg, 4, showStackTrace);
	}

	public static void info(Object msg) {
		writeLog(LogType.INFO, msg, 4, false);
	}

	public static void info(Object msg, boolean showStackTrace) {
		writeLog(LogType.INFO, msg, 4, showStackTrace);
	}

	public static void warn(Object msg) {
		writeLog(LogType.WARN, msg, 4, false);
	}

	public static void warn(Object msg, boolean showStackTrace) {
		writeLog(LogType.WARN, msg, 4, showStackTrace);
	}

	public static void error(Object msg, Throwable t) {
		writeLog(LogType.ERROR, msg, 4, t);
	}

	public static void error(Object msg, boolean showStackTrace) {
		writeLog(LogType.ERROR, msg, 4, showStackTrace);
	}

	public static void error(Object msg) {
		writeLog(LogType.ERROR, msg, 4, false);
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param funcLevel
	 *            函数层级
	 * @param t
	 *            错误对象
	 **/
	protected static void writeLog(LogType type, Object msg, int funcLevel, Throwable t) {
		// 过滤处理
		if (!handler.filtr(type)) {
			return;
		}
		String sts = (t != null) ? StringUtils.getExceptionString(t) : null;
		String fn = getStackTraceString(funcLevel, 1);
		String tn = Thread.currentThread().getName();
		handler.writeLog(type, new Date(), tn, fn, sts, msg);
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param funcLevel
	 *            函数层级
	 * @param showStackTrace
	 *            是否显示堆栈
	 **/
	protected static void writeLog(LogType type, Object msg, int funcLevel, boolean showStackTrace) {
		// 过滤处理
		if (!handler.filtr(type)) {
			return;
		}
		// 整理参数输出
		String sts = (showStackTrace) ? getStackTraceString(4, 0) : null;
		String fn = getStackTraceString(funcLevel, 1);
		String tn = Thread.currentThread().getName();
		handler.writeLog(type, new Date(), tn, fn, sts, msg);
	}

	/**
	 * 获取堆栈信息(输出第几条, 自己控制)
	 * 
	 * @param stackIndex
	 *            跳过几层堆栈
	 * @param depth
	 *            输出几层堆栈, 0为全部.
	 * **/
	protected static String getStackTraceString(int stackIndex, int depth) {
		// 获取堆栈信息
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		if (st == null) {
			return "";
		}
		// 判断范围
		if (stackIndex >= st.length || stackIndex < 0) {
			return "";
		}
		// 第一条肯定是java.lang.Thread.getStackTrace(Thread.java:1552), 因此过滤掉.
		// 第二条就是这个函数
		if (depth == 1) {
			return st[stackIndex].toString();
		}
		depth = (depth != 0) ? depth : st.length - stackIndex;
		// 层级输出
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(st[stackIndex].toString());
		for (int i = 0; i < depth - 1; i++) {
			strBdr.append("\r\n\tat ");
			strBdr.append(st[stackIndex + i + 1].toString());
		}
		return strBdr.toString();
	}

	/** 字符串输出 **/
	protected static String toString(String type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// 字符串
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(type);
		strBdr.append(" ");
		strBdr.append(format.format(time));
		strBdr.append("[");
		strBdr.append(threadName);
		strBdr.append("] ");
		strBdr.append(funcName);
		strBdr.append(":");
		strBdr.append("\r\n");
		strBdr.append(msg);
		if (stackTraceStr != null && stackTraceStr.length() > 0) {
			strBdr.append("\r\n");
			strBdr.append(stackTraceStr);
		}
		return strBdr.toString();
	}

	/** 日志类型 **/
	public enum LogType {
		/** debug运行输出 **/
		DEBUG, //
		/** 正常输出 **/
		INFO, //
		/** 警告输出 **/
		WARN, //
		/** 错误输出, 必须解决的日志. **/
		ERROR
	}

	/** 日志处理接口 **/
	public interface ILogHandler {
		/** 筛选过滤, 返回false为不输出 **/
		boolean filtr(LogType type);

		/** 写入日志 **/
		void writeLog(LogType type, Date time, String threadName, String funcName, String stackTraceStr, Object msg);
	}

	/** 控制台输出接口 **/
	public static class ConsoleLogHandler implements ILogHandler {
		protected LogType level;

		public ConsoleLogHandler(LogType level) {
			this.level = level;
		}

		@Override
		public void writeLog(LogType type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
			// 判断等级
			if (type.ordinal() < level.ordinal()) {
				return;
			}
			// 输出结果
			writeConsoleLog(type, time, threadName, funcName, stackTraceStr, msg);
		}

		/** 控制台输出 **/
		public static void writeConsoleLog(LogType type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
			String logStr = type.name();
			if (type == LogType.ERROR) {
				System.err.println(Log.toString(logStr, time, threadName, funcName, stackTraceStr, msg));
				return;
			}
			System.out.println(Log.toString(logStr, time, threadName, funcName, stackTraceStr, msg));
		}

		@Override
		public boolean filtr(LogType type) {
			return true;
		}
	}

	/** 控制台输出接口(加线程同步)<br> **/
	public static class ConsoleLockLogHandler extends ConsoleLogHandler {

		public ConsoleLockLogHandler(LogType level) {
			super(level);
		}

		@Override
		public void writeLog(LogType type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
			synchronized (this) {
				writeConsoleLog(type, time, threadName, funcName, stackTraceStr, msg);
			}
		}

	}
}
