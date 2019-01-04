package com.game.framework.framework.serializer;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * rpc序列化工具
 * KryoUtils.java
 * @author JiangBangMing
 * 2019年1月4日下午3:38:52
 */
public class KryoUtils {
	protected static Set<Class<?>> registers = new HashSet<>(); // 解析注册类

	public static void addRegisterClass(Class<?> clazz) {
		registers.add(clazz);
	}

	/** 序列化工具，线程安全 **/
	protected static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
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

	/** 把对象转成数组 **/
	public static byte[] toByte(Object obj) {
		return (obj != null) ? toByte(obj, obj.getClass()) : null;
	}

	/** 把对象转成数组 **/
	public static byte[] toByte(Object obj, Class<?> clazz) {
		if (obj == null) {
			return null;
		}
		// 创建消息
		Kryo kryo = getKryo();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Output output = new Output(stream);
		kryo.writeObjectOrNull(output, obj, clazz);
		output.flush();
		// 输出结果
		byte[] data = stream.toByteArray();
		return data;
	}

	/** 转化对象 **/
	public static <T> T toObject(byte[] data, Class<T> clazz) {
		if (data == null) {
			return null;
		}
		Kryo kryo = getKryo();
		Input input = new Input(data);
		return kryo.readObjectOrNull(input, clazz);
	}

}
