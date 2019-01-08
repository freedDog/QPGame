package com.game.framework.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;

/**
 * 检验工具
 * CheckUtils.java
 * @author JiangBangMing
 * 2019年1月8日上午10:03:39
 */
public final class CheckUtils {
	/** 检测包下类不重名 **/
	public static boolean checkUniqueClassName(String packet) {
		String regex = ResourceUtils.getPacketRegex(packet);
		List<Class<?>> classes = ResourceUtils.getClasses(regex);
		// 遍历检测类名
		Map<String, Class<?>> classNames = new HashMap<>();
		int csize = (classes != null) ? classes.size() : 0;
		for (int i = 0; i < csize; i++) {
			Class<?> clazz = classes.get(i);
			String cn = clazz.getSimpleName();
			Class<?> old = classNames.put(cn, clazz);
			if (old != null) {
				Log.error("存在重名类: clazz=" + clazz + " -> " + old);
				return false;
			}
		}
		return true;
	}

	/** 检测类中类型唯一性(检测类中定义的常量) **/
	public static boolean checkUniqueType(Class<?> clazz) {
		// 服务器常规检测
		Result result = CheckUtils.checkUniqueType(clazz, null);
		if (result == null || !result.isSucceed()) {
			String errorStr = (result != null) ? result.getMsg() : "未知错误";
			Log.error(clazz + " 检测错误! " + errorStr);
			return false;
		}
		return true;
	}

	/** 检测类中类型唯一性(检测类中定义的常量) **/
	private static Result checkUniqueType(Class<?> clazz, Object cobj) {
		try {
			Map<String, Field> map = new HashMap<>();
			Map<Object, Field> values = new HashMap<>();

			// 遍历变量
			// Field[] fields = clazz.getDeclaredFields();
			Field[] fields = clazz.getFields();
			for (Field field : fields) {
				String fname = field.getName();
				Object obj = field.get(cobj);
				// 判断是否存在
				if (values.containsKey(obj)) {
					Field oldField = values.get(obj);
					return Result.error("存在相同的值值: " + fname + "(" + obj + ") -> " + oldField.getName());
				}
				values.put(obj, field);
				map.put(fname, field);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("错误:" + e.getMessage());
		}
		// 成功
		return Result.succeed();
	}

}
