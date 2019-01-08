package com.game.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;

/**
 * 数据处理工具
 * DataUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午12:56:00
 */
public final class DataUtils {
	
	public static String dateToString(Date date){
		if(date == null)
			return null;
		try {
			DateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format0.format(date);
		} catch (Exception e) {
			Log.error("时间转化失败:" + e);
		}
		return null;
	}

	/** 时间转化 yyyy-MM-dd'T'HH:mm **/
	public static Date toHtmlDate(String dateStr) {
		if (dateStr == null) {
			return null;
		}
		if (dateStr.length() < 1) {
			return null;
		}

		try {
			DateFormat format0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			return format0.parse(dateStr);
		} catch (Exception e) {
			Log.error("时间转化失败:" + e);
		}

		return null;
	}

	/** 时间转化 yyyy-MM-dd HH:mm:ss **/
	public static Date toHtmlDate0(String dateStr) {

		if (dateStr == null) {
			return null;
		}
		if (dateStr.length() < 1) {
			return null;
		}

		try {
			DateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format0.parse(dateStr);
		} catch (Exception e) {
			Log.error("时间转化失败:" + e);
		}

		return null;
	}

	/** 转成Boolean **/
	public static boolean toBoolean(String str) {
		try {
			// 空检测
			if (str == null || str.length() <= 0) {
				return false;
			}
			// 值检测
			if (str.equals("true")) {
				return true;
			}
			if (str.equals("True")) {
				return true;
			}
			if (str.equals("TRUE")) {
				return true;
			}
			// 数值检测
			int v = toInt(str);
			if (v > 0) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/** 转化成int, 默认为0. **/
	public static short toShort(String str) {
		try {
			return Short.valueOf(str);
		} catch (Exception e) {
		}
		return 0;
	}

	/** 转化成int, 默认为0. **/
	public static int toInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return 0;
	}

	public static float toFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {

		}

		return 0;

	}

