package com.game.framework.framework.session;

/**
 * 连接处理接口
 * ISessionHandler.java
 * @author JiangBangMing
 * 2019年1月3日下午2:42:35
 */
public interface ISessionHandler<T> {

	void onConnect(Session session);

	void onClose(Session session);

	void onRead(Session session, T msg);
}
