package com.game.framework.component.service.record;

/**
 * 记录运行任务
 * TimeRecordRunnable.java
 * @author JiangBangMing
 * 2019年1月3日下午1:51:24
 */
public abstract class TimeRecordRunnable<K> implements Runnable {
	protected K id;
	protected final TimeMeter timeMeter = new TimeMeter();

	public TimeRecordRunnable(K id) {
		this.id = id;
	}

	@Override
	public void run() {
		timeMeter.start();
		try {
			run0();
		} catch (Exception e) {
			error(e);
			return;
		}
		int useTime = timeMeter.end();
		finish(useTime);
	}

	protected abstract void run0() throws Exception;

	protected abstract void finish(int useTime);

	protected abstract void error(Exception e);
}
