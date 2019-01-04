package com.game.framework.framework.session;

/**
 * Session接口
 * Session.java
 * @author JiangBangMing
 * 2019年1月3日下午2:43:02
 */
public abstract class Session implements ISession {

	@Override
	public void stop() {
	}

	@Override
	public boolean isConnect() {
		return true;
	}

}
