package com.game.framework.framework.bag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.framework.component.ChangeCounter;
import com.game.framework.component.change.IChangeResult;
import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;


/**
 * 背包系统<br>
 * Bag.java
 * @author JiangBangMing
 * 2019年1月3日下午2:37:22
 */
public abstract class Bag<T extends Item<D>, D extends IItemTempInfo> {
	protected Map<Long, T> elements; // 查找表
	protected Map<Integer, Long> slots; // 位置信息(用于查找检测, 以物品的为准.)
	protected Set<T> hides; // 隐藏列表(不在背包格子上显示的物品)

	protected Set<T> adds; // 新增列表
	protected Set<T> updates; // 更新列表
	protected Set<T> removes; // 更新列表

	public final ChangeCounter changeCounter;

	public Bag() {
		slots = new HashMap<>();
		elements = new LinkedHashMap<>(); // ConcurrentSkipListMap为有序哈希
		hides = new HashSet<>(); // 隐藏列表

		adds = new HashSet<>();
		updates = new HashSet<>();
		removes = new HashSet<>();

		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				flushCache();
			}
		};
	}

	/** 初始化背包, 放入物品 **/
	public boolean init(List<T> items) {
		try {
			this.changeCounter.beginChange();
			for (T item : items) {
				long itemId = item.getItemId();
				elements.put(itemId, item);
				// 记录位置
				int slot = item.getSlot();
				if (slot < 0) {
					hides.add(item);
					continue; // 隐藏物品
				}
				// 检测位置是否被占用
				Long nowId = slots.get(slot);
				if (nowId != null) {
					slot = this.getEmptySlot();
					item.setSlot(slot);
				}
				slots.put(slot, itemId);
			}
		} finally {
			this.changeCounter.commitChange(false);
		}
		return true;
	}

	/** 获取当前有空位的位置(从头遍历, 0为起始) **/
	public int getEmptySlot() {
		int slot = 0;
		while (true) {
			Long itemId = slots.get(slot);
			if (itemId == null || itemId == 0L) {
				return slot;
			}
			slot++;
		}
	}

	/** 重设位置 **/
	public boolean resetSlot(T item, int slot) {
		// 检测物品所需
		if (item == null || item.getBag() != this) {
			Log.warn("这个物品不是这个背包的! " + item, true);
			return false;
		}

		// 检测之前的位置
		int preSlot = item.getSlot();
		if (preSlot == slot) {
			return false; // 位置相同
		}

		// // 显示物品需要检测背包大小
		// if (preSlot <= 0 && slot > 0)
		// {
		//
		// }

		// 清除位置
		if (preSlot >= 0) {
			slots.remove(preSlot); // 从物品格子表中移除
		}

		// 判断位置
		if (slot < 0) {
			item.setSlot(slot); // 隐藏
			hides.add(item);
			item.update();
			return true;
		}

		// 处理修改
		Long nowId = this.slots.get(slot);
		if (nowId != null && nowId > 0) {
			// 如果原本没有位置, 给替换的对象找个新位置.
			preSlot = (preSlot >= 0) ? preSlot : this.getEmptySlot();

			// 替换位置
			T src = this.get(nowId);
			src.setSlot(preSlot);
			slots.put(preSlot, nowId);
			src.update();
		}

		// 位置上没物品, 直接移动
		item.setSlot(slot);
		slots.put(slot, item.getItemId());
		hides.remove(item); // 从隐藏列表中移除
		item.update();
		return true;
	}

	/** 背包整理, 把同类型的物品堆叠在一起 **/
	protected boolean tidyByMerge() {
		// 检测数量
		int isize = elements.size();
		if (isize <= 1) {
			return true; // 没有东西整理个蛋
		}
		// 列出当前所有物品
		List<T> items = new ArrayList<>(elements.values()); // 当前所有物品列表
		try {
			this.changeCounter.beginChange();
			// 倒序找出符合的插入的内容(从最后1个到第一个, 第一个不用检测.)
			for (int i = isize - 1; i > 0; i--) {
				T item = items.get(i);
				if (item == null || !item.isVisible() || item.getSlot() < 0) {
					continue; // 隐藏物品, 不整理
				}

				// 检测数量
				int count = item.getCount();
				if (count <= 0) {
					// 移除掉
					removeItem(item);
					// 不可能出现这种情况的吧, 出现了就有鬼了.
					Log.warn("物品中出现一个数量为0的物品, 应该是要删除的! " + item);
					continue;
				}

				// 检测模板
				D tempInfo = item.getTempInfo();
				int itemTempId = tempInfo.getTemplateId();
				int maxCount = tempInfo.getMaxCount(); // 堆叠上限

				// 判断是否是单体物品, 并且数量刚好1个.
				if (maxCount == 1) {
					continue; // 这个是个单体物品, 不用堆叠处理.
				}

				// 从前面找能堆叠入的数量
				for (int j = 0; j < i; j++) {
					T check = items.get(j);
					if (check == null || check.getTemplateId() != itemTempId || (check.getItemId() == item.getItemId())) {
						continue; // 空或者不符合条件
					}
					// 检测数量
					int needCount = maxCount - check.getCount();
					if (needCount <= 0) {
						continue; // 满了
					}
					// 检测可插入数量
					int addCount = (count <= needCount) ? count : needCount;
					count -= addCount;

					// 修改物品数量
					changeItem(item, count);
					changeItem(check, check.getCount() + addCount);

					// 检测还有没有数量
					if (count <= 0) {
						break;
					}
				}

				// 检测空处理
				if (count <= 0) {
					continue; // 这个物品已经合并完了, 当做不存在吧.
				}
			}
		} finally {
			this.changeCounter.commitChange();
		}
		return true;
	}

	/** 背包整理, 按照顺序排序格子 **/
	protected boolean tidyBySort() {
		// 列出当前所有物品
		hides.clear();
		List<T> tidys = new ArrayList<>(); // 当前所有物品列表
		for (T item : elements.values()) {
			if (item == null || !item.isVisible() || item.getSlot() < 0) {
				hides.add(item); // 添加到隐藏列表
				continue; // 隐藏物品, 不整理
			}
			tidys.add(item);
		}
		// 整理合并完毕, 重新获取一份当前所有的
		int tsize = (tidys != null) ? tidys.size() : 0;
		if (tsize >= 2) {
			Comparator<? super T> c = getTipyComparator();
			Collections.sort(tidys, c);
		}

		try {
			this.changeCounter.beginChange();
			// 整理位置
			this.slots.clear();
			for (int i = 0; i < tsize; i++) {
				int slot = i;
				// 设置
				T item = tidys.get(i);
				int preSlot = item.getSlot();
				if (preSlot != slot) {
					// 变化记录
					item.setSlot(slot);
					this.slots.put(slot, item.getItemId());
					updateItem(item);
					continue;
				}
				// 常规记录
				this.slots.put(slot, item.getItemId());
			}
		} finally {
			this.changeCounter.commitChange();
		}

		return true;
	}

	/** 背包整理(把可堆叠的堆叠起来) **/
	public void tidy() {
		// 判断是否有东西
		if (elements.isEmpty()) {
			return;
		}
		try {
			this.changeCounter.beginChange();
			// 整理合并
			tidyByMerge();
			// 排序整理
			tidyBySort();
		} finally {
			this.changeCounter.commitChange();
		}
	}

	/** 获取整理排序比较器 **/
	protected Comparator<? super T> getTipyComparator() {
		return new Comparator<T>() {
			@Override
			public int compare(T a, T b) {
				return Integer.compare(a.getTemplateId(), b.getTemplateId());
			}
		};
	}

	/** 检测是否能修改 **/
	public Result check(int itemTempId, long change, boolean stack) {
		if (change == 0) {
			return result(IChangeResult.NOCHANGE, itemTempId, change, (short) 0);
		}

		// 检测物品模板
		D tempInfo = getTempInfo(itemTempId);
		if (tempInfo == null) {
			return result(IChangeResult.IDERROR, itemTempId, change, (short) 0);
		}

		// 检测修改
		Result result = onChangeStart(null, tempInfo, change, (short) 0);
		if (!result.isSucceed()) {
			return result;
		}

		// 判断当前数量是新增还是删除
		if (change < 0) {
			// 判断数量是否够扣除
			int nowCount = getCount(itemTempId);
			if ((nowCount + change) < 0) {
				return result(IChangeResult.NOTENOUGH, itemTempId, change, (short) 0);
			}
			// 数量足够
			return result(IChangeResult.SUCCESS, itemTempId, change, (short) 0);
		}
		// 检测新增所需物品格子是否足够(判断堆叠数)
		if (!checkNewCell(tempInfo, (int) change, stack, null)) {
			return result(IChangeResult.NOSPACE, itemTempId, change, (short) 0);
		}
		return result(IChangeResult.SUCCESS, itemTempId, change, (short) 0);
	}

	/** 创建结果消息 **/
	protected Result result(int code, int tempTempId, long count, short source, Object... args) {
		return result(code, 0L, tempTempId, null, count, source, args);
	}

	/** 创建结果消息 **/
	protected Result result(int code, long id, int tempTempId, T item, long count, short source, Object... args) {
		return Result.create(code, null);
	}

	/**
	 * 添加物品<br>
	 * 
	 * @param stack
	 *            是否堆叠
	 * @param args
	 *            其他参数
	 **/
	protected Result add(int itemTempId, int count, short source, boolean stack, Object... args) {
		D tempInfo = getTempInfo(itemTempId);
		if (tempInfo == null) {
			return result(IChangeResult.IDERROR, itemTempId, count, source, args);
		}
		// 执行
		return add(tempInfo, count, source, stack, args);
	}

	/**
	 * 添加物品<br>
	 * 
	 * @param stack
	 *            是否堆叠
	 * **/
	protected Result add(D tempInfo, int count, short source, boolean stack, Object... args) {
		int itemTempId = tempInfo.getTemplateId();
		if (count <= 0) {
			Log.error("添加物品失败, 数量不对! itemId : " + tempInfo.getTemplateId(), true);
			return result(IChangeResult.NOCHANGE, itemTempId, count, source, args);
		}

		// 修改开始
		Result result = onChangeStart(null, tempInfo, count, source, args);
		if (!result.isSucceed()) {
			return result;
		}

		try {
			this.changeCounter.beginChange();

			int maxCount = tempInfo.getMaxCount();
			int prevCount = 0;
			int nowValue = 0;

			// 先计算可以往已存在物品上叠加的数量
			List<T> stackedItems = new ArrayList<>(); // 可队列的物品
			// 需要堆叠情况, 新增物品所需新增格子
			if (!checkNewCell(tempInfo, count, stack, stackedItems)) {
				return result(IChangeResult.NOSPACE, itemTempId, count, source, args);
			}

			// 获取修改前的数量
			prevCount = this.getCount(itemTempId);

			// 把物品堆叠上去
			int checkCount = count; // 重新统计剩余堆叠数量
			for (T element : stackedItems) {
				int remaining = Math.max(element.getMaxCount() - element.getCount(), 0);
				if (remaining <= 0) {
					continue; // 满了
				}
				// 堆叠物品
				int addCount = (checkCount >= remaining) ? remaining : checkCount; // 计算堆叠数量
				checkCount -= addCount;
				int setCount = element.getCount() + addCount;
				changeItem(element, setCount);

				// 检测还是否需要堆叠
				if (checkCount <= 0) {
					break;
				}
			}

			// 把剩余的没堆叠的按照新增的方式加上
			while (checkCount > 0) {
				int addCount = (checkCount >= maxCount) ? maxCount : checkCount; // 堆满一个数量
				checkCount -= addCount;
				// 插入新物品
				T newItem = createItem(tempInfo, addCount);
				long newId = newItem.getItemId();
				if (elements.containsKey(newId)) {
					Log.error("添加新物品错误, id已存在! " + newItem);
					break;
				}
				// 记录位置
				newItem.setSlot(getEmptySlot());
				slots.put(newItem.getSlot(), newItem.getItemId());
				// 插入物品
				elements.put(newItem.getItemId(), newItem);
				this.adds.add(newItem);
				onAddItem(newItem);
			}

			// 修改完后的数值变化
			nowValue = prevCount + count;
			// 修改事件
			onChange(null, tempInfo, prevCount, nowValue, count, source, args);
			// 修改完成事件
			onChangeComplete(null, tempInfo, prevCount, nowValue, count, source, args);
		} finally {
			changeCounter.commitChange();
		}
		return result(IChangeResult.SUCCESS, itemTempId, count, source, args);
	}

	/** 删除物品 **/
	protected Result remove(int itemTempId, int count, short source, Object... args) {
		D tempInfo = getTempInfo(itemTempId);
		if (tempInfo == null) {
			return result(IChangeResult.IDERROR, itemTempId, count, source, args);
		}
		return remove(tempInfo, count, source, args);
	}

	/** 删除物品 **/
	protected Result remove(D tempInfo, int count, short source, Object... args) {
		int itemTempId = tempInfo.getTemplateId();
		if (count <= 0) {
			Log.error("消耗物品失败, 数量不对! itemId : " + tempInfo.getTemplateId(), true);
			return result(IChangeResult.NOCHANGE, itemTempId, count, source, args);
		}
		// 修改开始
		Result result = onChangeStart(null, tempInfo, -count, source, args);
		if (!result.isSucceed()) {
			return result;
		}
		int prevCount = 0;
		int nowValue = 0;

		// 检测物品数量是否足够扣除
		List<T> removeItems = new ArrayList<>();
		int checkCount = 0;
		for (T element : elements.values()) {
			// 过滤不同类型的物品
			if (element == null || element.getTemplateId() != itemTempId || !element.isActive()) {
				continue;
			}
			checkCount += element.getCount(); // 统计剩余堆叠数量
			removeItems.add(element); // 添加入列表
			if (checkCount >= count) {
				break; // 这些东东够扣了.
			}
		}

		// 判断扣除数量
		if (checkCount < count) {
			return result(IChangeResult.NOTENOUGH, itemTempId, -count, source, args);
		}

		try {
			changeCounter.beginChange();

			// 获取修改前的数量
			prevCount = this.getCount(itemTempId);

			// 执行扣去
			checkCount = count; // 重新计算扣除数
			for (T element : removeItems) {
				int itemCount = element.getCount();
				int removeCount = (itemCount <= checkCount) ? itemCount : checkCount;
				checkCount -= removeCount;
				// 修改物品数量
				changeItem(element, itemCount - removeCount);
			}
			// 修改完后的数值变化
			nowValue = prevCount - count;
			// 修改事件
			onChange(null, tempInfo, prevCount, nowValue, -count, source, args);

			// 修改完成事件
			onChangeComplete(null, tempInfo, prevCount, nowValue, -count, source, args);
		} finally {
			changeCounter.commitChange(false);
		}
		return result(IChangeResult.SUCCESS, itemTempId, -count, source, args);
	}

	/** 删除单格上的物品(用于出售和使用消耗) **/
	protected Result remove(long itemId, int count, short source, Object... args) {
		if (count <= 0) {
			Log.error("删除物品错误, 数量不能少于0! count=" + count);
			return result(IChangeResult.NOCHANGE, itemId, 0, null, -count, source, args);
		}

		// 获取物品
		T item = this.elements.get(itemId);
		if (item == null) {
			return result(IChangeResult.NOOBJ, itemId, 0, null, -count, source, args);
		}
		int itemTempId = item.getTemplateId();
		D tempInfo = item.getTempInfo();

		// 修改开始
		Result result = onChangeStart(item, tempInfo, -count, source, args);
		if (!result.isSucceed()) {
			return result;
		}

		// 判断数量是否足够删除
		int nowCount = item.getCount();
		if (nowCount < count) {
			return result(IChangeResult.NOTENOUGH, itemTempId, -count, source, args);
		}

		try {
			changeCounter.beginChange();
			// 当前物品拥有数量
			int prevCount = this.getCount(itemTempId);

			// 执行扣除
			nowCount -= count;
			this.changeItem(item, nowCount);

			// 删除出售不属于消耗(待定)
			int nowValue = prevCount - count;
			// 修改事件
			onChange(item, tempInfo, prevCount, nowValue, -count, source, args);
			// 修改完成事件
			onChangeComplete(item, tempInfo, prevCount, nowValue, -count, source, args);
		} finally {
			changeCounter.commitChange();
		}
		return result(IChangeResult.SUCCESS, itemTempId, count, source);
	}

	/** 获取单个格子的物品信息 **/
	public T get(long id) {
		return this.elements.get(id);
	}

	@SuppressWarnings("unchecked")
	public final <E extends T> E get(long id, Class<E> clazz) {
		T item = get(id);
		if (!clazz.isInstance(item)) {
			return null;
		}
		return (E) item;
	}

	/** 获取符合模板的第一个物品 **/
	@SuppressWarnings("unchecked")
	public <E extends T> E getByTempId(int tempId) {
		for (T item : elements.values()) {
			if (item.getTemplateId() == tempId) {
				return (E) item;
			}
		}
		return null;
	}

	/** 获取模板对应所有物品并且对应类的东西 **/
	@SuppressWarnings("unchecked")
	public <E extends T> List<E> getAllByTempId(int tempId) {
		List<E> list = new ArrayList<>();
		for (T item : elements.values()) {
			if (item.getTemplateId() != tempId) {
				continue;
			}
			// 添加
			list.add((E) item);
		}
		return list;
	}

	/** 获取某种物品的数量 **/
	public int getCount(int itemTempId) {
		// 统计当前
		int total = 0;
		for (Item<D> element : elements.values())// 先计算可以往已存在物品上叠加的数量
		{
			if (element.getTemplateId() != itemTempId || !element.isActive()) {
				continue;
			}
			total += element.getCount();
		}
		return total;
	}

	/** 获取物品模板 **/
	public abstract D getTempInfo(int itemTempId);

	/** 修改开始过滤, item不一定有,tempInfo一定会有. **/
	protected Result onChangeStart(T item, D tempInfo, long change, short source, Object... args) {
		long id = (item != null) ? item.getItemId() : 0L;
		int tempId = tempInfo.getTemplateId();
		return result(IChangeResult.SUCCESS, id, tempId, item, change, source, args);
	}

	/** 物品(根据类型分)变化完成(锁定外处理). id或者tempId不一定有 **/
	protected void onChangeComplete(T item, D tempInfo, long preValue, long setValue, long change, short source, Object... args) {
	}

	/** 物品(根据类型分)变化事件(锁定中). id或者tempId不一定有 **/
	protected void onChange(T item, D tempInfo, long preValue, long nowValue, long change, short source, Object... args) {
		// Log.debug("物品修改: id=" + id + " change=" + change);
	}

	/** 获取所有物品(按照背包格子) **/
	public List<T> getItems() {
		return new ArrayList<T>(elements.values());
	}

	/** 获取背包所用格子数(过滤掉隐藏的格子) **/
	public int getCellCount() {
		return elements.size() - hides.size();
	}

	/** 背包格子数 **/
	public int getMaxCellCount() {
		return Integer.MAX_VALUE;
	}

	/** 获取剩余空间 **/
	public final int getFreeCellCount() {
		return getMaxCellCount() - getCellCount();
	}

	/** 是否满了 **/
	public final boolean isFull() {
		return getFreeCellCount() > 0;
	}

	/** 创建物品 **/
	protected abstract T createItem(D tempInfo, int count);

	/** 添加物品事件 **/
	protected void onAddItem(T item) {
	}

	/** 移除物品事件(东西是移除了) **/
	protected void onRemoveItem(T item) {
	}

	/** 删除物品 **/
	private void removeItem(T item) {
		// 移除位置
		int slot = item.getSlot();
		if (slot >= 0) {
			slots.remove(slot);
		} else {
			hides.remove(item);
		}
		// 执行移除
		elements.remove(item.getItemId());
		this.removes.add(item);
		onRemoveItem(item);
		// 触发事件
		changeCounter.change();
	}

	/** 修改物品 **/
	private void changeItem(T item, int count) {
		// 修改物品
		int prev = item.getCount();
		if (prev == count) {
			return; // 没变化
		}

		// 判断变化
		item.setCount(count);
		if (count <= 0) {
			// 移除
			removeItem(item);
			return;
		}

		// 修改完成, 触发事件
		this.updates.add(item);
		onChangeItem(item, prev, count);
		onUpdateItem(item);
		// 触发事件
		changeCounter.change();
	}

	/** 更新物品, 添加记录. **/
	protected void updateItem(T item) {
		this.updates.add(item);
		onUpdateItem(item);
		// 触发事件
		changeCounter.change();
	}

	/** 单个物品修改事件 **/
	protected void onChangeItem(T item, int prev, int now) {
	}

	/** 单个物品更新事件 **/
	protected void onUpdateItem(T item) {
	}

	/** 修改完成修改 **/
	protected void flushCache() {
		// 判断是否有改动
		if (adds.isEmpty() && updates.isEmpty() && removes.isEmpty()) {
			return; // 没有改动
		}

		// 整理记录
		updates.removeAll(removes); // 如果remove有的记录, update就不要了.
		updates.removeAll(adds); // 如果add有的记录, update就不要了.

		// 执行整理
		onFlushCache();

		// 清除记录
		this.adds.clear();
		this.updates.clear();
		this.removes.clear();
	}

	/** 变化缓存 **/
	protected void onFlushCache() {
	}

	/** 检查新增所需新格子数 **/
	private boolean checkNewCell(IItemTempInfo tempInfo, int addCount, boolean stack, List<T> stackedItems) {
		int maxCount = tempInfo.getMaxCount();
		int checkCount = (stack) ? getStackedItems(tempInfo, addCount, stackedItems) : 0;
		// 如果堆叠不够处理, 要判断背包格子.
		if (checkCount < addCount) {
			int newCell = (int) Math.ceil((addCount - checkCount) / (double) maxCount);
			int freeCellCount = getFreeCellCount();
			if (newCell > freeCellCount) {
				return false; // 背包数量不足
			}
		}
		// 全部可堆叠, 不需要新增
		return true;
	}

	/** 计算可堆叠的数量, 记录在列表中 **/
	private int getStackedItems(IItemTempInfo tempInfo, int addCount, List<T> stackedItems) {
		int itemTempId = tempInfo.getTemplateId();
		int maxCount = tempInfo.getMaxCount();
		int checkCount = 0; // 可堆叠数量

		// 先计算可以往已存在物品上叠加的数量
		for (T element : elements.values()) {
			// 过滤不同类型的物品
			if (element == null || element.getTemplateId() != itemTempId || !element.isActive()) {
				continue;
			}

			// 获取剩余堆叠数量
			int remaining = Math.max(maxCount - element.getCount(), 0);
			if (remaining <= 0) {
				continue; // 满了
			}
			// 记录可堆叠数量
			checkCount += remaining; // 统计剩余堆叠数量
			if (stackedItems != null) {
				stackedItems.add(element); // 添加入列表
			}
			if (checkCount >= addCount) {
				checkCount = addCount;
				break; // 堆叠完毕了
			}
		}
		return checkCount;
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("Bag [");
		// for (Map.Entry<Long, T> entry : elements.entrySet())
		// {
		// // Long id = entry.getKey();
		// T item = entry.getValue();
		// strBdr.append(item);
		// strBdr.append(", ");
		// }
		strBdr.append(elements);
		strBdr.append("]");

		return strBdr.toString();
	}

	/** 背包修改结果 **/
	public static class BagChangeResult<T extends Item<?>> extends Result {
		protected T item;
	}

}
