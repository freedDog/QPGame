package com.game.framework.component.collections.map.lru;

/**
 * 自动生成集群
 * <p/>
 * 如果获取的id没有数据, 则会创建一个新的.
 * 
 * <p/>
 * 可通过remove清除id对应的数据, 下次调用再创建一个新的.
 * LRUGather.java
 * @author JiangBangMing
 * 2019年1月3日下午1:30:22
 */
public abstract class LRUGather<K, T> extends LRUGather0<K, T> {

	public LRUGather(int size) {
		super(size);
	}

	/**
	 * 获取id回应的数据, 如果数据不存在,则调用创建函数创建一个新的.
	 * 
	 * @param id
	 * @return
	 */
	public T get(K id) {
		// return super.get0(id);
		return super.getAndCreate0(id);
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 * @return
	 */
	public T remove(K id) {
		return super.remove0(id);
	}

	/** 清除所有数据 **/
	public void clear() {
		super.clear0();
	}

	/**
	 * 是否存在数据
	 * 
	 * @param k
	 * @return
	 */
	public boolean exist(K k) {
		return super.containsKey0(k);
	}

	/**
	 * 设置移除监听器
	 * 
	 * @param listener
	 */
	public void setListener(LRURemoveListener<K, T> listener) {
		super.setListener0(listener);
	}

	/**
	 * 获取移除监听
	 * 
	 * @return
	 */
	public LRURemoveListener<K, T> getListener() {
		return super.getListener0();
	}

}