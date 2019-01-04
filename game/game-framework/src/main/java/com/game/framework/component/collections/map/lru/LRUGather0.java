package com.game.framework.component.collections.map.lru;

/**
 * 自动生成集群
 * <p/>
 * 如果获取的id没有数据, 则会创建一个新的.
 * 
 * <p/>
 * 可通过remove清除id对应的数据, 下次调用再创建一个新的.
 * LRUGather0.java
 * @author JiangBangMing
 * 2019年1月3日下午1:28:34
 */
public abstract class LRUGather0<K, T> extends LRULockCache0<K, T> {

	public LRUGather0(int size) {
		super(size);
		// 设置接口
		this.setListener0(new LRURemoveListener<K, T>() {
			@Override
			public void handle(K k, T v, int type) {
				onRemove0(k, v, type);
			}
		});

	}

	/**
	 * 获取id回应的数据, 如果数据不存在,则调用创建函数创建一个新的.
	 * 
	 * @param id
	 * @return
	 */
	protected T getAndCreate0(K id) {
		T obj = super.get0(id);
		if (obj == null) {
			// 没有聊天器, 创建一个
			obj = create(id);
			if (obj == null) {
				return null;
			}
			// 插入
			T old = super.putIfAbsent0(id, obj); // 插入线程(线程安全)
			if (old != null) {
				obj = old; // 被其他线程替换了
			}
		}
		return obj;
	}

	/**
	 * 删除数据
	 * 
	 * @param key
	 * @return
	 */
	// protected T remove0(K id) {
	// return super.remove0(id);
	// }

	protected void onRemove0(K k, T v, int type) {

	}

	protected abstract T create(K id);
}