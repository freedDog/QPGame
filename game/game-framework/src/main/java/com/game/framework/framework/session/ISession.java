package com.game.framework.framework.session;

/**
 * Session接口
 * ISession.java
 * @author JiangBangMing
 * 2019年1月3日下午2:42:01
 */
public interface ISession {

	void stop();

	boolean isConnect();

	boolean write(Object msg);
}
