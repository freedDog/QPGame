package com.game.base.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具<br>
 * 保证多线程下随机处理.
 * RandomUtils.java
 * @author JiangBangMing
 * 2019年1月4日下午4:20:27
 */
public final class RandomUtils {
	public static int randomInt(int max) {
		return ThreadLocalRandom.current().nextInt(max);
	}

	public static int randomInt(int min, int max) {
		if (min == max) {
			return min;
		}
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	/** 随机 **/
	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	/** 概率成功 **/
	public static boolean randomBoolean(int base, int total) {
		int r = RandomUtils.randomInt(total);
		return r < base;
	}

	/**
	 * 从数组出随机一个
	 * 
	 * @param <T>
	 * 
	 * @param <T>
	 **/
	public static int randomByArray(int array[]) {
		int i = randomInt(0, array.length);
		return array[i];
	}
}