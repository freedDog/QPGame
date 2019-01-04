package com.game.framework.component.data.manager;

/**
 * 数据对象
 * IElement.java
 * @author JiangBangMing
 * 2019年1月3日下午1:40:03
 */
public interface IElement
{
	/** 加载数据 **/
	boolean load();

	/** 卸载数据(最好在卸载时保存数据) **/
	void unload();

}