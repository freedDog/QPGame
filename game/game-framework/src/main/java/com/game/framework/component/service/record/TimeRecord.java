package com.game.framework.component.service.record;

import com.game.framework.utils.StringUtils;

/**
 *  时间记录对象
 * TimeRecord.java
 * @author JiangBangMing
 * 2019年1月3日下午1:50:15
 */
public class TimeRecord<K> {
	public final K id;
	protected int useMinTime = Integer.MAX_VALUE; // 最小时间
	protected int useMaxTime = Integer.MIN_VALUE; // 最大时间
	protected int useTime; // 总时间
	protected int useCount; // 总次数
	protected int warnCount; // 警告次数
	protected String tag; // 备注

	public TimeRecord(K id) {
		super();
		this.id = id;
	}

	public void add(int useTime) {
		this.useTime += useTime; // 总时间
		useCount++;
		useMinTime = Math.min(useMinTime, useTime);
		useMaxTime = Math.max(useMaxTime, useTime);
	}

	public void addWarn() {
		warnCount++;
	}

	public int getUserAverageTime() {
		// 木有统计
		if (useCount == 0) {
			return 0;
		}

		// 不足3次的统计
		if (useCount < 3) {
			return useTime / useCount;
		}

		// 正常的统计(去掉最大和最小)
		int useTime = this.useTime - useMaxTime - useMinTime;
		int useCount = this.useCount - 2;

		return (useTime / useCount);
	}

	public int getUseMinTime() {
		return (useMinTime != Integer.MIN_VALUE) ? useMinTime : -1;
	}

	public int getUseMaxTime() {
		return (useMaxTime != Integer.MAX_VALUE) ? useMaxTime : -1;
	}

	public int getWarnCount() {
		return warnCount;
	}

	public int getUseCount() {
		return useCount;
	}

	public K getId() {
		return id;
	}

	public int getUseTime() {
		return useTime;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append(id);
		strBuf.append(":\r\n");
		strBuf.append("averageTime:");
		strBuf.append(this.getUserAverageTime());
		strBuf.append("\tmaxTime:");
		strBuf.append(this.getUseMaxTime());
		strBuf.append("\tminTime:");
		strBuf.append(this.getUseMinTime());
		strBuf.append("\tuseCount:");
		strBuf.append(this.getUseCount());
		strBuf.append("\tuseTotalTime:");
		strBuf.append(this.getUseTime());
		strBuf.append("\twarnCount:");
		strBuf.append(this.getWarnCount());
		if (!StringUtils.isEmpty(tag)) {
			strBuf.append("\ttag:");
			strBuf.append(tag);
		}
		return strBuf.toString();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
