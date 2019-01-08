package com.game.framework.utils.collection.entity;

import com.game.framework.component.data.manager.IElement;

/**
 *  实体对象 
 * IEntity.java
 * @author JiangBangMing
 * 2019年1月8日上午10:10:35
 */
public interface IEntity extends IElement {
	/** 保存数据 **/
	boolean save();

	/** 判断是否有效 **/
	boolean isAlive();

	/** 更新有效时间 **/
	void updateActiveTime();
}
