package com.game.base.service.mgr;

import java.util.HashMap;
import java.util.Map;

import com.game.base.service.db.RedisMgr;
import com.game.framework.component.log.Log;
import com.game.framework.framework.serializer.KryoUtils;

import redis.clients.jedis.ShardedJedis;

/**
 *  进程共享数据管理器
 * SharedDataMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午3:24:20
 */
public class SharedDataMgr {

	/** 清除所有记录 **/
	protected static void clearAll(String name) {
		byte[] nbytes = name.toString().getBytes();

		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 获取数据
			jedis.del(nbytes);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}

	}

	/** 获取全部数据 **/
	protected static <K, T> Map<K, T> getAll(String name, IRedisHandler<K, T> handler) {
		byte[] nbytes = name.toString().getBytes();

		// 执行处理
		Map<byte[], byte[]> allMap = null;
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 获取数据
			allMap = jedis.hgetAll(nbytes);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}

		// 判断是否为空
		if (allMap == null || allMap.isEmpty()) {
			return null;
		}

		// 转化
		Class<T> clazz = handler.getDataClass();
		Map<K, T> map = new HashMap<>();
		for (Map.Entry<byte[], byte[]> entry : allMap.entrySet()) {
			byte[] kdata = entry.getKey();
			byte[] data = entry.getValue();
			// 转化
			K key0 = handler.toKey(kdata);
			T obj = KryoUtils.toObject(data, clazz);
			map.put(key0, obj);
		}
		return map;
	}

	/** 设置数据 **/
	protected static <K, T> boolean setIfNull(String name, K key, T obj, IRedisHandler<K, T> handler) {
		if (obj == null) {
			Log.error("没有数据", true);
			return false;
		}
		// 生成key数据
		byte[] nbytes = name.toString().getBytes();
		byte[] kbytes = handler.toKeyByte(key);
		int etime = handler.expireTime();
		// 执行处理
		byte[] data = (obj != null) ? handler.toByte(obj, handler.getDataClass()) : null;
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 设置参数
			long ret = jedis.hsetnx(nbytes, kbytes, data);
			boolean r = (ret == 1);
			if (r && etime > 0) {
				jedis.expire(nbytes, etime);
			}
			return r;
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}

	/** 设置数据 **/
	protected static <K, T> boolean set(String name, K key, T obj, IRedisHandler<K, T> handler) {
		byte[] nbytes = name.toString().getBytes();
		byte[] kbytes = handler.toKeyByte(key);
		int etime = handler.expireTime();
		// 执行处理
		byte[] data = (obj != null) ? handler.toByte(obj, handler.getDataClass()) : null;
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			long ret = 0;
			if (data == null) {
				ret = jedis.hdel(nbytes, kbytes);
			} else {
				ret = jedis.hset(nbytes, kbytes, data); // 设置数据
			}
			// 判断结果
			boolean r = (ret == 1);
			if (r && etime > 0) {
				jedis.expire(nbytes, etime);
			}
			return r;
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}

	/** 获取数据 **/
	protected static <K, T> T get(String name, K key, IRedisHandler<K, T> handler) {
		byte[] nbytes = name.toString().getBytes();
		byte[] kbytes = handler.toKeyByte(key);

		ShardedJedis jedis = null;
		byte[] data = null;
		try {
			jedis = RedisMgr.getJedis();
			// 读取数据
			data = jedis.hget(nbytes, kbytes);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}

		// 转化对象
		return handler.toObject(data, handler.getDataClass());
	}

	/** 处理接口 **/
	protected interface IRedisHandler<K, T> {
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

		/** 存活时间(秒) **/
		int expireTime();
	}

	/**
	 * 处理接口<br>
	 * 使用Kryo加密
	 * **/
	protected abstract static class RedisHandler<K, T> implements IRedisHandler<K, T> {

		@Override
		public byte[] toByte(Object obj, Class<?> clazz) {
			return KryoUtils.toByte(obj, clazz);
		}

		@Override
		public T toObject(byte[] data, Class<T> clazz) {
			return KryoUtils.toObject(data, clazz);
		}

		@Override
		public int expireTime() {
			return 0;
		}
	}
}
