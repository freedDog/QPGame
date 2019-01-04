package com.game.framework.framework.drop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.game.framework.component.log.Log;
import com.game.framework.framework.bean.IProduct;



/**
 * 掉落池基类
 * DropPool.java
 * @author JiangBangMing
 * 2019年1月3日下午2:55:40
 */
public abstract class DropPool<G extends IDropGroup, E extends IDropElement, P extends IProduct> {
	/** 掉落模板对应掉落组 **/
	protected Map<Integer, List<G>> dropGroups;
	/** 掉落组对应掉落物品 **/
	protected Map<Integer, List<E>> dropElements;
	/** 掉落组总概率 */
	protected Map<Integer, Integer> dropGroupTotalRates;

	/** 初始化掉落模板(模板检测规则, 从上自下, 不允许空.) **/
	protected boolean init(List<? extends G> dropGrops, List<? extends E> dropElements) {
		// 初始化掉落组
		this.dropGroups = new HashMap<>();
		for (G dropGrop : dropGrops) {
			// 插入数据
			List<G> list = this.dropGroups.get(dropGrop.getDropId());
			if (list == null) {
				list = new ArrayList<>();
				this.dropGroups.put(dropGrop.getDropId(), list);
			}
			list.add(dropGrop);
		}

		// 初始化掉落对象
		this.dropElements = new HashMap<>();
		for (E dropElement : dropElements) {
			// 插入数据
			List<E> list = this.dropElements.get(dropElement.getGroupId());
			if (list == null) {
				list = new ArrayList<>();
				this.dropElements.put(dropElement.getGroupId(), list);
			}
			list.add(dropElement);
		}

		// 检测数据
		for (int dropId : this.dropGroups.keySet()) {
			if (!checkDrop(dropId)) {
				return false;
			}
		}

		for (G dropGrop : dropGrops) {
			if (!checkDropGroup(dropGrop)) {
				return false;
			}
		}
		for (E dropElement : dropElements) {
			if (!checkDropElement(dropElement)) {
				return false;
			}
		}

		// 统计掉落总概率
		dropGroupTotalRates = new HashMap<>();
		Iterator<Map.Entry<Integer, List<G>>> iter = dropGroups.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, List<G>> entry = iter.next();
			// 获取掉落和掉落组
			List<G> groupList = entry.getValue();
			int gsize = (groupList != null) ? groupList.size() : 0;
			if (gsize <= 0) {
				continue;
			}

			// 遍历掉落组
			for (G group : groupList) {
				// 检测掉落对象数量
				int groupId = group.getGroupId();
				List<E> elements = this.dropElements.get(groupId);
				int esize = (elements != null) ? elements.size() : 0;
				if (esize <= 0) {
					continue;
				}

				// 根据类型判断是否要统计总概率
				int dropType = group.getType();
				if (dropType == DropType.COMBINATION || dropType == DropType.COMBINATION_REPEATABLE) {
					// 不重复类型检测
					if (group.getType() == DropType.COMBINATION && esize < group.getCount()) {
						Log.error("掉落组所属的掉落物品为空或数量异常, groupId : " + group.getGroupId());
						return false;
					}
					// 统计概率
					int maxRate = 0;
					for (IDropElement element : elements) {
						maxRate += element.getRate();
					}
					if (maxRate <= 0) {
						Log.error("掉落组概率和为0, groupId : " + group.getGroupId());
						return false;
					}
					// 记录数据
					dropGroupTotalRates.put(group.getGroupId(), maxRate);
				}
			}
		}
		return true;
	}

	/** 检测掉落 **/
	protected boolean checkDrop(int dropId) {
		List<G> groupList = dropGroups.get(dropId);
		int gsize = (groupList != null) ? groupList.size() : 0;
		if (gsize <= 0) {
			Log.error("掉落组为空! dropId=dropId" + dropId);
			return false;
		}
		return true;
	}

	/** 检测掉落组 **/
	protected boolean checkDropGroup(G dropGroup) {
		// 检测掉落组类型
		int dropType = dropGroup.getType();
		if (dropType != DropType.SIMGLE && dropType != DropType.COMBINATION && dropType != DropType.REGULAR && dropType != DropType.COMBINATION_REPEATABLE) {
			Log.error("未知掉落类型! " + dropGroup);
			return false;
		}

		// 检测数量
		int groupId = dropGroup.getGroupId();
		List<E> elements = dropElements.get(groupId);
		int esize = (elements != null) ? elements.size() : 0;
		if (esize <= 0) {
			Log.error("掉落组没有掉落对象! " + dropGroup);
			return false;
		}

		return true;
	}

	/** 检测掉落对象 **/
	protected boolean checkDropElement(E dropElement) {
		// 检测参数
		int rate = dropElement.getRate();
		if (rate <= 0) {
			Log.error("掉落概率不能少于0!" + dropElement);
			return false;
		}

		// 检测产品是否存在
		P[] products = getProducts(dropElement);
		int psize = (products != null) ? products.length : 0;
		if (psize <= 0) {
			Log.error("掉落对象找不到对应产品!" + dropElement);
			return false;
		}

		// 遍历检测
		for (int i = 0; i < psize; i++) {
			P product = products[i];
			if (product.getCount() <= 0) {
				Log.error("掉落对象对应产品数量不能小于0!" + dropElement + " " + product);
				return false;
			}
		}
		return true;
	}

	/** 执行掉落, 如果没有掉落Id, 返回null */
	public List<P> drop(int dropId) {
		return drop(dropId, 1);
	}

	/** 执行掉落, 如果没有掉落Id, 返回null */
	public List<P> drop(int dropId, int count) {
		// 获取掉落对应掉落组
		List<P> dropProducts = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!drop(dropId, dropProducts)) {
				return null;
			}
		}
		// 乱序
		Collections.shuffle(dropProducts);
		return dropProducts;
	}

	/** 是否存在掉落Id **/
	public boolean isExistDropId(int dropId) {
		return dropGroups.containsKey(dropId);
	}

	/** 使用掉落组处理掉落 **/
	protected boolean drop(int dropId, List<P> dropProducts) {
		// 获取掉落对应掉落组
		List<G> groups = dropGroups.get(dropId);
		if (groups == null || groups.size() == 0) {
			Log.warn("掉落为空!, dropId : " + dropId);
			return false;
		}
		// 遍历掉落组计算掉落
		for (G group : groups) {
			dropGrop(group, dropProducts);
		}
		return true;
	}

	/** 使用掉落组处理掉落 **/
	protected boolean dropGrop(G group, List<P> dropProducts) {
		// 获取掉落组中所有掉落对象
		List<E> elements = dropElements.get(group.getGroupId());
		if (elements == null) {
			Log.error("掉落分组的物品列表为空, groupId : " + group.getGroupId());
			return false;
		}
		// 根据掉落类型执行处理
		int dropType = group.getType();
		switch (dropType) {
		case DropType.SIMGLE:
			getSingleDrop(elements, dropProducts);
			return true;
		case DropType.COMBINATION:
			getCombinationDrop(group, elements, false, dropProducts);
			return true;
		case DropType.REGULAR:
			// 固定掉落, 直接放入列表就行了.
			for (E element : elements) {
				dropElement(element, dropProducts);
			}
			return true;
		case DropType.COMBINATION_REPEATABLE:
			getCombinationDrop(group, elements, true, dropProducts);
			return true;
		}

		Log.warn("未知掉落类型! group=" + group);
		return false;
	}

	/** 获取掉落所有可能掉落对象, 返回null代表不存在这个dropId. **/
	public List<E> getDropElementTempInfos(int dropId) {
		// 获取掉落组
		List<G> groups = dropGroups.get(dropId);
		if (groups == null || groups.size() == 0) {
			Log.error("掉落Id不存在, dropId : " + dropId);
			return null;
		}

		// 添加入列表
		List<E> retList = new ArrayList<>();
		for (IDropGroup group : groups) {
			List<E> elementTemps = dropElements.get(group.getGroupId());
			if (elementTemps == null) {
				continue;
			}
			retList.addAll(elementTemps);
		}
		return retList;
	}

	/** 获取掉落物品 **/
	protected void dropElement(E element, List<P> dropProducts) {
		P[] products = getProducts(element);
		if (products == null || products.length <= 0) {
			Log.warn("获取不到掉落对象对应的商品! " + element);
			return;
		}
		// 添加入物品
		for (int i = 0; i < products.length; i++) {
			P product = products[i];
			dropProducts.add(product);
		}
	}

	/** 获取掉落对象对应的商品 **/
	protected abstract P[] getProducts(E element);

	/** 单独掉落 **/
	protected void getSingleDrop(List<E> dropElements, List<P> dropProducts) {
		// 遍历各个单元
		for (E element : dropElements) {
			// 遍历次数
			for (int i = 0; i < element.getTime(); i++) {
				// 计算概率
				int random = ThreadLocalRandom.current().nextInt(getSimpleRate());
				if (random >= element.getRate()) {
					continue;
				}

				// 中奖了.
				dropElement(element, dropProducts);
			}
		}
	}

	/**
	 * 组合掉落 - M抽N<br>
	 * 
	 * @param repeatable
	 *            是否可重复
	 */
	protected void getCombinationDrop(G group, List<E> dropElements, boolean repeatable, List<P> dropProducts) {
		int size = dropElements.size();
		// 检测抽取数量
		int count = group.getCount();
		if (count > size) {
			count = size;
			Log.warn("组合掉落数量超过物品数量总和, groupId : " + group.getGroupId());
		}
		// 获取总概率
		int maxRate = getGroupTotalRate(group.getGroupId());
		if (maxRate <= 0) {
			return;
		}

		// 获取掉落表(不可重复要重新复制一份)
		List<E> dropTempArray = (!repeatable) ? new ArrayList<>(dropElements) : dropElements;
		// 遍历执行掉落
		for (int i = 0; i < count; i++) {
			int random = ThreadLocalRandom.current().nextInt(maxRate);
			// 遍历找出符合这个概率的数据
			int endPos = 0;
			for (int j = 0; j < size; j++) {
				E dropElementTempInfo = dropTempArray.get(j);
				// 统计概率范围
				endPos += dropElementTempInfo.getRate();
				if (random > endPos) {
					continue;
				}
				// 不可重复情况下, 去掉这个记录.
				if (!repeatable) {
					maxRate -= dropElementTempInfo.getRate();
					dropTempArray.remove(j);
				}

				// 就是他了骚年.
				dropElement(dropElementTempInfo, dropProducts);
				break;
			}
		}
	}

	/** 获取掉落组总概率 **/
	protected int getGroupTotalRate(int groupId) {
		Integer maxRate = dropGroupTotalRates.get(groupId);
		return (maxRate != null) ? maxRate : 0;
	}

	/** 获取基础概率值(10000),用于单独掉落类型的随机分母. **/
	public int getSimpleRate() {
		return 10000;
	}
}