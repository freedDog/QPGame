package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.GameKindType;
import com.game.base.service.constant.ProductType;
import com.game.base.service.mgr.SharedCounterMgr;
import com.game.entity.bean.ProductResult;
import com.game.entity.configuration.LobbyTempInfo;
import com.game.framework.component.change.IChangeResult;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;
import com.game.proto.rp.lobby.LobbyMsg;
import com.game.utils.DataUtils;

/**
 * 商品模板管理器
 */
public class LobbyTempMgr {
	public static final int landlord_primary_templateId = 1; // 斗地主低级场模板ID
	public static final int landlord_middle_templateId = 2; // 斗地主中级场模板ID
	public static final int landlord_senoir_templateId = 3; // 斗地主高级场模板ID
	public static final int landlord_free_templateId = 4; // 斗地主体验场模板ID
	
	public static final int bullgoldfight_primary_templateId = 10; // 斗金牛低级场模板ID
	public static final int bullgoldfight_middle_templateId = 11; // 斗金牛中级场模板ID
	public static final int bullgoldfight_senoir_templateId = 12; // 斗金牛高级场模板ID
	
	public static final int mahjong_free_templateId = 50; // 麻将体验场模板ID
	public static final int mahjong_primary_templateId = 51; // 麻将低级场模板ID
	public static final int mahjong_middle_templateId = 52; // 麻将中级场模板ID
	public static final int mahjong_senoir_templateId = 53; // 麻将高级场模板ID
	
	public static final int zhajinhua_primary_templateId = 71; // 炸金花低级场模板ID
	public static final int zhajinhua_middle_templateId = 72; // 炸金花中级场模板ID
	public static final int zhajinhua_senoir_templateId = 73; // 炸金花高级场模板ID
	
	public static final int daxuan_primary_templateId = 91; // 扯旋低级场模板ID
	public static final int daxuan_middle_templateId = 92; // 扯旋中级场模板ID
	public static final int daxuan_senoir_templateId = 93; // 扯旋高级场模板ID
	
	protected static Map<Integer, LobbyTempInfo> tempInfos;

	protected static boolean init() {
		tempInfos = new HashMap<>();

		// 加载配置
		String jsonStr = ConfigurationMgr.loadConfiguration("Lobby.json");
		if (StringUtils.isEmpty(jsonStr)) {
			Log.error("找不到配置文件!");
			return false;
		}

		// 读取随机名称模板
		List<LobbyTempInfo> tempInfos0 = JSON.parseArray(jsonStr, LobbyTempInfo.class);
		for (LobbyTempInfo tempInfo : tempInfos0) {
			int tempId = tempInfo.getTemplateId();
			if (!ConfigMgr.isDebug()) {
				// 数据验证
				int type = tempInfo.getKindType();
				if (type == GameKindType.DFDJ) {
					// 巅峰对决参数检测: 类型,Id, 地主胜利获得数量, 农民失败获取数量, 地主失败获得数量, 农民胜利获得数量
					String awards = tempInfo.getAwards();
					int[] values = DataUtils.splitToInt(awards, ",");
					int vsize = (values != null) ? values.length : 0;
					if (vsize < 6) {
						Log.error("巅峰对决奖励参数错误! 至少要6个参数!" + tempInfo);
						return false;
					}
					// 检测奖励
					int atype = values[0];
					if (atype != ProductType.CURRENCY) {
						Log.error("巅峰对决奖励参数错误! 参数1必须为货币类型! " + tempInfo);
						return false;
					}
					int aid = values[1];
					if (atype == 0 || aid == 0) {
						Log.error("巅峰对决奖励参数错误! 参数1和参数2为类型和Id, 不能为空! " + tempInfo);
						return false;
					}

				}

			}

			// 检测冲突
			LobbyTempInfo old = tempInfos.put(tempId, tempInfo);
			if (old != null) {
				Log.error("存在重复Id的大厅!" + tempInfo + " -> " + old);
				return false;
			}
		}

		// 重置玩家数量
		resetPlayerCount();
		return true;
	}
	
