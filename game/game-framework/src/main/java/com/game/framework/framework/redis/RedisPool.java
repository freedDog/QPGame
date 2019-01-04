package com.game.framework.framework.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis 
 * RedisPool.java
 * @author JiangBangMing
 * 2019年1月3日下午3:11:35
 */
public class RedisPool {
	private ShardedJedisPool pool = null;

	public boolean init(RedisConfig[] redisConfigs, JedisPoolConfig poolConfig) {
		return init(Arrays.asList(redisConfigs), poolConfig);
	}

	public boolean init(List<RedisConfig> redisConfigs, JedisPoolConfig poolConfig) {
		int csize = (redisConfigs != null) ? redisConfigs.size() : 0;
		if (csize <= 0) {
			Log.error("获取redis配置不能为空!");
			return false;
		}
		// 遍历列表
		List<JedisShardInfo> jedis = new ArrayList<>();
		for (RedisConfig config : redisConfigs) {
			JedisShardInfo jedisInfo = new JedisShardInfo(config.getHost(), config.getPort());
			// 设置密钥
			String auth = config.getAuth();
			if (!StringUtils.isEmpty(auth)) {
				jedisInfo.setPassword(auth);
			}
			// 设置超时时间
			jedisInfo.setConnectionTimeout(config.getTimeOut());
			jedisInfo.setSoTimeout(config.getTimeOut());
			// 添加连接
			jedis.add(jedisInfo);
		}
		// 创建连接池
		pool = new ShardedJedisPool(poolConfig, jedis);
		return true;
	}

	/** 获取Jedis实例 */
	public ShardedJedis getJedis() {
		try {
			if (pool != null) {
				return pool.getResource();
			} else {
				return null;
			}
		} catch (Exception e) {
			Log.error("获取redis错误!", e);
			return null;
		}
	}

	public void shutdown() {
		if (!pool.isClosed()) {
			pool.close();
			pool.destroy();
		}
		pool = null;
	}

	/** 创建默认的JedisPoolConfig **/
	public static JedisPoolConfig createDefaultPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		// 可用连接实例的最大数目，默认值为8;如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(1024);

		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
		config.setMaxIdle(200);

		// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(3 * 1000);

		// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);

		return config;
	}

	/** redis配置 **/
	public static class RedisConfig {
		protected String host; // host
		protected int port; // 端口
		protected String auth; // 密码
		protected int timeOut; // 访问超时

		// 空构造函数, 用于反射创建反序列等
		protected RedisConfig() {
		}

		public RedisConfig(String host, int port, String auth) {
			this.host = host;
			this.port = port;
			this.auth = auth;
			timeOut = 10 * 1000;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getAuth() {
			return auth;
		}

		public void setAuth(String auth) {
			this.auth = auth;
		}

		public int getTimeOut() {
			return timeOut;
		}

		public void setTimeOut(int timeOut) {
			this.timeOut = timeOut;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result + port;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RedisConfig other = (RedisConfig) obj;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (port != other.port)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "RedisConfig [host=" + host + ", port=" + port + ", auth=" + auth + ", timeOut=" + timeOut + "]";
		}

	}
}