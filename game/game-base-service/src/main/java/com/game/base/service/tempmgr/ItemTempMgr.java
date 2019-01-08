package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.ItemType;
import com.game.base.service.constant.ProductType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.Formatter;
import com.game.base.service.language.LanguageSet;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.configuration.ItemTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;

/**
 * 物品模板管理器
 * ItemTempMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午12:01:14
 */
public class ItemTempMgr {
	private static Map<Integer, ItemTempInfo> tempInfos; // 物品表

	public static boolean init() {
		tempInfos = new HashMap<>();
		// 加载配置
		String jsonStr = ConfigurationMgr.loadConfiguration("Item.json");
		if (StringUtils.isEmpty(jsonStr)) {
			Log.error("找不到配置文件!");
			return false;
		}
		// 读取随机名称模板
		List<ItemTempInfo> tempinfos0 = JSON.parseArray(jsonStr, ItemTempInfo.class);
		for (ItemTempInfo tempInfo : tempinfos0) {
			int itemTempId = tempInfo.getTemplateId();
			if (itemTempId == 0) {
				Log.error("物品模板Id不能为0!" + tempInfo);
				return false;
			}

			if (!ConfigMgr.isDebug()) {
				int mtype = tempInfo.getMasterType();
				int stype = tempInfo.getSonType();
				if (itemTempId < 0) {
					if (mtype != ItemType.CURRENCY) {
						Log.error("物品Id小于0的必须是货币!" + tempInfo);
						return false;
					}
				}
				// 按照类型检测
				switch (mtype) {
				case ItemType.NORMAL:

					break;
				case ItemType.USEABLE:
				case ItemType.AUTO_USE: {
					switch (stype) {
					case ItemType.UseableType.BOX: {
						// 检测参数
						if (StringUtils.isEmpty(tempInfo.getParam01()) || !Product.checkProductString(tempInfo.getParam01())) {
							Log.error("宝箱物品, 参数1无法解析产品!" + tempInfo);
							return false;
						}
						break;
					}
//					case ItemType.UseableType.VIP:
//					case ItemType.UseableType.BUFF: {
//						// 检测buff模板
//						int buffTempId = tempInfo.getParam01Int();
//						EffectTempInfo tinfo = EffectTempMgr.getBuffTempInfo(buffTempId);
//						if (tinfo == null) {
//							Log.error("buff物品类型, 参数1找不到对应的buff模板!" + tempInfo);
//							return false;
//						}
//						// 检测时间
//						int time = tempInfo.getParam02Int();
//						if (time < 0) {
//							Log.error("buff物品类型, 参数2时间错误!" + tempInfo);
//							return false;
//						}
//						break;
//					}
					default:
						Log.error("未知可用类型!" + tempInfo);
						return false;
					}
					break;
				}
				}
			}
			tempInfos.put(itemTempId, tempInfo);
		}

		return true;
	}

	public static ItemTempInfo getTempInfo(int tempId) {
		return tempInfos.get(tempId);
	}

	/** 创建处理结果 **/
	public static ProductResult createProductResult(int code, int type, int id, long change) {
		// 成功处理
		if (code > 0) {
			return ProductResult.create(ProductResult.class, code, null);
		}
		// 物品名称字符串
		String itemStr = Formatter.createItemString(id);

		// 错误字符串
		String errStr = null;
		switch (code) {
		case ProductResult.NOCHANGE:
			errStr = LanguageSet.get(TextTempId.ID_10, itemStr, change);
			break;
		case ProductResult.NOOBJ:
			errStr = LanguageSet.get(TextTempId.ID_7, "获取不了数据对象");
			Log.error("获取不到数据对象!? " + id, true);
			break;
		case ProductResult.NOTENOUGH:
			errStr = LanguageSet.get(TextTempId.ID_2007, itemStr);
			break;
		case ProductResult.TOOMUCH:
			errStr = LanguageSet.get(TextTempId.ID_2006, itemStr);
			break;
		case ProductResult.SETERROR:
			errStr = LanguageSet.get(TextTempId.ID_7, itemStr + " 修改失败");
			Log.error("设置数据失败!? " + id, true);
			break;
		case ProductResult.IDERROR:
			errStr = LanguageSet.get(TextTempId.ID_2004, ProductType.ITEM, id);
			break;
		case ProductResult.NOSPACE:
			errStr = LanguageSet.get(TextTempId.ID_2005);
			break;
		}

		// 创建消息
		return ProductResult.create(code, errStr, type, id, change);
	}

	/** 通过名字搜索(API接口) **/
	public static List<ItemTempInfo> findByName(String itemName) {
		List<ItemTempInfo> rets = new ArrayList<>();
		for (ItemTempInfo temp : tempInfos.values()) {
			if (temp.getName().contains(itemName)) {
				rets.add(temp);
			}
		}
		return rets;
	}
}
