package com.game.base.service.mailbox;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.module.ModuleName;

/**
 * mailbox对象<br>
 * 只是基于MailBoxMgr做缓存处理
 * MailBox.java
 * @author JiangBangMing
 * 2019年1月4日下午3:50:29
 */
public abstract class MailBox {
	/** 玩家类型 **/
	public static final int TYPE_PLAYER = 0;
	/** 公会类型 **/
	public static final int TYPE_GUILD = 1;

	/** 网关 **/
	public static final String KEY_GATE = "gate";

	// 缓存处理
	protected int cacheExpireTime = 10 * 1000; // 豪秒
	protected long cacheCheckTime;
	protected ServerConfig gate = null;
	protected ServerConfig core = null;
	protected ConcurrentMap<Object, ServerConfig> caches;

	public MailBox() {
		caches = new ConcurrentHashMap<>();
	}

	/** 缓存获取 **/
	protected ServerConfig getCache(Object key, boolean clean) {
		// 缓存时间处理
		if (clean) {
			cleanCache();
		}
		updateCacheExpireTime();

		// 读取数据
		if (key == KEY_GATE) {
			return gate;
		} else if (key == ModuleName.CORE) {
			return core;
		}
		return caches.get(key);
	}

	/** 缓存设置 **/
	protected void setCache(Object key, ServerConfig config) {
		// 更新缓存时间
		updateCacheExpireTime();

		// 设置数据
		if (key == KEY_GATE) {
			gate = config;
			return;
		} else if (key == ModuleName.CORE) {
			core = config;
			return;
		}
		// 其他设置
		if (config == null) {
			caches.remove(key);
			return;
		}
		caches.put(key, config);
	}

	protected void cleanCache() {
		// 检测清除时间
		long nowTime = System.currentTimeMillis();
		long dt = nowTime - cacheCheckTime;
		if (dt < cacheExpireTime) {
			return;
		}
		// 清除
		gate = null;
		core = null;
		caches.clear();
	}

	/** 更新时间 **/
	protected void updateCacheExpireTime() {
		cacheCheckTime = System.currentTimeMillis();
	}

	/** 获取mailbox 类型 **/
	public abstract int getType();

	/** 获取mailbox指定Id **/
	public abstract long getId();

	/** 设置数据 **/
	public boolean set(Object key, ServerConfig config) {
		// 设置
		if (!MailBoxMgr.set(getType(), getId(), key, config)) {
			return false;
		}
		// 设置缓存
		this.setCache(key, config);
		return true;
	}

	/** 设置数据 **/
	public boolean setIfNull(Object key, ServerConfig config) {
		// 设置
		if (!MailBoxMgr.setIfNull(getType(), getId(), key, config)) {
			return false;
		}
		// 设置缓存
		this.setCache(key, config);
		return true;
	}

	/** 获取数据 **/
	public ServerConfig get(Object key) {
		return get(key, false);
	}

	/** 获取数据 **/
	public ServerConfig get(Object key, boolean cache) {
		ServerConfig config = null;
		// 判断是否走缓存
		if (cache) {
			config = this.getCache(key, true);
			if (config != null) {
				MailBoxMgr.expire(getType(), getId(), key); // 更新到管理器
				return config;
			}
		}
		// 在管理器获取
		config = MailBoxMgr.get(getType(), getId(), key);
		if (config == null) {
			return null;
		}
		// 缓存
		this.setCache(key, config);
		return config;
	}

	/** 删除数据 **/
	public void remove(Object key) {
		// 修改
		if (MailBoxMgr.remove(getType(), getId(), key)) {
			return;
		}
		// 设置缓存
		this.setCache(key, null);
	}

	/************************* 网关 ****************************/

	/** 设置网关 **/
	public boolean setByGate(ServerConfig config) {
		return set(KEY_GATE, config);
	}

	/** 获取网关 **/
	public ServerConfig getByGate() {
		return get(KEY_GATE);
	}

	/** 删除网关绑定 **/
	public void removeByGate() {
		remove(KEY_GATE);
	}

	/************************* 静态网关 ****************************/

	public static boolean setByGate(long playerId, ServerConfig config) {
		return MailBoxMgr.set(TYPE_PLAYER, playerId, KEY_GATE, config);
	}

	public static ServerConfig getByGate(long playerId) {
		return MailBoxMgr.get(TYPE_PLAYER, playerId, KEY_GATE);
	}

	public static void removeByGate(long playerId) {
		MailBoxMgr.remove(TYPE_PLAYER, playerId, KEY_GATE);
	}

	public static boolean setIfNull(long playerId, Object key, ServerConfig config) {
		return MailBoxMgr.setIfNull(TYPE_PLAYER, playerId, key, config);
	}

	public static boolean set(long playerId, Object key, ServerConfig config) {
		return MailBoxMgr.set(TYPE_PLAYER, playerId, key, config);
	}

	/** 获取配置 **/
	public static ServerConfig get(long playerId, Object key) {
		return MailBoxMgr.get(TYPE_PLAYER, playerId, key);
	}

	public static void remove(long playerId, Object key) {
		MailBoxMgr.remove(TYPE_PLAYER, playerId, key);
	}

	/************************* 内嵌类 ****************************/
	public static class MailBoxImpl extends MailBox {
		protected int type;
		protected long id;

		public MailBoxImpl(int type, long id) {
			this.type = type;
			this.id = id;
		}

		@Override
		public int getType() {
			return type;
		}

		@Override
		public long getId() {
			return id;
		}

	}

}
