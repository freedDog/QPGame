package com.game.framework.framework.xml;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.ReflectUtils;
import com.game.framework.utils.StringUtils;


/**
 * xml工具
 * XmlUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午5:56:06
 */
public class XmlUtils {
	/** 获取参数, 支持基础类型的字符串转化. **/
	public static <T> T getAttribute(Element e, String attributeName, Class<T> clazz) {
		if (e == null) {
			return null;
		}
		// 获取属性
		Attribute attribute = e.attribute(attributeName);
		if (attribute == null) {
			return null;
		}
		// 获取属性
		String obj = attribute.getValue();
		return (T) ObjectUtils.toValue(obj, clazz);
	}

	/** 获取参数, 支持基础类型的字符串转化. **/
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(Element e, String attributeName, T defaultValue) {
		// 读取属性
		Class<?> clazz = (defaultValue != null) ? defaultValue.getClass() : String.class;
		Object obj = getAttribute(e, attributeName, clazz);
		if (obj == null) {
			return defaultValue;
		}
		return (T) obj;
	}

	/** 获取指定的Element **/
	public static Element getElementByAttribute(List<Element> list, String aname, String value) {
		// 判断长度
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 遍历检测
		for (Element e : list) {
			Attribute attribute = e.attribute(aname);
			if (attribute == null) {
				continue;
			}
			if (!value.equals(attribute.getValue())) {
				continue;
			}
			return e;
		}
		return null;
	}

	/** 文本转成xml **/
	public static XmlNode readXmlByString(String xmlStr) {
		XmlNode xmlNode = null;
		try {
			// 将xml格式的字符串转换成Document对象
			Document doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement(); // 获取根节点
			// 将map对象的数据转换成Bean对象
			xmlNode = XmlNode.getXml(root, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlNode;
	}

	/** 读取XML文件 **/
	public static Document readXml(String filePath) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(fileReader);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileReader != null)
					fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * xml字符串转换成bean对象
	 * 
	 * @param xmlStr
	 *            xml字符串
	 * @param clazzMap
	 *            待转换的class包括对象属性的class
	 * @return 转换后的对象
	 */
	public static <T> T toObject(String xmlStr, Class<T> clazz, Map<String, Class<?>> clazzMap) {
		T obj = null;
		try {
			// 将xml格式的字符串转换成Document对象
			Document doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement(); // 获取根节点
			// 将map对象的数据转换成Bean对象
			obj = toObject(root, clazz, clazzMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/** map转成对象 **/
	@SuppressWarnings("unchecked")
	public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz, Map<String, Class<?>> clazzMap) {
		int msize = (map != null) ? map.size() : 0;

		// 遍历节点
		Map<String, Object> beanMap = new HashMap<>(msize);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			String key = entry.getKey();
			// 判断是否是map表
			if (!Map.class.isInstance(value)) {
				beanMap.put(key, value);
				continue;
			}
			// 获取转化类型
			Class<T> childClass = (clazzMap != null) ? (Class<T>) clazzMap.get(key) : null;
			if (childClass == null) {
				beanMap.put(key, value); // 直接按照map放入
				continue;
			}
			// 转化对象
			Map<String, Object> childMap = (Map<String, Object>) value;
			Object childBean = mapToObject(childMap, childClass, clazzMap);
			beanMap.put(key, childBean);
		}

		try {
			// 创建对象并赋值
			T obj = clazz.newInstance();
			if (!ReflectUtils.setObjectByMap(obj, beanMap)) {
				return null;
			}
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将Map对象通过反射机制转换成Bean对象
	 * 
	 * @param clazzMap
	 *            待转换的class包括对象属性的class
	 * @return 转换后的Bean对象
	 */
	public static <T> T toObject(Element root, Class<T> clazz, Map<String, Class<?>> clazzMap) {
		// 转化成map
		Map<String, Object> map = toMap(root);
		// System.out.println(map);
		return mapToObject(map, clazz, clazzMap);
	}

	/** xml转map表 **/
	public static Map<String, Object> toMap(String xmlStr) {
		try {
			// 将xml格式的字符串转换成Document对象
			Document doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement(); // 获取根节点
			// 将map对象的数据转换成Bean对象
			return toMap(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/** 添加到列表, 如果存在值, 自动转成数组 **/
	// @SuppressWarnings("unchecked")
	private static void addToMap(Map<String, Object> map, String key, Object value) {
		// // 读取数据检测
		// Object old = map.get(key);
		// if (old == null) {
		// map.put(key, value); // 不存在数据, 插入
		// return;
		// }
		// // 存在数组
		// if (List.class.isInstance(old)) {
		// ((List<Object>) old).add(value); // 插入到数据
		// return;
		// }
		// // 存在数据, 转化成数组添加
		// List<Object> olist = new ArrayList<>(2);
		// olist.add(old);
		// olist.add(value);
		// map.put(key, olist);
		map.put(key, value);
	}

	/** 转成map **/
	public static Map<String, Object> toMap(Element root) {
		try {
			// 属性转化表
			Map<String, Object> map = new HashMap<String, Object>();

			// 遍历子节点
			List<Element> children = root.elements();
			int csize = (children != null) ? children.size() : 0;
			if (csize > 0) {
				for (int i = 0; i < csize; i++) {
					Element child = children.get(i);
					// 判断是否只是纯文本
					String textValue = child.getTextTrim();
					if (child.isTextOnly() && (textValue != null && textValue.length() > 0)) {
						addToMap(map, child.getName(), textValue);
						continue;
					}

					// 递归转化
					Object cmap = toMap(child);
					addToMap(map, child.getName(), cmap);
				}
			}

			// 遍历属性
			List<Attribute> attrs = root.attributes();
			for (Attribute att : attrs) {
				String text = att.getText();
				if (text == null || text.length() <= 0) {
					continue;
				}
				addToMap(map, att.getName(), text);
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 转成xml文本 **/
	public static <T> String toXml(String name, Map<String, T> map) {
		// 生成节点
		Element root = DocumentHelper.createElement(name);
		Document document = DocumentHelper.createDocument(root);

		// 遍历插入数据
		if (map != null && !map.isEmpty()) {
			// 遍历处理
			for (Map.Entry<String, T> entry : map.entrySet()) {
				// 判断数据
				T v = entry.getValue();
				if (v == null) {
					continue;
				}
				// 转成字符串
				String vstr = v.toString();
				if (StringUtils.isEmpty(vstr)) {
					continue;
				}
				String kname = entry.getKey();
				// 设置参数
				// root.addAttribute(kname,vstr);
				Element child = root.addElement(kname);
				child.setText(vstr);
			}
		}
		return document.asXML();

	}
}
