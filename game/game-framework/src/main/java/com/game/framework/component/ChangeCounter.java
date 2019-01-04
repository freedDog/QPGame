package com.game.framework.component;

import java.util.concurrent.atomic.AtomicInteger;

import com.game.framework.component.log.Log;



/**
 * 修改计数器<br>
 * 线程安全<br>
 * 在符文的基础上, 增加锁定过程中的更新检测<br>
 * 简单来说就是在beginChange和commitChange之间沒有change过, commitChange之后就不会触发onChange.
 */
public abstract class ChangeCounter {
	private final AtomicInteger lock = new AtomicInteger(); // 锁定标记
	private final AtomicInteger update = new AtomicInteger(); // 锁定期间更新标记
	private volatile boolean clock = false; // 更新中锁定

	/** 开始修改, 计数器+1 **/
	public void beginChange() {
		lock.incrementAndGet();
	}

	/** 完成修改, 计数器减少. **/
	public void commitChange() {
		commitChange(true);
	}

	/** 清除更新标记, 忽略更新 **/
	public void ignoreChange() {
		commitChange0(false);
	}

	/**
	 * 完成修改, 计数器减少.
	 * 
	 * @param update
	 *            为完成时是否触发一次change
	 **/
	public void commitChange(boolean c) {
		// 提交检测是否更新
		boolean r = commitChange0(c);
		if (!r) {
			return;
		}

		// 检测是否在更新中
		if (clock) {
			return; // 在更新中, 不能重复调用.
		}

		// 触发事件
		try {
			clock = true;
			onChange();
		} finally {
			clock = false;
		}
	}

	/** 提交, 返回是否需要更新 **/
	private boolean commitChange0(boolean c) {
		// 是否强制触发一次onchange
		if (c) {
			update.incrementAndGet();
		}

		// 解锁标记
		int cur = lock.decrementAndGet();
		if (cur < 0) {
			Log.error("修改标记错误, 减少到负数了! " + cur, true);
			lock.set(0);
		} else if (cur > 0) {
			return false; // 其他线程还在锁定中.
		}

		// 解锁成功, 执行触发.
		// 检测锁定期间是否有update过.
		int ucount = update.get();
		if (ucount <= 0) {
			return false; // 没有调用过change, 不触发事件.
		}
		update.set(0); // 清除标记

		// 可执行
		return true;
	}

	/**
	 * 触发一次修改<br>
	 * 修改触发事件, 如果计数器为0则触发修改任务.<br>
	 **/
	public void change() {
		this.beginChange();
		this.commitChange(true);
	}

	/**
	 * 触发修改事件
	 */
	protected abstract void onChange();

}
