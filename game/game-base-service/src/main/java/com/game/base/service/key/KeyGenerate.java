package com.game.base.service.key;

/**
 * key策略
 * KeyGenerate.java
 * @author JiangBangMing
 * 2019年1月8日上午10:36:47
 */
public interface KeyGenerate {
	
	public long longKey();
	
	public String stringKey();
	
	public void save(boolean doubleSave);
}

