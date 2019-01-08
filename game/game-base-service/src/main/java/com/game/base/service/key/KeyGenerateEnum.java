package com.game.base.service.key;

/**
 *  key名字
 * KeyGenerateEnum.java
 * @author JiangBangMing
 * 2019年1月8日上午10:36:17
 */
public enum KeyGenerateEnum {
	/** 默认策略生成的 单服Key 效率高 对于大量生成key 和唯一性要求高的 慎用*/
	Default,
	/** redis 唯一Key 效率底*/
	RedisKey,
	;
	private KeyGenerate key;
	
	public void setKey(KeyGenerate key) {
		this.key = key;
	}
	
	public long keyLong(){
		return key.longKey();
	}
	
	public String keyStr(){
		return key.stringKey();
	}
}