	public static String getLobbyName(int templateId) {
		String retStr = "";
		switch (templateId) {
		case landlord_primary_templateId:
			retStr = "初级场";
			break;
		case landlord_middle_templateId:
			retStr = "中级场";
			break;
		case landlord_senoir_templateId:
			retStr = "高级场";
			break;
		case landlord_free_templateId:
			retStr = "体验场";
			break;
		case bullgoldfight_primary_templateId:
			retStr = "初级场";
			break;
		case bullgoldfight_middle_templateId:
			retStr = "中级场";
			break;
		case bullgoldfight_senoir_templateId:
			retStr = "高级场";
			break;
		case mahjong_free_templateId:
			retStr = "体验场";
			break;
		case mahjong_primary_templateId:
			retStr = "初级场";
			break;
		case mahjong_middle_templateId:
			retStr = "中级场";
			break;
		case mahjong_senoir_templateId:
			retStr = "高级场";
			break;
		case zhajinhua_primary_templateId:
			retStr = "初级场";
			break;
		case zhajinhua_middle_templateId:
			retStr = "中级场";
			break;
		case zhajinhua_senoir_templateId:
			retStr = "高级场";
			break;
		case daxuan_primary_templateId:
			retStr = "初级场";
			break;
		case daxuan_middle_templateId:
			retStr = "中级场";
			break;
		case daxuan_senoir_templateId:
			retStr = "高级场";
			break;
		default:
			break;
		}
		return retStr;
	}

	/** 获取模板 **/
	public static LobbyTempInfo getTempInfo(int id) {
		return tempInfos.get(id);
	}

	public static List<LobbyTempInfo> getTempInfos() {
		return new ArrayList<>(tempInfos.values());
	}

	/** 创建消息 **/
	public static LobbyMsg createMsg(LobbyTempInfo tempInfo) {
		LobbyMsg msg = new LobbyMsg();
		msg.setId(tempInfo.getTemplateId());
		msg.setName(tempInfo.getName());
		msg.setBaseScore(tempInfo.getBaseScore());
		msg.setCurrencyId(tempInfo.getCurrencyId());
		msg.setCurrencyLimitMin(tempInfo.getCurrencyLimitMin());
		msg.setCurrencyLimitMax(tempInfo.getCurrencyLimitMax());
		msg.setExpend(tempInfo.getExpend());
		msg.setKindType(tempInfo.getKindType());
		msg.setPlayerCount(0);
		return msg;
	}

	/**
	 * 检测货币限制
	 * 
	 * @result code: IChangeResult.NOTENOUGH 不足, IChangeResult.TOOMUCH 超过
	 **/
	public static ProductResult checkCurrencyLimit(LobbyTempInfo tempInfo, long nowValue) {
		int currencyId = tempInfo.getCurrencyId();
		int lmin = tempInfo.getCurrencyLimitMin();
		int lmax = tempInfo.getCurrencyLimitMax();
		if (lmin >= 0 && nowValue < lmin) {
			return ItemTempMgr.createProductResult(IChangeResult.NOTENOUGH, ProductType.CURRENCY, currencyId, 0);
		} else if (lmax >= 0 && nowValue >= lmax) {
			return ItemTempMgr.createProductResult(IChangeResult.TOOMUCH, ProductType.CURRENCY, currencyId, 0);
		}
		return ProductResult.succeed();
	}

	/********************* 斗地主大厅人数同步 *************************/

	private static final String KEY_COUNTER = "LobbyPlayerCounter";

	/** 清除房间人数(中控模块调用) **/
	public static void resetPlayerCount() {
		SharedCounterMgr.clearAll(KEY_COUNTER);
	}

	/** 修改大厅人数 **/
	public static long changePlayerCount(int lobbyId, long change) {
		return SharedCounterMgr.change(KEY_COUNTER, lobbyId, change);
	}

	/** 获取大厅人数 **/
	public static long getPlayerCount(int lobbyId) {
		return SharedCounterMgr.get(KEY_COUNTER, lobbyId);
	}

}