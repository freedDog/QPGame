package com.game.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.game.framework.utils.ObjectUtils.BaseArrayUtils;


/**
 * 字符串工具
 * StringUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午3:28:38
 */
public class StringUtils {

	/** 字符串编码格式 **/
	public static class CharsetUtils {
		/** 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块 */
		public static final String US_ASCII = "US-ASCII";
		/** ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1 */
		public static final String ISO_8859_1 = "ISO-8859-1";
		/** 8 位 UCS 转换格式 */
		public static final String UTF8 = "UTF-8";
		/** 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序 */
		public static final String UTF_16BE = "UTF-16BE";
		/** 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序 */
		public static final String UTF_16LE = "UTF-16LE";
		/** 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识 */
		public static final String UTF_16 = "UTF-16";
		/** 中文超大字符集 */
		public static final String GBK = "GBK";

		/** 判断是否是GBK格式 **/
		public static boolean isGBK(String value) {
			return Charset.forName("GBK").newEncoder().canEncode(value);
		}

		/** 将字符编码转换成US-ASCII码 */
		public static String toASCII(String str) {
			return changeToCharset(str, US_ASCII);
		}

		/** 将字符编码转换成ISO-8859-1码 */
		public static String toISO_8859_1(String str) {
			return changeToCharset(str, ISO_8859_1);
		}

		/** 将字符编码转换成UTF-8码 */
		public static String toUTF8(String str) {
			return changeToCharset(str, UTF8);
		}

		/** 将字符编码转换成UTF-16BE码 */
		public static String toUTF_16BE(String str) {
			return changeToCharset(str, UTF_16BE);
		}

		/** 将字符编码转换成UTF-16LE码 */
		public static String toUTF_16LE(String str) {
			return changeToCharset(str, UTF_16LE);
		}

		/** 将字符编码转换成UTF-16码 */
		public static String toUTF_16(String str) {
			return changeToCharset(str, UTF_16);
		}

		/** 将字符编码转换成GBK码 */
		public static String toGBK(String str) {
			return changeToCharset(str, GBK);
		}

		/** 把US-ASCII码字符串转成本地编码格式 */
		public static String fromASCII(String str) {
			return changeFromCharset(str, US_ASCII);
		}

		/** 将字符编码转换成ISO-8859-1码 */
		public static String fromISO_8859_1(String str) {
			return changeFromCharset(str, ISO_8859_1);
		}

		/** 将字符编码转换成UTF-8码 */
		public static String fromUTF8(String str) {
			return changeFromCharset(str, UTF8);
		}

		/** 将字符编码转换成UTF-16BE码 */
		public static String fromUTF_16BE(String str) {
			return changeFromCharset(str, UTF_16BE);
		}

		/** 将字符编码转换成UTF-16LE码 */
		public static String fromUTF_16LE(String str) {
			return changeFromCharset(str, UTF_16LE);
		}

		/** 将字符编码转换成UTF-16码 */
		public static String fromUTF_16(String str) {
			return changeFromCharset(str, UTF_16);
		}

		/** 将字符编码转换成GBK码 */
		public static String fromGBK(String str) {
			return changeFromCharset(str, GBK);
		}

