package com.game.core.player;

import java.util.List;

import com.game.base.service.constant.CurrencyId;
import com.game.base.service.constant.GameConst;
import com.game.base.service.constant.ProductType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.player.PlayerInventory;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.core.player.CurrencyInventory.CurrencyData;
import com.game.core.rank.RankInventory;
import com.game.entity.bean.ProductResult;
import com.game.entity.entity.PlayerExtendInfo;
import com.game.entity.entity.PlayerInfo;
import com.game.framework.component.ChangeCounter;
import com.game.framework.component.change.ChangeUtils;
import com.game.framework.component.change.Changer;
import com.game.framework.component.change.IChangeResult;
import com.game.framework.component.component.ComponentRepeatMap;
import com.game.framework.utils.ResourceUtils;

/**
 * 货币处理
 * 
 */
public class CurrencyInventory extends PlayerInventory<CorePlayer> {
	private final Object lock = new Object(); // 货币修改锁
	public final ChangeCounter changeCounter; // 货币修改标记

	protected CurrencyInventory(CorePlayer player) {
		super(player);
		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				sendCurrencyMsg();
			}
		};

	}

	@Override
	protected boolean load() {
		return true;
	}

	@Override
	protected void unload() {
	}

	/** 发送货币消息 **/
	public void sendCurrencyMsg() {
//		CurrencyMsg msg = new CurrencyMsg();
////		msg.setGold((int) get(CurrencyId.GOLD));
//		msg.setPoint(get(CurrencyId.POINT));
////		msg.setUgold((int) get(CurrencyId.UGOLD));
////		msg.setUujun((int) get(CurrencyId.UJUN));
////		msg.setSnatch((int) get(CurrencyId.SNATCH));
//		msg.setFquan((int) get(CurrencyId.FQUAN));
//		msg.setRoomcard((int) get(CurrencyId.ROOMCARD));
//
//		player.sendPacket(Protocol.C_CURRENCY_INFO, msg);
	}


	/** 设置货币(通用化逻辑), 不对外开放. **/
	private ProductResult set(int id, long prev, final long now, long change) {
		PlayerInfo info = player.getPlayerInfo();
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		switch (id) {
		case CurrencyId.POINT:
			info.setPoint(now);
			break;
		case CurrencyId.FQUAN:
			extendInfo.setFQuan((int) now);
			break;
		case CurrencyId.ROOMCARD:
			extendInfo.setRoomCard((int) now);
			break;
		default:
			return ProductResult.create(IChangeResult.IDERROR, LanguageSet.get(
					TextTempId.ID_2004, ProductType.CURRENCY,
					String.valueOf(id)), ProductType.CURRENCY, id, 0);
		}
		return ProductResult.succeed(ProductType.CURRENCY, id, change);
	}

	/** 获取货币 **/
	public long get(int id) {
		PlayerInfo playerInfo = player.getPlayerInfo();
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		switch (id) {
//		case CurrencyId.UGOLD:
//			return extendInfo.getUGold();
//		case CurrencyId.UJUN:
//			return extendInfo.getUJun();
//		case CurrencyId.EXP:
//			return playerInfo.getExp();
//		case CurrencyId.GOLD:
//			return playerInfo.getGold();
		case CurrencyId.POINT:
			return playerInfo.getPoint();
//		case CurrencyId.SNATCH:
//			return extendInfo.getSnatch();
		case CurrencyId.FQUAN:
			return extendInfo.getFQuan();
		case CurrencyId.ROOMCARD:
			return extendInfo.getRoomCard();
		}
		return 0L;
	}

	/** 货币修改完成(锁定外处理). **/
	private void onChangeComplete(int id, long preValue, long setValue,
			long change, short source, boolean isNoticeClient) {
		switch (id) {
//		case CurrencyId.EXP:
//			player.changeCounter.change();
//			break;
//
//		case CurrencyId.GOLD: {
//			// 提交一个更新任务
//			LandlordInventory landlordInventory = player
//					.getInventory(LandlordInventory.class);
//			landlordInventory.updatePlayerInfoToRoomModule();
//
////			// 更新抢宝玩家数据
////			SnatchInventory snatchInventory = player
////					.getInventory(SnatchInventory.class);
////			snatchInventory.updateSnatchPlayerInfoModule();
//			// 货币消息下发
//			this.changeCounter.change();
//			break;
//		}
//		case CurrencyId.UJUN: {
////			// 更新抢宝玩家数据
////			SnatchInventory snatchInventory = player
////					.getInventory(SnatchInventory.class);
////			snatchInventory.updateSnatchPlayerInfoModule();
//			// 货币消息下发
//			this.changeCounter.change();
//			break;
//		}
//		case CurrencyId.UGOLD: {
//			// 提交一个更新任务
//			LandlordInventory landlordInventory = player
//					.getInventory(LandlordInventory.class);
//			landlordInventory.updatePlayerInfoToRoomModule();
//
//			// 货币消息下发
//			this.changeCounter.change();
//			break;
//		}
//		case CurrencyId.SNATCH: {
////			// 更新抢宝玩家数据
////			SnatchInventory snatchInventory = player
////					.getInventory(SnatchInventory.class);
////			snatchInventory.updateSnatchPlayerInfoModule();
//			// 货币消息下发
//			this.changeCounter.change();
//			break;
//		}
		default:
			// 货币消息下发
			this.changeCounter.change();
		}

		// 通用事件
		CorePlayer.changeProductComplete(player, ProductType.CURRENCY, id,
				preValue, setValue, change, source, isNoticeClient);
	}

	/** 货币修改事件(锁定中) **/
	private void onChange(int id, long preValue, long nowValue, long change,
			short source) {
		switch (id) {
//		case CurrencyId.EXP:
//			if (change > 0) {
//				updateLevel();
//			}
//			break;
//		case CurrencyId.GOLD: {
//			if (change > 0) {
//				player.getInventory(RankInventory.class).addGoldRecode((int) change);
//			}
//			break;
//		}
//		case CurrencyId.UGOLD: {
//			if (change > 0) {
//				player.getInventory(RankInventory.class).addUGoldRecode((int) change);
//			}
//			break;
//		}
		case CurrencyId.POINT: {
			if (change > 0) {
				player.getInventory(RankInventory.class).addDiamondRecode((int) change);
			}
		}
		case CurrencyId.FQUAN: {
			if (change > 0) {
				player.getInventory(RankInventory.class).addFuQuanRecode((int) change);
			}
		}
		case CurrencyId.ROOMCARD: {
			if (change > 0) {
				player.getInventory(RankInventory.class).addRoomCardRecode((int) change);
			}
		}

		}
	}

	/** 修改货币 **/
	public ProductResult change(int id, long change, short source,
			boolean isNoticeClient) {
		// 特殊处理
		switch (id) {
//		case CurrencyId.EXP: {
//			// 检测经验收益变量
//			if (change > 0) {
//				// 判断是否是VIP
//				if (player.isVip()) {
//					// 经验翻倍
//					IVipTempInfo vInfo = VipTempMgr.get(player.getVipLv());
//					change = (long) Math.max(change * vInfo.getExp(), 0);
//				}
//			}
//		}
		}

//		// 斗地主奖励添加
//		if (source == ProductSourceType.GAME_RESULT) {
//			change = checkEffect(id, change);
//		}
		/*
		 * // 活动奖励添加 if (source == ProductSourceType.BUY_REWARD) { change =
		 * ActivityTempMgr.DoCurrencyActivity(id, change, player.isVip()); }
		 */

		// 创建修改数据
		CurrencyData data = new CurrencyData();
		data.setPlayer(player);
		data.setNoticeClient(isNoticeClient);
		data.setSource(source);

		// 从其他绑定接口检测处理
		CurrencyChanger otherChanger = currencyChangers.get(id);
		if (otherChanger != null) {
			return ChangeUtils.change(data, id, change, lock, otherChanger);
		}

		// 货币常规修改
		return ChangeUtils.change(data, id, change, lock, changer);
	}

	/** 检测是否能修改 **/
	public ProductResult check(int id, long change) {
		// 特殊处理
		switch (id) {
		}

		// 创建修改数据
		CurrencyData data = new CurrencyData();
		data.setPlayer(player);

		// 从其他绑定接口检测处理
		CurrencyChanger otherChanger = currencyChangers.get(id);
		if (otherChanger != null) {
			return ChangeUtils.check(data, id, change, lock, otherChanger);
		}

		// 执行处理
		return ChangeUtils.check(data, id, change, lock, changer);
	}

	@Override
	protected boolean save() {
		return true;
	}

	/********************************* 静态 **************************************/
	/** 修改接口 **/
	private final CurrencyBaseChanger changer = new CurrencyBaseChanger() {

		@Override
		public long get(CurrencyData data, int id) {
			return data.getInventory(CurrencyInventory.class).get(id);
		}

		@Override
		public void onChange(CurrencyData data, int id, long preValue,
				long setValue, long change) {
			data.getInventory(CurrencyInventory.class).onChange(id, preValue,
					setValue, change, data.getSource());
		}

		@Override
		public void onChangeComplete(CurrencyData data, int id, long preValue,
				long setValue, long change) {
			data.getInventory(CurrencyInventory.class).onChangeComplete(id,
					preValue, setValue, change, data.getSource(),
					data.isNoticeClient());
		}

		@Override
		protected ProductResult set(CorePlayer player, CurrencyData data,
				int id, long prev, long now, long change) {
			return data.getInventory(CurrencyInventory.class).set(id, prev,
					now, change);
		}
	};
	/** 外部货币处理接口 **/
	protected final static ComponentRepeatMap<Integer, CurrencyChanger> currencyChangers;
	static {
		currencyChangers = new ComponentRepeatMap<Integer, CurrencyChanger>() {
			@Override
			protected boolean load() {
				String regex = ResourceUtils
						.getPacketRegex("com.changic.sg.core");
				List<Class<?>> classes = ResourceUtils.getClassesByClass(
						CurrencyChanger.class, regex);
				return super.init(classes);
			}

			@Override
			protected Integer[] getKeys(CurrencyChanger changer) {
				return (Integer[]) changer.getIds();
			}
		};

	}

	/********************************* 内嵌类 **************************************/
	/** 货币修改数据 **/
	public static class CurrencyData extends ChangeUtils.ChangeData {
		protected CorePlayer player;
		protected short source;
		protected boolean isNoticeClient;
		private Object inventory; // inventory缓存

		public CorePlayer getPlayer() {
			return player;
		}

		@SuppressWarnings("unchecked")
		public <D extends PlayerInventory<?>> D getInventory(Class<D> clazz) {
			// 检测当前缓存
			if (inventory != null && inventory.getClass() == clazz) {
				return (D) inventory;
			}
			// 尝试获取
			D inventory = player.getInventory(clazz);
			if (inventory == null) {
				return null; // 获取失败
			}
			// 记录缓存
			this.inventory = inventory;
			return inventory;
		}

		public short getSource() {
			return source;
		}

		public void setSource(short source) {
			this.source = source;
		}

		public boolean isNoticeClient() {
			return isNoticeClient;
		}

		public void setNoticeClient(boolean isNoticeClient) {
			this.isNoticeClient = isNoticeClient;
		}

		public void setPlayer(CorePlayer player) {
			this.player = player;
		}
	}

	/** 货币修改接口(起服会扫描获得) **/
	public static abstract class CurrencyChanger extends CurrencyBaseChanger {
		/** 筛选器 **/
		protected abstract Object[] getIds();
	}

}

/** 货币修改接口 **/
abstract class CurrencyBaseChanger extends
		Changer<CurrencyInventory.CurrencyData, ProductResult> {

	/** 修改货币 **/
	protected abstract ProductResult set(CorePlayer player, CurrencyData data,
			int id, long preValue, long setValue, long change);

	@Override
	public void onChangeComplete(CurrencyData data, int id, long prev,
			long now, long change) {
		// 通用事件
		CorePlayer.changeProductComplete(data.getPlayer(),
				ProductType.CURRENCY, id, prev, now, change, data.getSource(),
				data.isNoticeClient());
	}

	@Override
	public final ProductResult set(CurrencyData data, int id, long prev,
			long now, long change) {
		now = Math.min(GameConst.CURRENCY_MAX, now); // 多余21亿吃掉.
		change = now - prev;
		return set(data.getPlayer(), data, id, prev, now, change);
	}

	@Override
	public ProductResult result(int code, CurrencyData data, int id,
			long change, long prev, long now, long resultChange) {
		return ItemTempMgr.createProductResult(code, ProductType.CURRENCY, id,
				change);
	}

}
