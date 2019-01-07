package com.game.base.service.mgr;

import com.game.base.service.db.RedisMgr;
import com.game.base.utils.DataUtils;

import redis.clients.jedis.ShardedJedis;

/**
 * 进程共享数值管理器
 * 
 */
public class SharedCounterMgr {

	/** 清除所有记录 **/
	public static void clearAll(String name) {
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

	/** 修改数据 **/
	public static <K> long change(String name, K key, long change) {
		// 执行处理
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			return jedis.hincrBy(name.toString(), key.toString(), change);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}

	/** 获取数据 **/
	public static <K> long get(String name, K key) {
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 读取数据
			String dataStr = jedis.hget(name, key.toString());
			return DataUtils.toLong(dataStr);
		} finally {
			if (jedis != null) {
				jedis.close();
				jedis = null;
			}
		}
	}

}
