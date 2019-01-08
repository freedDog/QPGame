package com.game.base.service.mgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.tempmgr.ConfigurationMgr;
import com.game.entity.configuration.TextTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;

/**
 * 文本配置表数据
 * TextTempMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午1:32:42
 */
public class TextTempMgr {

	protected static Map<Integer, TextTempInfo> tempInfos;

	public static boolean init() {

		tempInfos = new HashMap<>();

		// 加载配置
		String jsonString = ConfigurationMgr.loadConfiguration("Text.json");
		if (StringUtils.isEmpty(jsonString)) {
			Log.error("文本配置的数据为空");
			return false;
		}

		// 读取模板列表
		List<TextTempInfo> infos = JSON.parseArray(jsonString, TextTempInfo.class);
		for (TextTempInfo tempInfo : infos) {
			int tempId = tempInfo.getTemplateId();
			// 检测对应条件模板
			if (!ConfigMgr.isDebug()) {
				// 检测获取方式
			}
			tempInfos.put(tempId, tempInfo);
		}

		return true;

	}

	/** 获取文本对象 **/
	public static TextTempInfo getTextTempInfo(int tempId) {
		return tempInfos.get(tempId);
	}

	/** 获取文本内容 **/
	public static String getText(int tempId) {
		TextTempInfo info = tempInfos.get(tempId);
		if (info == null) {
			return "获取内容失败，找不到模板ID" + tempId;
		}

		return info.getText();
	}
}

