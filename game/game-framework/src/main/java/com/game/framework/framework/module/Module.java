package com.game.framework.framework.module;

import com.game.framework.framework.component.ComponentMgr;

/**
 * 模块基类
 * Module.java
 * @author JiangBangMing
 * 2019年1月3日下午3:02:50
 */
public abstract class Module extends ComponentMgr {

	/** 模块初始化 **/
	public boolean init() {
		return false;
	}

	/** 模块关闭 **/
	public abstract void destroy();

}