package com.game.base.service.mailbox;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.db.RedisMgr;
import com.game.framework.component.log.Log;
import com.game.framework.framework.serializer.KryoUtils;

import redis.clients.jedis.ShardedJedis;

/**
 * mailBox处理接口<br>
 * 使用hget和hset
 * 
 */
public class MailBoxHashHandler implements MailBoxMgr.IMailBoxHandler {

	@Override
	public void expire(int type, long id, Object key, int liveTime) {
		// 执行处理
		ShardedJedis jedis = null;
		try {
			// 获取redis
			jedis = RedisMgr.getJedis();
			if (jedis == null) {
				return;
			}
			// 设置数据
			byte[] keydata = getKey(type, id);
			jedis.expire(keydata, liveTime);
		} catch (Exception e) {
			Log.error("mailbox操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean setIfNull(int type, long id, Object key, ServerConfig config, int expireTime) {
		// 执行处理
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 设置数据
			byte[] keydata = getKey(type, id);
			long r = jedis.hsetnx(keydata, key.toString().getBytes(), toByte(config, ServerConfig.class));
			boolean result = (r == 1); // 1为成功
			if (!result) {
				return false;
			}
			// 更新时间
			if (expireTime > 0) {
				jedis.expire(keydata, expireTime);
			}
			// Log.debug("mailbox setnx: type=" + type + " id=" + id + " key=" + key + " config=" + config);
			return true;
		} catch (Exception e) {
			Log.error("mailbox操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	@Override
	public boolean set(int type, long id, Object key, ServerConfig config, int expireTime) {
		// 执行处理
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 设置数据
			byte[] keydata = getKey(type, id);
			jedis.hset(keydata, key.toString().getBytes(), toByte(config, ServerConfig.class));
			// 设置数据不判断成功与否, 因为如果数据相同, 设置是返回false的.
			// 更新时间
			if (expireTime > 0) {
				jedis.expire(keydata, expireTime);
			}
			// Log.debug("mailbox set: type=" + type + " id=" + id + " key=" + key + " config=" + config);
			return true;
		} catch (Exception e) {
			Log.error("mailbox操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	@Override
	public ServerConfig get(int type, long id, Object key, int expireTime) {
		ShardedJedis jedis = null;
		try {
			// 获取redis
			jedis = RedisMgr.getJedis();
			if (jedis == null) {
				return null;
			}
			// 读取数据
			byte[] keydata = getKey(type, id);
			byte[] data = jedis.hget(keydata, key.toString().getBytes());
			// 转化数据
			ServerConfig config = toObject(data, ServerConfig.class);
			if (config == null) {
				return null;
			}
			// 更新时间
			if (expireTime > 0) {
				jedis.expire(keydata, expireTime);
			}
			return config;
		} catch (Exception e) {
			Log.error("mailbox操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	@Override
	public boolean remove(int type, long id, Object key, int expireTime) {
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 读取数据
			byte[] keydata = getKey(type, id);
			long r = jedis.hdel(keydata, key.toString().getBytes());
			boolean result = (r == 1);
			if (!result) {
				return false;
			}
			// 更新时间
			if (expireTime > 0) {
				jedis.expire(keydata, expireTime);
			}
			// Log.debug("mailbox del: type=" + type + " id=" + id + " key=" + key);
			return true;
		} catch (Exception e) {
			Log.error("mailbox操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	/** 转成数据流 **/
	public static byte[] toByte(Object obj, Class<?> clazz) {
		return (obj != null) ? KryoUtils.toByte(obj, clazz) : null;
	}

	/** 转成对象 **/
	public static <T> T toObject(byte[] data, Class<T> clazz) {
		return (data != null) ? KryoUtils.toObject(data, clazz) : null;
	}

	/** 保留字符串格式 **/
	protected static byte[] getKey(int type, long id) {
		return String.format("mb%d_%d", type, id).getBytes();
	}
}
