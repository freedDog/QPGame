package com.game.framework.framework.rpc;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.game.framework.component.log.Log;

/**
 * rpc工具 RpcUtils.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:18:07
 */
public class RpcUtils {

	/** 把对象转成数组 **/
	public static byte[] toByte(Object obj) {
		if (obj == null) {
			return null;
		}
		return toByte(obj, obj.getClass());
	}

	/** 把对象转成数组 **/
	public static byte[] toByte(Object obj, Class<?> clazz) {
		if (obj == null) {
			return null;
		}
		// 创建消息
		Kryo kryo = RpcSerializer.getKryo();
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
		Kryo kryo = RpcSerializer.getKryo();
		Input input = new Input(data);
		return kryo.readObjectOrNull(input, clazz);
	}

	/** 创建类型对比 **/
	public static Class<?>[] createTypes(Object... objs) {
		int tsize = (objs != null) ? objs.length : 0;
		Class<?>[] types = new Class<?>[tsize];
		for (int i = 0; i < tsize; i++) {
			Object obj = objs[i];
			types[i] = (obj != null) ? obj.getClass() : Void.class;
		}
		return types;
	}

	/** 根据函数名获取函数 **/
	public static Method getMethodByName(Class<?> clazz, String name) {
		Method[] methods = clazz.getDeclaredMethods();
		int msize = (methods != null) ? methods.length : 0;
		for (int i = 0; i < msize; i++) {
			Method method = methods[i];
			if (!method.getName().equals(name)) {
				continue;
			}
			return method;
		}
		return null;
	}

	/** 判断类型 **/
	public static boolean checkType(Class<?>[] paramTypes, Object[] args) {
		// 检测参数数量
		int paramSize = (paramTypes != null) ? paramTypes.length : 0;
		int argSize = (args != null) ? args.length : 0;
		if (argSize != paramSize) {
			Log.error("回调参数类型数量不对! paramSize=" + paramSize + " argSize=" + argSize, true);
			return false;
		}
		// 遍历检测函数数量
		for (int i = 0; i < paramSize; i++) {
			Class<?> paramType = paramTypes[i];
			Object arg = args[i];
			if (arg != null && !RpcUtils.checkType(paramType, arg)) {
				Log.error("参数类型不相符! i=" + i + " arg=[" + arg + "] type=[" + paramType + "]", true);
				return false;
			}
		}
		return true;
	}

	/** 判断类型 **/
	public static boolean checkType(Class<?> clazz, Object obj) {
		if (obj == null || clazz == null) {
			return false;
		}
		Class<?> objClass = obj.getClass();
		if (clazz == boolean.class) {
			return objClass == boolean.class || objClass == Boolean.class;
		} else if (clazz == byte.class) {
			return objClass == byte.class || objClass == Byte.class;
		} else if (clazz == short.class) {
			return objClass == short.class || objClass == Short.class;
		} else if (clazz == int.class) {
			return objClass == byte.class || objClass == Integer.class;
		} else if (clazz == long.class) {
			return objClass == long.class || objClass == Long.class;
		} else if (clazz == float.class) {
			return objClass == float.class || objClass == Float.class;
		} else if (clazz == double.class) {
			return objClass == double.class || objClass == Double.class;
		}

		return clazz.isInstance(obj);
	}

}
