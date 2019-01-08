package com.game.core.player;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.GameConst;
import com.game.base.service.constant.GoodsOrderState;
import com.game.base.service.constant.LoginConst;
import com.game.base.service.constant.OnlineState;
import com.game.base.service.constant.ProductType;
import com.game.base.service.constant.SexType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.gamezone.GameZone;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.base.service.player.PlayerInventory;
import com.game.base.service.tempmgr.IPDataTempMgr;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.core.CoreModule;
import com.game.core.goods.GoodsInventory;
import com.game.core.http.open.LoginRequest;
import com.game.core.player.event.Event;
import com.game.core.player.event.EventProxy;
import com.game.core.player.event.EventType;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.configuration.IPDataTempInfo;
import com.game.entity.dao.PlayerDAO;
import com.game.entity.dao.PlayerExtendDAO;
import com.game.entity.dao.UserDAO;
import com.game.entity.entity.GoodsInfo;
import com.game.entity.entity.PlayerExtendInfo;
import com.game.entity.entity.PlayerInfo;
import com.game.entity.entity.UserInfo;
import com.game.entity.shared.SimplePlayerInfo;
import com.game.framework.component.ChangeCounter;
import com.game.framework.component.annotation.Ignore;
import com.game.framework.component.log.Log;
import com.game.framework.framework.api.IPLocationInfo;
import com.game.framework.utils.ResourceUtils;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.TimeUtils;
import com.game.framework.utils.collection.entity.IEntity;
import com.game.framework.utils.collections.ArrayUtils;
import com.game.proto.msg.RpMessage;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.login.LoginBindMsg;
import com.game.proto.rp.player.SimplePlayerMsg;
import com.game.framework.component.action.ActionQueue;

/**
 * 游戏玩家对象<br>
 * 资源修改方案: check是检测是否超过上限和是否足够, 扣除的话不检测, 扣除到0或者加到上限.
 * CorePlayer.java
 * @author JiangBangMing
 * 2019年1月8日下午1:06:25
 */
public class CorePlayer extends Player implements IEntity {
	private long activeTime; // 这个实体数据上使用时间.
	protected long loadTime; // 加载时间
	public long lastSendNoticeTime; // 最后一次发送世界聊天消息时间

	// 玩家数据
	private UserInfo userInfo; // 账号信息
	private PlayerInfo info; // 玩家信息
	private PlayerExtendInfo extendInfo; // 额外信息
	private Integer playerType; // 强制修改的playerType

	/** 事件管理器 **/
	public final EventProxy eventProxy;
//	/** 效果管理器 **/
//	public final EffectProxy effectProxy;

	/** 更新计数器 **/
	public final ChangeCounter changeCounter;

