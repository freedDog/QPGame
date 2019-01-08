package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.List;

import com.game.base.service.constant.ProductType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.Formatter;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.player.Player;
import com.game.base.utils.AgingUtils;
import com.game.base.utils.GameUtils;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.configuration.ItemTempInfo;
import com.game.framework.component.change.IChangeResult;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.struct.result.Result;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.struct.ProductBatchListMsg;
import com.game.proto.rp.struct.ProductBuyListMsg;
import com.game.proto.rp.struct.ProductListMsg;
import com.game.proto.rp.struct.ProductMsg;
import com.game.proto.rp.text.ProductErrorMsg;

/**
 * 商品模板管理器
 */
public class ProductTempMgr {

	public static boolean init() {
		return true;
	}

	/** 检测产品有效性(判断是否有种东西) **/
	public static Result checkProducts(List<Product> products) {
		// 检测附件是否存在
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (!ProductTempMgr.checkProduct(product.getType(), product.getId())) {
				return Result.error("附件奖励物品不存在!" + product);
			}
		}
		return Result.succeed();
	}

	/** 检测产品有效性(判断是否有种东西) **/
	public static boolean checkProduct(int type, int tempId) {
		switch (type) {
		case ProductType.CURRENCY:
		case ProductType.ITEM: {
			ItemTempInfo tempInfo = ItemTempMgr.getTempInfo(tempId);
			if (tempInfo == null) {
				Log.error("不存在物品! type : " + type + ", tempId : " + tempId);
				return false;
			}
			return true;
		}
//		case ProductType.BUFF: {
//			EffectTempInfo tempInfo = EffectTempMgr.getBuffTempInfo(tempId);
//			if (tempInfo == null) {
//				Log.error("不存在buff! type : " + type + ", tempId : " + tempId);
//				return false;
//			}
//			return true;
//		}
		case ProductType.DROP:
			return true;
		}
		Log.error("产品类型不存在, type : " + type + ", tempId : " + tempId);
		return false;
	}

	/** 生成消息 **/
	public static ProductMsg createProductMsg(Product product) {
		ProductMsg infoMsg = new ProductMsg();
		infoMsg.setType(product.getType());
		infoMsg.setId(product.getId());
		infoMsg.setCount(product.getCount());
		return infoMsg;
	}

	/** 生成消息 **/
	public static ProductListMsg createProductListMsg(List<Product> products) {
		ProductListMsg msg = new ProductListMsg();
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			ProductMsg pmsg = createProductMsg(product);
			// 添加消息
			msg.addProduct(pmsg);
		}
		return msg;
	}

	/** 生成消息列表 **/
	public static List<ProductMsg> createProductMsgList(List<Product> products) {
		List<ProductMsg> list = new ArrayList<>();
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			ProductMsg pmsg = createProductMsg(product);
			list.add(pmsg);
		}
		return list;
	}

	/** 创建商品消息 **/
	public static ProductBuyListMsg createProductBuyMsg(List<Product> awardProducts, List<Product> vips, List<Product> activitys) {
		ProductBuyListMsg lMsg = new ProductBuyListMsg();
		lMsg.addAllProduct(createProductMsgList(awardProducts));
		lMsg.addAllVip(createProductMsgList(vips));
		lMsg.addAllActivity(createProductMsgList(activitys));
		return lMsg;
	}

	/** 创建商品消息 **/
	public static ProductBuyListMsg createProductBuyMsg(List<Product> awardProducts) {
		ProductBuyListMsg lMsg = new ProductBuyListMsg();
		lMsg.addAllProduct(createProductMsgList(awardProducts));
		return lMsg;
	}

	/** 生成消息 **/
	public static ProductBatchListMsg createProductBatchListMsg(List<List<Product>> list) {
		ProductBatchListMsg msg = new ProductBatchListMsg();
		int psize = (list != null) ? list.size() : 0;
		for (int i = 0; i < psize; i++) {
			List<Product> products = list.get(i);
			ProductListMsg pmsg = createProductListMsg(products);
			// 添加消息
			msg.addProduct(pmsg);
		}
		return msg;
	}

	/** 创建商品修改文本 **/
	public static String createProductChangeString(int type, int id, long count, long prevValue, long setValue) {

		String nameStr = null;
		switch (type) {
		case ProductType.CURRENCY:
		case ProductType.ITEM: {
			// 只处理获得, 不处理扣除.
			if (count <= 0) {
				return null;
			}
			nameStr = Formatter.createItemString(id);
			break;
		}
//		case ProductType.FASHION: {
//			nameStr = Formatter.createFashionString(id);
//			// 判断是否永久获得了
//			int nowTime = (int) setValue;
//			if (AgingUtils.isForeverTime(nowTime)) {
//				return LanguageSet.get(TextTempId.ID_2009, nameStr, 1);
//			}
//
//			// 时间变化
//			int changeTime = (int) count;
//			return LanguageSet.get(TextTempId.ID_2010, nameStr, GameUtils.toShowTimeString(changeTime));
//		}
//		case ProductType.BUFF: {
//			nameStr = Formatter.createBuffString(id);
//			// 判断是否永久获得了
//			int nowTime = (int) setValue;
//			if (AgingUtils.isForeverTime(nowTime)) {
//				return LanguageSet.get(TextTempId.ID_2009, nameStr, 1);
//			}
//
//			// 时间变化
//			int changeTime = (int) count;
//			return LanguageSet.get(TextTempId.ID_2010, nameStr, GameUtils.toShowTimeString(changeTime));
//		}
		}

		// 通用文本
		if (StringUtils.isEmpty(nameStr)) {
			nameStr = LanguageSet.get(TextTempId.ID_2004, type, id);
		}

		// 发送修改文本
		return LanguageSet.get(TextTempId.ID_2009, nameStr, count);
	}

	/** 创建产品错误消息 **/
	public static ProductErrorMsg createProductErrorMsg(ProductResult result) {
		ProductErrorMsg msg = new ProductErrorMsg();
		msg.setType(result.getType());
		msg.setId(result.getId());
		msg.setCount(result.getChange());
		return msg;
	}

	/** 发送产品修改错误消息 **/
	public static void sendProductErrorMsg(Player player, ProductResult result) {
		if (!player.isOnline() || player.isRobet()) {
			return;
		}
		// 发送消息
		sendProductErrorMsg(player.getPlayerId(), result);
	}

	/** 发送产品修改错误消息 **/
	public static void sendProductErrorMsg(long playerId, ProductResult result) {
		// 检测是否不足
		if (result.getCode() == IChangeResult.NOTENOUGH) {
			ProductErrorMsg msg = createProductErrorMsg(result);
			Player.sendPacket(playerId, Protocol.C_PRODUCT_ERROR, msg);
			return;
		}
		// 通用错误处理
		Player.sendText(playerId, result.getMsg());
	}

	/** 发送获取物品消息 **/
	public static void sendProductMsg(long playerId, List<Product> awardProducts, List<Product> vips, List<Product> activitys) {
		ProductBuyListMsg buyMsg = ProductTempMgr.createProductBuyMsg(awardProducts, vips, activitys);
		Player.sendPacket(playerId, Protocol.C_BUY_PRODUCT, buyMsg);
	}

	/** 发送获取物品消息 **/
	public static void sendProductMsg(Player player, List<Product> awardProducts, List<Product> vips, List<Product> activitys) {
		// 跳过不在线用户和机器人
		if (!player.isOnline() || player.isRobet()) {
			return;
		}
		// 发送消息
		sendProductMsg(player.getPlayerId(), awardProducts, vips, activitys);
	}

}