	/** 转化成long, 默认为0. **/
	public static long toLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
		}
		return 0L;
	}

	/** 裁剪字符串 **/
	public static float[] splitToFloat(String str, String regex) {
		// 空处理
		if (StringUtils.isEmpty(str)) {
			return new float[0];
		}
		try {
			// 裁剪
			String[] strs = str.split(regex);
			float[] array = new float[strs.length];
			for (int i = 0; i < strs.length; i++) {
				array[i] = Float.parseFloat(strs[i].trim());
			}
			return array;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + str + ",regex:" + regex, e);
		}
		return null;
	}

	/** 裁剪字符串 **/
	public static long[] splitToLong(String str, String regex) {
		if (StringUtils.isEmpty(str)) {
			return new long[0];
		}

		try {
			String[] strs = str.split(regex);
			long[] array = new long[strs.length];
			for (int i = 0; i < strs.length; i++) {
				array[i] = Long.parseLong(strs[i].trim());
			}
			return array;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + str + ",regex:" + regex, e);
		}
		return null;
	}

	/** 2级拆分 **/
	public static long[][] splitToLong2(String data, String regexA, String regexB) {
		// 空处理
		if (StringUtils.isEmpty(data)) {
			return new long[0][0];
		}
		try {
			String[] strs = data.split(regexA);
			long[][] rets = new long[strs.length][2];
			for (int i = 0; i < strs.length; i++) {
				rets[i] = splitToLong(strs[i], regexB);
				if (rets[i] == null) {
					return null; // 解析错误
				}
			}
			return rets;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + data + ",regex:" + regexA + ",regex2:" + regexB, e);
		}
		return null;
	}

	/** 裁剪字符串 **/
	public static List<Long> splitToLong(String str) {
		// 空处理
		if (StringUtils.isEmpty(str)) {
			return new ArrayList<>();
		}
		try {
			// 裁剪
			String[] strs = str.split(",");
			List<Long> longList = new ArrayList<>(strs.length);
			for (int i = 0; i < strs.length; i++) {
				longList.add(Long.parseLong(strs[i].trim()));
			}
			return longList;
		} catch (Exception e) {
			Log.error("拆分字符串异常,str:" + str, e);
		}
		return null;
	}

	/** 裁剪字符串 **/
	public static int[] splitToInt(String str, String regex) {
		// 空处理
		if (StringUtils.isEmpty(str)) {
			return new int[0];
		}
		try {
			// 裁剪
			String[] strs = str.split(regex);
			int[] array = new int[strs.length];
			for (int i = 0; i < strs.length; i++) {
				array[i] = Integer.parseInt(strs[i].trim());
			}
			return array;
		} catch (Exception e) {
			Log.error("拆分字符串异常,str=\"" + str + "\", regex=" + regex, e);
		}
		return null;
	}

	/** 2级拆分 **/
	public static int[][] splitToInt2(String data, String regexA, String regexB) {
		// 空处理
		if (StringUtils.isEmpty(data)) {
			return new int[0][0];
		}
		try {
			String[] strs = data.split(regexA);
			int[][] rets = new int[strs.length][2];
			for (int i = 0; i < strs.length; i++) {
				rets[i] = splitToInt(strs[i], regexB);
				if (rets[i] == null) {
					return null; // 解析错误
				}
			}
			return rets;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + data + ",regex:" + regexA + ",regex2:" + regexB, e);
		}
		return null;
	}

	/** 3级拆分 **/
	public static int[][][] splitToInt3(String data, String regexA, String regexB, String regexC) {
		// 空处理
		if (StringUtils.isEmpty(data)) {
			return new int[0][0][0];
		}

		try {
			String[] strs = data.split(regexA);
			int[][][] rets = new int[strs.length][][];
			for (int i = 0; i < strs.length; i++) {
				rets[i] = splitToInt2(strs[i], regexB, regexC);
				if (rets[i] == null) {
					return null; // 解析错误
				}
			}
			return rets;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + data + ",regex:" + regexA + ",regex2:" + regexB, e);
		}
		return null;
	}

	/** 裁剪字符串 **/
	public static double[] splitToDouble(String str, String regex) {
		// 空处理
		if (StringUtils.isEmpty(str)) {
			return new double[0];
		}
		try {
			// 裁剪
			String[] strs = str.split(regex);
			double[] array = new double[strs.length];
			for (int i = 0; i < strs.length; i++) {
				array[i] = Double.parseDouble(strs[i].trim());
			}
			return array;
		} catch (Exception e) {
			Log.error("拆分字符串异常,data:" + str + ",regex:" + regex, e);
		}
		return null;
	}

	/** 转成数组格式 **/
	public static String toString(long[] array, String regex) {
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
	public static String toString(int[] array, String regex) {
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

	/** 转成字符串 **/
	public static String toString(int[][] aryData) {
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0, len = aryData.length; i < len; i++) {
			if (i > 0) {
				strBdr.append("|");
			}
			strBdr.append(toString(aryData[i], ","));
		}
		return strBdr.toString();
	}

	/** 转成字符串 **/
	public static String toString(long[][] aryData) {
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0, len = aryData.length; i < len; i++) {
			if (i > 0) {
				strBdr.append("|");
			}
			strBdr.append(toString(aryData[i], ","));
		}
		return strBdr.toString();
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

	/** 根据Id获得对应的区服Id **/
	public static int getGameZoneId(long id) {
		return (id > 0) ? (int) (id / 10000000000000L) : 0;
	}
	
	/** 根据Id获得对应的区服Id **/
	public static int getGameZoneIdByGuildId(long guildId) {
		return (guildId > 0) ? (int) (guildId / 10000000L) : 0;
	}

	/** 获取区服起始Id **/
	public static long getGameZoneStartId(int idType, int gameZoneId) {
		if (idType == 12) { // 公会
			return gameZoneId * 10000000L;
		}
		return gameZoneId * 10000000000000L;
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static int[] resetArray(int[] array, int size) {
		int size0 = (array != null) ? array.length : 0;
		if (size0 != size) {
			// 重新创建数据
			int[] news = new int[size];
			System.arraycopy(array, 0, news, 0, size0);
			return news;
		}
		return array;
	}

	/** 重设数组长度, 数组长度一样则不改变. (不够则增加到对应长度用0代替, 超过则删掉数据) **/
	public static long[] resetArray(long[] array, int size) {
		int size0 = (array != null) ? array.length : 0;
		if (size0 != size) {
			// 重新创建数据
			long[] news = new long[size];
			System.arraycopy(array, 0, news, 0, size0);
			return news;
		}
		return array;
	}

	/** 把数据添加到数组中(新增复制数组) **/
	public static int[] copyToNew(int[] aryData, int data) {
		int[] aryNewData = new int[aryData.length + 1];
		System.arraycopy(aryData, 0, aryNewData, 0, aryData.length);
		aryNewData[aryNewData.length - 1] = data;
		return aryNewData;
	}

	/** 判断数组中是否有相同的数字 **/
	public static boolean isRepeated(int[] aryData) {
		for (int i = 0; i < aryData.length - 1; i++) {
			for (int j = i + 1; j < aryData.length; j++) {
				if (aryData[i] > 0 && aryData[i] == aryData[j]) {
					return true;
				}
			}
		}

		return false;
	}

}
