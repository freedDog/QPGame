package com.game.framework.framework.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.collections.ListUtils;


/**
 *  随机名称池<br>
 * RandomNamePool.java
 * @author JiangBangMing
 * 2019年1月3日下午3:10:06
 */
public class RandomNamePool<T extends RandomNamePool.IRandomNameTempInfo> {
	private Map<Integer, List<T>> names;
	private Set<String> lockNames; // 线上使用锁定名称

	/** 初始化随机模板 **/
	public boolean init(List<T> tempInfos) {
		names = new HashMap<>();
		lockNames = new HashSet<>();

		// 读取随机名称模板
		for (T tempInfo : tempInfos) {
			// 过滤处理
			int level = tempInfo.getLevel();
			String value = tempInfo.getName();
			if (level == 1 && StringUtils.isEmpty(value)) {
				Log.error("level为1的随机名字不能为空! " + tempInfo);
				return false;
			}
			// 按照等级划分
			List<T> list = names.get(level);
			if (list == null) {
				list = new ArrayList<>();
				names.put(level, list);
			}
			list.add(tempInfo);
		}
		return true;
	}

	/** 获取一个随机名称, 可能为空. **/
	public String getRandomName(IRandomNameFilter<T> filter) {
		StringBuilder strBdr = new StringBuilder();
		// 随机执行
		final int len = 3; // 名字组装长度
		for (int i = 0; i < len; i++) {
			int level = i + 1;
			// 筛选内容
			List<T> list = names.get(level);
			list = ListUtils.findAll(list, filter, 0); // 筛选出符合的内容
			int lsize = (list != null) ? list.size() : 0;
			if (lsize <= 0) {
				continue; // 没有这个
			}

			// 执行随机
			T tempinfo = RandomPool.randomOne(list, -1);
			if (tempinfo == null) {
				continue;
			}
			// 跳过空名称
			if (StringUtils.isEmpty(tempinfo.getName())) {
				continue;
			}
			// 组合文本
			strBdr.append(tempinfo.getName());
		}
		// 空判断
		if (StringUtils.isEmpty(strBdr.toString())) {
			return null;
		}
		return strBdr.toString();
	}

	/** 随机多组名字 **/
	public List<String> getRandomNames(IRandomNameFilter<T> filter, int count) {
		List<String> retList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String name = getRandomName(filter);
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			retList.add(name);
		}
		return retList;
	}

	/** 锁定名字(锁定成功返回true, 否则被人占用了就返回false) **/
	public synchronized boolean lockName(String name) {
		return lockNames.add(name);
	}

	/** 解锁锁定名字 **/
	public synchronized void unlockName(String name) {
		lockNames.remove(name);
	}

	/** 过滤接口 **/
	public interface IRandomNameFilter<T> extends ListUtils.IFilter<T> {
	}

	/** 随机名模板接口 **/
	public interface IRandomNameTempInfo extends RandomPool.IRandomTempInfo {
		/** 获取随机层级 **/
		int getLevel();

		/** 获取名字 **/
		String getName();
	}
}