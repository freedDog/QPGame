package com.game.base.service.db;

/**
 * Dao管理器<br>
 * DaoMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:20:55
 */
public abstract class DaoMgr {
	protected static DaoMgr instance = null;

	public static DaoMgr getInstance() {
		return instance;
	}

	/** 获取对应的DAO **/
	public <T> T getDao(final Class<T> daoClass) {
		return getDao(1, daoClass);
	}

	/** 获取区服对应的DAO **/
	protected abstract <T> T getDao(int gameZoneId, final Class<T> daoClass);

}
