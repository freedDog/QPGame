package com.game.base.service.log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.game.framework.component.log.Log;
/**
 * Log输出配置<br>
 * 基于log4j.
 * LogConfig.java
 * @author JiangBangMing
 * 2019年1月4日下午3:33:43
 */
public final class LogConfig extends Log {
	// TODO:LZGLZG后续考虑升级到log4j2.6以上的版本，因为说是GC减少了很多对比2.6版本之前的。直接考虑使用最新的版本即可。
	// Log4j 2.x版本的异步日志据说性能很不错，依赖了Disruptor这个据说吊炸天的库，后续可以考虑试试，对比目前内部的线程池那块的实现
	private static final String msgSep = "\r\n";
	private static Logger logger;
	private static String logPath; // 日志路径
	private static LogType level; // 输出等级
	private static int roomLoggerlevel; // 房间日志输出级别
	private static boolean roomLoggerConsole; // 房间日志控制台输出
	
	/** 使用默认配置 **/
	public static void init(String logPath0, String prefix, LogType level0, int roomLoglevel, boolean roomLogConsole) {
		init(logPath0, prefix, level0);
		roomLoggerlevel = roomLoglevel;
		roomLoggerConsole = roomLogConsole;
	}

	/** 使用默认配置 **/
	public static void init(String logPath0, String prefix, LogType level0) {
		// 创建路径
		File file = new File(logPath0);
		if (!file.exists()) {
			file.mkdirs();
		}
		logPath = file.getAbsolutePath();
		level = level0;
		System.out.println("运行日志输出路径: " + logPath);

		// 创建默认配置, 程序中统一以SGLOG输出.
		Properties prop = new Properties();
		prop.setProperty("log4j.rootLogger", "");
		prop.setProperty("log4j.addivity.org.apache", "true");
		// 处理等级
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("ALL");
		strBdr.append(",CONSOLE");
		initLogProps(prop, "CONSOLE", logPath, prefix);
		if (level.ordinal() <= LogType.DEBUG.ordinal()) {
			strBdr.append(",DEBUG");
			initLogProps(prop, "DEBUG", logPath, prefix);
		}
		if (level.ordinal() <= LogType.INFO.ordinal()) {
			strBdr.append(",INFO");
			initLogProps(prop, "INFO", logPath, prefix);
		}
		if (level.ordinal() <= LogType.WARN.ordinal()) {
			strBdr.append(",WARN");
			initLogProps(prop, "WARN", logPath, prefix);
		}
		if (level.ordinal() <= LogType.ERROR.ordinal()) {
			strBdr.append(",ERROR");
			initLogProps(prop, "ERROR", logPath, prefix);
		}
		prop.setProperty("log4j.logger.SGLOG", strBdr.toString());
		PropertyConfigurator.configure(prop);

		// 创建logger
		logger = Logger.getLogger("SGLOG");

		// 路径输出
		showLogPath();

		// 设置log接口
		Log.setHandler(new ILogHandler() {
			@Override
			public void writeLog(LogType type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
				// 判断logger是否支持
				String logStr = type.name();
				String str = LogConfig.toString(logStr, time, threadName, funcName, stackTraceStr, msg);
				if (logger == null) {
					// 控制台输出
					ConsoleLogHandler.writeConsoleLog(type, time, threadName, funcName, stackTraceStr, msg);
					return;
				}

				// logger输出
				if (type == LogType.DEBUG) {
					logger.debug(str);
				} else if (type == LogType.INFO) {
					logger.info(str);
				} else if (type == LogType.WARN) {
					logger.warn(str);
				} else if (type == LogType.ERROR) {
					logger.error(str);
				} else {
					System.err.println("错误日志类型! " + type);
				}
			}

			@Override
			public boolean filtr(LogType type) {
				return type.ordinal() >= level.ordinal();
			}
		});
	}

	/** 初始化单个日志配置 **/
	private static void initLogProps(Properties prop, String catg, String logPath, String prefix) {
		String key = "log4j.appender." + catg;
		if (catg.equals("CONSOLE")) {
			// 控制台输出
			prop.setProperty(key + ".Target", "System.out");
			prop.setProperty(key, "org.apache.log4j.ConsoleAppender");
		} else {
			// 日志输出
			String logfile = logPath + File.separator + prefix + catg.toLowerCase() + ".log";
			prop.setProperty(key, "org.apache.log4j.DailyRollingFileAppender");
			prop.setProperty(key + ".File", logfile);
			prop.setProperty(key + ".DatePattern", "'.'yyyyMMdd");
			prop.setProperty(key + ".Append", "true");
		}
		// 设置参数
		// prop.setProperty("log4j.logger.info", catg);
		prop.setProperty(key + ".Threshold", catg);
		prop.setProperty(key + ".layout", "org.apache.log4j.PatternLayout");

		// String logFormat = "%-5p:%d-%c-%-2r[%t]%x%n%m %n"; // 常规数据
		String logFormat = "%x%n%m"; // 直接输出
		// String logFormat = "%-5p %d[%t]%x%m%n"; // log输出
		prop.setProperty(key + ".layout.ConversionPattern", logFormat);

	}

	/** 日期格式多线程支持. **/
	private static final ThreadLocal<DateFormat> format = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	/** 完整字符串输出 **/
	protected static String toString(String type, Date time, String threadName, String funcName, String stackTraceStr, Object msg) {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(type);
		strBdr.append(" ");
		strBdr.append(format.get().format(time));
		strBdr.append("[");
		strBdr.append(threadName);
		strBdr.append("] ");
		strBdr.append(funcName);
		strBdr.append(msgSep);
		strBdr.append(msg);
		if (stackTraceStr != null && stackTraceStr.length() > 0) {
			strBdr.append(msgSep);
			strBdr.append(stackTraceStr);
		}
		return strBdr.toString();
	}

	/** 输出当前日志路径(只支持log4j) **/
	private static void showLogPath() {
		StringBuilder strBdr = new StringBuilder();
		// 遍历所有日志输出路径
		Enumeration<?> enumeration = Logger.getLogger("SGLOG").getAllAppenders();
		while (enumeration.hasMoreElements()) {
			// 筛选文件输出log
			Object obj = enumeration.nextElement();
			if (obj instanceof FileAppender) {
				FileAppender fd = (FileAppender) obj;
				String filePath = fd.getFile();
				File file = new File(filePath);
				String path = file.getAbsolutePath();

				// 只输出一个就够了
				strBdr.append(path);
				break;
			}
		}
		System.out.println("配置输出路径: " + strBdr.toString());
	}

	/** 获取日志输出路径 **/
	public static String getLogPath() {
		return logPath;
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param funcLevel
	 *            函数层级
	 * @param t
	 *            错误对象
	 **/
	public static void writeLog(LogType type, Object msg, int funcLevel, Throwable t) {
		Log.writeLog(type, msg, funcLevel, t);
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param funcLevel
	 *            函数层级
	 * @param showStackTrace
	 *            是否显示堆栈
	 **/
	public static void writeLog(LogType type, Object msg, int funcLevel, boolean showStackTrace) {
		Log.writeLog(type, msg, funcLevel, showStackTrace);
	}
	
	
	public static int getRoomLoggerLevel() {
		return roomLoggerlevel;
	}

	public static boolean isRoomLoggerConsole() {
		return roomLoggerConsole;
	}
	
	
}
