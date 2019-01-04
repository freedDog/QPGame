package com.game.framework.jdbc;

import java.net.URL;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.game.framework.utils.ResourceUtils;


/**
 * 基于spring的JadeBeanFactory JadeBeanFactory.java
 * 
 * @author JiangBangMing 2019年1月3日下午5:13:05
 */
// @Component
public class JadeBeanFactory extends JadeBaseFactory implements BeanFactoryPostProcessor {
	protected ConfigurableListableBeanFactory beanFactory;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

		// 获取DAO的资源并创建处理器
		// final List<URL> resources =
		// zyt.utils.ResourceUtils.getResources(".+DAO.class");
		List<URL> resources = ResourceUtils.getResources("(.*)[0-z]DAO.class");
		for (URL url : resources) {
			// System.out.println(url);
			try {
				Class<?> clazz = ResourceUtils.loadClass(url);
				createAndSetDAO(beanFactory, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/** 创建并且设置DAO **/
	protected boolean createAndSetDAO(ConfigurableListableBeanFactory beanFactory, Class<?> clazz) throws Exception {
		// 创建对象
		Object proxy = createDAO(clazz);
		if (proxy == null) {
			return false;
		}

		// System.out.println("create and set dao:" + clazz);
		// 设置到工厂中
		DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) beanFactory;
		defaultBeanFactory.registerSingleton(clazz.getName(), proxy);
		return true;
	}

	@Override
	public DataSource getDataSource() {
		return beanFactory.getBean(DataSource.class);
	}

}
