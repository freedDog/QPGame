package com.game.framework.component.resource;

import java.io.InputStream;

/**
 * 资源对象<br>
 * Resource.java
 * @author JiangBangMing
 * 2019年1月3日下午1:49:02
 */
public abstract class Resource {

	public boolean reload() {
		if (!reload0()) {
			return false;
		}
		return true;
	}

	public void release() {
	}

	/**
	 * 加载
	 * 
	 * @return
	 */
	protected boolean reload0() {
		// 读取资源路径
		String resourceFile = this.getResourceFile();
		if (resourceFile == null) {
			return false;
		}
		resourceFile = "/" + resourceFile;

		// 加载文件
		try {
			InputStream is = null;
			try {
				is = this.getClass().getResourceAsStream(resourceFile);
				this.reload(is);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 获取资源类型
	 * 
	 * @return {@link ResourceType}
	 */
	public ResourceType getResoureType() {
		String resourceFile = this.getResourceFile();
		if (resourceFile == null) {
			return ResourceType.NULL;
		}
		// 检测后缀
		int indexOf = resourceFile.lastIndexOf('.');
		if (indexOf < 0) {
			return ResourceType.NULL; // 没有后缀
		}

		// 读取后缀
		String postfix = resourceFile.substring(indexOf + 1);
		postfix = postfix.toLowerCase(); // 转成小写

		// 根据类型返回枚举
		if (postfix.equals("xml")) {
			return ResourceType.XML;
		} else if (postfix.equals("properties")) {
			return ResourceType.PROPERTIES;
		} else if (postfix.equals("txt")) {
			return ResourceType.TEXT;
		}

		return ResourceType.NULL;
	}

	/**
	 * 加载文件
	 * 
	 * @param is
	 * @throws Exception
	 */
	protected abstract void reload(InputStream is) throws Exception;

	/**
	 * 读取资源路径
	 */
	public abstract String getResourceFile();

	/**
	 * 资源类型
	 * 
	 */
	public enum ResourceType {
		XML, PROPERTIES, TEXT, NULL,
	}
}
