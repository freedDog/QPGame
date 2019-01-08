package com.game.base.service.player;

import java.util.Arrays;
import java.util.List;

import com.game.base.service.constant.ProductType;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.framework.component.SyncObject;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.proto.rp.player.SimplePlayerMsg;

/**
 * 跨服玩家<br>
 * 1. 新增了跨服修改core模块玩家资源的功能.<br>
 * RemotePlayer.java
 * @author JiangBangMing
 * 2019年1月8日下午4:54:49
 */
public abstract class RemotePlayer extends Player {

	/** 获取core模块接口 **/
	protected ICoreService getCoreService() {
		return GameChannelMgr.getChannelServiceByPlayerId(this.getPlayerId(), ModuleName.CORE, ICoreService.class);
	}

	/** 获得是否开透视功能 **/
	public boolean isPrespective() {
		return getCoreService().isPrespective(getPlayerId());
	}

	/** 检测资源是否足够 **/
	public ProductResult checkProduct(Product product, double scale) {
		Product[] products = new Product[] { product, };
		return checkProducts(Arrays.asList(products), scale);
	}

	/** 修改资源 **/
	public final ProductResult changeProduct(Product product, double scale, short source) {
		Product[] products = new Product[] { product, };
		return changeProducts(Arrays.asList(products), scale, source);
	}

	/** 修改货币 **/
	public ProductResult changeCurrency(int currencyId, long change, short source) {
		Product[] products = new Product[] { new Product(ProductType.CURRENCY, currencyId, change), };
		return changeProducts(Arrays.asList(products), 1, source);
	}

	/** 检测是否能消耗 **/
	public ProductResult checkProducts(List<Product> products, double scale) {
		return checkProducts(getPlayerId(), products, scale);
	}

	/** 修改资源 **/
	public ProductResult changeProducts(List<Product> products, double scale, short source) {
		return changeProducts(getPlayerId(), products, scale, source);
	}

	/** 获取玩家货币 **/
	public long getCurrency(int currencyId) {
		return getCurrency(getPlayerId(), currencyId);
	}

	/** 创建消息 **/
	public SimplePlayerMsg createSimpleMsg() {
		SimplePlayerMsg msg = new SimplePlayerMsg();
		msg.setPlayerId(getPlayerId());
		msg.setName(getName());
		msg.setLevel(getLevel());
		return msg;
	}

	/*********************** 静态 *************************/

	/** 获取玩家货币(迫不得已这样还是不好的, 效率低) **/
	public static long getCurrency(long playerId, int currencyId) {
		ICoreService service = GameChannelMgr.getChannelServiceByPlayerId(playerId, ModuleName.CORE, ICoreService.class);
		return service.getCurrency(playerId, currencyId);
	}

	/** 检测是否能消耗 **/
	public static ProductResult checkProducts(long playerId, List<Product> products, double scale) {
		// 发送修改
		ICoreService service = GameChannelMgr.getChannelServiceByPlayerId(playerId, ModuleName.CORE, ICoreService.class);
		return service.checkProducts(playerId, products, scale);
	}

	/** 修改资源 **/
	public static ProductResult changeProducts(long playerId, List<Product> products, double scale, short source) {
		// ProductResult result = checkProducts(playerId, products, scale);
		// if (!result.isSucceed()) {
		// return result;
		// }

		// 同步对象
		final SyncObject<ProductResult> syncObj = new SyncObject<>();
		syncObj.start();

		// 发送修改
		ICoreService service = GameChannelMgr.getChannelServiceByPlayerId(playerId, ModuleName.CORE, ICoreService.class);
		service.changeProducts(playerId, products, scale, source, new RpcCallback() {
			@Override
			protected void onTimeOut() {
				syncObj.complete(false, "修改货币超时", null);
			}

			@SuppressWarnings("unused")
			void onCallBack(ProductResult result) {
				syncObj.success(result);
			}
		});

		// 遍历等待
		int waitTime = 10 * 1000;
		while (syncObj.waiting(waitTime)) {
			// Thread.sleep(1); // 1是最合理的, 0反倒不合理(随机激活).
			Thread.yield(); // 切换线程等待, 效率最高.
		}

		// 判断失败
		if (!syncObj.isSucceed()) {
			return ProductResult.error("修改资源超时");
		}
		return syncObj.getObj();
	}
}
