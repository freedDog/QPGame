package com.game.framework.component.collections.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 树节点工具
 * NodeUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:31:30
 */
public class NodeUtils {

	/** 移除单个 **/
	public static <T extends INode<?, T>> T remove(T node, final IFilter<? super T> filter, boolean all) {
		List<T> list = removeAll(node, filter, 1, all);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	/** 批量移除 **/
	public static <T extends INode<?, T>> List<T> removeAll(T node, final IFilter<? super T> filter, final int maxCount, boolean all) {
		// 判断子节点数量
		if (node.childSize() <= 0) {
			return null;
		}
		// 创建执行器
		final List<T> list = new ArrayList<>();
		NodeUtils.Action<T> action = new NodeUtils.Action<T>() {
			@Override
			public boolean action(T data, Iterator<?> iter) {
				// 判断过滤
				if (filter == null || filter.check(data)) {
					iter.remove(); // 执行删除
					actionCount++;
					list.add(data);

					// 检测最大数量限制
					if (maxCount > 0) {
						return actionCount < maxCount;
					}
				}
				return true;
			}
		};
		action(node, action, 0, all);
		return list; // 输出执行次数
	}

	// public static <T extends Node<T>> List<T> removeAll(T node, final
	// IFilter<? super T> filter, final int maxCount, boolean all) {
	// final List<T> removeList = new ArrayList<T>();
	//
	// NodeUtils.IAction<T> action = new NodeUtils.IAction<T>() {
	// protected int actionCount = 0;
	//
	// @Override
	// public boolean action(T data, Iterator<?> iter) {
	// // 判断过滤
	// if (filter == null || filter.check(data)) {
	// iter.remove(); // 执行删除
	// removeList.add(data); // 加入列表
	// actionCount++;
	// }
	// // 检测最大数量限制
	// if (maxCount > 0) {
	// return actionCount < maxCount;
	// }
	// return true;
	// }
	// };
	//
	// node.action(action, all);
	// return removeList;
	// }

	public static <T extends INode<?, T>> T find(T node, final IFilter<? super T> filter, boolean all) {
		List<T> findList = findAll(node, filter, 1, all);
		int count = (findList != null) ? findList.size() : 0;
		if (count > 0) {
			return findList.get(0);
		}
		return null; // 没有

	}

	/** 查找 **/
	public static <T extends INode<?, T>> List<T> findAll(T node, final IFilter<? super T> filter, final int maxCount, boolean all) {
		// 判断子节点数量
		if (node.childSize() <= 0) {
			return null;
		}
		// 读取列表
		final List<T> findList = new ArrayList<T>();
		// 遍历器
		NodeUtils.Action<T> action = new NodeUtils.Action<T>() {
			@Override
			public boolean action(T data, Iterator<?> iter) {
				// 判断过滤
				if (filter == null || filter.check(data)) {
					findList.add(data); // 加入列表
					actionCount++;
					// 检测最大数量限制
					if (maxCount > 0) {
						return actionCount < maxCount;
					}
				}
				return true;
			}
		};
		action(node, action, -1, all);
		return findList;
	}

	/** 遍历处理 **/
	public static <T extends INode<?, T>> int action(T node, IAction<? super T> action, int maxCount, boolean all) {
		// 判断子节点数量
		if (node.childSize() <= 0) {
			return 0;
		}
		Collection<T> childs = node.getChilds();

		// 遍历节点
		int actionCount = 0;
		Iterator<T> iter = childs.iterator();
		while (iter.hasNext()) {
			// 读取子类
			T child = iter.next();

			// 执行处理
			if (!action.action(child, iter)) {
				return actionCount; // 执行失败,终止
			}
			actionCount++; // 执行成功
			// 检查执行数限制
			if (maxCount > 0) {
				if (actionCount >= maxCount) {
					break;
				}
			}

			// 遍历子节点的节点
			if (all) {
				// 计算子节点最多执行多少个
				int nextCount = maxCount;
				if (maxCount > 0) {
					nextCount = maxCount - actionCount;
				}
				// 执行子节点
				int ccount = action(child, action, nextCount, all);
				if (ccount <= 0) {
					continue;
				}
				actionCount += ccount;
				// 检查执行数限制
				if (maxCount > 0) {
					if (actionCount >= maxCount) {
						break;
					}
				}
			}
		}
		return actionCount;
	}

	/** 过滤接口 **/
	public interface IFilter<T> {
		public boolean check(T d);
	}

	/** 处理接口 **/
	public interface IAction<T> {
		public boolean action(T data, Iterator<?> iter);
	}

	protected static abstract class Action<T> implements IAction<T> {
		protected int actionCount; // 执行次数

		public int getActionCount() {
			return actionCount;
		}

	}

}
