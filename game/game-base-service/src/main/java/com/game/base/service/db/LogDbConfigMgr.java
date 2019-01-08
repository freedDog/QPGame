package com.game.base.service.db;

/**
 * LogDB路由表<br>
 * 用于管理更新路由表内容<br>
 * LogDbConfigMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:45:39
 */
public class LogDbConfigMgr extends DBConfigMgr {

	/************************ 静态句柄 *****************************/

	protected static LogDbConfigMgr instance = new LogDbConfigMgr();

	public static LogDbConfigMgr getLogInstance() {
		return instance;
	}
}
