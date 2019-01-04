package com.game.framework.component.collections.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.collections.node.NodeUtils.IAction;
import com.game.framework.component.collections.node.NodeUtils.IFilter;
import com.game.framework.utils.collections.ListUtils;



/**
 * 树结构节点<br>
 * Node.java
 * @author JiangBangMing
 * 2019年1月3日下午1:32:09
 */
public class Node<K, N extends Node<K, N>> implements INode<K, N> {
	protected final K key;
	protected N parent;
	protected final Map<K, N> childs;

	public Node(K key) {
		this.key = key;
		childs = new HashMap<>();
		parent = null;
	}

	/** 获取第一个子节点 **/
	public N getFrist() {
		return (childs.size() > 0) ? childs.values().iterator().next() : null;
	}

	@Override
	public int childSize() {
		return (childs != null) ? childs.size() : 0;
	}

	@Override
	public Collection<N> getChilds() {
		return childs.values();
	}

	/** 添加子节点 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(N child) {
		if (child.parent == this) {
			return false; // 已经是了
		}
		if (child.parent != null) {
			child.parent.remove(child);
		}
		// 添加修改
		child.parent = (N) this;
		childs.put(child.getKey(), child);
		return true;
	}

	@Override
	public N get(final K key, boolean all) {
		if (!all) {
			return childs.get(key);
		}
		// 遍历子节点查找
		IFilter<N> filter = new IFilter<N>() {
			@Override
			public boolean check(N d) {
				if (key == d.getKey()) {
					return true;
				}
				return false;
			}
		};
		return this.find(filter, all);
	}

	/**
	 * 遍历
	 * 
	 * @param action
	 * @param all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int action(IAction<? super N> action, int maxCount, boolean all) {
		return NodeUtils.action((N) this, action, maxCount, all);
	}

	/**
	 * 筛选出符合条件的所有节点
	 * 
	 * @param filter
	 * @param maxCount
	 * @param all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<N> findAll(IFilter<? super N> filter, int maxCount, boolean all) {
		return NodeUtils.findAll((N) this, filter, maxCount, all);
	}

	/**
	 * 筛选出符合条件的第一个节点
	 * 
	 * @param filter
	 * @param all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public N find(IFilter<? super N> filter, boolean all) {
		return NodeUtils.find((N) this, filter, all);
	}

	/**
	 * 查找子节点中符合的条件
	 * 
	 * @param id
	 * @return
	 */
	public N find(final K id) {
		ListUtils.IFilter<N> filter = new ListUtils.IFilter<N>() {
			@Override
			public boolean check(N d) {
				if (id == d.getKey()) {
					return true;
				}
				return false;
			}

		};
		return ListUtils.find(childs.values(), filter);
	}

	/**
	 * 刪除符合條件的节点
	 * 
	 * @param filter
	 * @param maxCount
	 * @param all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<N> removeAll(IFilter<? super N> filter, int maxCount, boolean all) {
		return NodeUtils.removeAll((N) this, filter, maxCount, all);
	}

	/**
	 * 删除符合条件的节点
	 * 
	 * @param filter
	 * @param all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public N remove(IFilter<? super N> filter, boolean all) {
		return NodeUtils.remove((N) this, filter, all);
	}

	@Override
	public boolean remove(N child) {
		if (child.parent == this) {
			child.parent = null;
		}
		this.childs.remove(child.getKey());
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N remove(final K key, boolean all) {
		IFilter<N> filter = new IFilter<N>() {
			@Override
			public boolean check(N d) {
				if (key == d.getKey()) {
					return true;
				}
				return false;
			}
		};
		return NodeUtils.remove((N) this, filter, all);
	}

	/**
	 * 从父节点中删除自己
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public N removeOfParant() {
		N parent = (N) this.parent;
		if (parent != null) {
			parent.remove((N) this);
			return (N) parent;
		}
		return null;
	}

	@Override
	public K getKey() {
		return key;
	}

	public N getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "Node [key=" + key + " childs=" + childs.values() + "]";
	}

}
