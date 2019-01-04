package com.game.framework.framework.app;

import com.game.framework.component.log.Log;
import com.game.framework.framework.component.ComponentMgr;
import com.game.framework.utils.ThreadUtils;

/**
 * 服务应用对象.
 * App.java
 * @author JiangBangMing
 * 2019年1月3日下午2:34:13
 */
public abstract class App extends ComponentMgr {
	protected volatile boolean running; // 是否运行
	protected long shutdownTime; // 关闭时间

	protected App() {
		// instance = this;
		running = false;
		shutdownTime = 0L;
	}

	/** 服务器初始化 **/
	protected abstract boolean init(String[] args) throws Exception;

	/** 停止服务器 **/
	protected void stop() {
		running = false;
	}

	/** 延迟停止服务器 **/
	public boolean stop(int waitTime) {
		// 延迟关闭
		Log.info("服务器" + waitTime + "ms后关闭");
		this.shutdownTime = System.currentTimeMillis() + waitTime;
		return true;
	}

	/** 服务器销毁 **/
	protected void destroy() throws Exception {
		// 组件关闭
		destroyAllComponent();
	}

	/** 启动服务器(阻塞) **/
	public boolean start(String[] args) throws Exception {

		try {
			shutdownTime = 0L;
			running = true; // 在初始化之前就running, 允许初始化成功时停止.

			// 初始化服务器
			if (!init(args)) {
				Log.info("服务器初始化失败");
				return false;
			}
			Log.info("服务器启动成功");

			// 执行服务器
			long prevTime = System.currentTimeMillis();
			int count = 0;

			// 主线程阻塞等待
			while (running) {
				ThreadUtils.sleep(1000);
				count++;
				// 主线程更新事件
				long nowTime = System.currentTimeMillis();
				long prevTime0 = prevTime;
				prevTime = nowTime;

				// 处理更新事件
				long dt = nowTime - prevTime0;
				if (!onUpdate(count, dt, nowTime, prevTime)) {
					Log.error("更新失败, 停止服务器!");
				}

				// 计算定时关闭
				if (shutdownTime > 0) {
					// 计算是否结束时间
					if (nowTime >= shutdownTime) {
						stop();
						break;
					}
				}
			}

			Log.info("服务器执行关闭");
		} catch (Exception e) {
			Log.error("服务器运行错误! ", e);
			System.exit(0); // 一定要带这个, 否则linux不能正常结束
			return false;
		} finally {
			// 关闭服务器
			try {
				destroy();
			} catch (Exception e) {
				Log.error("服务器关闭报错! ", e);
			}
		}

		Log.info("服务器关闭成功");
		System.exit(0); // 一定要带这个, 否则linux不能正常结束
		return true;
	}

	/** 服务器运行更新(主线程) **/
	protected boolean onUpdate(int count, long dt, long now, long prev) {
		return true;
	}

	public boolean isRunning() {
		return running;
	}

	/** 获取服务器名 **/
	public String getAppName() {
		return "app";
	}

	public int getAppId() {
		return 1;
	}

	/********** 静态 *********/
	// protected static App instance;
	//
	// @SuppressWarnings("unchecked")
	// public static <T extends App> T getInstance() {
	// if (instance == null) {
	// return null;
	// }
	// return (T) instance;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public static <T extends App> T getInstance(Class<T> clazz) {
	// if (instance == null) {
	// Log.error("服务器应用尚未创建! ", true);
	// return null;
	// }
	// return (T) instance;
	// }

}
