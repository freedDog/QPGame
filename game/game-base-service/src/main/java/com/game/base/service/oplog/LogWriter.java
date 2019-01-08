package com.game.base.service.oplog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.game.base.service.db.LogDaoMgr;
import com.game.framework.component.log.Log;

/**
 * 日志表写入器
 * LogWriter.java
 * @author JiangBangMing
 * 2019年1月8日下午3:35:49
 */
public class LogWriter<I> {
	// List<**Info>
	protected List<I> list;
	// **DAO.class
	protected Class<?> dcls;
	// **Info.class
	protected Class<I> icls;

	private Method method; // 反射函数
	private Object dao; // dao对象

	// 初始化 传入info，以及对应的DAO
	public LogWriter(Class<?> dcls, Class<I> icls) {
		list = new ArrayList<I>();
		this.dcls = dcls;
		this.icls = icls;
		try {
			// 反射函数获取
			method = dcls.getDeclaredMethod("insert", new Class<?>[] { List.class });

			// 获取dao
			dao = LogDaoMgr.getInstance().getDao(dcls);
		} catch (Exception e) {
			Log.error("初始化日志写入器错误!", e.getCause());
		}
	}

	// 收集日志数据
	public void write(I info) {
		synchronized (list) {
			list.add(info);
		}
	}

	// 写入到数据库
	protected void save() {
		try {
			// 判断数据
			if (list.isEmpty()) {
				return;
			}
			// 提取日志, 用新数组代替.
			List<I> tempList = list;
			list = new ArrayList<I>();

			// 执行保存
			method.invoke(dao, tempList);
			// Log.info("save log: " + icls + " " + tempList.size());
		} catch (Exception e) {
			Log.error("保存日志错误!", e.getCause());
		}

	}

}
