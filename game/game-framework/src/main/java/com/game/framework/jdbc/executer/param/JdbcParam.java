package com.game.framework.jdbc.executer.param;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.game.framework.utils.ReflectUtils;


/**
 * jdbc参数处理<br>
 * 细分一个SQL语句中, 参数的获取.
 * JdbcParam.java
 * @author JiangBangMing
 * 2019年1月3日下午4:44:02
 */
public class JdbcParam {
	protected int argIndex; // 获取的参数序号
	protected String fieldName; // 参数的子对象

	protected String sql; // 原语句
	protected String paramStr; // 参数字段

	protected int[][] subRanges; // 接取范围

	protected JdbcParam(String sql, Class<?>[] parameterTypes, String paramStr) throws Exception {
		// 函数参数
		this.paramStr = paramStr;
		this.sql = sql;
		this.subRanges = new int[2][];

		// 从字段中截取出数字, 确定参数序号.
		int nameSize = paramStr.length();
		int end = paramStr.indexOf('.');
		end = (end < 0) ? nameSize : end;
		String indexStr = paramStr.substring(1, end);
		Integer indexObject = Integer.valueOf(indexStr);
		this.argIndex = (indexObject != null) ? indexObject - 1 : -1;
		if (argIndex < 0 || argIndex >= parameterTypes.length) {
			throw new Exception("参数超过函数上限! sql=" + sql + " -> paramStr=" + paramStr);
		}

		// 有后续, 只能是.
		int lastSize = nameSize - end;
		if (lastSize == 1) {
			throw new Exception("参数变量名为空, 点号之后没内容! :" + paramStr);
		} else if (lastSize >= 2) {
			Class<?> clazz = parameterTypes[this.argIndex];
			if (clazz == String.class || !(Object.class.isAssignableFrom(clazz))) {
				throw new Exception("参数类型不可能有变量:" + paramStr + " " + clazz);
			}
			// 获取后缀
			String suffix = paramStr.substring(end + 1, nameSize);
			this.fieldName = suffix;
		}
		return;
	}

	/**
	 * 获取参数
	 * 
	 * @param args
	 *            所有参数(函数对应所有参数)
	 * @param index
	 *            -1为单独获取, -1为数组获取, 即如果参数是数组, 获取对应索引的对象.
	 * @return
	 */
	public Object getParam(Object[] args, int index) throws Exception {
		// 参数获取
		Object obj = args[argIndex];
		// 数组获取
		if (index >= 0) {
			// 判断是否是数组或者列表
			if (List.class.isInstance(obj)) {
				obj = ((List<?>) obj).get(index);
			} else if (Array.class.isInstance(obj)) {
				obj = ((Object[]) obj)[index];
			}
		}
		// 通过get函数获取
		return getObjValue(obj, this);
	}

	public int getArgIndex() {
		return argIndex;
	}

	public String getFieldName() {
		return fieldName;
	}

	/** 获取接取范围起始 **/
	public int getSubStart(int index) {
		return this.subRanges[index][0];
	}

	/** 获取接取范围结束 **/
	public int getSubEnd(int index) {
		return this.subRanges[index][1];
	}

	@Override
	public String toString() {
		return "JdbcParam [argIndex=" + argIndex + ", fieldName=" + fieldName + "]";
	}

	/***************** 静态处理 *****************/

	/** 参数开始字符 **/
	protected static final char startChar = ':';

	/** 参数结束字符 **/
	protected static final char[] endChars = new char[] { ' ', ';', ',', '(', ')', '\'' };

	/** 参数排序 **/
	protected static final Comparator<JdbcParam> paramComparator = new Comparator<JdbcParam>() {
		@Override
		public int compare(JdbcParam o1, JdbcParam o2) {
			int a = o1.argIndex;
			int b = o2.argIndex;
			return Integer.compare(a, b);
		}
	};

	/** 接取范围索引, 源字段范围. **/
	public static final int SUBINDEX_SRC = 0;
	/** 接取范围索引, 运行字段范围. **/
	public static final int SUBINDEX_RUN = 1;

	/** 提取sql语句中的参数(所有带:的字段), 返回带顺序. */
	public static List<JdbcParam> createParams(String sql, Class<?>[] parameterTypes) throws Exception {
		List<JdbcParam> params = new ArrayList<>();
		// 遍历查找所有替换格式
		int offset = 0;
		int sqlSize = sql.length();
		do {
			// 查找:的位置
			int start = sql.indexOf(startChar, offset);
			if (start < 0) {
				break;
			}
			// 找到对应结束位置
			int end = nextChar(sql, endChars, start + 1);
			if (end < 0) {
				end = sqlSize; // 直接到最后.
			}

			// 截取出数据
			String paramStr = sql.substring(start, end);
			JdbcParam jdbcParam = new JdbcParam(sql, parameterTypes, paramStr);
			jdbcParam.subRanges[SUBINDEX_SRC] = new int[] { start, end };
			params.add(jdbcParam);

			// System.out.println("[" + start + ", " + end + "] " + paramStr);
			offset = end + 1;
		} while (offset < sqlSize);
		return params;
	}

	/** 把语句中对应的参数替换成? **/
	public static String resetSql(String sql, List<JdbcParam> params) {
		// 根据序号排序
		int psize = (params != null) ? params.size() : 0;
		if (psize <= 0) {
			return sql;
		}
		// 排序再生成语句(因为必须有序才能找对位置替换)
		// if (psize >= 2)
		// {
		// params = new ArrayList<>(params);
		// Collections.sort(params, paramComparator);
		// }

		// 字符串长度和标记
		int strLen = (sql != null) ? sql.length() : 0;
		int index = 0;
		// 替换处理
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0; i < psize; i++) {
			JdbcParam param = params.get(i);
			// 字段添加
			strBdr.append(sql.substring(index, param.getSubStart(SUBINDEX_SRC)));
			int rsStart = strBdr.length();
			strBdr.append('?');
			int rsEnd = strBdr.length();
			index = param.getSubEnd(SUBINDEX_SRC);
			param.subRanges[SUBINDEX_RUN] = new int[] { rsStart, rsEnd };
		}
		// 最后段
		if (strLen > index) {
			strBdr.append(sql.substring(index));
		}
		return strBdr.toString();
	}

	/** 获取最近的一个相关字符 **/
	protected static int nextChar(String str, char[] chars, int offset) {
		int size = str.length();
		for (int i = offset; i < size; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < chars.length; j++) {
				if (ch == chars[j]) {
					return i;
				}
			}
		}
		return -1;
	}

	/** 获取对象对应变量数据 **/
	@SuppressWarnings("rawtypes")
	public static Object getObjValue(Object obj, JdbcParam param) throws Exception {
		// 空过滤
		String fname = param.getFieldName();
		if (fname == null || fname.length() <= 0) {
			return obj;
		}

		// 判断是否是map
		if (Map.class.isInstance(obj)) {
			return ((Map) obj).get(fname);
		}

		// 对象变量获取
		return ReflectUtils.getValue(obj, fname);
	}
}
