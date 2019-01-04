package com.game.framework.framework.rpc.msg;

import java.util.Arrays;

import com.game.framework.framework.rpc.RpcUtils;


/**
 * rpc消息体(用于传输) RpcMsg.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:17:31
 */
public class RpcMsg {
	protected short code; // 消息码
	protected byte[] data; // 数据

	protected int mode; // 消息模式

	protected byte[] from; // 来源
	protected byte[] to; // 用于转发

	public static RpcMsg create(short code, Object data) {
		byte[] data0 = (data != null) ? RpcUtils.toByte(data, data.getClass()) : null;
		return create(code, data0);
	}

	/** 获取来源 **/
	public <T> T getFrom(Class<T> clazz) {
		if (from == null) {
			return null;
		}
		return RpcUtils.toObject(from, clazz);
	}

	public <T> void setFrom(Object from, Class<T> clazz) {
		this.from = (from != null) ? RpcUtils.toByte(from, clazz) : null;
	}

	public <T> void setData(Object data, Class<T> clazz) {
		this.data = (data != null) ? RpcUtils.toByte(data, clazz) : null;
	}

	public <T> void setTo(Object to, Class<T> clazz) {
		this.to = (to != null) ? RpcUtils.toByte(to, clazz) : null;
	}

	/** 获取去处 **/
	public <T> T getTo(Class<T> clazz) {
		if (to == null) {
			return null;
		}
		return RpcUtils.toObject(to, clazz);
	}

	/** 获取数据 **/
	public <T> T getData(Class<T> clazz) {
		if (data == null) {
			return null;
		}
		return RpcUtils.toObject(data, clazz);
	}

	/** 创建消息 **/
	public static RpcMsg create(short code, byte[] data) {
		RpcMsg rpcMsg = new RpcMsg();
		rpcMsg.code = code;
		rpcMsg.data = data;
		return rpcMsg;
	}

	public short getCode() {
		return code;
	}

	public byte[] getData() {
		return data;
	}

	public byte[] getTo() {
		return to;
	}

	@Override
	public String toString() {
		String dataStr = (data == null) ? "0[]" : data.length + Arrays.toString(data);
		return "RpcMsg [code=" + code + ", data=" + dataStr + "]";
	}

	public byte[] getFrom() {
		return from;
	}

	public void setFrom(byte[] from) {
		this.from = from;
	}

	public void setCode(short code) {
		this.code = code;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setTo(byte[] to) {
		this.to = to;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
