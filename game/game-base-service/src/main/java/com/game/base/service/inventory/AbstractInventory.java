package com.game.base.service.inventory;

/**
 * 数据存活<br>
 * 继承以前的用法, 这里只负责处理逻辑.<br>
 * save先不管, 还没定怎么用.
 * AbstractInventory.java
 * @author JiangBangMing
 * 2019年1月4日下午5:03:54
 */
public abstract class AbstractInventory {
	/** 加载数据 **/
	protected abstract boolean load();

	/** 卸载数据 **/
	protected abstract void unload();

	/** 保存数据 **/
	protected abstract boolean save();

}