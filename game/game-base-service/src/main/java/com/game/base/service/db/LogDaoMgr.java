package com.game.base.service.db;

/**
 *  Dao管理器<br>
 * LogDaoMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:21:20
 */
public abstract class LogDaoMgr extends DaoMgr {
	protected static LogDaoMgr instance = null;

	public static LogDaoMgr getInstance() {
		return instance;
	}

}