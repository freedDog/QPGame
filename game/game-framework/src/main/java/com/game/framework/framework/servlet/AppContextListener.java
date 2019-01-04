package com.game.framework.framework.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.game.framework.component.log.Log;
import com.game.framework.framework.component.ComponentMgr;


/**
 * tomcat 启动处理监听
 * AppContextListener.java
 * @author JiangBangMing
 * 2019年1月3日下午6:11:38
 */
public abstract class AppContextListener extends ComponentMgr implements ServletContextListener {

	/** 初始化 **/
	protected abstract boolean init(ServletContext ctx);

	/** 销毁 **/
	protected void destroy(ServletContext ctx) {
		destroyAllComponent();
	}

	/** 启动初始化 **/
	@Override
	public final void contextInitialized(ServletContextEvent event) {
		try {
			ServletContext ctx = event.getServletContext();
			if (!init(ctx)) {
				System.exit(0);
				return;
			}
		} catch (Throwable t) {
			Log.error("启动异常", t);
			System.exit(0);
		}
	}

	/** 关闭处理 **/
	@Override
	public final void contextDestroyed(ServletContextEvent event) {
		try {
			ServletContext ctx = event.getServletContext();
			destroy(ctx);
		} catch (Throwable t) {
			Log.error("关闭异常", t);
		}
		// System.exit(0); //这个调试时会导致进程关闭
	}

}
