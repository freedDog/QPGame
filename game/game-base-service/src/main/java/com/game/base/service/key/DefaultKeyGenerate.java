package com.game.base.service.key;

import java.util.UUID;

/**
 * 默认实现
 * 100000 次 8线程测试 几乎几个重复
 * DefaultKeyGenerate.java
 * @author JiangBangMing
 * 2019年1月8日上午10:37:43
 */
public class DefaultKeyGenerate implements KeyGenerate {
	
	/**
	 * 分组
	 */
	protected String group;
	
	protected int formatsLength = 14;
	
	protected String formats = "%014d";
	
	public DefaultKeyGenerate(int group) {
		if(group > 9999){
			throw new RuntimeException("key group < 10000!!!");
		}
		this.group = Integer.toString(group + 1);
		if(this.group.length() > 1){
			formatsLength -= (this.group.length() -1);
			formats = "%0"+formatsLength+"d";
		}
	}

	@Override
	public long longKey() {
		return Long.valueOf(stringKey());
	}

	@Override
	public String stringKey() {
		int code = UUID.randomUUID().toString().hashCode();
		if(code < 0){
			code =-code;
		}
		return group + String.format(formats, code);
	}

	@Override
	public void save(boolean doubleSave) {
		
	}
}