package com.game.framework.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对象类型处理工具
 * ObjectUtils.java
 * @author JiangBangMing
 * 2019年1月3日上午11:57:38
 */
public class ObjectUtils {
	private static final List<ClassInfo> classInfos; // 基础数据类列表
	private static final int indexOfLong; // long类型的索引

	// private static final Map<Class<?>, Map<Class<?>, Method>> transFunc; // 转化函数

	static {
		classInfos = Arrays.asList(new ClassInfo[] { //
				new ClassInfo(boolean.class, Boolean.class, boolean[].class, Boolean[].class, false),//
						new ClassInfo(byte.class, Byte.class, byte[].class, Byte[].class, (byte) 0),//
						new ClassInfo(char.class, Character.class, char[].class, Character[].class, (char) 0),//
						new ClassInfo(short.class, Short.class, short[].class, Short[].class, (short) 0),//
						new ClassInfo(int.class, Integer.class, int[].class, Integer[].class, 0),//
						new ClassInfo(long.class, Long.class, long[].class, Long[].class, 0L),//
						new ClassInfo(float.class, Float.class, float[].class, Float[].class, 0.0f),//
						new ClassInfo(double.class, Double.class, double[].class, Double[].class, 0.0),//
				});

		// 判断测试
		final int bsize = classInfos.size();

		// 二次初始化处理
		for (int i = 0; i < bsize; i++) {
			ClassInfo classInfo = classInfos.get(i);
			classInfo.init(classInfos);
		}

		// long的索引
		indexOfLong = indexOfBaseClass(long.class);
	}

	/** 获取基础数组类型的索引 **/
	private static int indexOfBaseArrayClass(Class<?> clazz) {
		for (int i = 0; i < classInfos.size(); i++) {
			ClassInfo classInfo = classInfos.get(i);
			if (classInfo.baseArrayClass == clazz) {
				return i;
			} else if (classInfo.packagingArrayClass == clazz) {
				return i;
			}
		}
		return -1;
	}

	/** 获取基础类型的索引 **/
	private static int indexOfBaseClass(Class<?> clazz) {
		for (int i = 0; i < classInfos.size(); i++) {
			ClassInfo classInfo = classInfos.get(i);
			if (classInfo.baseClass == clazz) {
				return i;
			} else if (classInfo.packagingClass == clazz) {
				return i;
			}
		}
		return -1;
	}

	/** 判断是否兼容类型, A兼容B(B的对象是否能赋值给A)(判断包括封箱类型) **/
	public static boolean isCompatibleClass(Class<?> a, Class<?> b) {
		// 判断类型是否相符
		if (a == b) {
			return true; // 同一个类
		}
		// 判断是否继承关系 A.isAssignableFrom(B): B是否继承自A
		if (a.isAssignableFrom(b)) {
			return true;
		}
		// 判断是否是记录类型
		int aindex = indexOfBaseClass(a);
		if (aindex < 0) {
			return false; // 不是基础类型不判断了.
		}
		int bindex = indexOfBaseClass(b);
		if (bindex < 0) {
			return false; // 不是基础类型不判断了.
		}
		// 是相互封箱类型
		if (aindex == bindex) {
			return true;
		}
		return false;
	}

	/** 获取封箱类型 **/
	public static Class<?> getDevanningClass(Class<?> clazz) {
		// 判断是否是对象类
		if (!Object.class.isAssignableFrom(clazz)) {
			return null; // 封箱类型都是对象类
		}
		// 判断是否是基础类型
		for (int i = 0; i < classInfos.size(); i++) {
			ClassInfo classInfo = classInfos.get(i);
			if (classInfo.packagingClass == clazz) {
				return classInfo.baseClass;
			}
		}
		return null;
	}

	/** 获取封箱类型 **/
	public static Class<?> getPackagingClass(Class<?> clazz) {
		// 判断是否是对象类
		if (Object.class.isAssignableFrom(clazz)) {
			return null; // 基础类型都不是对象类
		}
		// 判断是否是基础类型
		for (int i = 0; i < classInfos.size(); i++) {
			ClassInfo classInfo = classInfos.get(i);
			if (classInfo.baseClass == clazz) {
				return classInfo.packagingClass;
			}
		}
		return null;
	}

	/** 获取基础默认值 */
	@SuppressWarnings("unchecked")
	public static <T> T defualtValue(Class<T> cls) {
		// 判断是否是基础类型
		int index = indexOfBaseClass(cls);
		if (index >= 0) {
			return (T) classInfos.get(index).defaultValue;
		}
		return null;
	}

