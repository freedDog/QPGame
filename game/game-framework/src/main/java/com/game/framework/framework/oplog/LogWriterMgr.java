package com.game.framework.framework.oplog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 运营日志输出管理器 LogWriterMgr.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:05:04
 */
public class LogWriterMgr {
	protected Map<LogWriter.ILogType, LogWriter> writers;
	protected String logPath; // 日志输出文件夹路径
	protected String fileFormat; // 文件名称格式

	/** 初始化运营日志输出管理器 **/
	protected boolean init(String logPath, String fileFormat, LogWriter.ILogType[] types) {
		// 转化成绝对路径
		File file = new File(logPath);
		logPath = file.getAbsolutePath();
		System.out.println("游戏运营日志路径: " + logPath);

		// 设置参数
		this.logPath = logPath;
		this.fileFormat = fileFormat;
		writers = new HashMap<>();

		// 初始化写入器
		long nowTime = System.currentTimeMillis();
		for (LogWriter.ILogType type : types) {
			// 日志写入
			LogWriter writer = createWriter(type);
			writers.put(type, writer);
			writer.check(nowTime); // 初始化
		}
		// // 绑定定时器
		// int dt = ConfigMgr.isDebug() ? 1000 : 60 * 1000;
		// TimeMgr.register(new ActionTimer("oplog", dt)
		// {
		// @Override
		// protected void execute(int runCount)
		// {
		// LogWriterMgr.this.update();
		// }
		// });
		return true;
	}

	/** 停止所有写入器(全部写入) **/
	public void stop() {
		for (LogWriter writer : writers.values()) {
			writer.flushCache();
		}
	}

	/** 更新写入所有写入器 **/
	public void update() {
		long nowTime = System.currentTimeMillis();
		for (LogWriter writer : writers.values()) {
			writer.flushCache(); // 写入内容
			writer.check(nowTime); // 检测切割
		}
	}

	/** 获取写入器 **/
	public LogWriter getWriter(LogWriter.ILogType logType) {
		return writers.get(logType);
	}

	/** 创建写入器 **/
	protected LogWriter createWriter(LogWriter.ILogType logType) {
		// 生成文件列表
		String namePrefix = String.format(fileFormat, logType.getName());
		String folderPath = logPath + File.separator + "log_" + logType.getName();
		return new LogWriter(folderPath, namePrefix, logType);
	}

}
