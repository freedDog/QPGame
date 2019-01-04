package com.game.framework.framework.random;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机池<br>
 * 支持概率随机
 * RandomPool.java
 * @author JiangBangMing
 * 2019年1月3日下午3:09:34
 */
public class RandomPool<T extends RandomPool.IRandomTempInfo> {
	protected List<T> tempInfos;
	protected int totalRate;

	public boolean init(List<T> tempInfos) {
		this.tempInfos = tempInfos;
		totalRate = getTotalRate(tempInfos);
		return true;
	}

	/**
	 * 单独掉落 <br>
	 * 每一组都单独计算概率掉落<br>
	 * 
	 * @param singleRate
	 *            计算的概率
	 * @param maxcount
	 *            最大数量, 超过这个数量, 不在随机出来了.
	 * **/
	public void randomSingle(List<T> out, int maxcount, int singleRate) {
		randomSingle(out, singleRate, maxcount, tempInfos);
	}

	/**
	 * 组合掉落 - M抽N<br>
	 * 
	 * @param repeatable
	 *            是否可重复
	 * @param totalRate
	 *            总概率, 如果为-1, 则自动从total列表统计出来
	 * @param count
	 *            随机出来的数量
	 */
	public void randomCombination(List<T> out, boolean repeatable, int count) {
		randomCombination(out, repeatable, count, tempInfos, totalRate);
	}

	/**
	 * 掉落 - M抽1<br>
	 * 
	 * @param totalRate
	 *            总概率, 如果为-1, 则自动从total列表统计出来
	 */
	public T randomOne() {
		return randomOne(tempInfos, totalRate);
	}

	/**
	 * 单独掉落 <br>
	 * 每一组都单独计算概率掉落<br>
	 * 
	 * @param singleRate
	 *            计算的概率
	 * @param maxcount
	 *            最大数量, 超过这个数量, 不在随机出来了.
	 * **/
	public static <T extends RandomPool.IRandomTempInfo> void randomSingle(List<T> out, int singleRate, int maxcount, List<T> totalList) {
		// 遍历各个单元
		int count = 0;
		for (T tempInfo : totalList) {
			// 计算概率
			int random = ThreadLocalRandom.current().nextInt(singleRate);
			if (random >= tempInfo.getRate()) {
				continue; // 没中咯
			}

			// 中奖了.
			out.add(tempInfo);

			// 检测数量
			count++;
			if (count >= maxcount) {
				break;
			}
		}
	}

	/**
	 * 掉落 - M抽1<br>
	 * 
	 * @param totalRate
	 *            总概率, 如果为-1, 则自动从total列表统计出来
	 */
	public static <T extends RandomPool.IRandomTempInfo> T randomOne(List<T> totalList, int totalRate) {
		List<T> out = new ArrayList<>(1);
		randomCombination(out, false, 1, totalList, totalRate);
		return (out.size() > 0) ? out.get(0) : null;
	}

	/**
	 * 组合掉落 - M抽N<br>
	 * 
	 * @param repeatable
	 *            是否可重复
	 * @param totalRate
	 *            总概率, 如果为-1, 则自动从total列表统计出来
	 * @param count
	 *            随机出来的数量
	 */
	public static <T extends RandomPool.IRandomTempInfo> void randomCombination(List<T> out, boolean repeatable, int count, List<T> totalList, int totalRate) {
		// 判断数量
		int size = (totalList != null) ? totalList.size() : 0;
		if (size <= 0) {
			return;
		}

		// 获取总概率
		if (totalRate < 0) {
			totalRate = getTotalRate(totalList); // 重新统计总概率
			if (totalRate <= 0) {
				return;
			}
		}

		// 获取掉落表(不可重复要重新复制一份)
		List<T> tempList = (!repeatable) ? new ArrayList<>(totalList) : totalList;
		// 遍历执行掉落
		for (int i = 0; i < count; i++) {
			int random = ThreadLocalRandom.current().nextInt(totalRate);
			// 遍历找出符合这个概率的数据
			int endPos = 0;
			for (int j = 0; j < size; j++) {
				T tempInfo = tempList.get(j);
				// 统计概率范围
				endPos += tempInfo.getRate();
				if (random > endPos) {
					continue;
				}
				// 不可重复情况下, 去掉这个记录.
				if (!repeatable) {
					totalRate -= tempInfo.getRate();
					tempList.remove(j);
				}

				// 就是他了骚年.
				out.add(tempInfo);
				break;
			}
		}
	}

	/** 获取掉落组总概率 **/
	public static <T extends RandomPool.IRandomTempInfo> int getTotalRate(List<T> totalList) {
		// 空过滤
		if (totalList == null) {
			return 0;
		}
		// 遍历统计
		int total = 0;
		for (T tempInfo : totalList) {
			total += tempInfo.getRate();
		}
		return total;
	}

	/** 随机名模板接口 **/
	public interface IRandomTempInfo {
		/** 获取概率权重 **/
		int getRate();
	}
}