	public CorePlayer(PlayerInfo info) {
		this.info = info;
		eventProxy = new EventProxy();
//		effectProxy = new EffectProxy(this);
		// 创建队列
		queue = new ActionQueue(CoreModule.getExecutor());

		// 信息更新计数器
		changeCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				sendPlayerMsg();
			}
		};
	}

	/** 设置账号(登陆或者绑定账号处理) **/
	public void resetUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	/** 判断是否需要更新地址信息 **/
	private static boolean isNeedUpdateAddrByIp(CorePlayer player) {
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		// 判断是否需要更新信息地址
		if (StringUtils.isEmpty(extendInfo.getUpdateIP())) {
			return false; // 没有IP不能更新
		}

		// 判断地址
		String address = extendInfo.getAddress();
		if (StringUtils.isEmpty(address)) {
			return true; // 空地区, 更新
		} else {
			IPLocationInfo addr = new IPLocationInfo();
			if (!addr.toDecode(address)) {
				return true; // 解析不出来, 更新.
			}
		}
		// 判断定位
		if (StringUtils.isEmpty(extendInfo.getLocation())) {
			return true;
		} else {
			// 判断定位信息
			double[] location = StringUtils.splitToDouble(
					extendInfo.getLocation(), ",");
			location = ArrayUtils.resetArray(location, 2);
			if (location[0] == 0 && location[1] == 0) {
				return true;
			}
		}
		// 不同更新啦
		return false;
	}

	/** 玩家登陆处理 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void login() {
		// 判断是否在线
		if (this.isOnline()) {
			// Log.debug("玩家重复登录: " + this, true);
			return;
		}
		// Log.debug("玩家登陆: " + this);

		// 预先加载数据, 避免其他操作卡顿.
		for (Class<?> inventoryClass : inventoryClasses) {
			getInventory((Class<PlayerInventory>) inventoryClass); // 加载模块数据
		}

		// 处理登陆逻辑
		super.login();

		// 判断是否是机器人，并且判断该机器人的IP数据是否为空，否则就随机一个IP数据
		if (isRobet()) {
			// 机器人更新Ip
			if (StringUtils.isEmpty(extendInfo.getUpdateIP())) {
				IPDataTempInfo ipTempInfo = IPDataTempMgr.getRandomTempInfo();
				extendInfo.setUpdateIP(ipTempInfo.getIP());
				extendInfo.setAddress(ipTempInfo.getArea());
				extendInfo.setLocation(ipTempInfo.getCoordinate());
			}
		}

		// 判断是否需要更新信息地址
		if (isNeedUpdateAddrByIp(this) && isRobet()) {
			// 更新地址定位信息
			LoginRequest.updateAddress(this, extendInfo.getUpdateIP());
		}

		// 登陆检测每日重置
		updateByDay(0);
		update(0);

		// 检查订单状态
		checkGoodsOrder();
		
//		// 检查任务
//		TaskInventory taskInventory = getInventory(TaskInventory.class);
//		if (taskInventory.getTasks() == null || taskInventory.getTasks().isEmpty()) {
//			taskInventory.resetRandomTask(TaskType.DAY, GameConfigMgr.taskRandomCount);
//			taskInventory.resetRandomTask(TaskType.WEEK, GameConfigMgr.taskRandomCount);
//		}

		// 隔天登陆
		if (System.currentTimeMillis() - info.getLoginTime().getTime() >= TimeUtils.oneDayTimeL) {
			eventProxy.dispatchEvent(EventType.PLAYER_LOGIN, new Event(1));
		}
		
//		PrisePlayerUtilDAO dao = DaoMgr.getInstance().getDao(PrisePlayerUtilDAO.class);
//		
//		PrisePlayerInfo priseInfo = dao.getPlayerPriseByMobilePhone(userInfo.getAccount());
//		
//		if (priseInfo != null && priseInfo.getIsPrised() == 0) {
////			Log.info("priseInfo:" + priseInfo);
//			Product[] products = new Product[] { new Product(ProductType.CURRENCY, CurrencyId.POINT, PlayerConstant.TEST_TAKE_PART_IN_PRISE_POINT_NUM), };
//			MailMgr.addMailByPlayer(this, "游戏测试阶段参与奖励", "感谢您参与了本游戏的测试阶段，祝您游戏愉快.", Arrays.asList(products), TimeUtils.oneWeekTime, ProductSourceType.TEST_PRISE);
//			priseInfo.setPlayerId(getPlayerId());
//			priseInfo.setIsPrised(1);
//			priseInfo.setUpdateTime(new Date());
//			PrisePlayerDAO priseDAO = DaoMgr.getInstance().getDao(PrisePlayerDAO.class);
//			priseDAO.insertOrUpdate(priseInfo);
//		}
		
		// 切换为登陆状态
		info.setLoginTime(new Date());
		setOnlineState(OnlineState.ONLINE);
	}

	/** 玩家登出处理 **/
	@Override
	public void logout() {
		Log.info("logout, id:" + getPlayerId());
		if (!this.isOnline()) {
			return;
		}
		// Log.debug("玩家登出: " + this);
		save();
		Log.warn("玩家（"+getPlayerId()+"）登出 强制save数据");
		// 处理登出处理
		super.logout();

		// 清除类型设置
		if (playerType != null) {
			setType(null);
		}

		// 登出
		setOnlineState(OnlineState.OFFLINE);
		info.setLogoutTime(new Date());
	}

	/** 每日重置处理 **/
	@Override
	protected void onDayReset(int day) {
//		Log.debug("玩家每日重置! " + this + " " + day);

		try {
			changeCounter.beginChange();

			// 数据每日重置
			this.extendInfo.setBrokeAwardCount(0);

			// 每日数据处理
			super.onDayReset(day); // 执行组件每日重置

			if (day > 0) {
				// 相应每日登陆事件, 隔了多天也按照1天算.
				// getEventProxy().dispatchEvent(GameEventType.PLAYER_LOGIN, new
				// EventData(1));
			}
		} finally {
			changeCounter.commitChange(false);
		}

	}

	@Override
	public synchronized boolean load() {
		loadTime = System.currentTimeMillis();

		// 检测创建PlayerExtendInfo
		long playerId = this.getPlayerId();
		PlayerExtendDAO edao = DaoMgr.getInstance().getDao(
				PlayerExtendDAO.class);
		extendInfo = edao.get(playerId);
		if (extendInfo == null) {
			extendInfo = new PlayerExtendInfo();
			extendInfo.setPlayerId(playerId);
			saveSelf();
		}
		info.setVipLv(getVipLv());

		// 读取账号信息
		long userId = info.getUserId();
		if (userId > 0) {
			UserDAO udao = DaoMgr.getInstance().getDao(UserDAO.class);
			userInfo = udao.get(userId);
		}

		// 加载其他数据
		if (!super.load()) {
			Log.error("玩家加载数据失败!" + this);
			return false;
		}

		// 数据整理
		if (!SexType.isSex(info.getSex())) {
			info.setSex(SexType.BOY);
		}
		return true;
	}

	@Override
	public synchronized void unload() {
		logout(); // 执行离线
		saveSelf(); // 保存一下自己

		// 卸载
		super.unload();
	}

	/** 检查订单状态 **/
	public void checkGoodsOrder() {
		List<GoodsInfo> listInfo = this.getInventory(GoodsInventory.class)
				.getListGoods(this.getPlayerId());
		Calendar cal = Calendar.getInstance();
		long time1 = 0;
		cal.setTime(new Date());
		long time2 = cal.getTimeInMillis();
		for (GoodsInfo info : listInfo) {
			if (info.getState() != GoodsOrderState.GOING) {
				continue;
			}
			try {
				Date orderDate = info.getStartTime();
				cal.setTime(orderDate);
				time1 = cal.getTimeInMillis();
				long between_days = (time2 - time1) / (1000 * 3600 * 24);
				{
					if (between_days > 7) {
						info.setState(GoodsOrderState.RECV);
						// info.setEndTime(new Date());

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/** 修改玩家类型(强制修改, 不影响数据库) **/
	public void setType(Integer type) {
		playerType = type;
	}

	/** 设置定位 **/
	public void setLocation(double x, double y) {
//		if (x == 0 && y == 0) {
//			return;
//		}
		extendInfo.setLocation(x + "," + y);
	}

	@Override
	public int getType() {
		// 测试模式, 如果有人登陆, 转成普通玩家
		if (playerType != null) {
			return playerType;
		}
		return (info != null) ? info.getType() : 0;
	}

	/** 检测是否能修改 **/
	public ProductResult checkProduct(int type, int id, long change) {
		switch (type) {
		case ProductType.CURRENCY:
			return getInventory(CurrencyInventory.class).check(id, change);
//		case ProductType.ITEM:
//			return getInventory(BagInventory.class).check(id, (int) change);
//		case ProductType.FASHION:
//			return getInventory(FashionInventory.class).check(id, (int) change);
//		case ProductType.BUFF:
//			return getInventory(BuffInventory.class).check(id, (int) change);

		case ProductType.DROP:
			if (change <= 0) {
				return ProductResult.error("掉落不能扣除"); // 掉落类只能增加, 不能删除.
			}
			return ProductResult.succeed(); // 掉落的话不检测.
		}
		return ProductResult.error(LanguageSet.get(TextTempId.ID_3006, type));
	}

	/** 修改货物接口, 添加失败默认不会发邮件 **/
	private ProductResult changeProduct(int type, int id, long change,
			short source, boolean isNoticeClient) {
		switch (type) {
		case ProductType.CURRENCY:
			return getInventory(CurrencyInventory.class).change(id, change,
					source, isNoticeClient);
//		case ProductType.ITEM:
//			return getInventory(BagInventory.class).change(id, (int) change,
//					source, isNoticeClient);
//		case ProductType.FASHION:
//			return getInventory(FashionInventory.class).change(id,
//					(int) change, source, isNoticeClient);
//		case ProductType.BUFF:
//			return getInventory(BuffInventory.class).change(id, (int) change,
//					source, isNoticeClient);
		case ProductType.DROP:
			if (change <= 0) {
				return ProductResult.error("掉落不能扣除!"); // 掉落类只能增加, 不能删除.
			}
			return addDrop(id, source, isNoticeClient);
		}
		return ProductResult.error(LanguageSet.get(TextTempId.ID_3006, type));
	}

	/**
	 * 修改货物接口<br>
	 * 
	 * @param fullSendMail
	 *            添加失败是否转为邮件<br>
	 **/
	public ProductResult changeProduct(int type, int id, long change,
			short source, boolean isNoticeClient, boolean fullSendMail) {
		// 陈一库图案变化
		ProductResult result = changeProduct(type, id, change, source,
				isNoticeClient);
		if (result.isSucceed()) {
			return result;
		}
		// 检测是否为添加
		if (change <= 0) {
			return result; // 失败就失败咯
		}

		// 检测是否为添加, 不足发邮件
		if (fullSendMail) {
			// // 检测自动发送, 成功就是成功了.
			// List<Product> products = new ArrayList<>();
			// products.add(new Product(type, id, change));
			// ProductResult result0 = autoSendToMail(products, 1, source);
			// if (result0.isSucceed()) {
			// return result; // 还是按照原来的错误返回
			// }
		}
		return result;
	}

	/** 添加货币 **/
	public ProductResult addCurrency(int id, long count, short sourceType,
			boolean isNoticeClient) {
		return addProduct(ProductType.CURRENCY, id, count, sourceType,
				isNoticeClient);
	}

	public ProductResult removeCurrency(int id, long count, short sourceType) {
		return removeProduct(ProductType.CURRENCY, id, count, sourceType);
	}

	public ProductResult changeCurrency(int id, long count, short sourceType,
			boolean isNoticeClient) {
		return changeProduct(ProductType.CURRENCY, id, count, sourceType,
				isNoticeClient, true);
	}

	/** 获取货币数量 **/
	public long getCurrency(int id) {
		return getInventory(CurrencyInventory.class).get(id);
	}

	/**
	 *添加货物 
	 *@param sourceType 是物品来源 用于 日志记录
	 **/
	public ProductResult addProduct(Product product, short sourceType,
			boolean isNoticeClient) {
		return addProduct(product.getType(), product.getId(),
				product.getCount(), sourceType, isNoticeClient);
	}

	/** 添加货物 **/
	public ProductResult addProduct(Product product, short sourceType,
			boolean isNoticeClient, boolean fullSendMail) {
		return addProduct(product.getType(), product.getId(),
				product.getCount(), sourceType, isNoticeClient, fullSendMail);
	}

	/** 添加货物 **/
	public ProductResult addProduct(int type, int id, long count,
			short sourceType, boolean isNoticeClient) {
		return addProduct(type, id, count, sourceType, isNoticeClient, true);
	}

	/** 添加货物 **/
	public ProductResult addProduct(int type, int id, long count,
			short sourceType, boolean isNoticeClient, boolean fullSendMail) {
		if (count < 0) {
			return ItemTempMgr.createProductResult(ProductResult.NOCHANGE,
					type, id, count);
		}
		return changeProduct(type, id, count, sourceType, isNoticeClient,
				fullSendMail);
	}

	/** 扣除货物 **/
	public ProductResult removeProduct(int type, int id, long count,
			short sourceType) {
		if (count < 0) {
			return ItemTempMgr.createProductResult(ProductResult.NOCHANGE,
					type, id, count);
		}
		return changeProduct(type, id, -count, sourceType, false);
	}

	/** 扣除货物 **/
	public ProductResult removeProduct(Product product, short sourceType) {
		return removeProduct(product.getType(), product.getId(),
				product.getCount(), sourceType);
	}

	/** 获取掉落 **/
	public ProductResult addDrop(int dropId, short source,
			boolean isNoticeClient) {
		// // 执行掉落
		// List<Product> products = DropMgr.getNormalPool().drop(dropId);
		// if (products == null) {
		// Log.error("掉落不存在! dropId=" + dropId);
		// return ProductResult.error("掉落不存在!?");
		// }
		// // 获取物品(掉落出来都是增加的)
		// return addProducts(products, source, isNoticeClient);
		return ProductResult.error("尚未实现");
	}

	/** 检测是否能修改 **/
	public ProductResult checkProduct(Product product, int scale) {
		return checkProduct(product.getType(), product.getId(),
				scale * product.getCount());
	}

	/**
	 * 批量检测<br>
	 * 
	 * @param products
	 * @param scale
	 *            数量缩放
	 * @return 是否可以处理
	 */
	public ProductResult checkProducts(Product[] products, int scale) {
		return checkProducts(Arrays.asList(products), scale);
	}

	/**
	 * 批量检测是否能添加产品<br>
	 **/
	public ProductResult checkProducts(List<Product> products) {
		return checkProducts(products, 1);
	}

	/** 批量检测 **/
	public ProductResult checkProducts(List<Product> products, double scale) {
		return checkProducts(products, scale, true);
	}

	/**
	 * 批量检测<br>
	 * 
	 * @param products
	 * @param scale
	 *            数量缩放
	 * @return 是否可以处理
	 **/
	private ProductResult checkProducts(List<Product> products, double scale,
			boolean merge) {
		// 整合内容
		if (merge) {
			products = Product.parser.mergeAndCopy(products);
		}

		// 检测数量
		int psize = (products != null) ? products.size() : 0;
		if (psize <= 0) {
			Log.warn("不能添加空产品! ", true);
			return ProductResult.error("不能添加空产品");
		}

		// 先批量检测
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (product == null) {
				Log.warn("不能添加空产品! " + products, true);
				return ProductResult.error("不能添加空产品");
			}
			// 检测修改数量
			long change0 = (long) (scale * product.getCount());
			// 当数量为0，为无限时间
			// if (change0 == 0) {
			// return ProductResult.create(IChangeResult.NOCHANGE, product +
			// "数量为0", product.getType(), product.getId(), change0);
			// }

			// 判断能否添加
			ProductResult result = checkProduct(product.getType(),
					product.getId(), change0);
			if (!result.isSucceed()) {
				return result;
			}
		}
		return ProductResult.succeed();
	}

	/** 修改货物接口, 0为无改动. **/
	public ProductResult changeProduct(Product product, short source,
			boolean isNoticeClient) {
		return changeProduct(product.getType(), product.getId(),
				product.getCount(), source, isNoticeClient, true);
	}

	/** 批量检测并增加 **/
	public ProductResult addProducts(Product[] products, short source,
			boolean isNoticeClient) {
		return addProducts(Arrays.asList(products), source, isNoticeClient);
	}

	/** 批量扣除 **/
	public ProductResult removeProducts(Product[] products, short source) {
		return removeProducts(Arrays.asList(products), source);
	}

	/** 批量扣除 **/
	public ProductResult removeProducts(Product[] products, double scale,
			short source) {
		return removeProducts(Arrays.asList(products), scale, source);
	}

	/** 批量扣除 **/
	public ProductResult removeProducts(List<Product> products, short source) {
		return removeProducts(products, 1, source);
	}

	/** 批量扣除 **/
	public ProductResult removeProducts(List<Product> products, double scale,
			short source) {
		// 检测数量
		int psize = (products != null) ? products.size() : 0;
		if (psize <= 0) {
			return ProductResult.error("数量为0");
		}
		// 先批量检测
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (product.getCount() < 0) {
				Log.warn("不能删除小于0的产品! " + product, true);
				return ProductResult.error("物品数量失败");
			}
		}
		return changeProducts(products, -1 * scale, source, false);
	}

	/** 批量检测并增加 **/
	public ProductResult addProducts(List<Product> products, short source,
			boolean isNoticeClient) {
		return addProducts(products, 1.0, source, isNoticeClient);
	}

	/** 批量检测并增加 **/
	public ProductResult addProducts(List<Product> products, double scale,
			short source, boolean isNoticeClient) {
		// 检测数量
		int psize = (products != null) ? products.size() : 0;
		if (psize <= 0) {
			return ProductResult.error("数量错误!");
		}
		// 先批量检测
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (product.getCount() < 0) {
				Log.warn("不能添加小于0的产品! " + product, true);
				return ProductResult.error("不能添加小于0的产品!");
			}
		}
		return changeProducts(products, scale, source, isNoticeClient);
	}

	/** 批量检测并修改 **/
	public ProductResult changeProducts(Product[] products, short source,
			boolean isNoticeClient) {
		return changeProducts(Arrays.asList(products), 1, source,
				isNoticeClient);
	}

	/** 批量检测并修改 **/
	public ProductResult changeProducts(List<Product> products, short source,
			boolean isNoticeClient) {
		return changeProducts(products, 1, source, isNoticeClient);
	}

	public ProductResult changeProducts(List<Product> products, double scale,
			short source, boolean isNoticeClient) {
		return changeProducts(products, scale, source, isNoticeClient, true);
	}

	/** 产品修改锁 **/
	public void beginProductChange() {
		getInventory(CurrencyInventory.class).changeCounter.beginChange();
		changeCounter.beginChange();
	}

	/** 产品修改锁 **/
	public void commitProductChange() {
		commitProductChange(true);
	}

	/** 产品修改锁 **/
	public void commitProductChange(boolean c) {
		getInventory(CurrencyInventory.class).changeCounter.commitChange(c);
		changeCounter.commitChange(c);
	}

	/**
	 * 批量检测并修改(内部用, 兼容新增和删除)
	 * 
	 * @param products
	 * @param scale
	 *            缩放(在计算处理时, 数量进行处理)
	 * @param source
	 * @param isNoticeClient
	 *            是否发送消息通知客户端(只是文本消息)
	 * @param fullSendMail
	 *            添加满了自动发邮件
	 * @return 是否成功
	 */
	private ProductResult changeProducts(List<Product> products, double scale,
			short source, boolean isNoticeClient, boolean fullSendMail) {
		// 判断缩放
		if (scale == 0.0) {
			Log.warn("缩放不能为0! ", true);
			return ProductResult.error("缩放不能为0!");
		}
		// 检测数量
		int psize = (products != null) ? products.size() : 0;
		if (psize == 1) {
			Product product = products.get(0);
			long change = (long) (scale * product.getCount());
			return changeProduct(product.getType(), product.getId(), change,
					source, isNoticeClient);
		}

		// 整合内容
		products = Product.parser.mergeAndCopy(products);

		// 先批量检测
		ProductResult checkResult = checkProducts(products, scale, false);
		if (!checkResult.isSucceed()) {
			// 检测是否为添加, 不足发邮件
			// if (fullSendMail) {
			// // 检测自动发送, 成功就是成功了.
			// ProductResult result0 = autoSendToMail(products, scale, source);
			// if (result0.isSucceed()) {
			// return result; // 还是按照原来的错误返回
			// }
			// }
			return checkResult;
		}

		try {
			// 批量上锁, 防止消息发送过多.
			beginProductChange();

			// 批量增加
			psize = (products != null) ? products.size() : 0; // 重新计算数量
			for (int i = 0; i < psize; i++) {
				Product product = products.get(i);
				if (product == null) {
					Log.error("居然允许添加空产品了!? " + products, true);
					continue; // 过滤空, 之前检测过应该是不可能有的
				}

				try {
					// 添加产品
					long change0 = (long) (scale * product.getCount());
					ProductResult changeResult = changeProduct(
							product.getType(), product.getId(), change0,
							source, isNoticeClient);
					if (!changeResult.isSucceed()) {
						Log.warn("添加产品失败!" + product + " " + changeResult); // 这里通过上面的检测,
																			// 不应该会失败的.

						// 复查检测
						if (ConfigMgr.isDebug()) {
							ProductResult cresult = checkProducts(products,
									scale, false);
							changeResult = changeProduct(product.getType(),
									product.getId(), change0, source, false);
							Log.warn("复查添加产品结果!" + cresult + " / "
									+ changeResult);
						}

					}
				} catch (Exception e) {
					Log.error("添加产品错误!" + product, e);
				}
			}
		} finally {
			commitProductChange(false);
		}
		return ProductResult.succeed();
	}

	/** 自动发送到邮件, 必须所有物品都是正添加的 **/
	protected ProductResult autoSendToMail(List<Product> products,
			double scale, short source) {
		// 检测数量
		int psize = (products != null) ? products.size() : 0;
		if (psize <= 0) {
			return ProductResult.error("不能添加空产品");
		}
		// 先批量检测
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (product == null) {
				Log.warn("不能添加空产品! " + products, true);
				return ProductResult.error("不能添加空产品");
			}

			// 判断类型
			int type = product.getType();
			// switch (type)
			// {
			// case ProductType.FASHION:
			// return ProductResult.error("时装不能发送邮件");
			// case ProductType.HORSE:
			// return ProductResult.error("坐骑不能发送邮件");
			// }
			if (type != ProductType.ITEM) {
				// continue; // 跳过除了物品之外的东西发邮件.
				return ProductResult.error("处理物品外其他不自动发邮件");
			}

			// 判断能否添加
			long change0 = (long) (scale * product.getCount());
			if (change0 <= 0) {
				return ProductResult.error("发送邮件不能扣除物品!? " + product + " "
						+ scale);
			}
		}

		// // 都是正向添加的物品, 发送邮件
		// int gameZoneId = this.getGameZoneId();
		// long playerId = this.getPlayerId();
		// Product.mult(products, scale);
		// String title = "系统邮件";
		// String content = "你的背包已满, 转发物品给你.";
		// MailMgr.addOneMail(gameZoneId, playerId, title, content, products,
		// ProductSourceType.API);
		//
		// return ProductResult.succeed(0);

		return ProductResult.error("尚未实现邮件下发功能");
	}

	@Override
	public int getLevel() {
		return info.getLevel();
	}
	@Override
	public long getRankScore(){
		return info.getRankScore();
	}
	public short getSex() {
		return info.getSex();
	}

	@Override
	public long getPlayerId() {
		return info.getPlayerId();
	}

	@Override
	public String getName() {
		return info.getName();
	}

	@Override
	public long getUserId() {
		return info.getUserId();
	}

	/** 获得时装ID **/
	public int getFashionID() {
		return info.getFashionId();
		// 时装
	/*	FashionInventory inventory = this.getInventory(FashionInventory.class);
		return inventory.getInstallFashionTempId();*/
	}

	/** 获得玩家账号 **/
	public String getAccount() {
		return (userInfo != null) ? userInfo.getAccount() : null;
	}

	/** 获得VIP等级 **/
	public int getVipLv() {
		return 0;
//		BuffInventory buffInventory = this.getInventory(BuffInventory.class);
//		Buff buff = buffInventory.getElement(EffectType.VIP);
//		if (buff == null || !buff.isActive()) {
//			return 0;
//		}
//		return buff.getTempInfo().getLevel();
	}

	/** 获得UpdateIP的IP **/
	public String getUpdateIP() {
		return extendInfo.getUpdateIP();
	}

	/** 获得IP地址的地址区域 **/
	public String getAddress() {
		return extendInfo.getAddress();
	}

	/** 判断VIP状态 **/
	public boolean isVip() {
		return (getVipLv() > 0) ? true : false;
	}

	/**
	 * 判断保险箱是否已经开启
	 * @param player
	 * @return
	 */
	public boolean checkSafeBoxOpen(){
		if(getPlayerInfo().getSafeBoxPwd() == null || getPlayerInfo().getSafeBoxPwd().isEmpty()){
			return false;
		}
		return true;
	}
	
	/** 设置代理码 **/
	public void setAgencyNumber(String value) {
		info.setAgencyNumber(value);
		/*
		boolean result = true;
		ResultMsg msg = new ResultMsg();
		msg.setResult(result);
		String text = result ? "绑定邀请码成功" : "您输入的邀请码不存在，请重新输入";
		msg.setValue(text);
		this.sendPacket(ClientProtocol.C_BIND_AGENCYNUMBER, msg);*/
		if (info.isUpdate()) {
			this.sendPlayerMsg();
			info.commit();
		}
	}

	/** 获得代理码 **/
	public long getProxyId() {
		return info.getProxyId();
	}

	/** 设置代理码 **/
	public void setProxyId(long proxyId) {
		info.setProxyId(proxyId);
	}

	public void setProxy(int isAgency){
		info.setIsAgency(isAgency);
	}
	public void setSuperAccount(int isSuperAccount){
		info.setIsSuperAccount(isSuperAccount);
	}
	/** 填充基础信息 **/
	public void createSimplePlayerInfo(SimplePlayerInfo pinfo) {
		// 设置参数
		pinfo.setPlayerId(info.getPlayerId());
		pinfo.setName(info.getName());
		pinfo.setLevel(info.getLevel());
		pinfo.setSex(info.getSex());
		pinfo.setVipLv(getVipLv());
		pinfo.setType(info.getType());
		pinfo.setProxyId(info.getProxyId());
		pinfo.setHeadImgUrl(info.getHeadImgUrl());
		pinfo.setIp(extendInfo.getUpdateIP());
		pinfo.setIsAgency(info.getIsAgency());
		pinfo.setIsSuperAccount(info.getIsSuperAccount());
		// 获取地址信息
		double[] location = StringUtils.splitToDouble(extendInfo.getLocation(),
				",");
		location = ArrayUtils.resetArray(location, 2);
		pinfo.setLocationX(location[0]);
		pinfo.setLocationY(location[1]);
		pinfo.setVisitor(isVisitor());
		// Log.debug("pinfo=" + pinfo + " " + Arrays.toString(location));
		// 时装
//		FashionInventory finventory = getInventory(FashionInventory.class);
//		int fashionTempId = finventory.getInstallFashionTempId();
//		pinfo.setFashionId(fashionTempId);
		pinfo.setFashionId(getFashionID());
		// pinfo.setTitleId(fashionTempId);
	}

	/** 设置在线状态 **/
	@Override
	protected boolean setOnlineState(short onlineState) {
		if (!super.setOnlineState(onlineState)) {
			return false;
		}
		info.setOnlineState(onlineState);
		return true;
	}

	@Override
	public boolean isAlive() {
		return (System.currentTimeMillis() - activeTime) < GameConst.PLAYER_UNLOAD_INTERVAL;
	}

	@Override
	public void updateActiveTime() {
		activeTime = System.currentTimeMillis();
	}

	/** 获取玩家登陆时间 **/
	public Date getLoginTime() {
		return info.getLoginTime();
	}

	/** 获取玩家离线时间 **/
	public long getLogoutTime() {
		Date logoutDate = info.getLogoutTime();
		return (logoutDate != null) ? logoutDate.getTime() : 0L;
	}

	/** 保存自身数据 **/
	protected boolean saveSelf() {
		boolean result = true;
		try {
			// 保存数据
			if (info.isUpdate()) {
				PlayerDAO dao = DaoMgr.getInstance().getDao(PlayerDAO.class);
				dao.insertOrUpdate(info);
				info.commit();
			}
		} catch (Exception e) {
			result = false;
			Log.error("保存玩家消息失败!" + info, e);
		}

		try {
			if (extendInfo.isUpdate()) {
				PlayerExtendDAO edao = DaoMgr.getInstance().getDao(
						PlayerExtendDAO.class);
				edao.insertOrUpdate(extendInfo);
				extendInfo.commit();
			}
		} catch (Exception e) {
			result = false;
			Log.error("保存玩家消息失败!" + extendInfo, e);
		}

		try {
			if (userInfo != null && userInfo.isUpdate()) {
				UserDAO userDAO = DaoMgr.getInstance().getDao(UserDAO.class);
				userDAO.insertOrUpdate(userInfo);
				userInfo.commit();
			}
		} catch (Exception e) {
			result = false;
			Log.error("保存玩家账号消息失败!" + extendInfo, e);
		}
		return result;
	}

	@Override
	public boolean save() {
		// 保存自身数据
		boolean result = true;
		if (!saveSelf()) {
			result = false;
		}

		// 其他数据保存
		if (!super.save()) {
			result = false;
		}
		return result;
	}

	/** 发送玩家信息 **/
	public void sendPlayerMsg() {

//		PlayerMsg msg = new PlayerMsg();
//		msg.setPlayerId(getPlayerId());
//		msg.setUserId(info.getUserId());
//		msg.setName(info.getName());
//		msg.setLevel(info.getLevel());
//		msg.setSex(info.getSex());
//		msg.setTitleId(info.getLevel());
//		msg.setExp(info.getExp());
//		msg.setHeadImgUrl(info.getHeadImgUrl());
//		msg.setAgencyNumber(info.getAgencyNumber());
//		msg.setIsBindPhone(userInfo.getMobilePhone() != null);
//
//		// 设置VIP等级
//		msg.setVip(getVipLv());
//
//		// 判断账号状态
//		msg.setAccountState(AccountState.getAccountState(userInfo));
//
//		// 时装
//		msg.setFashionId(info.getFashionId());
//
////		// 破产补助
////		int lastCount = Math.max(0, GameConfigMgr.brokeAwardCount - extendInfo.getBrokeAwardCount());
////		msg.setBrokeAwardCount(lastCount);
//
//		sendPacket(Protocol.C_PLAYER_INFO, msg);
	}

	/** 发送玩家首充奖励和新手礼包状态 **/
	public void sendPlayerAwardMsg() {
//		PlayerAwardMsg msg = new PlayerAwardMsg();
//		msg.setNewPlayerAwards(this.extendInfo.getNewPlayerAward() <= 0);
//		if (this.extendInfo.getAllPay() > 0
//				&& extendInfo.getFirstPayState() == 0) {
//			extendInfo.setFirstPayState(1);
//		}
//		if (this.extendInfo.getAllPay() <= 0
//				&& extendInfo.getFirstPayState() == 1) {
//			extendInfo.setFirstPayState(1);
//		}
//		msg.setFirstPayState(extendInfo.getFirstPayState());
//		sendPacket(Protocol.C_PLAYERWARDSTATE, msg);
	}

	/** 发送玩家收货地址信息 **/
	public void sendAddressMsg() {
//		ShippingAddressMsg msg = new ShippingAddressMsg();
//		msg.setShippingAddress(extendInfo.getShippingAddress());
//		msg.setShippingName(extendInfo.getShippingName());
//		msg.setShippingPhone(extendInfo.getShippingPhone());
//		sendPacket(Protocol.C_SHIPPINGADDRESS, msg);
	}

	/** 是否是游客 **/
	public boolean isVisitor() {
		if (userInfo != null) {
			return userInfo.getPlatform().equals(LoginConst.PLATFORM_VISITOR);
		}
		return false;
	}

	/** 发送玩家绑定消息 **/
	public void sendBindMsg() {
		// 创建消息发送
		LoginBindMsg msg = new LoginBindMsg();

		int type = (isVisitor()) ? 1 : 0; // 是否是游客
		if (userInfo != null) {
			msg.setPlatform(userInfo.getPlatform());
			msg.setModilePhone(userInfo.getMobilePhone());
		}
		msg.setType(type);
		msg.setPassword(Integer.toString(getInfo().getIsAgency()));
		// 发送消息
		sendPacket(Protocol.C_LOGIN_BIND, msg);
	}

	/** 生成简易玩家的消息 **/
	public SimplePlayerMsg createSimplePlayerMsg() {
		long playerId = info.getPlayerId();
		SimplePlayerMsg msg = new SimplePlayerMsg();
		msg.setPlayerId(playerId);
		msg.setName(info.getName());
		msg.setLevel(info.getLevel());
		msg.setSex(info.getSex());
		msg.setTitleId(info.getLevel());
		msg.setHeadImgUrl(info.getHeadImgUrl());
//		if (extendInfo != null) {
//			msg.setIp(extendInfo.getUpdateIP());
//		}
		// 获得VIP等级
		msg.setVip(getVipLv());

		// 时装
//		FashionInventory inventory = this.getInventory(FashionInventory.class);
//		int fashionTempId = inventory.getInstallFashionTempId();
//		msg.setFashionId(fashionTempId);
//
//		// 战绩
//		ExploitInventory exploitInventory = this
//				.getInventory(ExploitInventory.class);
//		msg.addAllExploits(exploitInventory.createListMsg());
		return msg;
	}

	/** 用户修改产品接口 **/
	public static void changeProductComplete(CorePlayer player, int type,
			int id, long preValue, long setValue, long change, short source,
			boolean isNoticeClient) {
//		// 物品修改记录
//		// OpLogUtils.writeResourceLog(player, type, id, preValue, setValue,
//		// change, source);
//
//		// 收集资源变动，写入日志
//		if (type == ProductType.CURRENCY || type == ProductType.ITEM) {
//			ProductLogInfo info = new ProductLogInfo();
//			info.setPlayerId(player.getPlayerId());
//			info.setProductType(type);
//			info.setProductId(id);
//			info.setNowCount(setValue);
//			info.setChange((int) change);
//			info.setChangeTime(new Date());
//			info.setProductSourceType(source);
//			OpLogMgr.instance.get(ProductLogInfo.class).write(info);
//		}
//		// 处理客户端tips提醒
//		if (change > 0 && isNoticeClient) {
//			player.sendText(MsgType.WINDOWS, ProductTempMgr
//					.createProductChangeString(type, id, change, preValue,
//							setValue));
//		}
	}

	/** 获取Core的区服(不要放在Player) **/
	public GameZone getGameZone() {
		return getGameZone(ModuleName.CORE);
	}

	public PlayerInfo getPlayerInfo() {
		return info;
	}

	public PlayerExtendInfo getExtendInfo() {
		return extendInfo;
	}

	@Override
	protected long getUpdateTime() {
		Date updateDate = info.getUpdateTime();
		return (updateDate != null) ? updateDate.getTime() : System
				.currentTimeMillis() - 5 * 1000;
	}

	@Override
	protected void setUpdateTime(long updateTime) {
		info.setUpdateTime(new Date(updateTime));
		// this.sendText("绑定成功!");
	}

	@Override
	protected void onTimeUpdate(long prevTime, long nowTime, long dt) {
		// 定时更新
		super.onTimeUpdate(prevTime, nowTime, dt);

		// 更新全局事件
		// GlobalEventMgr.update(this, prevTime, nowTime);

		// 检测加载时间检测

	}

	public PlayerInfo getInfo() {
		return info;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	@Override
	public void sendPacket(short code, RpMessage rpMsg) {
		super.sendPacketByOnline(code, rpMsg);
	}

	/******************** 静态 ************************/

	/** 玩家所有inventory类 **/
	protected final static List<Class<?>> inventoryClasses;
	static {
		// 筛选符合条件的类
		String regex = ResourceUtils.getPacketRegex("com.tgt.uu.core");
		inventoryClasses = ResourceUtils.getClassesByClass(
				PlayerInventory.class, regex);
		// 移除忽略
		Iterator<Class<?>> iter = inventoryClasses.iterator();
		while (iter.hasNext()) {
			Class<?> inventoryClass = iter.next();
			if (inventoryClass.getAnnotation(Ignore.class) != null) {
				iter.remove();
			}
		}
	}
	@Override
	public boolean isSuperAccount() {
		if (getInfo() == null)
			return false;
		return getInfo().getIsSuperAccount() == 1;
	}

}
