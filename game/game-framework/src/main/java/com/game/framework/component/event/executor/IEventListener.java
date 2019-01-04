package com.game.framework.component.event.executor;

/**
 *  事件处理监听
 * IEventListener.java
 * @author JiangBangMing
 * 2019年1月3日下午1:42:57
 */
public interface IEventListener<K, M, S>
{
	boolean exec(K key, M message, S session);
}
