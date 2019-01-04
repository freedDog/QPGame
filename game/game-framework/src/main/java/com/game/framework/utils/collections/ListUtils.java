package com.game.framework.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.ObjectUtils.BaseArrayUtils;



/**
 * List工具<br>
 * toList去掉, 改用Arrays.asList(). !!asList似乎没那么全面
 * ListUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:07:06
 */
public final class ListUtils {

	/** 创建数组, 插入一样的数据 **/
	public static <T> List<T> createListBySame(T value, int count) {
		List<T> list = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			list.add(value);
		}
		return list;
	}

	/** 重复查找索引 **/
	private static <T> int[] indexOfOne(List<T> list, List<T> items) {
		// 检测移除数量
		int isize = (items != null) ? items.size() : 0;
		if (isize <= 0) {
			return null;
		}
		// 处理相同重复的值
		Map<T, Integer> map = new HashMap<>(isize);
		int[] indexs = new int[isize];
		// 遍历执行移除处理
		for (int j = 0; j < isize; j++) {
			T item = items.get(j);
			// 获取是否重复的索引
			Integer offset0 = map.get(item);
			int offset = (offset0 != null) ? offset0 : 0;
			// 遍历执行移除
			int index = indexOf(list, item, offset);
			if (index < 0) {
				return null;
			}
			indexs[j] = index;
			map.put(item, index + 1);
		}
		return indexs;
	}

	/** 索引检测 **/
	private static <T> int indexOf(List<T> list, T value, int offset) {
		int lsize = (list != null) ? list.size() : 0;
		for (int i = offset; i < lsize; i++) {
			if (list.get(i) == value) {
				return i;
			}
		}
		return -1;
	}

	public static <T> boolean containsAllOne(List<T> list, List<T> items) {
		// 遍历检测
		int[] indexOf = indexOfOne(list, items);
		return indexOf != null;
	}

	/** 批量删除单个数据 **/
	public static <T> boolean removeAllOne(List<T> list, List<T> removes) {
		// 查找索引
		int[] indexOf = indexOfOne(list, removes);
		if (indexOf == null) {
			return false;
		}

		// 遍历删除
		for (T remove : removes) {
			list.remove((Object) remove);
		}
		return true;
	}

	/** 随机筛选出一个 **/
	public static <T> T random(Collection<T> list) {
		List<T> rets = random(list, 1);
		return (rets != null && rets.size() > 0) ? rets.get(0) : null;
	}

	/** 随机筛选出一定数量 **/
	public static <T> List<T> random(Collection<T> list, int count) {
		count = Math.min(count, list.size()); // 筛选大小.
		List<T> rets = new ArrayList<>(list); // 复制
		Collections.shuffle(rets); // 乱序
		return rets.subList(0, count); // 裁剪
	}

	/** 创建列表 **/
	public static <T> List<T> create(T[] array) {
		List<T> list = new ArrayList<T>();
		int size = (array != null) ? array.length : 0;
		for (int i = 0; i < size; i++) {
			T obj = array[i];
			if (obj == null) {
				continue;
			}
			list.add(obj);
		}
		return list;
	}

	/**
	 * 根据条件删除
	 * 
	 * @param list
	 * @param filter
	 * @return
	 */
	public static <T> boolean remove(Collection<T> list, IFilter<? super T> filter) {
		int count = removeAll(list, filter, 1);
		return count > 0;
	}

	/**
	 * 根据条件删除
	 * 
	 * @param list
	 * @param filter
	 *            筛选器
	 * @param maxCount
	 *            最多执行多少个, 0为无限制
	 * @return
	 */
	public static <T> int removeAll(Collection<T> list, final IFilter<? super T> filter, final int maxCount) {
		// 遍历读取
		IAction<T> action = new IAction<T>() {
			protected int actionCount = 0;

			@Override
			public boolean action(T data, Iterator<?> iter) {
				// 判断过滤
				if (filter == null || filter.check(data)) {
					iter.remove(); // 执行删除
					actionCount++;
				}
				return (maxCount > 0) ? actionCount < maxCount : true; // 数量限制
			}
		};
		return action(list, action, 0); // 顺序,越早得数据在前面
	}

	/**
	 * 获取符合条件的数据
	 * 
	 * @param filter
	 *            过滤器
	 * @return 符合条件的数据
	 */
	public static <T> T find(Collection<T> list, IFilter<? super T> filter) {
		List<T> findList = findAll(list, filter, 1);
		int count = (findList != null) ? findList.size() : 0;
		if (count > 0) {
			return findList.get(0);
		}
		return null; // 没有
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
	public static <T> List<T> findAll(Collection<T> list, final IFilter<? super T> filter, final int maxCount) {
		// 遍历读取
		final List<T> findList = new ArrayList<T>();
		IAction<T> action = new IAction<T>() {
			protected int actionCount = 0;

			@Override
			public boolean action(T data, Iterator<?> iter) {
				// 判断过滤
				if (filter == null || filter.check(data)) {
					findList.add(data); // 加入列表
					actionCount++;
				}
				return (maxCount > 0) ? actionCount < maxCount : true; // 数量限制
			}
		};
		action(list, action, 0); // 顺序,越早得数据在前面
		return findList;
	}

	/** 获取所有相同值的数据数量 **/
	public static <T> int findNum(Collection<T> list, Object v) {
		int num = 0;
		for (T t : list) {
			if (t.equals(v)) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 遍历执行
	 * 
	 * @param action
	 *            执行器
	 * @param maxCount
	 *            最多支持执行数量
	 * @return 执行个数
	 */
	public static <T> int action(Collection<T> list, IAction<? super T> action, int maxCount) {
		if (list.isEmpty() || action == null) {
			return 0; // 空的,无视
		}

		// 遍历运行
		int actionCount = 0;
		Iterator<T> iter = list.iterator();
		while (iter.hasNext()) {
			// 提取消息
			T data = iter.next();
			if (data == null) {
				continue; // 跳过
			}

			// 执行处理
			if (!action.action(data, iter)) {
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

	/**
	 * 倒序执行(用get方式倒序)
	 * 
	 * @param action
	 *            执行器
	 * @param maxCount
	 *            最多支持执行数量
	 * @return 执行个数
	 */
	public static <T> int actionN(List<T> list, IAction<T> action, int maxCount) {
		if (list.isEmpty()) {
			return 0; // 空的,无视
		}

		// 遍历运行
		int actionCount = 0;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			int index = size - i - 1;
			T data = list.get(index);
			// 执行处理
			if (!action.action(data, null)) {
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

	/**
	 * 从数组中读取转换出对应类型的转化<br>
	 * 
	 * @param list
	 * @param cls
	 *            符合的类型
	 * @return
	 */
	public static <T extends P, P> List<T> convert(Collection<P> list, Class<T> cls) {
		List<T> retList = new ArrayList<T>();
		// 遍历转换
		Iterator<P> iter = list.iterator();
		while (iter.hasNext()) {
			P obj = iter.next();
			T ret = ObjectUtils.get(obj, cls);
			if (ret != null) {
				retList.add(ret);
			}
		}
		return retList;
	}

	/**
	 * 复制数组<br>
	 * 建议使用List.subList提高效率.
	 */
	@Deprecated
	public static <T> List<T> subList(List<T> src, int offset, int size) {
		List<T> list = new ArrayList<T>();
		int end = offset + size;
		end = Math.min(end, src.size());
		for (int i = offset; i < end; i++) {
			T obj = src.get(i);
			list.add(obj);
		}
		return list;
	}

	/** 分割数组列表, maxSize为单个数组最大数量. **/
	public static <T> List<List<T>> sublist(List<T> list, int maxSize) {
		int lsize = (list != null) ? list.size() : 0; // 计算总数量
		int ssize = (int) Math.ceil(lsize / (double) maxSize); // 拆分数量

		// 进行切割
		List<List<T>> retList = new ArrayList<>(ssize);
		for (int i = 0; i < ssize; i++) {
			int start = i * maxSize;
			int end = Math.min((i + 1) * maxSize, lsize);
			retList.add(list.subList(start, end));
		}
		return retList;
	}

	/** 转化成list **/
	@SuppressWarnings("unchecked")
	public static <T> List<T> asList(T[] array, int offset, int size) {
		return (List<T>) BaseArrayUtils.asList(Object.class, array, offset, size);
	}

	/** 转化成list **/
	public static <T, S> List<T> asList(List<S> slist, IData<S, T> handler, int offset, int size) {
		// 计算长度
		int ssize = (slist != null) ? slist.size() : 0;
		int esize = Math.min(offset + size, ssize);

		// 插入数据
		List<T> retList = new ArrayList<>(size);
		for (int i = offset; i < esize; i++) {
			S obj = slist.get(i);
			T value = handler.get(obj, slist, i);
			if (value == null) {
				continue;
			}
			retList.add(value);
		}
		return retList;
	}

	/** 转化成list **/
	public static <T, S> List<T> asList(List<S> slist, IData<S, T> handler) {
		return asList(slist, handler, 0, slist.size());
	}

	/** Java有源生的Arrays.asList */
	@Deprecated
	public static <T> List<T> asList(T[] array) {
		return asList(array, 0, (array != null) ? array.length : 0);
	}

	/** 列表显示, 格式: 换行输出 **/
	public static <T> String toString(List<T> list) {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("[\n");
		int lsize = (list != null) ? list.size() : 0;
		for (int i = 0; i < lsize; i++) {
			if (i > 0) {
				strBdr.append(",\n");
			}
			T item = list.get(i);
			strBdr.append(item);
		}
		strBdr.append("\n]\n");
		return strBdr.toString();
	}

	// 过滤接口
	public interface IFilter<T> {
		public boolean check(T d);
	}

	// 处理接口
	public interface IAction<T> {
		public boolean action(T data, Iterator<?> iter);
	}

	/** 遍历接口 **/
	public static abstract class Foreach<T> implements IAction<T> {
		public abstract void action(T data);

		@Override
		public boolean action(T data, Iterator<?> iter) {
			action(data);
			return true;
		}
	}

	/** 筛选对象 **/
	public static abstract class Filter<T, K> implements IFilter<T> {
		protected K key;

		public Filter(K k) {
			this.key = k;
		}
	}

	/** 数据转化接口 **/
	public interface IData<T1, T2> {
		T2 get(T1 data, List<T1> list, int index);
	}

	/********************** 模板函数 ****************************/
	// /** Template Begin **/ 和/** Template End **/不能删除, 用于模板导入

	/** Template Begin **/
	/** 转化成list **/
	public static List<Boolean> asList(boolean[] array, int offset, int size) {
		return BaseArrayUtils.asList(Boolean.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Boolean> asList(boolean[] array) {
		return BaseArrayUtils.asList(Boolean.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Byte> asList(byte[] array, int offset, int size) {
		return BaseArrayUtils.asList(Byte.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Byte> asList(byte[] array) {
		return BaseArrayUtils.asList(Byte.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Short> asList(short[] array, int offset, int size) {
		return BaseArrayUtils.asList(Short.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Short> asList(short[] array) {
		return BaseArrayUtils.asList(Short.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Integer> asList(int[] array, int offset, int size) {
		return BaseArrayUtils.asList(Integer.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Integer> asList(int[] array) {
		return BaseArrayUtils.asList(Integer.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Long> asList(long[] array, int offset, int size) {
		return BaseArrayUtils.asList(Long.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Long> asList(long[] array) {
		return BaseArrayUtils.asList(Long.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Float> asList(float[] array, int offset, int size) {
		return BaseArrayUtils.asList(Float.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Float> asList(float[] array) {
		return BaseArrayUtils.asList(Float.class, array, 0, array.length);
	}

	/** 转化成list **/
	public static List<Double> asList(double[] array, int offset, int size) {
		return BaseArrayUtils.asList(Double.class, array, offset, size);
	}

	/** 转化成list **/
	public static List<Double> asList(double[] array) {
		return BaseArrayUtils.asList(Double.class, array, 0, array.length);
	}

	/** Template End **/

}
