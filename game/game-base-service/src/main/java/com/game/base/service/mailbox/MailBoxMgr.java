package com.game.base.service.mailbox;

import com.game.base.service.config.ServerConfig;
import com.game.framework.component.log.Log;

/**
 * mailBox管理器
 * MailBoxMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午3:52:46
 */
public abstract class MailBoxMgr {
	protected static int expireTime = 60 * 3600 * 24;
	protected static IMailBoxHandler handler = new MailBoxHashHandler();

	// protected static IMailBoxHandler handler = new MailBoxHandler();

	/** 更新数据时间 **/
	protected static void expire(int type, long id, Object key) {
		// 参数检测
		if (id <= 0) {
			Log.error("参数错误! type=" + type + " id=" + id, true);
			return;
		}
		// 检测接口
		if (handler == null) {
			Log.error("接口尚未设置", true);
			return;
		}
		// 检测时间
		if (expireTime <= 0) {
			return;
		}
		handler.expire(type, id, key, expireTime);
	}

	/** 设置数据 **/
	public static boolean setIfNull(int type, long id, Object key, ServerConfig config) {
		// 检测删除
		if (config == null) {
			return remove(type, id, key);
		}
		// 参数检测
		if (key == null || id <= 0) {
			Log.error("参数错误! type=" + type + " id=" + id + " key=" + key, true);
			return false;
		}
		// 检测接口
		if (handler == null) {
			Log.error("接口尚未设置", true);
			return false;
		}

		// 尝试设置
		if (!handler.setIfNull(type, id, key, config, expireTime)) {
			return false;
		}
		return true;
	}

	/** 设置数据 **/
	public static boolean set(int type, long id, Object key, ServerConfig config) {
		// 检测删除
		if (config == null) {
			return remove(type, id, key);
		}
		// 参数检测
		if (key == null || id <= 0) {
			Log.error("参数错误! type=" + type + " id=" + id + " key=" + key, true);
			return false;
		}
		// 检测接口
		if (handler == null) {
			Log.error("接口尚未设置", true);
			return false;
		}

		// 尝试设置
		if (!handler.set(type, id, key, config, expireTime)) {
			return false;
		}
		return true;
	}

	/** 获取设备信息 **/
	public static ServerConfig get(int type, long id, Object key) {
		// 检测接口
		if (handler == null) {
			Log.error("接口尚未设置", true);
			return null;
		}
		// 参数检测
		if (key == null || id <= 0) {
			Log.error("参数错误! type=" + type + " id=" + id + " key=" + key, true);
			return null;
		}
		// 读取
		ServerConfig config = handler.get(type, id, key, expireTime);
		if (config == null) {
			return null;
		}
		return config;
	}

	/** 删除数据 **/
	public static boolean remove(int type, long id, Object key) {
		// 检测接口
		if (handler == null) {
			Log.error("接口尚未设置", true);
			return false;
		}
		// 参数检测
		if (key == null || id <= 0) {
			Log.error("参数错误! type=" + type + " id=" + id + " key=" + key, true);
			return false;
		}
		// 尝试设置
		if (!handler.remove(type, id, key, expireTime)) {
			return false;
		}
		return true;
	}

	/** 处理接口 **/
	public interface IMailBoxHandler {
		void expire(int type, long id, Object key, int expireTime);

		/** 设置数据 **/
		boolean setIfNull(int type, long id, Object key, ServerConfig config, int expireTime);

		/** 设置数据 **/
		boolean set(int type, long id, Object key, ServerConfig config, int expireTime);

		/** 获取设备信息 **/
		ServerConfig get(int type, long id, Object key, int expireTime);

		/** 删除数据 **/
		boolean remove(int type, long id, Object key, int expireTime);
	}

}
