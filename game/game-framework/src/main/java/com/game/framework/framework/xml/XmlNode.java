package com.game.framework.framework.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.game.framework.utils.ObjectUtils;


/**
 * xml节点
 * XmlNode.java
 * @author JiangBangMing
 * 2019年1月3日下午5:55:28
 */
public class XmlNode {
	protected final String name;
	protected final Map<String, Object> attributes; // 属性
	protected final List<XmlNode> elements; // 子节点
	protected final Map<String, List<XmlNode>> emap;
	protected final XmlNode parent; // 父节点

	public XmlNode(String name, XmlNode parent) {
		this.name = name;
		attributes = new HashMap<>();
		elements = new ArrayList<>();
		emap = new HashMap<>();
		this.parent = parent;
	}

	/** 获取参数, 支持基础类型的字符串转化. **/
	@SuppressWarnings("unchecked")
	public <T> T getAttr(String attributeName, T default0) {
		String obj = (String) attributes.get(attributeName);
		if (obj == null) {
			return default0;
		}
		return (default0 != null) ? (T) ObjectUtils.toValue(obj,
				default0.getClass()) : null;
	}

	public Object getAttr(String attributeName) {
		return attributes.get(attributeName);
	}

	public XmlNode getElem(String name) {
		List<XmlNode> list = emap.get(name);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	public List<XmlNode> getElems(String name) {
		return emap.get(name);
	}

	public String getName() {
		return name;
	}

	public XmlNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "XmlNode [name=" + name + ", attributes=" + attributes
				+ ", elements=" + elements + "]";
	}

	/** 处理node为空的情况 **/
	public static List<XmlNode> getElems(XmlNode node, String name) {
		if (node == null) {
			return null;
		}
		return node.getElems(name);
	}

	/** 获取参数, 支持基础类型的字符串转化. **/
	public static <T> T getAttr(XmlNode node, String attributeName, T defualt) {
		return (node != null) ? node.getAttr(attributeName, defualt) : defualt;
	}

	/** 读取xml文件 **/
	public static XmlNode getXml(String configPath) {
		return getXml(XmlUtils.readXml(configPath).getRootElement(), null);
	}

	/** 获取xml **/
	public static XmlNode getXml(Element e, XmlNode parent) {
		List<Attribute> attributes = e.attributes();
		List<Element> elements = e.elements();

		// 创建节点
		XmlNode node = new XmlNode(e.getName(), parent);
		// 属性
		for (Attribute attribute : attributes) {
			node.attributes.put(attribute.getName(), attribute.getValue());
		}
		// 子节点
		for (Element element : elements) {
			XmlNode cnode = getXml(element, node);
			if (cnode == null) {
				continue;
			}
			node.elements.add(cnode);

			// 列表添加
			List<XmlNode> list = node.emap.get(cnode.getName());
			if (list == null) {
				list = new ArrayList<>();
				node.emap.put(cnode.getName(), list);
			}
			list.add(cnode);
		}
		return node;
	}

}