		/** 编码转换 **/
		public static String changeCharset(String str, String newCharset, String oldCharset) {
			if (str == null) {
				return null;
			}
			try {
				byte[] bs = str.getBytes(oldCharset);
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 编码转换 **/
		public static String changeToCharset(String str, String newCharset) {
			if (str == null) {
				return null;
			}
			try {
				byte[] bs = str.getBytes();
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 编码转换(把原本不同的编码转成本地) **/
		public static String changeFromCharset(String str, String oldCharset) {
			if (str == null) {
				return null;
			}
			try {
				byte[] bs = str.getBytes(oldCharset);
				return new String(bs);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/** 字符串是否为空(包括null和空字符) **/
	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() <= 0);
	}

	/** 字符串比较(包括null的比较) **/
	public static int compare(String src, String dsc) {
		boolean emptyA = src == null || src.length() <= 0;
		boolean emptyB = dsc == null || dsc.length() <= 0;
		if (emptyA && emptyB) {
			return 0; // 2个都空
		} else if (!emptyA && emptyB) {
			return 1; // 不一样空
		} else if (emptyA && !emptyB) {
			return -1;
		}
		// 比较
		return src.compareTo(dsc);

	}

	/** 随机字符 **/
	public static final char[] random_chars = new char[] { 'A', 'B', 'C', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'K', 'L', 'Z', 'X', 'S', 'T', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', };

	/** 随机数值字符 **/
	public static final char[] random_numbers = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', };

	/** 随机字符 **/
	public static String random(int count) {
		return random(random_chars, count);
	}

	/** 随机数字 **/
	public static String randomNumber(int count) {
		return random(random_numbers, count);
	}

	/** 从char数组中随机出N个长度的字符串 **/
	public static String random(char[] chars, int count) {
		int charCount = chars.length;
		if (charCount <= 0) {
			return "";
		}
		// 生成字符串
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < count; i++) {
			int index = ((int) (Math.random() * charCount));
			char c = chars[index];
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	/** 从数组中随机出N个长度的字符串 **/
	public static String random(String[] chars, int count) {
		int charCount = chars.length;
		if (charCount <= 0) {
			return "";
		}
		// 生成字符串
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < count; i++) {
			int index = ((int) (Math.random() * charCount));
			String c = chars[index];
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	/** 首字母大写 **/
	public static String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 标准化转换字符串<br>
	 * 浮点型保留2位<br>
	 * 数组转化成[a,b,c,...]<br>
	 * **/
	public static String toString(Object obj) {
		// 空处理
		if (obj == null) {
			return "";
		}
		// 返回数据
		Class<?> clazz = obj.getClass();
		if (clazz == Double.class || clazz == double.class) {
			double v = (Double) obj;
			if (Math.abs(v - 0.0) < 0.001) {
				return "0";
			}
			return String.format("%.02f", (float) v);
		}
		if (clazz == Float.class || clazz == float.class) {
			float v = (Float) obj;
			if (Math.abs(v - 0.0) < 0.001) {
				return "0";
			}
			return String.format("%.02f", (float) v);
		} else if (clazz == java.math.BigDecimal.class) {
			double v = ((java.math.BigDecimal) obj).doubleValue();
			if (Math.abs(v - 0.0) < 0.001) {
				return "0";
			}
			return String.format("%.02f", v);
		} else if (Object[].class.isAssignableFrom(clazz)) {
			return Arrays.toString((Object[]) obj);
		} else if (clazz == int[].class) {
			return Arrays.toString((int[]) obj);
		} else if (clazz == int[][].class) {
			StringBuilder strBdr = new StringBuilder();
			strBdr.append("[");
			int[][] v = (int[][]) obj;
			for (int i = 0; i < v.length; i++) {
				int[] v0 = v[i];
				if (i > 0) {
					strBdr.append(",");
				}
				strBdr.append(Arrays.toString((int[]) v0));
			}
			strBdr.append("]");
			return strBdr.toString();
		}

		return obj.toString();
	}

	/** 输出无符号的byte数组 **/
	public static String toString(byte[] buffer) {
		int count = (buffer != null) ? buffer.length : 0;
		if (count <= 0) {
			return "[]";
		}
		// 遍历输出文本
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("[");
		for (int i = 0; i < count; i++) {
			byte b = buffer[i];
			int v = (int) (b & 0xff);
			strBuf.append(v);
			// 不是最后一个加上,
			if (i < count - 1) {
				strBuf.append(",");
			}
		}
		strBuf.append("]");
		return strBuf.toString();
	}

	/** 生成唯一uuid **/
	public static String uuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/** 创建字符串, 失败返回null **/
	public static String createString(byte[] data, String charsetName) {
		try {
			return new String(data, charsetName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将异常信息转化成字符串 **/
	public static String getExceptionString(Throwable t) {
		if (t == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			t.printStackTrace(new PrintStream(baos));
		} catch (Exception e) {
		} finally {
			try {
				baos.close();
			} catch (Exception e) {
			}
		}
		return baos.toString();
	}

	/** 获取类的包名 **/
	public static String getPacketName(Class<?> clazz) {
		String cn = clazz.getName();
		String sn = clazz.getSimpleName();
		String packetName = cn.substring(0, cn.lastIndexOf(sn) - 1);
		return packetName;
	}

	/** 是否是数字 **/
	public static boolean isNumeric(String data) {
		if (data == null) {
			return false;
		}
		return data.matches("-?[0-9]+");
	}

	/** 获取字符串的长度，如果有中文，则每个中文字符计为2位 */
	public static int length(String value) {
		// // 处理数据
		// int valueLength = 0;
		// String chinese = "[\u0391-\uFFE5]";
		// // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		// for (int i = 0; i < value.length(); i++) {
		// /* 获取一个字符 */
		// String temp = value.substring(i, i + 1);
		// /* 判断是否为中文字符 */
		// if (temp.matches(chinese)) {
		// /* 中文字符长度为2 */
		// valueLength += 2;
		// } else {
		// /* 其他字符长度为1 */
		// valueLength += 1;
		// }
		// }
		// return valueLength;
		// 分类
		int[] rets = lengthByClassify(value);
		return rets[0] + rets[1] * 2;
	}

	/**
	 * 获取字符串的长度<br>
	 * 
	 * @return [字符数量, 中文数量]
	 * */
	public static int[] lengthByClassify(String value) {
		// 处理数据
		int[] rets = new int[] { 0, 0 };
		String chinese = "[\u0391-\uFFE5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				rets[1]++;
			} else {
				/* 其他字符长度为1 */
				rets[0]++;
			}
		}
		return rets;
	}

	/** 裁剪字符串(二维数据) **/
	public static <K, T> Map<K, T> splitToMap(String str, String regexA, String regexB, ISplitMap<? extends K, ? extends T> split) {
		if (StringUtils.isEmpty(str)) {
			return new HashMap<>();
		}
		try {
			String[] strs = str.split(regexA);
			int ssize = (strs != null) ? strs.length : 0;
			// 遍历处理
			Map<K, T> map = new HashMap<>(ssize);
			for (int i = 0; i < ssize; i++) {
				String[] strs0 = strs[i].split(regexB);
				int ssize0 = (strs0 != null) ? strs0.length : 0;
				if (ssize0 < 2) {
					continue; // 错误
				}
				// 解析参数
				K key = split.splitKey(strs0[0]);
				T value = split.split(strs0[1]);
				map.put(key, value);
			}
			return map;
		} catch (Exception e) {
			System.err.println("拆分字符串异常,data:" + str + ",regexA:" + regexA + ",regexB:" + regexB);
		}
		return null;
	}

	/** 裁剪字符串 **/
	public static <T> List<T> splitToList(String str, String regex, ISplit<? extends T> split) {
		if (StringUtils.isEmpty(str)) {
			return new ArrayList<>();
		}

		try {
			String[] strs = str.split(regex);
			int ssize = (strs != null) ? strs.length : 0;
			// 遍历处理
			List<T> list = new ArrayList<>(ssize);
			for (int i = 0; i < ssize; i++) {
				T obj = split.split(strs[i].trim());
				list.add(obj);
			}
			return list;
		} catch (Exception e) {
			System.err.println("拆分字符串异常,data:" + str + ",regex:" + regex);
		}
		return null;
	}

	/** 裁剪解析接口 **/
	public interface ISplit<T> {
		T split(String str);
	}

	/** 裁剪解析接口 **/
	public interface ISplitMap<K, T> extends ISplit<T> {
		K splitKey(String str);
	}

	/** 转成数组格式 **/
	public static <T> String toString(T[] array, String regex) {
		StringBuilder strBdr = new StringBuilder();
		int asize = (array != null) ? array.length : 0;
		for (int i = 0; i < asize; i++) {
			if (i > 0) {
				strBdr.append(regex);
			}
			strBdr.append(array[i]);
		}
		return strBdr.toString();
	}

	/** 转成数组格式 **/
	public static <T> String toString(List<T> list, String regex) {
		StringBuilder strBdr = new StringBuilder();
		int asize = (list != null) ? list.size() : 0;
		for (int i = 0; i < asize; i++) {
			if (i > 0) {
				strBdr.append(regex);
			}
			strBdr.append(list.get(i));
		}
		return strBdr.toString();
	}

	/** 转成数组格式 **/
	public static <K, T> String toString(Map<K, T> map, String regexA, String regexB) {
		StringBuilder strBdr = new StringBuilder();
		int i = 0;
		for (Map.Entry<K, T> entry : map.entrySet()) {
			if (i > 0) {
				strBdr.append(regexA);
			}
			i++;

			// 字符串
			K key = entry.getKey();
			T value = entry.getValue();
			strBdr.append(key);
			strBdr.append(regexB);
			strBdr.append(value);
		}
		return strBdr.toString();
	}

	/** 提取正则匹配数组 **/
	private static int match(String str, String regex, List<String> out, int maxSize) {
		// 正则表达式处理
		int index = 0;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String oneTermContent = matcher.group();
			out.add(oneTermContent);
			// 判断上限
			index++;
			if (maxSize > 0 && index > maxSize) {
				break;
			}
		}
		return index;
	}

	/** 提取正则匹配数组 **/
	public static List<String> match(String str, String regex, int maxSize) {
		List<String> strList = new ArrayList<>(maxSize);
		match(str, regex, strList, maxSize);
		return strList;
	}

	/** 提取正则匹配数组 **/
	public static List<String> match(String str, String regex) {
		return match(str, regex, 0);
	}

	/** 提取正则匹配的第一个 **/
	public static String matchOne(String str, String regex) {
		List<String> list = match(str, regex, 1);
		return (list.size() > 0) ? list.get(0) : null;
	}

	/********************** 模板函数 ****************************/
	// /** Template Begin **/ 和/** Template End **/不能删除, 用于模板导入

	/** Template Begin **/
	/** 拆分成数组 **/
	public static boolean[] splitToBoolean(String str, String regex) {
		return BaseArrayUtils.splitTo(boolean[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static boolean[][] splitToBoolean2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(boolean[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static boolean[][][] splitToBoolean3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(boolean[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成boolean, 无法转化自动返回默认值. **/
	public static boolean toBoolean(String str) {
		return ObjectUtils.stringToValue(str, boolean.class);
	}

	/** 转化成数组文本. **/
	public static String toString(boolean[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static byte[] splitToByte(String str, String regex) {
		return BaseArrayUtils.splitTo(byte[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static byte[][] splitToByte2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(byte[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static byte[][][] splitToByte3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(byte[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成byte, 无法转化自动返回默认值. **/
	public static byte toByte(String str) {
		return ObjectUtils.stringToValue(str, byte.class);
	}

	/** 转化成数组文本. **/
	public static String toString(byte[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static short[] splitToShort(String str, String regex) {
		return BaseArrayUtils.splitTo(short[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static short[][] splitToShort2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(short[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static short[][][] splitToShort3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(short[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成short, 无法转化自动返回默认值. **/
	public static short toShort(String str) {
		return ObjectUtils.stringToValue(str, short.class);
	}

	/** 转化成数组文本. **/
	public static String toString(short[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static int[] splitToInt(String str, String regex) {
		return BaseArrayUtils.splitTo(int[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static int[][] splitToInt2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(int[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static int[][][] splitToInt3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(int[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成int, 无法转化自动返回默认值. **/
	public static int toInt(String str) {
		return ObjectUtils.stringToValue(str, int.class);
	}

	/** 转化成数组文本. **/
	public static String toString(int[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static long[] splitToLong(String str, String regex) {
		return BaseArrayUtils.splitTo(long[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static long[][] splitToLong2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(long[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static long[][][] splitToLong3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(long[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成long, 无法转化自动返回默认值. **/
	public static long toLong(String str) {
		return ObjectUtils.stringToValue(str, long.class);
	}

	/** 转化成数组文本. **/
	public static String toString(long[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static float[] splitToFloat(String str, String regex) {
		return BaseArrayUtils.splitTo(float[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static float[][] splitToFloat2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(float[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static float[][][] splitToFloat3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(float[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成float, 无法转化自动返回默认值. **/
	public static float toFloat(String str) {
		return ObjectUtils.stringToValue(str, float.class);
	}

	/** 转化成数组文本. **/
	public static String toString(float[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** 拆分成数组 **/
	public static double[] splitToDouble(String str, String regex) {
		return BaseArrayUtils.splitTo(double[].class, str, regex);
	}

	/** 拆分成2级数组 **/
	public static double[][] splitToDouble2(String str, String regexA, String regexB) {
		return BaseArrayUtils.splitTo2(double[][].class, str, regexA, regexB);
	}

	/** 拆分成3级数组 **/
	public static double[][][] splitToDouble3(String str, String regexA, String regexB, String regexC) {
		return BaseArrayUtils.splitTo3(double[][][].class, str, regexA, regexB, regexC);
	}

	/** 转化成double, 无法转化自动返回默认值. **/
	public static double toDouble(String str) {
		return ObjectUtils.stringToValue(str, double.class);
	}

	/** 转化成数组文本. **/
	public static String toString(double[] array, String regex) {
		return BaseArrayUtils.toString((Object)array, regex);
	}

	/** Template End **/

}
