package com.game.framework.utils.data;

/**
 * 数据<br>
 * 处理数据更新 IUpdateData.java
 * 
 * @author JiangBangMing 2019年1月8日上午10:16:19
 */
public interface IUpdateData {
	/** 是否更新 **/
	boolean isUpdate();

	/** 标记更新 **/
	void update();

	/** 完成更新 **/
	void commit();
}
