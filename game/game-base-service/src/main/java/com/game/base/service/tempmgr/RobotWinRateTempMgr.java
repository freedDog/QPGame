package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.mgr.RobotIncomeExpenseMgr;
import com.game.base.utils.RandomUtils;
import com.game.entity.configuration.RobotwinrateTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;
import com.game.utils.DataUtils;

/**
 * 机器人胜率配置模板管理
 * @author Administrator
 *
 */
public class RobotWinRateTempMgr {
	
	public static final int WIN_TYPE_LOST = -1; // 必输
	public static final int WIN_TYPE_RANDOM = 0; // 随机
	public static final int WIN_TYPE_WIN = 1; // 必赢
	
	
	private static Map<Integer, List<RobotWinRateInfo>> tempInfos;
	
	private static Map<Integer, List<RobotWinRateInfo>> sortInfos;
	
	public static boolean init() {
		tempInfos = new HashMap<>();
		sortInfos = new HashMap<>();

		// 加载配置
		String jsonStr = ConfigurationMgr.loadConfiguration("Robotwinrate.json");
		if (StringUtils.isEmpty(jsonStr)) {
			return false;
		}

		// 读取模板列表
		List<RobotwinrateTempInfo> etempInfos = JSON.parseArray(jsonStr,
				RobotwinrateTempInfo.class);
//		System.out.println("etempInfos size:" + etempInfos.size());
		for (RobotwinrateTempInfo tempInfo : etempInfos) {
			if (tempInfo == null)
				continue;
			RobotWinRateInfo info = new RobotWinRateInfo();
			info.setTemplateId(tempInfo.getTemplateId());
			info.setWinRate(DataUtils.splitToInt(tempInfo.getWinRate(), ","));
			info.setGameType(tempInfo.getGameType());
			info.setFactor(Double.parseDouble(tempInfo.getFactor()));
			List<RobotWinRateInfo> gameTempList = tempInfos.get(info.getGameType());
			if (gameTempList == null) {
				gameTempList = new ArrayList<>();
				tempInfos.put(info.getGameType(), gameTempList);
			}
			gameTempList.add(info);
		}
		sortInfos.putAll(tempInfos);
		
		for(List<RobotWinRateInfo> infoList : sortInfos.values()) {
			Collections.sort(infoList, new RobotWinRateInfoComparator());
			Log.info("info:" + infoList);
		}
		
		
		return true;
	}
	
	/**
	 * 获取机器人的胜负类型
	 * @return -1 必输模式 0 随机模式 1 必赢模式
	 */
	public static int getRobotWinType(int gameType) {
		long expense = RobotIncomeExpenseMgr.getExpenseCount(gameType); // 支出为负值
		long income = RobotIncomeExpenseMgr.getIncomeCount(gameType);
		double rate = 0;
		if (income != 0 && expense != 0) {
			rate = (-expense) / (income * 1D);
		}
		List<RobotWinRateInfo> sortList = sortInfos.get(gameType);
		if (sortList == null || sortList.isEmpty()) {
			Log.error("机器人获取胜负概率错误，无法找到概率配置, gameType:" + gameType + ", rate:" + rate);
			return WIN_TYPE_RANDOM;
		}
		Log.info("getRobotWinType, gameType:" + gameType + ", expense:" + expense + ", income:" + income + ", rate:" + rate);
		for(int i = 0; i < sortList.size(); i++) {
			RobotWinRateInfo info = sortList.get(i);
			if (rate >= info.getFactor()) {
				Log.info("info:" + info);
				return getWinType(info);
			}
		}
		Log.info("info:" + sortList.get(sortList.size() - 1));
		// 超过最大值配置，取最后一条数据
		return getWinType(sortList.get(sortList.size() - 1));
	}
	
	
	private static int getWinType(RobotWinRateInfo info) {
		int total = 0;
		for(int i = 0; i < info.getWinRate().length; i++) {
			total += info.getWinRate()[i];
		}
		int random = RandomUtils.randomInt(total);
		Log.info("getWinType random:" + random);
		int temp = 0;
		int result = 0;
		for(int i = 0; i < info.getWinRate().length; i++) {
			if (random < (temp += info.getWinRate()[i])) {
				result = i;
				break;
			}
		}
		switch (result) {
		case 0:
			result = WIN_TYPE_LOST;
			break;
		case 1:
			result = WIN_TYPE_RANDOM;
			break;
		case 2:
			result = WIN_TYPE_WIN;
			break;
		default:
			result = WIN_TYPE_RANDOM;
			break;
		}
		return result;
	}
	
	public static class RobotWinRateInfoComparator implements Comparator<RobotWinRateInfo>{

		@Override
		public int compare(RobotWinRateInfo o1, RobotWinRateInfo o2) {
			if (o1.getFactor() > o2.getFactor())
				return -1;
			else {
				return 1;
			}
		}
	}
	
	public static class RobotWinRateInfo {
		private int templateId; // 模板id
		
		private double factor; // 计算因子
		
		private int gameType; // 游戏类型
		
		private int[] winRate;

		public int getTemplateId() {
			return templateId;
		}

		public void setTemplateId(int templateId) {
			this.templateId = templateId;
		}

		public double getFactor() {
			return factor;
		}

		public void setFactor(double factor) {
			this.factor = factor;
		}

		public int[] getWinRate() {
			return winRate;
		}

		public void setWinRate(int[] winRate) {
			this.winRate = winRate;
		}
		
		public int getGameType() {
			return gameType;
		}

		public void setGameType(int gameType) {
			this.gameType = gameType;
		}

		@Override
		public String toString() {
			return "RobotWinRateInfo [templateId=" + templateId + ", factor="
					+ factor + ", gameType=" + gameType + ", winRate="
					+ Arrays.toString(winRate) + "]";
		}

	}
	
	
}
