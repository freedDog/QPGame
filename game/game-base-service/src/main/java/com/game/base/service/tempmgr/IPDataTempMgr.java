package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.utils.RandomUtils;
import com.game.entity.configuration.IPDataTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;

/**
 * 机器人IP配置管理器
 * 
 * @author Administrator
 * 
 */
public final class IPDataTempMgr {

	private static Map<Integer, IPDataTempInfo> tempInfos;

	private static List<IPDataTempInfo> infos;

	public static boolean init() {
		tempInfos = new HashMap<>();
		infos = new ArrayList<>();

		// 加载商城配置
		String jsonString = ConfigurationMgr.loadConfiguration("IPData.json");
		if (StringUtils.isEmpty(jsonString)) {
			Log.error("机器人IP配置数据为空! IPData.json");
			return false;
		}

		// 读取商城配置模板
		infos = JSON.parseArray(jsonString, IPDataTempInfo.class);
		// 读取IP配置表
		for (IPDataTempInfo tempInfo : infos) {
			int tempId = tempInfo.getTemplateId();
			// 检测模板
			if (ConfigMgr.isDebug()) {

			}
			tempInfos.put(tempId, tempInfo);
		}

		return true;
	}

	/** 获取机器人IP配置数据 **/
	public static IPDataTempInfo getIpDataTempInfo(int templateId) {
		if (templateId == 0) {
			Log.error("获取机器人IP配置数据失败templateId=" + templateId);
			return null;
		}
		return tempInfos.get(templateId);
	}

	/** 随机获得机器人IP配置数据 **/
	public static IPDataTempInfo getRandomTempInfo() {
		if (infos == null) {
			return null;
		}
		int randomId = RandomUtils.randomInt(0, infos.size());
		return infos.get(randomId);
	}
}
