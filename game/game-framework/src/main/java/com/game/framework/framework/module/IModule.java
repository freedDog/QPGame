package com.game.framework.framework.module;

/**
 * 模块接口
 * IModule.java
 * @author JiangBangMing
 * 2019年1月3日下午3:02:13
 */
public interface IModule {
	/** 模块初始化 **/
	boolean init();

	/** 模块关闭 **/
	void destroy();
}
