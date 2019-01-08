package com.game.base.service.tempmgr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.game.base.service.config.ConfigMgr;
import com.game.framework.component.log.Log;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.FileUtils;

/**
 * 配置管理器
 * 
 */
public final class ConfigurationMgr {

	private static List<String> folders; // 配置文件夹路径
	private static List<String> zips; // 配置zip路径

	protected static boolean init() {
		XmlNode node = ConfigMgr.getElem("Configuration");
		if (node == null) {
			Log.error("找不到配置数据节点!");
			return false;
		}
		// 设置文件夹路径
		String folderStrs = node.getAttr("folders", "");
		// 设置压缩包路径
		String zipStrs = node.getAttr("zips", "");
		if (!reset(folderStrs, zipStrs)) {
			Log.error("设置配置路径错误! folderStrs=" + folderStrs + " zips=" + zipStrs);
			return false;
		}
		return true;
	}

	/** 重设配置路径 **/
	public static boolean reset(String folderStrs, String zipStrs) {
		folders = new ArrayList<>();
		zips = new ArrayList<>();

		// 配置路径
		String[] folderStrs0 = folderStrs.split(",");
		for (String folderStr : folderStrs0) {
			folders.add(folderStr);
		}

		// 设置压缩包路径
		String[] zipStrs0 = zipStrs.split(",");
		for (String zipStr : zipStrs0) {
			zips.add(zipStr);
		}

		return true;
	}

	/** 加载配置 **/
	public static String loadConfiguration(String fileName) {
		// 检测数量
		// 遍历文件夹加载
		for (String folder : folders) {
			String result = loadConfigurationByFolder(folder, fileName);
			if (result != null) {
				return result; // 读取成功
			}
		}
		// 遍历压缩包加载
		for (String zip : zips) {
			String result = loadConfigurationByZip(zip, fileName);
			if (result != null) {
				return result; // 读取成功
			}
		}

		Log.error("无法读取配置文件! " + fileName + " -> " + folders + " " + zips);
		return null;
	}

	/** 从zip中加载 **/
	private static String loadConfigurationByZip(String zip, String fileName) {
		// 判断文件是否存在
		File file = new File(zip);
		if (file == null || !file.exists()) {
			return null;
		}
		ZipFile zipFile = null;
		InputStream in = null;

		// zip读取
		try {
			zipFile = new ZipFile(file);
			@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

			// 枚举zip文件内的文件/
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				// 读取目标对象
				if (ze.getName().equals(fileName)) {
					in = zipFile.getInputStream(ze);
					String result = FileUtils.loadFile(in, "UTF-8");
					if (result != null) {
						return result;
					}
				}
			}

		} catch (Exception e) {
			Log.error("读取zip错误!" + zip, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.error("关闭输入流错误!" + zip, e);
				}
				in = null;
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					Log.error("关闭zip错误!" + zip, e);
				}
				zipFile = null;
			}
		}

		return null;
	}

	/** 从folder中加载 **/
	private static String loadConfigurationByFolder(String folder, String fileName) {
		String filePath = folder + "/" + fileName;
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return null;
		}
		return FileUtils.loadFile(filePath, "UTF-8");
	}
}
