package com.game.framework.framework.component;

/**
 * 组件基类<br>
 * 只用于继承,没其他作用.
 * Component.java
 * @author JiangBangMing
 * 2019年1月3日上午11:54:53
 */
public abstract class Component {
	/** 初始化函数 **/
	protected abstract boolean init();

	/** 销毁函数 **/
	protected abstract void destroy();
}
