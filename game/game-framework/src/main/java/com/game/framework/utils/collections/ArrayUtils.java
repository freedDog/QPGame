package com.game.framework.utils.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.framework.utils.ObjectUtils.BaseArrayUtils;


/**
 * 数组工具<br>
 * Arrays.copyOf 复制数组
 * ArrayUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午2:20:06
 */
public class ArrayUtils {

	/** 获取数据, 检测数据长度(如果不存在或者超出上限, 返回0) **/
	@SuppressWarnings("unchecked")
	public static <T> T get(T[] array, int index) {
		return (T) BaseArrayUtils.get((Object) array, index);
	}

	/**
	 * 批量设置<br>
	 * 建议使用源生方法Arrays.fill
	 * **/
	@Deprecated
	public static void set(int[] array, int v) {
		int asize = (array != null) ? array.length : 0;
		for (int i = 0; i < asize; i++) {
			array[i] = v;
		}
	}

	/** 检测数据是否在列表中 **/
	public static <T> boolean contains(T[] array, Object value) {
		return indexOf(array, value) >= 0;
	}

	/** 检测数据是否在列表中 **/
	public static <T> int indexOf(T[] array, Object value) {
		return BaseArrayUtils.indexOf((Object) array, value);
	}

	/** 添加数据到数组(会产生新数组) **/
	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] array, T obj) {
		Class<Object> clazz = (Class<Object>) ((Object) array).getClass(); // 获取数组类型
		return (T[]) BaseArrayUtils.add(clazz, array, obj);
	}

	/** 合并数组 **/
	@SuppressWarnings("unchecked")
	public static <T> T[] addArray(T[] array1, T[] array2) {
		Class<Object> clazz = (Class<Object>) ((Object) array1).getClass(); // 获取数组类型
		return (T[]) BaseArrayUtils.addArray(clazz, array1, array2);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	@SuppressWarnings("unchecked")
	public static <T> T[] resetArray(T[] array, int size) {
		Class<Object> clazz = (Class<Object>) ((Object) array).getClass(); // 获取数组类型
		return (T[]) BaseArrayUtils.resetArray(clazz, array, size);
	}

	/** 获取符合条件的数据 */
	public static <T> T find(T[] array, IFilter<? super T> filter, int offset, int size) {
		int index = find((Object) array, filter, offset, size);
		return (index >= 0) ? array[index] : null;
	}

	/** 获取符合条件的数据 */
	public static <T> T find(T[] array, IFilter<? super T> filter) {
		return find(array, filter, 0, array.length);
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param list
	 *            列表
	 * @param filter
	 *            过滤器
	 * @param maxCount
	 *            最大数量, 0为全部
	 * @return 符合条件的数据数组
	 */
	public static <T> List<T> findAll(T[] array, IFilter<? super T> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 遍历执行(不提供删除处理) **/
	@SuppressWarnings("unchecked")
	public static <T> int action(T[] array, IAction<? super T> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction<Object>) action, maxCount, offset, size);
	}

	/** 倒序执行 **/
	@SuppressWarnings("unchecked")
	public static <T> int actionN(T[] array, IAction<? super T> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction<Object>) action, maxCount, offset, size);
	}

	/** 查找单个数据的索引 **/
	private static <T, A> int find(Object array, IFilter<? super T> filter, int offset, int size) {
		// 查找符合条件的索引
		@SuppressWarnings("unchecked")
		List<Integer> indexs = findAllIndex(array, (IFilter<Object>) filter, 1, offset, size);
		int isize = (indexs != null) ? indexs.size() : 0;
		return (isize > 0) ? indexs.get(0) : -1;
	}

	/** 查找符合的数据位置索引 **/
	private static List<Integer> findAllIndex(Object array, final IFilter<Object> filter, final int maxCount, int offset, int size) {
		// 遍历读取
		final List<Integer> findList = new ArrayList<Integer>();
		IAction<Object> action = new IAction<Object>() {
			protected int actionCount = 0;

			@Override
			public boolean action(Object data, int index) {
				// 判断过滤
				if (filter == null || filter.check(data, index)) {
					findList.add(index); // 加入列表
					actionCount++;
					return (maxCount > 0) ? actionCount < maxCount : true; // 数量限制
				}
				return true;
			}
		};
		action(array, (IAction<Object>) action, 0, offset, size); // 顺序,越早得数据在前面
		return findList;
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> findAll(Object array, IFilter<? super T> filter, int maxCount, int offset, int size) {
		// 查找符合条件的索引
		List<Integer> indexs = findAllIndex(array, (IFilter<Object>) filter, maxCount, offset, size);
		int isize = (indexs != null) ? indexs.size() : 0;
		// 返回数据
		List<T> result = new ArrayList<>(isize);
		if (isize <= 0) {
			return result;
		}
		// 遍历提取数据
		for (Integer index : indexs) {
			Object obj = Array.get(array, index);
			result.add((T) obj);
		}
		return result;
	}

	/** 通用的数组倒序遍历 **/
	private static int actionN(Object array, IAction<Object> action, int maxCount, int offset, int size) {
		// 获取数组长度
		int alen = (array != null) ? Array.getLength(array) : 0;
		if (alen <= 0) {
			return 0;
		}
		// 参数调整
		int start = Math.max(offset, 0);
		int end = Math.min(alen, offset + size);
		int actionCount = 0;

		// 遍历运行
		for (int i = (end - 1); i >= start; i--) {
			Object obj = Array.get(array, i);
			if (obj == null) {
				continue;
			}
			// 执行处理
			if (!action.action(obj, i)) {
				return actionCount; // 执行失败,终止
			}
			actionCount++; // 执行成功
			// 检查执行数限制
			if (maxCount > 0 && actionCount >= maxCount) {
				break;
			}
		}
		return actionCount;
	}

	/** 通用的数组遍历 **/
	private static int action(Object array, IAction<Object> action, int maxCount, int offset, int size) {
		// 获取数组长度
		int alen = (array != null) ? Array.getLength(array) : 0;
		if (alen <= 0) {
			return 0;
		}
		// 参数调整
		int start = Math.max(offset, 0);
		int end = Math.min(alen, offset + size);
		int actionCount = 0;

		// 遍历运行
		for (int i = start; i < end; i++) {
			Object obj = Array.get(array, i);
			if (obj == null) {
				continue;
			}
			// 执行处理
			if (!action.action(obj, i)) {
				return actionCount; // 执行失败,终止
			}
			actionCount++; // 执行成功
			// 检查执行数限制
			if (maxCount > 0 && actionCount >= maxCount) {
				break;
			}
		}
		return actionCount;
	}

	/** 筛选接口 **/
	public interface IFilter<T> {
		/** 筛选, 返回true为符合条件 **/
		public boolean check(T d, int index);
	}

	/** 处理接口 **/
	public interface IAction<T> {
		/** 遍历, 返回false终止遍历. **/
		public boolean action(T data, int index);
	}

	/********************** 模板函数 ****************************/
	// /** Template Begin **/ 和/** Template End **/不能删除, 用于模板导入

	/** Template Begin **/
	/** 拆分数组 **/
	public static boolean[] subArray(boolean[] array, int offset, int size) {
		return BaseArrayUtils.subArray(boolean[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static boolean get(boolean[] array, int index) {
		return (boolean)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(boolean[] array, boolean value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(boolean[] array, boolean value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static boolean[] add(boolean[] array, boolean value) {
		return BaseArrayUtils.add(boolean[].class, array, value);
	}

	/** 添加到数组中 **/
	public static boolean[] addArray(boolean[] array, boolean[] other) {
		return BaseArrayUtils.addArray(boolean[].class, array, other);
	}

	/** 转成数组 **/
	public static boolean[] toBooleanArray(Collection<Boolean> list) {
		return BaseArrayUtils.toArray(list, boolean[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static boolean[] resetArray(boolean[] array, int size) {
		return BaseArrayUtils.resetArray(boolean[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(boolean[] array, IFilter<Boolean> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(boolean[] array, IFilter<Boolean> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Boolean> findAll(boolean[] array, IFilter<Boolean> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Boolean> findAll(boolean[] array, IFilter<Boolean> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(boolean[] array, IAction<Boolean> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(boolean[] array, IAction<Boolean> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static byte[] subArray(byte[] array, int offset, int size) {
		return BaseArrayUtils.subArray(byte[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static byte get(byte[] array, int index) {
		return (byte)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(byte[] array, byte value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(byte[] array, byte value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static byte[] add(byte[] array, byte value) {
		return BaseArrayUtils.add(byte[].class, array, value);
	}

	/** 添加到数组中 **/
	public static byte[] addArray(byte[] array, byte[] other) {
		return BaseArrayUtils.addArray(byte[].class, array, other);
	}

	/** 转成数组 **/
	public static byte[] toByteArray(Collection<Byte> list) {
		return BaseArrayUtils.toArray(list, byte[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static byte[] resetArray(byte[] array, int size) {
		return BaseArrayUtils.resetArray(byte[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(byte[] array, IFilter<Byte> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(byte[] array, IFilter<Byte> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Byte> findAll(byte[] array, IFilter<Byte> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Byte> findAll(byte[] array, IFilter<Byte> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(byte[] array, IAction<Byte> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(byte[] array, IAction<Byte> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static short[] subArray(short[] array, int offset, int size) {
		return BaseArrayUtils.subArray(short[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static short get(short[] array, int index) {
		return (short)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(short[] array, short value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(short[] array, short value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static short[] add(short[] array, short value) {
		return BaseArrayUtils.add(short[].class, array, value);
	}

	/** 添加到数组中 **/
	public static short[] addArray(short[] array, short[] other) {
		return BaseArrayUtils.addArray(short[].class, array, other);
	}

	/** 转成数组 **/
	public static short[] toShortArray(Collection<Short> list) {
		return BaseArrayUtils.toArray(list, short[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static short[] resetArray(short[] array, int size) {
		return BaseArrayUtils.resetArray(short[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(short[] array, IFilter<Short> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(short[] array, IFilter<Short> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Short> findAll(short[] array, IFilter<Short> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Short> findAll(short[] array, IFilter<Short> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(short[] array, IAction<Short> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(short[] array, IAction<Short> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static int[] subArray(int[] array, int offset, int size) {
		return BaseArrayUtils.subArray(int[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static int get(int[] array, int index) {
		return (int)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(int[] array, int value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(int[] array, int value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static int[] add(int[] array, int value) {
		return BaseArrayUtils.add(int[].class, array, value);
	}

	/** 添加到数组中 **/
	public static int[] addArray(int[] array, int[] other) {
		return BaseArrayUtils.addArray(int[].class, array, other);
	}

	/** 转成数组 **/
	public static int[] toIntArray(Collection<Integer> list) {
		return BaseArrayUtils.toArray(list, int[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static int[] resetArray(int[] array, int size) {
		return BaseArrayUtils.resetArray(int[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(int[] array, IFilter<Integer> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(int[] array, IFilter<Integer> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Integer> findAll(int[] array, IFilter<Integer> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Integer> findAll(int[] array, IFilter<Integer> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(int[] array, IAction<Integer> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(int[] array, IAction<Integer> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static long[] subArray(long[] array, int offset, int size) {
		return BaseArrayUtils.subArray(long[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static long get(long[] array, int index) {
		return (long)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(long[] array, long value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(long[] array, long value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static long[] add(long[] array, long value) {
		return BaseArrayUtils.add(long[].class, array, value);
	}

	/** 添加到数组中 **/
	public static long[] addArray(long[] array, long[] other) {
		return BaseArrayUtils.addArray(long[].class, array, other);
	}

	/** 转成数组 **/
	public static long[] toLongArray(Collection<Long> list) {
		return BaseArrayUtils.toArray(list, long[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static long[] resetArray(long[] array, int size) {
		return BaseArrayUtils.resetArray(long[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(long[] array, IFilter<Long> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(long[] array, IFilter<Long> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Long> findAll(long[] array, IFilter<Long> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Long> findAll(long[] array, IFilter<Long> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(long[] array, IAction<Long> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(long[] array, IAction<Long> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static float[] subArray(float[] array, int offset, int size) {
		return BaseArrayUtils.subArray(float[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static float get(float[] array, int index) {
		return (float)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(float[] array, float value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(float[] array, float value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static float[] add(float[] array, float value) {
		return BaseArrayUtils.add(float[].class, array, value);
	}

	/** 添加到数组中 **/
	public static float[] addArray(float[] array, float[] other) {
		return BaseArrayUtils.addArray(float[].class, array, other);
	}

	/** 转成数组 **/
	public static float[] toFloatArray(Collection<Float> list) {
		return BaseArrayUtils.toArray(list, float[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static float[] resetArray(float[] array, int size) {
		return BaseArrayUtils.resetArray(float[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(float[] array, IFilter<Float> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(float[] array, IFilter<Float> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Float> findAll(float[] array, IFilter<Float> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Float> findAll(float[] array, IFilter<Float> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(float[] array, IAction<Float> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(float[] array, IAction<Float> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 拆分数组 **/
	public static double[] subArray(double[] array, int offset, int size) {
		return BaseArrayUtils.subArray(double[].class, array, offset, size);
	}

	/** 安全获取数据 **/
	public static double get(double[] array, int index) {
		return (double)BaseArrayUtils.get((Object)array, index);
	}

	/** 获取数据在数组中的索引 **/
	public static int indexOf(double[] array, double value) {
		return BaseArrayUtils.indexOf((Object)array, value);
	}

	/** 判断数据是否在数组中 **/
	public static boolean contains(double[] array, double value) {
		return BaseArrayUtils.indexOf((Object)array, value) >= 0;
	}

	/** 添加到数组中 **/
	public static double[] add(double[] array, double value) {
		return BaseArrayUtils.add(double[].class, array, value);
	}

	/** 添加到数组中 **/
	public static double[] addArray(double[] array, double[] other) {
		return BaseArrayUtils.addArray(double[].class, array, other);
	}

	/** 转成数组 **/
	public static double[] toDoubleArray(Collection<Double> list) {
		return BaseArrayUtils.toArray(list, double[].class);
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static double[] resetArray(double[] array, int size) {
		return BaseArrayUtils.resetArray(double[].class, array, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(double[] array, IFilter<Double> filter, int offset, int size) {
		return find((Object) array, filter, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static int find(double[] array, IFilter<Double> filter) {
		return find((Object) array, filter, 0, array.length);
	}

	/** 获取符合条件的数据 **/
	public static List<Double> findAll(double[] array, IFilter<Double> filter, int maxCount, int offset, int size) {
		return findAll((Object) array, filter, maxCount, offset, size);
	}

	/** 获取符合条件的数据 **/
	public static List<Double> findAll(double[] array, IFilter<Double> filter, int maxCount) {
		return findAll((Object) array, filter, maxCount, 0, array.length);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int action(double[] array, IAction<Double> action, int maxCount, int offset, int size) {
		return action((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** 遍历数据, 参数: maxCount:遍历最大数量, 0为全部遍历 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int actionN(double[] array, IAction<Double> action, int maxCount, int offset, int size) {
		return actionN((Object) array, (IAction) action, maxCount, offset, size);
	}

	/** Template End **/

}
