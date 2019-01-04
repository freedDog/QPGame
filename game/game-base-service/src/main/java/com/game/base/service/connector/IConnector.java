package com.game.base.service.connector;

import com.game.proto.msg.RpMessage;

/**
 * 连接器接口<br>
 * 用于实现最基础的消息发送
 * IConnector.java
 * @author JiangBangMing
 * 2019年1月4日下午5:08:28
 */
public interface IConnector {
	/** 发送消息给玩家客户端 */
	void sendPacket(short code, RpMessage rpMsg);

	/** 发送多语言文本消息 **/
	void sendLanguageText(short type, Object key, Object... params);

	/** 发送多语言文本消息 **/
	void sendLanguageText(Object key, Object... params);

	/** 发送文本消息 **/
	void sendText(short type, String text);

	/** 发送文本消息 **/
	void sendText(String text);

	/** 发送错误消息 **/
	void sendLanguageError(short code, Object key, Object... params);

	/** 发送错误消息 **/
	void sendError(short code, String msg);

}
