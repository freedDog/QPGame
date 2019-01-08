package com.game.base.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.DBConfig;
import com.game.framework.component.log.Log;
import com.game.framework.framework.db.DBDataSource;
import com.game.framework.framework.db.DBPool;
import com.game.framework.jdbc.JadeFactory;
import com.game.framework.utils.collection.entity.EntityMgr0;
import com.game.framework.utils.collection.entity.IEntity;
import com.game.base.service.db.DBPoolMgr.DBPoolImpl;

/**
 * 连接池管理器<br>
 * DBPoolMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午10:05:53
 */
public class DBPoolMgr extends EntityMgr0<DBConfig, DBPoolImpl> {
	private static DBPoolMgr instance = new DBPoolMgr();
	private Map<Integer, DBConfig> configs = new HashMap<>();

	public static DBPoolMgr getInstance() {
		return instance;
	}

	/** 重载数据库连接 **/
	public boolean reload(List<DBConfig> configs) {
		// 判断是否有链接
		int dsize = (configs != null) ? configs.size() : 0;
		if (dsize <= 0) {
			return true;
		}
		// 遍历初始化
		Map<Integer, DBConfig> configMap = new HashMap<>();
		if (!DBConfigMgr.getInstance().initAndCheckConfigs(configs, configMap)) {
			return false;
		}
		// 先关闭全部.
		this.removeAll();
		// 更换数据
		this.configs = configMap;
		return true;
	}

	/** 关闭数据库连接 **/
	public void shutdown() {
		this.removeAll();
	}

	/** 获取区服对应的配置 **/
	private DBConfig getConfig(int gameZoneId) {
		DBConfig config = configs.get(gameZoneId);
		if (config == null) {
			return configs.get(0);
		}
		return config;
	}

	/** 获取取JadeFactory **/
	public JadeFactory getFactory(int gameZoneId) {
		DBPoolImpl pool = (DBPoolImpl) getDBPool(gameZoneId);
		return (pool != null) ? pool.getFactory() : null;
	}

	/** 获取dbPool **/
	public DBPool getDBPool() {
		return getDBPool(1);
	}

	/** 获取gameZoneId对应的dbPool **/
	public DBPool getDBPool(int gameZoneId) {
		if (gameZoneId <= 0) {
			Log.error("不允许直接获取GameZoneId为0的连接! gameZoneId=" + gameZoneId, true);
			return null;
		}
		// 获取对应配置
		DBConfig config = getConfig(gameZoneId);
		if (config == null) {
			Log.error("找不到GameZoneId的数据库连接! gameZoneId=" + gameZoneId, true);
			return null;
		}
		return this.get(config);
	}

	@Override
	protected void onRemove(DBConfig key, DBPoolImpl pool) {
		pool.shutdown(); // 关闭
		Log.debug("数据连接池关闭 " + pool);
	}

	public Map<Integer, DBConfig> getConfigs() {
		return configs;
	}

	@Override
	protected DBPoolImpl create(DBConfig config) {
		return new DBPoolImpl(config);
	}

	/** 数据库pool **/
	class DBPoolImpl extends DBPool implements IEntity {
		private static final long UNLOAD_INTERVAL = 10 * 60 * 1000; // 数据卸载时间
		private long activeTime; // 这个实体数据上使用时间.

		protected final DBConfig config; // 配置
		protected final JadeFactory factory;

		public DBPoolImpl(DBConfig config) {
			this.config = config;
			factory = new GameJadeFactory(new DBDataSource(this));
		}

		/** 通过配置初始化 **/
		protected boolean initByConfig(DBConfig config) {
			// 读取连接线程数
			int thread = Runtime.getRuntime().availableProcessors();

			// 获取数据库参数
			String host = config.getHost();
			int port = config.getPort();
			String dbName = config.getDbName();
			String username = config.getUsername();
			String password = config.getPassword();
			short minConnCount = (short) Math.min(thread, config.getMinConnCount());
			short maxConnCount = (short) Math.min(thread * 2, config.getMaxConnCount());
			// 执行初始化
			return super.init(host, port, dbName, username, password, minConnCount, maxConnCount);
		}

		@Override
		public boolean load() {
			return initByConfig(config);
		}

		@Override
		public void unload() {
		}

		@Override
		public boolean save() {
			return true;
		}

		@Override
		public boolean isAlive() {
			return (System.currentTimeMillis() - activeTime) < UNLOAD_INTERVAL;
		}

		@Override
		public void updateActiveTime() {
			activeTime = System.currentTimeMillis();
		}

		public JadeFactory getFactory() {
			return factory;
		}
	}
}

