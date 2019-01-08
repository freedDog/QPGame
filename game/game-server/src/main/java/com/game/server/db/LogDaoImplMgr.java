package com.game.server.db;

import com.game.base.service.db.LogDaoMgr;
import com.game.base.service.db.LogDbPoolMgr;
import com.game.framework.jdbc.JadeFactory;

/**
 * Dao管理器<br>
 * 用于生成DAO执行数据库操作.
 * LogDaoImplMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:23:36
 */
public class LogDaoImplMgr extends LogDaoMgr {

	public LogDaoImplMgr() {
		instance = this;
	}

	/** 获取区服对应的DAO **/
	@Override
	public <T> T getDao(int gameZoneId, Class<T> clazz) {
		// 获取区服对应数据库
		JadeFactory factory = LogDbPoolMgr.getInstance().getFactory(gameZoneId);
		if (factory == null) {
			return null;
		}
		return factory.getDAO(clazz);
	}

}