package com.game.framework.framework.rpc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.msg.RpcCallBackMsg;
import com.game.framework.framework.rpc.msg.RpcCallMsg;
import com.game.framework.framework.rpc.msg.RpcMsg;
import com.game.framework.utils.StringUtils;

/**
 * rpc序列化工具 RpcSerializer.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:19:41
 */
class RpcSerializer {
	protected static Set<Class<?>> registers = new HashSet<>(); // 解析注册类

	public static void addRegisterClass(Class<?> clazz) {
		registers.add(clazz);
	}

	/** 序列化工具，线程安全 **/
	protected static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.register(RpcCallBackMsg.class);
			kryo.register(RpcCallMsg.class);
			kryo.register(RpcMsg.class);
			kryo.register(ArrayList.class);
			kryo.register(HashMap.class);
			// 遍历添加
			for (Class<?> register : registers) {
				kryo.register(register);
			}

			return kryo;
		};
	};

	/** 获取Kryo **/
	protected static Kryo getKryo() {
		return kryos.get();
	}

	/** 序列化处理过程替换类型, 把特定类型替换成另外的类型执行. **/
	protected Class<?> getObjectClass(Class<?> clazz) {
		if (clazz == List.class) {
			return ArrayList.class;
		} else if (clazz == Map.class) {
			return HashMap.class;
		}
		return clazz;
	}

	/** 转化单个对象 **/
	protected boolean toByte(RpcChannel channel, Object arg, Class<?> type, Kryo kryo, Output output) throws Exception {
		type = getObjectClass(type);

		// 回调类型处理
		if (type != null && RpcChannel.class.isAssignableFrom(type)) {
			// 忽略处理
			return true;
		} else if ((type == null || arg == null)) {
			// 写入空对象
			kryo.writeObjectOrNull(output, null, Object.class);
			return true;
		} else if (RpcCallback.class.isAssignableFrom(type)) {
			RpcCallback callback = (RpcCallback) arg;
			// 获取设备
			RpcDevice<?> device = channel.getDevice();
			RpcDevice<?>.Callback c = device.addCallBack(callback);
			if (c == null) {
				throw new Exception("添加回调失败! channel=" + channel);
			}
			long callbackId = c.getCallbackId();
			kryo.writeObjectOrNull(output, callbackId, long.class);
			if (device.isVerify()) {
				Class<?>[] paramTypes = callback.getParamTypes();
				paramTypes = (paramTypes == null) ? c.getMethod().getParameterTypes() : paramTypes;
				kryo.writeObjectOrNull(output, paramTypes, Class[].class);
			} else {
				// 写入空
				kryo.writeObjectOrNull(output, null, Class[].class);
			}

			// Log.debug("绑定回调! callbackId=" + callbackId +
			// " paramTypes=" + Arrays.toString(paramTypes) +
			// " channel=" + channel);
			return true;
		}

		// 写入对象
		kryo.writeObjectOrNull(output, arg, type);
		return true;
	}

	/** 转化单个对象 **/
	protected Object toObject(RpcChannel channel, Class<?> type, Kryo kryo, Input input, RpcMsg packet)
			throws Exception {
		type = getObjectClass(type);

		// 回调类型处理
		if (type == RpcCallback.class) {
			long callbackId = kryo.readObjectOrNull(input, long.class);
			Class<?>[] paramTypes = null;
			paramTypes = kryo.readObjectOrNull(input, Class[].class);

			// Log.debug("生成回调! callbackId=" + callbackId + " paramTypes=" +
			// Arrays.toString(paramTypes) + " channel=" + channel);
			return new RpcCallbackImpl(channel, callbackId, paramTypes, packet);
		} else if (type == RpcChannel.class || RpcChannel.class.isAssignableFrom(type)) {
			// kryo.readObjectOrNull(input, Object.class); // 把空对象提取出来
			return channel; // 就是自身.
		} else if (RpcChannel.class.isAssignableFrom(type)) {
			// Log.error("不支持这种类型的参数! type=" + type);
			// return null;
			throw new Exception("不支持这种类型的参数! type=" + type);
		}

		// 检测结果
		return kryo.readObjectOrNull(input, type);
	}

	/** 把对象列表转成二进制数组 **/
	public byte[] toBytes(RpcChannel channel, Object[] objs, Class<?>[] types) throws Exception {
		byte[] out = null;

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Output output = new Output(stream);
		try {
			Kryo kryo = getKryo();
			int tsize = (types != null) ? types.length : 0;
			for (int i = 0; i < tsize; ++i) {
				Object arg = (objs != null) ? objs[i] : null;
				Class<?> type = types[i];

				// 判断参数是否对应, 如果有参数, 那么类型必须相符.
				if (arg != null && !RpcUtils.checkType(type, arg)) {
					throw new Exception("参数不对应! i=" + i + " type=" + type + " arg=" + arg);
				}

				// 写入对象
				if (!toByte(channel, arg, type, kryo, output)) {
					throw new Exception("参数写入错误! i=" + i + " type=" + StringUtils.toString(type) + " arg="
							+ StringUtils.toString(arg));
				}
			}
			output.flush();
			out = stream.toByteArray();
		} finally {
			output.flush();
			output.close();
			stream.close();
		}
		return out;
	}

	/** 二进制数组根据类性转成对象列表 **/
	public Object[] toObjects(RpcChannel channel, byte[] data, Class<?>[] types, RpcMsg msg) throws Exception {
		int tsize = (types != null) ? types.length : 0;
		Object[] args = new Object[tsize];
		if (tsize <= 0) {
			return args; // 没函数
		}
		// 空处理
		if (data == null) {
			return args;
		}

		// 解析消息
		Input input = new Input(data);
		try {
			Kryo kryo = getKryo();
			for (int i = 0; i < tsize; ++i) {
				Class<?> type = types[i];
				try {
					args[i] = toObject(channel, type, kryo, input, msg);
				} catch (Exception e) {
					Log.error("解析参数失败! i=" + i + " type=" + type);
					throw e;
				}
			}
		} finally {
			input.close();
		}
		return args;
	}
}
