package com.game.base.service.inventory;

/**
 * 游戏个体存货<br>
 * GameEntityInventory.java
 * @author JiangBangMing
 * 2019年1月4日下午5:04:25
 */
public abstract class GameEntityInventory extends AbstractInventory {
	/** 在线更新(PS: 如果这个Inventory尚未被加载是不会执行的) **/
	protected void onTimeUpdate(long prevTime, long nowTime, long dt) {
	}

	/** 数据每日重置 **/
	protected void onDayReset() {
	}

	/** 数据每周重置 **/
	protected void onWeekReset() {
	}

	/** 数据每月重置 **/
	protected void onMonthReset() {
	}
}
