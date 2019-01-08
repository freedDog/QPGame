package com.game.base.service.db;

/**
 * 日志数据库连接池管理器<br>
 * LogDbPoolMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:20:21
 */
public class LogDbPoolMgr extends DBPoolMgr {
	private static LogDbPoolMgr instance = new LogDbPoolMgr();

	public static LogDbPoolMgr getInstance() {
		return instance;
	}

}
