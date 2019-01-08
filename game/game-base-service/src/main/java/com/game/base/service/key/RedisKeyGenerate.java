package com.game.base.service.key;

import java.util.concurrent.atomic.AtomicLong;

import com.game.base.service.db.RedisMgr;
import com.game.framework.component.log.Log;
import com.game.framework.utils.FileUtil;

import redis.clients.jedis.ShardedJedis;

/**
 *  redis key
 * RedisKeyGenerate.java
 * @author JiangBangMing
 * 2019年1月8日上午10:38:32
 */
public class RedisKeyGenerate extends DefaultKeyGenerate{
	private Object lock = new Object();
	private static final String SEQUENCE_KEY = "redis_key_generate_hset";
	
	protected AtomicLong keylong = new AtomicLong();
	
	private String redis_key;
	
	private String path;
	public RedisKeyGenerate(int group,String path) {
		super(group);
		this.redis_key = SEQUENCE_KEY + "-" + this.group;
		this.path = path;
	}

	@Override
	public String stringKey() {
		synchronized (lock) {
			long key =  keylong.incrementAndGet();
			save(false);
			return group + String.format(formats, key);
		}
	}
	
	/**
	 * 基值
	 * @return
	 */
	public boolean initBasic(){
		ShardedJedis jedis = null;
		try {
			// 获取redis
			jedis = RedisMgr.getJedis();
			if (jedis == null) {
				return false;
			}
			String longStr = jedis.get(redis_key);
			if(longStr == null || longStr.isEmpty()){
				longStr = "0";
			}
			String txt = FileUtil.readFileText(this.path, redis_key);
			if(txt == null || txt.isEmpty()){
				txt = "0";
			}
			long redisVal = Long.valueOf(longStr);
			long txtVal = Long.valueOf(txt);
			if(txtVal > redisVal){
				keylong.set(txtVal);
			}else{
				keylong.set(redisVal);
			}
			return true;
		} catch (Exception e) {
			Log.error("RoomCardGameResult操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	@Override
	public void save(boolean doubleSave) {
		ShardedJedis jedis = null;
		try {
			// 获取redis
			jedis = RedisMgr.getJedis();
			if(doubleSave){
				if (jedis != null) {
					jedis.set(redis_key, Long.toString(keylong.get()));
				}
				FileUtil.saveFile(this.path, redis_key, Long.toString(keylong.get()));
			}else{
				if (jedis != null) {
					jedis.set(redis_key, Long.toString(keylong.get()));
				}else{
					FileUtil.saveFile(this.path, redis_key, Long.toString(keylong.get()));
				}
			}
		} catch (Exception e) {
			Log.error("RoomCardGameResult操作错误!", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
