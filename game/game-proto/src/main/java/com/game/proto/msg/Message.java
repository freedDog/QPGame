package com.game.proto.msg;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.game.framework.component.log.Log;

/**
 * 服务器消息对象<br>
 * serverId与crossId进行区分, 现在服务器进行跨服间的消息直传,同时也支持多个跨服负载均衡,<br>
 * 所以一个跨服消息除了知道是哪个服传过来的也要知道哪个跨服传过来的.
 * Message.java
 * @author JiangBangMing
 * 2019年1月4日下午2:58:49
 */
public class Message {
	private short code; // 指令id
	private Object data; // 数据体
	// 以下数据是额外数据, 并不写入传输过程.
	private long playerId; // 玩家Id
	private long connectId; // 连接Id

	protected Message() {

	}

	protected Message(short code) {
		this.code = code;
	}

	protected Message(short code, Object data) {
		this(code);
		this.data = data;
	}

	public short getCode() {
		return code;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public void setObject(Object obj) {
		this.data = obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject() {
		return (T) data;
	}

	@SuppressWarnings("unchecked")
	public <T extends RpMessage> T getMessage(Class<T> clazz) {
		if (data == null) {
			return null;
		}
		// 判断类型
		if (clazz.isInstance(data)) {
			return (T) data; // 直接进行类型转换
		} else if (data instanceof byte[]) {
			// 解析数据
			try {
				Method method = clazz.getMethod("deserialize", new Class[] { byte[].class });
				Object obj = method.invoke(null, data);
				return (T) obj;
			} catch (Exception e) {
				Log.error("消息解析错误:" + clazz, e);
			}
		} else {
			Log.error("未知消息类型解析: data=" + data.getClass() + " " + data);
		}

		return null;
	}

	public byte[] getByteArray() {
		if (data instanceof byte[]) {
			return (byte[]) data;
		}

		return null;
	}

	public static Message buildMessage(short code) {
		return new Message(code);
	}

	public static Message buildMessage(short code, Object data) {
		return new Message(code, data);
	}

	public static Message buildMessage(long playerId, short code, Object data) {
		Message message = buildMessage(code, data);
		message.setPlayerId(playerId);
		return message;
	}

	/** 简化消息 **/
	public String toSimpleString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("Message[playerId=");
		strBdr.append(playerId);
		strBdr.append(", code=");
		strBdr.append(code);
		strBdr.append("]");
		return strBdr.toString();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("Message[playerId=");
		strBdr.append(playerId);
		strBdr.append(", code=");
		strBdr.append(code);
		strBdr.append(", data(");
		Class<?> dataClass = (data != null) ? data.getClass() : null;
		strBdr.append((dataClass != null) ? dataClass.getSimpleName() : "null");
		strBdr.append(")=[");

		// 数据字符串
		String dataStr = null;
		if (data != null) {
			if (byte[].class.isInstance(data)) {
				byte[] bs = (byte[]) data;
				dataStr = Arrays.toString(bs);
			} else {
				dataStr = data.toString();
			}
		} else {
			dataStr = "null";
		}
		dataStr = dataStr.replaceAll("\n", " ");

		strBdr.append(dataStr);
		strBdr.append("]]");

		return strBdr.toString();
	}

	public long getConnectId() {
		return connectId;
	}

	public void setConnectId(long connectId) {
		this.connectId = connectId;
	}

}
