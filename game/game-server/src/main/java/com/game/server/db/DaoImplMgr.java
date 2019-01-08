package com.game.server.db;

import com.game.base.service.db.DBPoolMgr;
import com.game.base.service.db.DaoMgr;
import com.game.framework.jdbc.JadeFactory;

/**
 * Dao管理器<br>
 * 用于生成DAO执行数据库操作.
 * DaoImplMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:22:37
 */
public class DaoImplMgr extends DaoMgr {

	public DaoImplMgr() {
		instance = this;
	}

	/** 获取区服对应的DAO **/
	@Override
	public <T> T getDao(int gameZoneId, Class<T> clazz) {
		// 获取区服对应数据库
		JadeFactory factory = DBPoolMgr.getInstance().getFactory(gameZoneId);
		if (factory == null) {
			return null;
		}
		return factory.getDAO(clazz);
	}
	


}
