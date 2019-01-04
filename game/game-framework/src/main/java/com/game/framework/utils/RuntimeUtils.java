package com.game.framework.utils;

/**
 * 运行时工具类 RuntimeUtils.java
 * 
 * @author JiangBangMing 2019年1月2日下午5:19:41
 */
public class RuntimeUtils {
	
	/**
	 * 获取堆栈信息
	 * @param stackIndex
	 * 				开始位置
	 * @param depth
	 *            深度
	 * @return
	 */
	public static String getStackTraceString(int stackIndex, int depth) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		if (st == null) {
			return "";
		}
		if ((stackIndex >= st.length) || (stackIndex < 0)) {
			return "";
		}
		if (depth == 1) {
			return st[stackIndex].toString();
		}
		depth = depth != 0 ? depth : st.length - stackIndex;

		StringBuilder strBdr = new StringBuilder();
		strBdr.append(st[stackIndex].toString());
		for (int i = 0; i < depth - 1; i++) {
			strBdr.append("\r\n\tat ");
			strBdr.append(st[(stackIndex + i + 1)].toString());
		}
		return strBdr.toString();
	}
}
