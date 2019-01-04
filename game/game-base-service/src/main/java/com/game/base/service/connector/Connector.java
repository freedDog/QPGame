package com.game.base.service.connector;

import com.game.base.service.constant.MsgType;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mgr.RouteMgr;
import com.game.framework.utils.collections.ListUtils;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.text.ErrorMsg;
import com.game.proto.rp.text.TextMsg;

/**
 * 连接器<br>
 * 用于发送消息给客户端
 * Connector.java
 * @author JiangBangMing
 * 2019年1月4日下午5:09:07
 */
public abstract class Connector implements IConnector {

	/** 获取玩家Id **/
	public abstract long getPlayerId();

	@Override
	public void sendLanguageText(short type, Object key, Object... params) {
		sendText(type, LanguageSet.get(key, params));
	}

	/** 发送多语言文本消息 **/
	public void sendLanguageText(short[] types, Object key, Object... params) {
		sendText(types, LanguageSet.get(key, params));
	}

	@Override
	public void sendLanguageText(Object key, Object... params) {
		sendLanguageText(MsgType.TIP, key, params);
	}

	@Override
	public void sendText(short type, String text) {
		sendText(new short[] { type }, text);
	}

	/** 发送文本消息 **/
	public void sendText(short[] types, String text) {
		sendPacket(Protocol.C_TEXT, createTextMsg(types, text));
	}

	@Override
	public void sendText(String text) {
		sendText(MsgType.TIP, text);
	}

	@Override
	public void sendLanguageError(short code, Object key, Object... params) {
		sendError(code, LanguageSet.get(key, params));
	}

	@Override
	public void sendError(short code, String msg) {
		sendPacket(Protocol.C_ERROR, creatErrorMsg(code, msg));
	}

	/******************** 静态方法 ********************/

	/** 创建错误消息 **/
	public static ErrorMsg creatErrorMsg(short code, String msg) {
		ErrorMsg errMsg = new ErrorMsg();
		errMsg.setCode(code);
		errMsg.setMsg(msg);
		return errMsg;
	}

	/** 创建文本消息 **/
	public static TextMsg createTextMsg(short type, String text) {
		return createTextMsg(new short[] { type }, text);
	}

	/** 创建文本消息 **/
	public static TextMsg createTextMsg(short[] types, String text) {
		TextMsg textMsg = new TextMsg();
		textMsg.setText(text);
		textMsg.addAllType(ListUtils.asList(types));
		return textMsg;
	}

	/** 发送消息(最底层的消息发送) **/
	public static void sendPacket(long playerId, short code, RpMessage rpMsg) {
		RouteMgr.getInstance().sendPacket(RouteMgr.mailboxRouter, Message.buildMessage(playerId, code, rpMsg));
	}

	/** 发送消息给所有玩家 **/
	public static void sendPacketToAll(short code, RpMessage rpMsg) {
		RouteMgr.getInstance().sendPacketToAll(Message.buildMessage(code, rpMsg));
	}

	/** 创建连接器 **/
	public static Connector create(final long playerId) {
		return new Connector() {
			@Override
			public long getPlayerId() {
				return playerId;
			}

			@Override
			public void sendPacket(short code, RpMessage rpMsg) {
				sendPacket(getPlayerId(), code, rpMsg); // 静态方法发送
			}
		};
	}
}

