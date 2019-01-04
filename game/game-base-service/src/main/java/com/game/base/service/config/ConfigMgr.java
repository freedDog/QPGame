package com.game.base.service.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.game.base.service.constant.LanguageType;
import com.game.framework.component.log.Log;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.framework.xml.XmlUtils;
import com.game.framework.utils.EncryptUtils;
import com.game.framework.utils.FileUtils;

/**
 * 配置管理器<br>
 * 读取起服的xml配置文件.
 * ConfigMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午3:15:15
 */
public final class ConfigMgr {
	private static XmlNode root; // 所有配置
	private static XmlNode config; // 当前配置
	private static XmlNode global; // 公用配置
	private static XmlNode manager; // 管理器配置
	private static String configRootPath; // 配置所在文件路径

	private static boolean isDebug = true; // debug状态, 未初始化状态为true, 初始化默认为ture
	public static String languageType = LanguageType.ZH; // 默认中文

	/** 配置结构 **/
	private final static String[][] elemKeys = new String[][] { { "GameManager" } // 进程管理器配置
			, { "Games", "Game" } // 逻辑服配置
			, { "Gates", "Gate" } // 网关服配置
			, { "Logins", "Login" } // 登陆服配置
	};

	/** 初始化配置 **/
	public static boolean init(boolean debug) {
		isDebug = debug;
		return true;
	}

	/** 初始化配置 **/
	public static boolean init(String configPath, String serverName) {
		try {
			// 判断文件是否存在
			File file = new File(configPath);
			if (!file.exists()) {
				Log.error("配置文件找不到! path: " + file.getAbsolutePath());
				return false;
			}
			configRootPath = FileUtils.getFilePath(configPath); // 获取文件路径
			// 尝试解密xml
			root = XmlUtils.readXmlByString(ConfigLoader.loadEncryptFile(configPath, "UTF-8"));
			// 遍历获取对应服务器名字的配置
			for (String[] elemKey : elemKeys) {
				int esize = (elemKey != null) ? elemKey.length : 0;
				if (esize == 1) {
					String elemName = elemKey[0];
					config = findChildNode(root.getElems(elemName), "name", serverName);
				} else if (esize == 2) {
					String elemParent = elemKey[0];
					String elemName = elemKey[1];
					config = findChildNode(XmlNode.getElems(root.getElem(elemParent), elemName), "name", serverName);
				}

				// 判断是否找到配置
				if (config != null) {
					break;
				}
			}

			// 检测配置
			if (config == null) {
				Log.error("找不到对应服务器! serverName=" + serverName);
				return false;
			}

			// 查找符合的字段
			global = root.getElem("Global");
			global = (global == null) ? new XmlNode("Global", null) : global;

			// 管理器配置
			manager = root.getElem("GameManager");
			if (manager == null) {
				Log.error("找不到对应管理器服务器配置!");
				return false;
			}

			// 读取debug状态
			isDebug = getAttr("debug", false);

			/** 获取当前服务器语言 **/
			XmlNode languageNode = ConfigMgr.getElem("Language");
			languageType = languageNode != null ? languageNode.getAttr("language", languageType) : languageType;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/** 从列表中找去符合的节点 **/
	protected static XmlNode findChildNode(List<XmlNode> nodes, String key, String value) {
		int nsize = (nodes != null) ? nodes.size() : 0;
		for (int i = 0; i < nsize; i++) {
			XmlNode node = nodes.get(i);
			if (node == null) {
				continue;
			}
			// 检测结果
			String value0 = (String) node.getAttr(key);
			if (value0 != null && value0.equals(value)) {
				return node;
			}
		}
		return null;
	}

	/** 获取GameManager配置 **/
	public static XmlNode getManagerConfig() {
		return manager;
	}

	/** 获取当前服务器配置 **/
	public static XmlNode getConfig() {
		return config;
	}

	/** 获取配置根节点 **/
	public static XmlNode getRoot() {
		return root;
	}

	/** 获取公用 **/
	public static XmlNode getGlobal() {
		return global;
	}

	/** 获取子节点, 如果当前配置没有, 从全局配置中获取. **/
	public static XmlNode getElem(String name) {
		XmlNode v = config.getElem(name);
		if (v != null) {
			return v;
		}
		return (global != null) ? global.getElem(name) : null; // 没有配置时,读取默认值
	}

	public static List<XmlNode> getElems(String name) {
		List<XmlNode> v = config.getElems(name);
		if (v != null) {
			return v;
		}
		return (global != null) ? global.getElems(name) : null; // 没有配置时,读取默认值
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttr(String attributeName, T defualt) {
		Object v = config.getAttr(attributeName, defualt);
		if (v != null && v != defualt) {
			return (T) v;
		}
		return (global != null) ? global.getAttr(attributeName, defualt) : defualt; // 没有配置时,读取默认值
	}

	/** 服务器是否是debug模式 **/
	public static boolean isDebug() {
		return isDebug;
	}

	public static String getConfigRootPath() {
		return configRootPath;
	}

	/** 配置加载 **/
	public static class ConfigLoader {
		private static String key = "asdaskj123123zxcoijviozjvoijsdhq"; // 配置文件加密key
		private static String verify = "keyHead"; // 校验码

		/** 读取文件 **/
		public static String loadEncryptFile(String path, String enc) {
			return loadEncryptFile(path, enc, key, verify);
		}

		/** 读取文件加密保存到 **/
		public static boolean encryptFileTo(String path, String outPath) {
			return encryptFileTo(path, key, verify, outPath);
		}

		/** 读取文件 **/
		public static String loadEncryptFile(String path, String enc, String key, String verify) {
			// 读取文件内容
			byte[] data = FileUtils.loadFile(path);
			if (data == null) {
				Log.error("读取文件失败: " + path + " -> " + FileUtils.getAbsolutePath(path));
				return null;
			}

			// 执行解密
			byte[] ndata = EncryptUtils.SZED.decrypt(data, key.getBytes(), verify.getBytes());
			try {
				return new String(ndata, enc);
			} catch (UnsupportedEncodingException e) {
				Log.error("文件内容编码转化失败! " + path + " " + enc, e);
			}
			return null;
		}

		/** 加密数据保存到文件 **/
		private static boolean encryptToFile(String data, String key, String verify, String outPath) {
			byte[] ndata = EncryptUtils.SZED.encrypt(data.getBytes(), key.getBytes(), verify.getBytes());
			return FileUtils.saveFile(outPath, ndata);
		}

		/** 读取文件加密保存到 **/
		public static boolean encryptFileTo(String path, String key, String verify, String outPath) {
			// 读取文件内容
			String fstr = FileUtils.loadFile(path, "UTF-8");
			if (fstr == null) {
				Log.error("读取文件失败: " + path + " -> " + FileUtils.getAbsolutePath(path));
				return false; // 找不到文件
			}
			return encryptToFile(fstr, key, verify, outPath);
		}
	}
}

