package com.game.framework.utils.struct.result;

/**
 * 执行结果接口 
 * IResult.java
 * @author JiangBangMing
 * 2019年1月2日下午5:51:27
 */
public interface IResult {

	public static final int SUCCESS=1;

	public static final int FAIL=0;
	
	public static final int ERROR=0;
	
	/**
	 * 获取code
	 * @return
	 */
	public abstract int getCode();
	
	/**
	 * 获取消息
	 * @return
	 */
	public abstract String getMsg();
	
	/**
	 * 是否成功
	 * @return
	 */
	public abstract boolean isSucceed();
}