	/** 是否是基础类型 **/
	public static boolean isBaseObject(Object obj) {
		return (obj != null) ? isBaseClass(obj.getClass()) : false;
	}

	/** 是否是基础类 */
	public static boolean isBaseClass(Class<?> cls) {
		int index = indexOfBaseClass(cls);
		return index >= 0;
	}

	/** 把字符串转成基础数据 , 请用stringToValue */
	@Deprecated
	public static <T> T baseValue(String value, Class<T> cls) {
		T obj = stringToValue0(value, cls);
		return (obj != null) ? obj : defualtValue(cls);
	}

	/** 把字符串转成基础数据, 请用stringToValue */
	@Deprecated
	public static <T> T baseValue0(String value, Class<T> cls) {
		return stringToValue0(value, cls);
	}

	/** 把字符串转成基础数据 */
	public static <T> T stringToValue(String value, Class<T> cls) {
		T obj = stringToValue0(value, cls);
		return (obj != null) ? obj : defualtValue(cls);
	}

	/** 把字符串转成基础数据 */
	@SuppressWarnings("unchecked")
	public static <T> T stringToValue0(String value, Class<T> cls) {
		// 判断对象
		if (value == null) {
			return null;
		}
		try {
			// 字符串处理
			if (String.class.isAssignableFrom(cls)) {
				return (T) value;
			}
			// 基础类型处理
			int index = indexOfBaseClass(cls);
			if (index >= 0) {
				// 获取函数
				Method method = classInfos.get(index).valueOfMethod;
				if (method != null) {
					return (T) method.invoke(null, value);
				}
				// 特殊处理
				Class<?> clazz = classInfos.get(index).packagingClass;
				if (clazz == Character.class) {
					return (T) (Character) value.charAt(0);
				}
			}
		} catch (Exception ex) {
			// 容错处理
		}
		return null;
	}

	/** 转化出数值 **/
	public static Long numberValue0(Object obj) {
		// 判断对象
		if (obj == null) {
			return null;
		}
		try {
			// 特殊类型判断
			Class<?> clazz = obj.getClass();
			if (clazz == Long.class || clazz == long.class) {
				return (Long) obj;
			} else if (clazz == java.math.BigDecimal.class) {
				return ((java.math.BigDecimal) obj).longValue(); // 大浮点
			} else if (clazz == java.math.BigInteger.class) {
				return ((java.math.BigInteger) obj).longValue(); // 大整形
			} else if (clazz == String.class) {
				return Long.valueOf((String) obj);
			}

			// 判断基础类型
			int index = indexOfBaseClass(clazz);
			if (index >= 0) {
				ClassInfo classInfo = classInfos.get(index);
				Method method = classInfo.transMethods[indexOfLong];
				if (method == null) {
					return null; // 这个基础类型没有转化成long的函数
				}
				// 调用处理
				return (Long) method.invoke(obj);
			}
		} catch (Exception e) {
			// 容错, 解析错误不打紧
		}
		return null;
	}

	/** 转化出数值 **/
	public static long numberValue(Object obj) {
		Long value = numberValue0(obj);
		return (value != null) ? value : 0L;
	}

	/** 转化数据(基础数据之间的转化), 转化失败返回默认值 **/
	public static <T> T toValue(Object obj, Class<T> clazz) {
		T value = toValue0(obj, clazz);
		return (value != null) ? value : (T) defualtValue(clazz);
	}

