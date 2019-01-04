package com.game.framework.component.resource;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import com.game.framework.component.log.Log;



/**
 * 参数配置<br>
 * ParamConfig.java
 * @author JiangBangMing
 * 2019年1月3日下午1:48:07
 */
public class ParamConfig extends Config {
	protected static final Locale locale = new Locale("zh", "CN"); // 本地优先中文
	protected Map<Object, String> map; // 数据内容
	protected String filePath; // 文件路径

	public ParamConfig() {
	}

	public ParamConfig(String filePath) {
		this.reload(filePath);
	}

	public boolean reload() {
		if (filePath == null) {
			return false;
		}
		// 加载
		reload(filePath);
		return true;
	}

	public void release() {
	}

	@Override
	protected String get(Object key) {
		return (map != null) ? map.get(key) : null;
	}

	/** 加载文件 **/
	protected <K, V> boolean reload(Set<Map.Entry<K, V>> set) {
		// 遍历读取
		Map<Object, String> map = new HashMap<>();
		for (Map.Entry<K, V> entry : set) {
			K key = entry.getKey();
			V value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			map.put(key, value.toString());
		}
		map = Collections.unmodifiableMap(map);

		// 绑定数据
		this.map = map;
		return true;
	}

	/** 加载文件 **/
	protected boolean reload(ResourceBundle bundle) {
		// 遍历读取
		Map<Object, String> map = new HashMap<>();
		Iterator<String> iter = bundle.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = bundle.getString(key);
			map.put(key, value);
		}
		map = Collections.unmodifiableMap(map);

		// 绑定数据
		this.map = map;
		return true;
	}


	/** 重新加载文件(只能相对路径) */
	protected boolean reload(String filePath) {
		// 加载配置
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(filePath));
		} catch (Exception e) {
			Log.error("读取配置错误!" + filePath, e);
		}
		// 重载文件
		if (!reload(properties.entrySet())) {
			return false;
		}
		this.filePath = filePath;
		return true;
	}

}
