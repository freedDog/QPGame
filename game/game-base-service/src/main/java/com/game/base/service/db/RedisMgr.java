package com.game.base.service.db;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.game.base.service.server.App;
import com.game.framework.component.log.Log;
import com.game.framework.framework.redis.RedisPool;
import com.game.framework.framework.redis.RedisPool.RedisConfig;
import com.game.framework.framework.serializer.KryoUtils;

import redis.clients.jedis.ShardedJedis;

/**
 * redis管理器
 * 
 */
public class RedisMgr {
	protected static RedisPool pool;

	/** 重载 **/
	public static boolean reload(List<RedisConfig> redisConfigs) {
		// 创建redis连接池
		RedisPool tempPool = new RedisPool();
		if (!tempPool.init(redisConfigs, RedisPool.createDefaultPoolConfig())) {
			return false;
		}
		pool = tempPool;

		try {
			App app = App.getInstance();
			if (app != null) {
				// 创建成功, 进行测试.
				ShardedJedis jedis = pool.getJedis();
				jedis.set(app.getAppName(), app.getConfig().toString());
				jedis.close();
			}
		} catch (Exception e) {
			Log.error("连接redis错误!" + redisConfigs, e);
			return false;
		}

		return true;
	}

	/** 获取个redis客户端 **/
	public static ShardedJedis getJedis() {
		return (pool != null) ? pool.getJedis() : null;
	}

	public static void shutdown() {
		if (pool != null) {
			pool.shutdown();
			pool = null;
		}
	}

	// /** 常规设置 **/
	// public static String get(String key) {
	// try {
	// // 创建成功, 进行测试.
	// ShardedJedis jedis = pool.getJedis();
	// return jedis.get(key);
	// } catch (Exception e) {
	// Log.error("redis获取值失败", e);
	// return null;
	// }
	// }

	/** 读取数据 **/
	public static <K, T> T get(IRedisHandler<K, T> handler, K key) {
		// 先检测绑定机器人模块
		ShardedJedis jedis = null;
		try {
			// 转化数据
			byte[] keyBytes = handler.toKeyByte(key);

			// 获取redis
			jedis = getJedis();

			// 写入数据
			byte[] valueBytes = jedis.get(keyBytes);
			if (valueBytes == null) {
				return null;
			}
			return handler.toObject(valueBytes, handler.getDataClass());
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}

	/** 设置数据 **/
	public static <K, T> boolean setnx(IRedisHandler<K, T> handler, K key, T value) {
		// 先检测绑定机器人模块
		ShardedJedis jedis = null;
		try {
			// 转化数据
			byte[] keyBytes = handler.toKeyByte(key);
			byte[] valueBytes = (value != null) ? handler.toByte(value, handler.getDataClass()) : null;

			// 获取redis
			jedis = getJedis();

			// 写入数据
			long r = 0;
			if (valueBytes == null) {
				jedis.del(keyBytes);
			} else {
				r = jedis.setnx(keyBytes, valueBytes);
			}
			return (r == 1);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}
	
	/** 设置hash*/
	public static <K,T> boolean setHash(String key,K hKey,T vlu){
		// 先检测绑定机器人模块
		ShardedJedis jedis = null;
		try {
			// 转化数据
			byte[] keyBytes = KryoUtils.toByte(key);
			byte[] hkeys = KryoUtils.toByte(hKey);
			byte[] valueBytes = KryoUtils.toByte(vlu);

			// 获取redis
			jedis = getJedis();
			// 写入数据
			long r = jedis.hset(keyBytes, hkeys, valueBytes);
			if (valueBytes == null) {
				jedis.del(keyBytes);
			} else {
				r = jedis.setnx(keyBytes, valueBytes);
			}
			return (r == 1);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}
	/**设置数值*/
	public static <T> boolean set(String key,T vlu){
		// 先检测绑定机器人模块
		ShardedJedis jedis = null;
		try {
			// 转化数据
			byte[] keyBytes = key.getBytes("utf-8");
			byte[] valueBytes = KryoUtils.toByte(vlu);
			// 获取redis
			jedis = getJedis();
			// 写入数据
			return "OK".equalsIgnoreCase(jedis.set(keyBytes, valueBytes));
		} catch (UnsupportedEncodingException e) {
			Log.error("出错",e);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
		return false;
	}
	
	/**设置数值*/
	public static <T> T get(String key,Class<T> clz){
		// 先检测绑定机器人模块
		ShardedJedis jedis = null;
		try {
			// 转化数据
			byte[] keyBytes = key.getBytes("utf-8");
			// 获取redis
			jedis = getJedis();
			
			byte[] valBytes = jedis.get(keyBytes);
			
			// 写入数据
			return KryoUtils.toObject(valBytes,clz);
		} catch (UnsupportedEncodingException e) {
			Log.error("出错",e);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
		return null;
	}
	
	/** 处理接口 **/
	public interface IRedisHandler<K, T> {
		/** 获取转化类 **/
		Class<T> getDataClass();

		/** 转化key **/
		K toKey(byte[] kdata);

		/** 转化key **/
		byte[] toKeyByte(K key);

		/** 转化成二进制流 **/
		byte[] toByte(Object obj, Class<?> clazz);

		/** 转成对象 **/
		T toObject(byte[] data, Class<T> clazz);
	}

	/**
	 * 处理接口<br>
	 * 使用Kryo加密
	 * **/
	public abstract static class RedisHandler<K, T> implements IRedisHandler<K, T> {

		@Override
		public byte[] toByte(Object obj, Class<?> clazz) {
			return KryoUtils.toByte(obj, clazz);
		}

		@Override
		public T toObject(byte[] data, Class<T> clazz) {
			return KryoUtils.toObject(data, clazz);
		}

	}

	/** key-value **/
	public static class KeyValueHandler<V> extends RedisHandler<String, V> {
		protected Class<V> clazz;

		public KeyValueHandler(Class<V> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Class<V> getDataClass() {
			return clazz;
		}

		@Override
		public String toKey(byte[] kdata) {
			String kstr = new String(kdata);
			return kstr;
		}

		@Override
		public byte[] toKeyByte(String key) {
			return key.getBytes();
		}
	}

}
