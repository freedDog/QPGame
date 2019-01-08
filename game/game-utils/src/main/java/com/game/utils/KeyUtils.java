package com.game.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.game.framework.component.log.Log;
import com.game.framework.utils.EncryptUtils;

/**
 * key处理工具
 * KeyUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午1:26:17
 */
public class KeyUtils {
	protected static Set<Class<?>> registers = new HashSet<>(); // 解析注册类
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

	/** 加密 **/
	public static String encrypt(String encryptKey, Object obj) {
		try {
			// 转化对象
			Kryo kryo = getKryo();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Output output = new Output(stream);
			kryo.writeObjectOrNull(output, obj, obj.getClass());
			output.flush();

			// 加密数据
			byte[] data = stream.toByteArray();
			byte[] keys = encryptKey.getBytes();
			data = EncryptUtils.ZED.encryptEnhanced(data, keys);
			return EncryptUtils.Base64.encode(data);
		} catch (Exception e) {
			Log.error("加密key失败!" + obj, e);
			return null;
		}
	}

	/** 解密 **/
	public static <T> T decrypt(String encryptKey, String str, Class<T> clazz) {
		try {
			byte[] keys = encryptKey.getBytes();
			byte[] data = EncryptUtils.Base64.decode(str);
			data = EncryptUtils.ZED.decryptEnhanced(data, keys);
			// 转化对象
			Kryo kryo = getKryo();
			Input input = new Input(data);
			return kryo.readObjectOrNull(input, clazz);
		} catch (Exception e) {
			Log.error("解密key失败! key=" + encryptKey + " str=" + str, e);
			return null;
		}
	}

}

