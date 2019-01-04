package com.game.framework.component.collections.map.lru;

/**
 * 移除监听器,在数据从缓存中移除时调用
 * LRURemoveListener.java
 * @author JiangBangMing
 * 2019年1月3日下午1:23:55
 */
public interface LRURemoveListener<K, V> {
	/** 自动删除 */
	public static final int removeType_auto = 0;
	/** 操作删除 */
	public static final int removeType_remove = 1;
	/** 操作全部清除 */
	public static final int removeType_clear = 2;

	public void handle(K k, V v, int type);
}