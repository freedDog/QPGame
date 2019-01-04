package com.game.framework.framework.rpc;

import java.util.Arrays;

import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.framework.framework.rpc.msg.RpcProtocol;


/**
 * rpc回调函数支持(在对方服务器上的支持) RpcCallbackImpl.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:34:36
 */
public class RpcCallbackImpl extends RpcCallback {
	protected RpcChannel channel;
	protected Class<?>[] paramTypes;
	protected short type; // 是否是阻塞的回调
	protected RpcMsg srcMsg; // 产生回调的消息

	protected RpcCallbackImpl(RpcChannel channel, long callbackId, Class<?>[] paramTypes, RpcMsg msg) {
		this.callbackId = callbackId;
		this.channel = channel;
		this.paramTypes = paramTypes;
		this.srcMsg = msg;
	}

	@Override
	public boolean callBack(Object... args) {
		// 验证参数类型
		Class<?>[] paramTypes = (this.paramTypes != null) ? this.paramTypes : RpcUtils.createTypes(args);
		return callBack(paramTypes, args);
	}

	protected boolean callBack(Class<?>[] paramTypes, Object[] args) {
		// 获取设备
		RpcDevice<?> device = (channel != null) ? channel.getDevice() : null;
		if (device == null) {
			Log.error("没找到连接的设备! channel=" + channel, true);
			return false;
		}

		// 验证参数类型
		if (!RpcUtils.checkType(paramTypes, args)) {
			Log.error("回调参数类型不对! paramTypes=" + Arrays.toString(paramTypes) + " args=" + Arrays.toString(args));
			return false;
		}

		// 加密数据
		byte[] data = null;
		try {
			data = device.toBytes(channel, args, paramTypes);
		} catch (Exception e) {
			Log.error("转化参数失败!", e);
			return false;
		}
		// 消息模式
		int mode = (this.srcMsg != null) ? this.srcMsg.getMode() : 0;

		// 生成消息
		RpcCallBackMsg msg = new RpcCallBackMsg();
		msg.setCallbackId(callbackId);
		msg.setData(data);
		msg.setType(type);
		if (RpcDevice.checkMode(mode, RpcDevice.MODE_PARAMTYPE)) {
			msg.setParamTypes(paramTypes);
		}
		// 来源去处倒置
		RpcMsg sendMsg = RpcMsg.create(RpcProtocol.RPC_CALLBACK, RpcUtils.toByte(msg));
		sendMsg.setTo((this.srcMsg != null) ? this.srcMsg.getFrom() : null);
		sendMsg.setFrom((this.srcMsg != null) ? this.srcMsg.getTo() : null);
		sendMsg.setMode(mode);

		// 发送消息
		channel.write(sendMsg);
		return true;
	}

	@Override
	public void onTimeOut() {
		throw new RuntimeException("只能在远程客户端上使用.");
	}

	@Override
	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}
}
