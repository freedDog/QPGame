package com.game.framework.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 反射处理
 * ReflectUtils.java
 * @author JiangBangMing
 * 2019年1月3日上午11:47:11
 */
public class ReflectUtils {

	/** 根据map赋值给对象 **/
	@SuppressWarnings("unchecked")
	public static <T, V> T mapToObject(Class<T> clazz, Map<String, V> values) {
		try {
			Object obj = createInstance(clazz);
			if (!setObjectByMap(obj, values)) {
				return null;
			}
			return (T) obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 根据map赋值给对象 **/
	public static <T> boolean setObjectByMap(Object obj, Map<String, T> values) {
		// 判断对象
		if (obj == null) {
			return false;
		}
		// 判断变量参数
		if (values == null || values.isEmpty()) {
			return false;
		}
		// 遍历赋值
		@SuppressWarnings("unchecked")
		Class<Object> clazz = (Class<Object>) obj.getClass();
		for (Map.Entry<String, T> entry : values.entrySet()) {
			// 获取参数属性
			String fieldName = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			// 获取ClassField
			ClassField<Object> classField = ClassField.getClassField(clazz, fieldName);
			if (classField == null) {
				continue; // 没有这个变量, 跳过
			}
			// 获取变量类型
			Class<?> vclazz = classField.getDeclaringClass();
			Object tv = ObjectUtils.toValue(value, vclazz);
			tv = (tv != null) ? tv : value;
			// 设置变量
			try {
				classField.set(obj, tv);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/** 拷贝对象 */
	public static <T extends P, P> boolean copy(P src, T obj) {
		// 读取源类
		Class<?> classType = src.getClass();
		// 遍历成员变量
		Field[] fields = classType.getDeclaredFields();
		for (Field filed : fields) {
			if (Modifier.isStatic(filed.getModifiers())) {
				continue; // 是静态数据
			}
			if (Modifier.isTransient(filed.getModifiers())) {
				continue; // 临时数据, 跳过不保存
			}

			// 生成获取和设置函数名
			String firstLetter = filed.getName().substring(0, 1).toUpperCase(); // 首字母大写
			String otherString = filed.getName().substring(1);
			String getMethodName = "get" + firstLetter + otherString;
			String setMethodName = "set" + firstLetter + otherString;
			// 通过反射获取函数
			Method getMethod = null;
			Method setMethod = null;
			try {
				getMethod = classType.getMethod(getMethodName, new Class[] {});
				setMethod = classType.getMethod(setMethodName, new Class[] { filed.getType() });
			} catch (Exception e) {
				// e.printStackTrace();
			}
			filed.setAccessible(true); // 强制读取

			// 读取变量
			Object value = null;
			try {
				if (getMethod != null) {
					getMethod.setAccessible(true);
					value = getMethod.invoke(src, new Object[] {}); // 通过函数获取
				} else {
					value = filed.get(src); // 直接获取
				}
			} catch (Exception e) {
				System.err.println("反射读取对象变量错误!" + filed);
				e.printStackTrace();
				continue; // 读取失败, 跳过
			}

			try {
				// 设置变量
				if (setMethod != null) {
					setMethod.setAccessible(true);
					setMethod.invoke(obj, new Object[] { value }); // 通过函数设置
				} else {
					filed.set(obj, value); // 直接设置
				}
			} catch (Exception e) {
				System.err.println("反射设置对象变量错误!" + filed + " obj" + obj);
				e.printStackTrace();
				continue;
			}
		}
		return true;
	}

	/** 根据函数名获取函数, 不管参数. **/
	public static Method getMethodByName(Class<?> clazz, String name) {
		return getMethodByName(clazz, name, null);
	}

	/** 判断参数列表A是否兼容参数列表B(B的所有参数是否能赋值给A) **/
	protected static boolean checkParams(Class<?>[] params, Class<?>[] mparams) {
		// 先判断参数数量
		int psize = (params != null) ? params.length : 0;
		int mpsize = (mparams != null) ? mparams.length : 0;
		if (mpsize != psize) {
			return false;
		}

		// 遍历检测参数是否符合
		for (int j = 0; j < mpsize; j++) {
			Class<?> mparam = mparams[j];
			Class<?> param = params[j];
			// 判断是否兼容
			if (!ObjectUtils.isCompatibleClass(param, mparam)) {
				return false;
			}
		}
		return true;
	}

	/** 通过java本身的方法获取函数, 过滤错误. **/
	public static Method getMethodByNameByJava(Class<?> clazz, String name, Class<?>[] params) {
		// 先查找公开的
		Method m = getMethodByNameByJava(clazz, name, params, true);
		if (m != null) {
			return m;
		}
		// 非公开的也获取
		return getMethodByNameByJava(clazz, name, params, false);
	}

	/** 通过java本身的方法获取函数, 过滤错误. **/
	public static Method getMethodByNameByJava(Class<?> clazz, String name, Class<?>[] params, boolean declared) {
		try {
			// 判断获取
			if (declared) {
				return clazz.getDeclaredMethod(name, params);
			}
			return clazz.getMethod(name, params);
		} catch (Exception e) {
		}
		return null;
	}

	/** 通过函数名和参数获取函数 **/
	public static Method getMethodByName(Class<?> clazz, String name, Class<?>[] params) {
		// 先查找公开的
		Method m = getMethodByName(clazz, name, params, true);
		if (m != null) {
			return m;
		}
		// 非公开的也获取
		return getMethodByName(clazz, name, params, false);
	}

	/**
	 * 根据函数名获取函数<br>
	 * 
	 * @param declared
	 *            是否获取公开的函数 getDeclaredMethods, 否则就是全部函数
	 * 
	 * **/
	public static Method getMethodByName(Class<?> clazz, String name, Class<?>[] params, boolean declared) {
		// 获取函数列表
		// Method[] methods = clazz.getDeclaredMethods(); //用这个方法会导致获取不到超类函数
		Method[] methods = (declared) ? clazz.getDeclaredMethods() : clazz.getMethods();

		// 遍历函数列表
		int msize = (methods != null) ? methods.length : 0;
		for (int i = 0; i < msize; i++) {
			Method method = methods[i];
			if (!method.getName().equals(name)) {
				continue;
			}

			// 判断是否有参数要求
			if (params != null) {
				Class<?>[] mparams = method.getParameterTypes();
				if (!checkParams(mparams, params)) {
					continue;
				}
			}

			return method;
		}
		return null;
	}

	/** 获取对象的变量 **/
	public static Object getValue(Object obj, String fieldName) {
		if (obj == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Class<Object> clazz = (Class<Object>) obj.getClass();
		ClassField<Object> classField = ClassField.getClassField(clazz, fieldName);
		if (classField == null) {
			return null;
		}
		try {
			return classField.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 设置取对象的变量(强制) **/
	public static boolean setValue(Object obj, String fieldName, Object v) {
		if (obj == null) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Class<Object> clazz = (Class<Object>) obj.getClass();
		ClassField<Object> classField = ClassField.getClassField(clazz, fieldName);
		if (classField == null) {
			return false;
		}

		try {
			return classField.set(obj, v);
		} catch (Exception e) {
			System.err.println("设置变量错误! set=" + v + " by " + fieldName + "(" + classField.getDeclaringClass() + ") obj=" + obj);
			e.printStackTrace();
		}
		return false;
	}

	/** 设置取对象的变量 **/
	public static boolean setValueByAuto(Object obj, String fieldName, Object v) {
		if (obj == null) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Class<Object> clazz = (Class<Object>) obj.getClass();
		ClassField<Object> classField = ClassField.getClassField(clazz, fieldName);
		if (classField == null) {
			return false;
		}
		// 转化数据
		Object value = v;
		if (value != null) {
			// 判断类型是否相符
			if (value.getClass() != classField.getClass()) {
				// 转换类型(尝试)
				value = ObjectUtils.toValue(value, classField.getDeclaringClass());
			}
		}

		try {
			// 设置数据
			return classField.set(obj, value);
		} catch (Exception e) {
			System.err.println("设置变量错误! set=" + value + " by " + fieldName + "(" + classField.getDeclaringClass() + ") obj=" + obj);
			e.printStackTrace();
		}
		return false;
	}

	/** 根据(数组数据)创建他们对应的(类列表) **/
	public static Class<?>[] createClasses(Object... args) {
		// 读取参数数量
		int argCount = (args != null) ? args.length : 0;
		if (argCount <= 0) {
			return new Class<?>[] {};
		}
		// 遍历生成
		Class<?>[] types = new Class<?>[argCount];
		for (int i = 0; i < argCount; i++) {
			Object arg = args[i];
			if (arg == null) {
				types[i] = Object.class;
				continue;
			}
			types[i] = arg.getClass(); // 读取类型
		}

		return types;
	}

	/**
	 * 带参数构造<br>
	 * 
	 * @param cls
	 * @param args
	 * @return
	 */
	public static <T> T createInstance(Class<T> cls, boolean declared, Object... args) {
		Class<?>[] paramTypes = createClasses(args);
		return createInstance(cls, declared, paramTypes, args);
	}

	/**
	 * 调用带参数构造<br>
	 * 可调用保护构造函数.
	 */
	public static <T> T createInstance(Class<T> cls, boolean declared, Class<?>[] paramTypes, Object... args) {
		try {
			final Constructor<T> con;
			if (declared) {
				con = cls.getDeclaredConstructor(paramTypes); // 读取私有构造器
			} else {
				con = cls.getConstructor(paramTypes); // 读取构造器
			}
			con.setAccessible(true); // 启动权限获取
			return (T) con.newInstance(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 创建对象, 可调用保护的构造函数. */
	public static <T> T createInstance(Class<T> cls) {
		// 先尝试简单的创建
		try {
			T obj = cls.newInstance();
			if (obj != null) {
				return obj;
			}
		} catch (Exception e) {
		}
		// 不行再通过构造函数处理
		return createInstance(cls, false, new Class<?>[] {}, new Object[] {});
	}

	/** 旧API **/
	@Deprecated
	protected static class OldAPI {

		/** 强制设置静态变量 */
		public static void setFinalStatic(Field field, Object newValue) throws Exception {
			field.setAccessible(true);
			// 获取field的属性
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL); // 修改掉final属性
			field.set(null, newValue); // 强制设置
		}

		/** 强制设置静态变量 */
		public static void setFinalStatic(Class<?> clazz, String fieldName, Object newValue) throws Exception {
			Field field = clazz.getField(fieldName);
			if (field == null) {
				throw new Exception("找不到对应变量");
			}
			setFinalStatic(field, newValue);
		}

		/** 获取类型函数的返回值 */
		public static Class<?> getMethodReturnType(Class<?> clazz, String methodName, Class<?>[] clazzs) {
			try {
				Method method = clazz.getMethod(methodName, clazzs);
				if (method == null) {
					return null; // 木有函数
				}
				return method.getReturnType();
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return null;
		}

		/** 调用函数 */
		public static Object invoke(Object object, String methodName, Object[] args, Class<?>[] clazzs) {
			try {
				Class<?> clazz = object.getClass();
				Method method = clazz.getMethod(methodName, clazzs);
				if (method == null) {
					return null; // 木有函数
				}
				Object value = method.invoke(object, args); // 通过函数获取
				return value;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 执行类函数 */
		public static Object invoke(Object object, String methodName, Object[] args) {
			Class<?>[] clazzs = createClasses(args);
			return invoke(object, methodName, args, clazzs);
		}

		/**
		 * 创建对象复制对象<br>
		 * 类必须支持无参数构造函数.
		 */
		public static <T extends P, P> T create(Class<T> cls, P src) {
			try {
				T obj = (T) cls.newInstance();
				ReflectUtils.copy(src, obj);
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 获取类的返回值(第一个对应名字的函数) */
		public static Class<?> getMethodReturnType(Class<?> clazz, String methodName) {
			try {
				Method method = getMethodByName(clazz, methodName);
				if (method != null) {
					return method.getReturnType();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return null;
		}

		/** 复制对象 **/
		@SuppressWarnings("unchecked")
		public static <T> Object copy(T src) {
			// 调用对象的clone, 保护函数.
			return create((Class<T>) src.getClass(), src);
		}

	}

	/**
	 * 类变量属性<br>
	 * 用于智能获取或者设置对象变量<br>
	 */
	public static class ClassField<T> {
		protected Class<?> clazz; // 对象类
		protected Field field; // 变量数据
		protected Method writeMethod; // 写入函数(可能为空)
		protected Method readMethod; // 获取函数(可能为空)

		protected ClassField(Class<?> clazz, String fieldName) throws Exception {
			init(clazz, fieldName);
		}

		/** 初始化 **/
		private boolean init(Class<?> clazz, String fieldName) throws Exception {
			// 参数赋值
			this.clazz = clazz;
			// 获取 field
			field = getField(clazz, fieldName);
			if (field != null) {
				field.setAccessible(true);
			}
			// 获取set函数
			writeMethod = getWriteMethod(clazz, fieldName);
			if (writeMethod != null) {
				writeMethod.setAccessible(true);
			}
			// 获取get函数
			readMethod = getReadMethod(clazz, fieldName);
			if (readMethod != null) {
				readMethod.setAccessible(true);
			}
			return true;
		}

		/** 获取属性类型 **/
		public Class<?> getDeclaringClass() {
			// 根据变量对象获取
			if (field != null) {
				return field.getType();
			}
			// 根据读取函数获取
			if (readMethod != null) {
				return readMethod.getReturnType();
			}
			return null;
		}

		/** 读取变量 **/
		public Object get(T obj) throws Exception {
			if (readMethod != null) {
				return readMethod.invoke(obj, new Object[] {});
			}
			// 参数获取
			if (field != null) {
				return field.get(obj);
			}
			return null;
		}

		/** 设置变量 **/
		public boolean set(T obj, Object v) throws Exception {
			// 写函数调用
			if (writeMethod != null) {
				writeMethod.invoke(obj, new Object[] { v });
				return true;
			}
			// 变量直接写入
			if (field != null) {
				field.set(obj, v);
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "ClassField [clazz=" + clazz + ", field=" + field + "]";
		}

		/**************** 静态 ****************/
		protected static final ConcurrentMap<Class<?>, ConcurrentMap<String, ClassField<?>>> fields;
		static {
			fields = new ConcurrentHashMap<>();
		}

		// /**
		// * 从map表中获取变量<br>
		// *
		// * @param auto
		// * 自动转化名字(待定)
		// * **/
		// @SuppressWarnings("unchecked")
		// protected static <T> ClassField<T> getClassFieldByMap(ConcurrentMap<String, ClassField<?>> map, String fname, boolean auto) {
		// // 获取属性
		// ClassField<T> classField = (ClassField<T>) map.get(fname);
		// if (classField != null) {
		// return classField;
		// }
		//
		// return null;
		// }

		/** 获取类变量对象 **/
		@SuppressWarnings("unchecked")
		public static <T> ClassField<T> getClassField(Class<T> clazz, String fname) {
			// 获取类表
			ConcurrentMap<String, ClassField<?>> map = fields.get(clazz);
			if (map == null) {
				map = new ConcurrentHashMap<>();
				ConcurrentMap<String, ClassField<?>> old = fields.putIfAbsent(clazz, map);
				map = (old != null) ? old : map;
			}

			// 获取属性
			ClassField<T> classField = (ClassField<T>) map.get(fname);
			if (classField != null) {
				return classField;
			}

			try {
				// 创建插入
				classField = new ClassField<>(clazz, fname);
				ClassField<?> old = map.putIfAbsent(fname, classField);
				classField = (old != null) ? (ClassField<T>) old : classField;
			} catch (Exception e) {
				System.err.println("创建ClassField错误!");
				e.printStackTrace();
				return null;
			}
			return classField;
		}

		/** 是否是相同的变量名 **/
		private static boolean isSampleName(String src, String dst) {
			// 简单检测
			if (src == dst || src.equals(dst)) {
				return true;
			}
			// 忽略大小写
			if (src.toLowerCase().equals(dst.toLowerCase())) {
				return true;
			}
			return false;
		}

		/** 获取变量属性 **/
		private static Field getField(Class<?> clazz, String fieldName, boolean declared) {
			try {
				// 直接获取
				if (declared) {
					return clazz.getDeclaredField(fieldName);
				} else {
					return clazz.getField(fieldName);
				}
			} catch (Exception e) {
			}
			return null;
		}

		/** 根据名称获取变量 **/
		public static Field getField(Class<?> clazz, String fieldName) {
			// 直接获取
			Field field = getField(clazz, fieldName, true);
			if (field != null) {
				return field;
			}
			field = getField(clazz, fieldName, false);
			if (field != null) {
				return field;
			}
			// 遍历所有父类找
			do {
				// 遍历查找
				Field[] fields = clazz.getDeclaredFields();
				int fsize = (fields != null) ? fields.length : 0;
				for (int i = 0; i < fsize; i++) {
					field = fields[i];
					if (isSampleName(field.getName(), fieldName)) {
						return field;
					}
				}
				// 到父类找
				clazz = clazz.getSuperclass();
			} while (clazz != Object.class);
			return null;
		}

		/** 获取写函数 **/
		public static Method getWriteMethod(Class<?> clazz, String fieldName) {
			// 获取set函数
			try {
				StringBuilder strBdr = new StringBuilder();
				strBdr.append("set");
				strBdr.append(fieldName.substring(0, 1).toUpperCase());
				strBdr.append(fieldName.substring(1));
				// 获取函数
				String methodName = strBdr.toString();
				Method method = ReflectUtils.getMethodByName(clazz, methodName);
				if (method == null) {
					return null;
				}
				return method;
			} catch (Exception e) {
			}
			return null;
		}

		/** 获取写函数 **/
		public static Method getReadMethod(Class<?> clazz, String fieldName) {
			// 获取get函数
			try {
				String fname = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

				// 获取get方法
				String methodName = "get" + fname;
				Method readMethod = ReflectUtils.getMethodByName(clazz, methodName);
				if (readMethod != null) {
					return null;
				}

				// 如果是boolean类型, 尝试获取is函数
				methodName = "is" + fname;
				readMethod = ReflectUtils.getMethodByName(clazz, methodName);
				if (readMethod != null) {
					return null;
				}

			} catch (Exception e) {
			}
			return null;
		}
	}

}
