package com.game.base.utils;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DateBuilder;

import com.game.base.service.log.LogConfig;
import com.game.base.service.server.App;
import com.game.framework.component.log.Log;

public class RoomLogger {

	public static final int ALL = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;
	public static final int OFF = 100;

	private ThreadLocal<DateFormat> formater = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}
	};
	private ThreadLocal<DateFormat> logTimeFormater = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private PrintStream ps;
	private int level = ALL;
	private boolean console = true;
	private String identifier;

	private boolean daySplit;
	private long splitTime;

	static {
		File logPath = new File(LogConfig.getLogPath() + File.separator + "room");
		logPath.mkdir();
	}

	public RoomLogger(long roomId, int gameType) {
		identifier = getRoomTypeName(gameType) + "_" + roomId + "_" + formater.get().format(new Date()) + ".log";
		String roomLogId = LogConfig.getLogPath() + File.separator + "room" + File.separator + identifier;
		daySplit = false;
		splitTime = 0;
		this.level = LogConfig.getRoomLoggerLevel();
		this.console = LogConfig.isRoomLoggerConsole();
		try {
			ps = new PrintStream(roomLogId, "utf-8");
		} catch (Exception e) {
			Log.error("创建房间日志" + roomLogId + "出错:", e);
		}
		info("Log文件创建成功！GamerServerId:" + App.getInstance().getAppId());
	}

	public void close() {
		ps.close();
	}

	public void setDaySplit(boolean daySplit){
		this.daySplit = daySplit;
		this.splitTime = DateBuilder.tomorrowAt(0, 0, 0).getTime();
		//this.splitTime = DateBuilder.evenMinuteDateAfterNow().getTime();
	}

	private String getRoomTypeName(int gameType){
		String[] names = {"", "LANDLORDS", "MAHJOIN", "BULLGOLDFIGHT", "SLOTS","GBMAHJONG", "BJL", "ZHAJINHUA","TEXASHOLDEM", "LEIZHOU","MJ_CHENGDU","YIBINMJ","DAXUAN", "LHD"};
		return names[gameType];
	}
	
	private synchronized void splitByDay(long now){
		if(!daySplit || now < splitTime){
			return;
		}

		String prefix = identifier.substring(0, identifier.lastIndexOf("_"));

		identifier = prefix + "_" + formater.get().format(new Date()) + ".log";
		String roomLogId = LogConfig.getLogPath() + File.separator + "room" + File.separator + identifier;

		PrintStream newPs = null;
		try {
			newPs = new PrintStream(roomLogId, "utf-8");
		} catch (Exception e) {
			error("分割房间日志" + roomLogId + "出错:", e);
		}

		// 重置下次分割时间
		this.splitTime += 24 * 60 * 60 * 1000;
		// this.splitTime += 60 * 1000;

		if(newPs != null) {
			ps.close();
			ps = newPs;
			info("Log文件分割成功！GamerServerId:" + App.getInstance().getAppId());
		}
	}

	private void log(String level, String msg, Throwable t) {
		long now = System.currentTimeMillis();
		if(daySplit && now >= splitTime){
			splitByDay(now);
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		sb.append(level);
		sb.append("] ");
		sb.append(logTimeFormater.get().format(new Date(now)));
		sb.append("[");
		sb.append(Thread.currentThread().getName());
		sb.append("] ");
		sb.append(getStackTraceString(4, level.equals("ERROR") ? 0 : 1));
		sb.append("\r\n");
		sb.append(msg);
		if (console) {
			System.out.println(sb.toString());
		}
		ps.println(sb.toString());
		
//
//		ps.print(logTimeFormater.get().format(new Date(now)));
//
//		// 线程信息
//		ps.print("[");
//		ps.print(Thread.currentThread().getName());
//		ps.print("]");
//
//		// Level
//		ps.print("[");
//		ps.print(level);
//		ps.print("] ");
//
//		ps.println(msg);
//		if (t != null) {
//			t.printStackTrace(ps);
//		}

		// 将Log输出到System.out
		if (level.equals("ERROR")) {
			Log.error(identifier + " 文件出现异常:");
		}
		if (t != null) {
			Log.error(msg, t);
		}
		//logger.info(msg);
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

	public void debug(String msg) {
		if (level <= DEBUG)
			log("DEBUG", msg, null);
	}

	public void debug(String msg, Throwable t) {
		if (level <= DEBUG)
			log("DEBUG", msg, t);
	}

	public void info(String msg) {
		if (level <= INFO)
			log("INFO", msg, null);
	}

	public void info(String msg, Throwable t) {
		if (level <= INFO)
			log("INFO", msg, t);
	}
	
	public void warn(String msg) {
		if (level <= WARN)
			log("WARN", msg, null);
	}
	
	public void warn(String msg, Throwable t) {
		if (level <= WARN)
			log("WARN", msg, t);
	}

	public void error(String msg) {
		if (level <= ERROR)
			log("ERROR", msg, null);
	}

	public void error(String msg, Throwable t) {
		if (level <= ERROR)
			log("ERROR", msg, t);
	}
}
