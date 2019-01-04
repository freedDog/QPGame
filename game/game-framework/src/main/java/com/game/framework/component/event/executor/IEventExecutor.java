package com.game.framework.component.event.executor;

/**
 * 事件处理器接口<br>
 * 传入key可找到对应事件监听, 并且执行<br>
 * 同个事件可包含多个key.
 * IEventExecutor.java
 * @author JiangBangMing
 * 2019年1月3日下午1:42:11
 */
public interface IEventExecutor<K, M, S>
{
	/** 触发事件 **/
	boolean exec(K key, M message, S session);
}
