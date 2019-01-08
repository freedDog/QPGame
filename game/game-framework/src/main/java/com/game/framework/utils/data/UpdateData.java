package com.game.framework.utils.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 数据<br>
 * 处理数据更新
 * UpdateData.java
 * @author JiangBangMing
 * 2019年1月8日上午10:16:57
 */
public class UpdateData implements IUpdateData, Cloneable {
	private volatile int mod = 0; // 更新计数器
	private volatile int sav; // 保存计数器

	/** 标记更新 **/
	@Override
	public void update() {
		int now = mod; // 为啥这么写, find bug不接受, 但是这里不用太纠结.
		mod = now + 1;
	}

	@Override
	public void commit() {
		commit(this.getMod());
	}

	public synchronized void commit(int sav) {
		this.sav = sav;
	}

	/** 是否发生更新了. sav不等於mod **/
	@JSONField(serialize = false)
	public boolean isUpdate() {
		return mod != sav;
	}

	@JSONField(serialize = false)
	public int getMod() {
		return mod;
	}

	/** 获取更新的列表 **/
	public static <T extends IUpdateData> List<T> getUpdataList(Collection<T> list, boolean commit) {
		// 空判断
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 遍历筛选
		List<T> rets = new ArrayList<>();
		for (T d : list) {
			if (d.isUpdate()) {
				// 自动完成提交
				if (commit) {
					d.commit();
				}
				rets.add(d);
			}
		}
		return rets;
	}
}