	/** 转化数据(基础数据之间的转化), 转化失败返回null **/
	@SuppressWarnings("unchecked")
	public static <T> T toValue0(Object obj, Class<T> clazz) {
		// 判断对象是否为空
		if (obj == null || clazz == null) {
			return null;
		}
		// 特殊类型判断(不需要处理的转化类型)
		if (clazz == Object.class) {
			return (T) obj;
		} else if (clazz == String.class) {
			return (T) obj.toString();
		}
		// 判断类型是否相符
		Class<?> nclass = obj.getClass();
		if (nclass == clazz) {
			return (T) obj;
		} else if (isCompatibleClass(clazz, nclass)) {
			return (T) obj;
		} else if (nclass == String.class) {
			return stringToValue0(obj.toString(), clazz);
		}
		// 判断转化目标是否是基础类型
		int tindex = indexOfBaseClass(clazz);
		if (tindex < 0) {
			return null; // 不是基础数据类型之间的转化
		}

		// // 通过字符串实现转化(估计效率不好吧)
		// String str = obj.toString();
		// return stringToValue0(str, clazz);

		// 基础对象之间的转化
		int nindex = indexOfBaseClass(nclass);
		if (nindex < 0) {
			return null; // 不是基础数据类型之间的转化
		}

		// 获取转化函数
		ClassInfo classInfo = classInfos.get(nindex);
		try {
			Method method = classInfo.transMethods[tindex];
			if (method != null) {
				T value = (T) method.invoke(obj);
				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 是不是基础数组类 **/
	public static boolean isBaseArrayClass(Class<?> cls) {
		// 判断是否是数组类型
		if (!Array.class.isAssignableFrom(cls)) {
			return false;
		}
		// 特殊类型判断
		if (String[].class.isAssignableFrom(cls)) {
			return true;
		}
		// 判断是否是基础数组类型
		int index = indexOfBaseArrayClass(cls);
		if (index >= 0) {
			return true;
		}
		return false;
	}

	/** 从字符串中解析出基础对象数据 */
	public static Object toObject(String str) {
		// boolean
		try {
			if (str.equals("true")) {
				return true;
			} else if (str.equals("false")) {
				return false;
			}
		} catch (Exception ex) {
		}

		// integer
		try {
			Integer i = Integer.valueOf(str);
			if (i != null) {
				return i;
			}
		} catch (Exception ex) {
		}
		// long
		try {
			Long l = Long.valueOf(str);
			if (l != null) {
				return l;
			}
		} catch (Exception ex) {
		}

		// float
		try {
			Float f = Float.valueOf(str);
			if (f != null) {
				return f;
			}
		} catch (Exception ex) {
		}
		// double
		try {
			Double d = Double.valueOf(str);
			if (d != null) {
				return d;
			}
		} catch (Exception ex) {
		}
		return str;
	}

	/** 把数据转化成最合理的基础数据对象 **/
	public static Map<String, Object> createParams(Map<String, String> args) {
		Map<String, Object> params = new HashMap<>();

		// 遍历转化
		Iterator<Map.Entry<String, String>> iter = args.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();

			// 转化数据
			Object v = toObject(value);
			params.put(key, v);
		}
		return params;
	}

	/** 对象类型转换, 如果类型不符合返回null */
	@SuppressWarnings("unchecked")
	public static <T> T get(Object obj, Class<T> cls) {
		if (obj != null && cls.isInstance(obj)) {
			return (T) obj;
		}
		return null;
	}

	/** 基础类信息 **/
	static class ClassInfo {
		public final Class<?> baseClass; // 基础类
		public final Class<?> packagingClass; // 封包类
		public final Class<?> baseArrayClass; // 基础数组类型
		public final Class<?> packagingArrayClass; // 封包数组类型
		public final Object defaultValue; // 默认值

		public final Method valueOfMethod; // 基础类型对应的初始数据

		public Method[] transMethods; // 转换方法(基于packagingClass)

		public ClassInfo(Class<?> baseClass, Class<?> packagingClass, Class<?> baseArrayClass, Class<?> packagingArrayClass, Object defaultValue) {
			this.baseClass = baseClass;
			this.packagingClass = packagingClass;
			this.baseArrayClass = baseArrayClass;
			this.packagingArrayClass = packagingArrayClass;
			this.defaultValue = defaultValue;
			Method method = null;

			try {
				method = null;
				// parseInt等效率更高, 在valueOf中是调用parseInt的
				// https://zhidao.baidu.com/question/1947700552673497308.html
				method = packagingClass.getMethod("valueOf", new Class<?>[] { String.class });
			} catch (Exception e) {
				// System.err.println("获取valueOf函数失败:" + pclazz);
				// 没有就标记为空, 使用的时候判断null特殊处理即可
			}
			valueOfMethod = method;
		}

		/** 初始化类信息 **/
		protected void init(List<ClassInfo> classInfos) {
			// 基础名字
			final List<String> baseNames = Arrays.asList("bool", "byte", "chat", "short", "int", "long", "float", "double");

			// 检测数据长度
			int csize = (classInfos != null) ? classInfos.size() : 0;
			Class<?> pclazz = packagingClass;
			// 执行对比
			Method[] mlist = new Method[csize];
			for (int i = 0; i < csize; i++) {
				// 过滤检测
				ClassInfo classInfo = classInfos.get(i);
				if (classInfo == this) {
					continue; // 跳过自身
				}
				// Class<?> tclazz = packagingClass.get(j);
				// 获取类, 进行比较
				String baseName = baseNames.get(i);
				try {
					String mname = baseName + "Value";
					Method method = pclazz.getMethod(mname, new Class<?>[] {});
					mlist[i] = method;
				} catch (Exception e) {
					// System.err.println("获取longValue函数失败:" + pclazz);
					// 没有就标记为空, 使用的时候判断null特殊处理即可
				}
			}
			this.transMethods = mlist;
		}

		@Override
		public String toString() {
			return "ClassInfo [baseClass=" + baseClass + "]";
		}

	}

	/** 基础的数组处理工具 **/
	public static final class BaseArrayUtils {
		/** 裁剪字符串转化基础数组 **/
		@SuppressWarnings("unchecked")
		public static <T> T splitTo(Class<T> clazz, String str, String regex) {
			Class<?> componentType = clazz.getComponentType(); // 数组项类型
			// 空处理
			if (StringUtils.isEmpty(str)) {
				return (T) Array.newInstance(componentType, 0); // 空数组
			}
			try {
				// 裁剪
				String[] strs = str.split(regex);
				Object array = Array.newInstance(componentType, strs.length); // 新数组
				for (int i = 0; i < strs.length; i++) {
					// 转化设置到数组
					Object obj = ObjectUtils.stringToValue(strs[i].trim(), componentType);
					Array.set(array, i, obj);
				}
				return (T) array;
			} catch (Exception e) {
				// Log.error("拆分字符串异常,data:" + str + ",regex:" + regex, e);
				throw new RuntimeException(e);
			}
		}

		/** 2级拆分 **/
		@SuppressWarnings("unchecked")
		public static <T> T splitTo2(Class<?> clazz, String str, String regexA, String regexB) {
			Class<?> componentType = clazz.getComponentType(); // 数组项类型
			// 空处理
			if (StringUtils.isEmpty(str)) {
				return (T) Array.newInstance(componentType, 0); // 空数组
			}
			try {
				String[] strs = str.split(regexA);
				Object temps = Array.newInstance(componentType, strs.length); // 空数组
				for (int i = 0; i < strs.length; i++) {
					Object child = splitTo(componentType, strs[i], regexB);
					if (child == null) {
						return null; // 解析错误
					}
					Array.set(temps, i, child);
				}
				return (T) temps;
			} catch (Exception e) {
				// Log.error("拆分字符串异常,data:" + data + ",regex:" + regexA + ",regex2:" + regexB, e);
				throw new RuntimeException(e);
			}
		}

		/** 3级拆分 **/
		@SuppressWarnings("unchecked")
		public static <T> T splitTo3(Class<?> clazz, String str, String regexA, String regexB, String regexC) {
			Class<?> componentType = clazz.getComponentType(); // 数组项类型
			// 空处理
			if (StringUtils.isEmpty(str)) {
				return (T) Array.newInstance(componentType, 0); // 空数组
			}

			try {
				String[] strs = str.split(regexA);
				Object temps = Array.newInstance(componentType, strs.length); // 空数组
				for (int i = 0; i < strs.length; i++) {
					Object child = splitTo2(componentType, strs[i], regexB, regexC);
					if (child == null) {
						return null; // 解析错误
					}
					Array.set(temps, i, child);
				}
				return (T) temps;
			} catch (Exception e) {
				// Log.error("拆分字符串异常,data:" + data + ",regex:" + regexA + ",regex2:" + regexB, e);
				throw new RuntimeException(e);
			}
		}

		/** 转成数组文本 **/
		public static String toString(Object array, String regex) {
			int asize = (array != null) ? Array.getLength(array) : 0;
			StringBuilder strBdr = new StringBuilder();
			for (int i = 0; i < asize; i++) {
				if (i > 0) {
					strBdr.append(regex);
				}
				strBdr.append(Array.get(array, i));
			}
			return strBdr.toString();
		}

		/** 拆分数组, 不够就少一点. **/
		@SuppressWarnings("unchecked")
		public static <T> T subArray(Class<?> clazz, Object array, int offset, int size) {
			Class<?> componentType = clazz.getComponentType(); // 数组项类型
			int ssize = (array != null) ? Array.getLength(array) : 0;
			offset = Math.max(offset, 0);
			int esize = Math.min(ssize - offset, size);
			// 遍历提取
			Object temps = Array.newInstance(componentType, esize); // 新数组
			// for (int i = 0; i < esize; i++) {
			// Object obj = Array.get(array, offset + i);
			// Array.set(temps, i, obj);
			// }
			System.arraycopy(array, offset, temps, 0, esize);
			return (T) temps;
		}

		/** 获取数据, 检测数据长度(如果不存在或者超出上限, 返回默认值) **/
		public static Object get(Object array, int index) {
			// 获取数据
			int ssize = (array != null) ? Array.getLength(array) : 0;
			if (index >= 0 && index < ssize) {
				return Array.get(array, index);
			}
			// 不存在数据, 返回默认值. 获数组类中的子类
			Class<?> aclass = array.getClass();
			Class<?> dclass = aclass.getComponentType(); // 子类类型
			return ObjectUtils.defualtValue(dclass);
		}

		/**
		 * 检测数据是否在列表中<br>
		 * 不能使用 Arrays.binarySearch()二分法查找
		 * **/
		public static int indexOf(Object array, Object value) {
			int ssize = (array != null) ? Array.getLength(array) : 0;
			for (int i = 0; i < ssize; i++) {
				Object v = Array.get(array, i);
				if (v.equals(value)) {
					return i;
				}
			}
			return -1; // 没有
		}

		/** 添加数据到数组(会产生新数组) **/
		@SuppressWarnings("unchecked")
		public static <T> T add(Class<T> clazz, T array, Object value) {
			Class<?> componentType = clazz.getComponentType(); // 数组单对象类
			// 获取长度
			int size = Array.getLength(array);

			// 判断数量是否没有(空)
			Object temps = null;
			if (size <= 0) {
				temps = Array.newInstance(componentType, 1);
				Array.set(temps, 0, value);
				return array;
			}
			// 创建
			temps = Array.newInstance(componentType, size + 1);
			System.arraycopy(array, 0, temps, 0, size);
			Array.set(temps, size, value);
			return (T) temps;
		}

		/** 合并数组, 2个数组必须相同 **/
		@SuppressWarnings("unchecked")
		public static <T> T addArray(Class<T> clazz, T array1, T array2) {
			Class<?> componentType = clazz.getComponentType(); // 数组单对象类
			// 计算长度
			int size1 = Array.getLength(array1);
			int size2 = Array.getLength(array2);
			// 创建
			Object temps = Array.newInstance(componentType, size1 + size2);
			// 复制1
			System.arraycopy(array1, 0, temps, 0, size1);
			// 复制2
			System.arraycopy(array2, 0, temps, size1, size2);
			return (T) temps;
		}

		/** 转成数组 */
		@SuppressWarnings("unchecked")
		public static <T, A> A toArray(Collection<T> list, Class<A> clazz) {
			int size = list.size();
			Object array = Array.newInstance(clazz.getComponentType(), size);
			// 遍历获取
			int index = 0;
			Iterator<T> iter = list.iterator();
			while (iter.hasNext()) {
				T value = iter.next();
				Array.set(array, index++, value);
			}
			return (A) array;
		}

		/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
		@SuppressWarnings("unchecked")
		public static <T> T resetArray(Class<T> clazz, T array, int size) {
			int size0 = (array != null) ? Array.getLength(array) : 0;
			if (size0 == size) {
				return array;
			}
			try {
				// 重新创建数据
				Object temps = Array.newInstance(clazz.getComponentType(), size);
				int csize = Math.min(size, size0); // 最小复制长度
				System.arraycopy(array, 0, temps, 0, csize);
				return (T) temps;
			} catch (Exception e) {
				System.err.println("重设数组错误! " + array + " " + size0 + " -> " + size);
				throw new RuntimeException(e);
			}
		}

		/** 数组转化成list, array必须是对应数组 **/
		@SuppressWarnings("unchecked")
		public static <T> List<T> asList(Class<T> clazz, Object array, int offset, int size) {
			// 计算长度
			int ssize = (array != null) ? Array.getLength(array) : 0;
			int esize = Math.min(offset + size, ssize);

			// 插入数据
			List<T> retList = new ArrayList<>(size);
			for (int i = offset; i < esize; i++) {
				Object obj = Array.get(array, i);
				retList.add((T) obj);
			}
			return retList;
		}
	}

}
