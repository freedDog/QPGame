package com.game.base.service.rpc.handler;

/**
 * 基础服务<br>
 * IBaseService.java
 * 
 * @author JiangBangMing 2019年1月4日下午5:57:00
 */
public interface IBaseService {
	/** 远程关闭服务器 **/
	String shutdown(String key, int waitTime);
}
