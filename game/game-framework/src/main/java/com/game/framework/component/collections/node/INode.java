package com.game.framework.component.collections.node;

import java.util.Collection;

/**
 * 节点接口
 * INode.java
 * @author JiangBangMing
 * 2019年1月3日下午1:30:58
 */
public interface INode<K, N> {
	/** key值 **/
	K getKey();

	/** 子节点数量 **/
	int childSize();

	/** 获取子节点 **/
	Collection<N> getChilds();

	/** 添加子节点 **/
	boolean add(N child);

	/** 获取子节点 **/
	N get(K key, boolean all);

	/** 删除子节点 **/
	N remove(K key, boolean all);

	/** 删除子节点 **/
	boolean remove(N node);

}
