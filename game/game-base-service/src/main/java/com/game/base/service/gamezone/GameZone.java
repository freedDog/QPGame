package com.game.base.service.gamezone;

import java.util.Arrays;
import java.util.List;

import com.game.framework.utils.collections.ArrayUtils;
import com.game.framework.utils.collections.ListUtils;

/**
 * 区服, 包含多个区服Id代表合服
 * GameZone.java
 * @author JiangBangMing
 * 2019年1月4日下午4:58:48
 */
public class GameZone {
	protected int[] ids; // 对应区服Id

	protected GameZone() {
	}

	public GameZone(int[] ids) {
		super();
		this.ids = ids;
	}

	/** 是否包含区服Id **/
	public boolean contains(int id) {
		return ArrayUtils.contains(ids, id);
	}

	/** 获取第一个gameZoneId **/
	public int getFristId() {
		return (ids != null && ids.length > 0) ? ids[0] : 0;
	}

	public int[] getIds() {
		return ids;
	}

	public List<Integer> getIdList() {
		return ListUtils.asList(ids);
	}

	@Override
	public String toString() {
		return "GameZone [ids=" + Arrays.toString(ids) + "]";
	}

}