package com.game.core.goods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.base.service.constant.GoodsOrderState;
import com.game.base.service.constant.GoodsType;
import com.game.base.service.constant.OrderType;
import com.game.base.service.constant.ProductSourceType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.oplog.OpLogMgr;
import com.game.base.service.player.PlayerInventory;
import com.game.base.service.struct.GoodsOrderInit;
import com.game.base.service.tempmgr.GameConfigMgr;
import com.game.base.service.tempmgr.GoodsTempMgr;
import com.game.base.service.tempmgr.ProductTempMgr;
import com.game.base.service.uid.UniqueId;
import com.game.core.player.CorePlayer;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.dao.GoodsDAO;
import com.game.entity.dao.GoodsUtilsDAO;
import com.game.entity.entity.GoodsInfo;
import com.game.entity.entity.GoodsLogInfo;
import com.game.entity.entity.GoodsTempInfo;
import com.game.framework.component.ChangeCounter;
import com.game.framework.utils.data.UpdateData;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.goods.GoodsOrderListMsg;
import com.game.proto.rp.goods.GoodsOrderMsg;
import com.game.proto.rp.goods.GoodsTempMsg;

/**
 * 实物逻辑处理
 * 
 */
public class GoodsInventory extends PlayerInventory<CorePlayer> {

	protected Map<Long, GoodsInfo> infos; // 订单列表
	protected Set<GoodsInfo> updates; // 订单更新列表
	protected List<Long> removeIds; // 订单删除Id
	protected final ChangeCounter changeCounter;

	protected GoodsInventory(CorePlayer player) {
		super(player);

		updates = new HashSet<>();
		removeIds = new ArrayList<>();
		// 创建更新器
		changeCounter = new ChangeCounter() {
			protected void onChange() {
				onChangeCounter();
			}
		};

	}

	/** 触发更新 **/
	private void onChangeCounter() {
		sendUpdateListMsg();
	}

	private GoodsInfo get(long id) {
		return infos.get(id);
	}

	/** 添加一个订单,应该先填好实物ID和数量 **/
	public boolean addGoodsOrder(GoodsOrderInit initInfo, boolean subCount) {
		if (initInfo == null) {
			return false;
		}
		// 如果需要扣除库存的话(兑换会在兑换模块先扣了)
		if (subCount) {
			if (!GoodsTempMgr.changeGoodsCount(initInfo.getTemplateId(), -initInfo.getCount())) {
				return false;
			}
		}

		// 获取实物模板
		GoodsTempInfo tempInfo = GoodsTempMgr.getTempInfo(initInfo.getTemplateId());
		GoodsInfo info = new GoodsInfo();

		// 设置实物模板相关
		info.setIcon(tempInfo.getIcon());
		info.setTemplateName(tempInfo.getName());
		info.setTemplateId(initInfo.getTemplateId());

		// 设置不同订单初始化的信息
		info.setOrderType(initInfo.getOrderType());
		info.setOrderSourceId(initInfo.getOrderSourceId());
		info.setOrderDesc(initInfo.getOrderDesc());
		info.setExpend(initInfo.getExpend());
		info.setCount(initInfo.getCount());
		info.setLuckNumber(initInfo.getLuckNumber());

		// 设置订单自动必须填充的信息
		info.setId(UniqueId.GOOD_ORDER.getUniqueId());
		info.setPlayerId(this.getPlayerId());
		info.setPlayerName(this.getPlayer().getName());
		info.setState(GoodsOrderState.INIT);
		info.setStartTime(new Date());
		// 添加到数据库
		GoodsDAO goodsDAO = DaoMgr.getInstance().getDao(GoodsDAO.class);
		goodsDAO.insertOrUpdate(info);

		// 添加实物日志记录
		GoodsLogInfo gLogInfo = new GoodsLogInfo();
		gLogInfo.setId(0);
		gLogInfo.setTemplateId(tempInfo.getTemplateId());
		gLogInfo.setTemplateName(tempInfo.getName());
		gLogInfo.setType(tempInfo.getType());
		gLogInfo.setSubCount(1);
		gLogInfo.setAllCost(tempInfo.getCost() * 1);
		gLogInfo.setAllMarket(tempInfo.getMarket() * 1);

		gLogInfo.setChangeTime(new Date());
		switch (initInfo.getOrderType()) {
		case OrderType.EXCHANGE:
			gLogInfo.setByExchange(1);
			break;
		case OrderType.SNATCH:
			gLogInfo.setBySnatch(1);
			break;
		case OrderType.CONTEST:
			gLogInfo.setByContest(1);
			break;
		}
		OpLogMgr.write(gLogInfo);
		// 添加到管理列表
		infos.put(info.getId(), info);
		updates.add(info);// 添加更新记录
		// 触发消息同步
		changeCounter.change();
		return true;
	}

	/** 客户端确定订单的个人信息 **/
	public void doSetAddr(long id, String addr, String name, String phone) {
		GoodsInfo info = get(id);
		// 要回馈的信息；
		String text = "";
		if (info == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到这个订单");
			return;
		}
		GoodsTempInfo gInfo = GoodsTempMgr.getTempInfo(info.getTemplateId());

		if (gInfo == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到这个实物");
			return;
		}
		if (gInfo.getType() != GoodsType.CADR) {

			if (name.length() > 10 || name.length() < 1) {
				player.sendText(LanguageSet.get(TextTempId.ID_1116));
				return;
			}
			if (addr.length() > 60 || addr.length() < 3) {
				player.sendText(LanguageSet.get(TextTempId.ID_1115));
				return;
			}
			text = "收货地址已成功提交给客服妹纸，请耐心等待审核哦~";
		} else {
			text = "充值手机号码已成功提交给客服妹纸，请耐心等待审核哦~";
		}
		if (phone.length() != 11) {
			player.sendText(LanguageSet.get(TextTempId.ID_1117));
			return;
		}
		// 设置订单消息
		info.setAddress(addr);
		info.setName(name);
		info.setPhone(phone);
		info.setAckTime(new Date());
		info.setState(GoodsOrderState.WAIT);
		updates.add(info); // 记录更新
		GoodsDAO dao = DaoMgr.getInstance().getDao(GoodsDAO.class);
		dao.insertOrUpdate(info);
		player.sendText(text);
		this.changeCounter.change();
	}

	/** GM修改信息 **/
	public void gmSetState(long id, short state, String desc) {

		GoodsInfo info = get(id);
		if (info == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到该订单");
			return;
		}
		info.setState(state);
		info.setChangeTime(new Date());

		// 如果是发货状态， 就设置快递信息
		if (info.getState() == GoodsOrderState.GOING) {

			Date date = new Date();
			String str[] = desc.split(",");
			if (str.length > 1) {
				info.setTrackingNumber(str[0]);
				info.setTrackingName(str[1]);
			}
			info.setDesc("");
			info.setSendTime(date);
		} else {
			// 如果非发货状态，设置备注
			String str = info.getHistoryDesc();
			str = (str == null) ? "" : str;
			str = str + desc + " ;  ";
			info.setHistoryDesc(str);
			info.setDesc(desc);

		}

		GoodsDAO dao = DaoMgr.getInstance().getDao(GoodsDAO.class);
		dao.insertOrUpdate(info);
		updates.add(info); // 记录更新
		this.changeCounter.change();
	}

	/** 客户端确定收货 **/
	public boolean doOverOrder(long id) {
		GoodsInfo info = get(id);
		if (info == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到这个订单");
			return false;
		}
		if (info.getState() != GoodsOrderState.GOING) {
			player.sendLanguageText(TextTempId.ID_7, "订单状态不正确");
		}
		info.setState(GoodsOrderState.RECV);

		info.setDoneTime(new Date());

		GoodsDAO dao = DaoMgr.getInstance().getDao(GoodsDAO.class);
		dao.insertOrUpdate(info);
		updates.add(info); // 记录更新
		this.changeCounter.change();
		return true;
	}

	/** 完成晒单 **/
	public boolean doShareOrder(long id) {
		GoodsInfo info = get(id);
		if (info == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到这个订单");
			return false;
		}
		if (info.getState() != GoodsOrderState.RECV) {
			player.sendLanguageText(TextTempId.ID_7, "订单状态不正确");
			return false;
		}
		info.setState(GoodsOrderState.DONE);
		info.setShowTime(new Date());
		GoodsDAO dao = DaoMgr.getInstance().getDao(GoodsDAO.class);
		dao.insertOrUpdate(info);
		updates.add(info); // 记录更新
		this.changeCounter.change();

		// 发送奖励
		// GameConfigMgr.
		// 下发奖励
		Product[] awards = GameConfigMgr.shareReward;
		ProductResult result = player.addProducts(awards, ProductSourceType.SHAREAWARD, false);
		if (!result.isSucceed()) {
			player.sendText(result.getMsg());
		}

		// 发送弹出消息
		player.sendPacket(Protocol.C_PLAYERWARD, ProductTempMgr.createProductListMsg(Arrays.asList(awards)));

		return true;
	}

	/** 客户端删除订单 **/
	public boolean doSetRemove(long id) {
		GoodsInfo info = get(id);
		if (info == null) {
			player.sendLanguageText(TextTempId.ID_7, "找不到这个订单");
			return false;
		}
		// 移除订单
		info.setState(GoodsOrderState.CLOSE);
		info.commit();
		removeIds.add(id);
		player.sendText("收到货，快点晒一下单吧！");
		this.changeCounter.change();
		return true;

	}

	/** 发送全部订单消息 **/
	public void sendAllMsg() {
		GoodsOrderListMsg msg = new GoodsOrderListMsg();
		for (GoodsInfo info : infos.values()) {
			GoodsOrderMsg orderMsg = createMsg(info);
			msg.addOrderList(orderMsg);
		}
		msg.setAll(true);
		player.sendPacket(Protocol.C_GET_ORDER_LIST, msg);
	}

	/** 发送更新的訂單列表 **/
	public void sendUpdateListMsg() {
		// 检测是否有更新内容
		if (updates.isEmpty() && removeIds.isEmpty()) {
			return; // 没必要更新
		}
		GoodsOrderListMsg lmsg = new GoodsOrderListMsg();
		// 遍历获取更新的订单
		for (GoodsInfo info : updates) {
			lmsg.addOrderList(createMsg(info));
		}
		updates.clear(); // 清除记录
		lmsg.addAllRemoveId(removeIds);
		removeIds.clear();
		player.sendPacket(Protocol.C_GET_ORDER_LIST, lmsg);
	}

	/** 创建一个订单消息 **/
	private GoodsOrderMsg createMsg(GoodsInfo info) {
		GoodsTempInfo tempInfo = GoodsTempMgr.getTempInfo(info.getTemplateId());
		GoodsTempMsg goodMsg = GoodsTempMgr.createMsg(tempInfo); // 生成一个实物消息
		GoodsOrderMsg msg = new GoodsOrderMsg();

		msg.setId(info.getId());
		msg.setOrderSourceId((int) info.getOrderSourceId());
		msg.setGood(goodMsg);
		msg.setExpend(info.getExpend());
		msg.setCount(info.getCount());
		msg.setShippingName(info.getName());
		msg.setShippingAddress(info.getAddress());
		msg.setShippingPhone(info.getPhone());
		msg.setOrderType(info.getOrderType());
		msg.setOrderDesc(info.getOrderDesc());
		msg.setDesc(info.getDesc());

		String time = new String();
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (info.getStartTime() != null) {
			time = timeFormat.format(info.getStartTime());
			msg.setStartTime(time);
		}
		if (info.getAckTime() != null) {
			time = timeFormat.format(info.getAckTime());
			msg.setAckTime(time);
		}
		if (info.getSendTime() != null) {
			time = timeFormat.format(info.getSendTime());
			msg.setSendTime(time);
		}
		if (info.getDoneTime() != null) {
			time = timeFormat.format(info.getDoneTime());
			msg.setDoneTime(time);
		}
		if (info.getShowTime() != null) {
			time = timeFormat.format(info.getShowTime());
			msg.setShowTime(time);
		}
		msg.setTrackingNumber(info.getTrackingNumber());
		msg.setTrackingName(info.getTrackingName());
		msg.setState(info.getState());

		return msg;
	}

	/** 获取玩家订单列表 **/
	public List<GoodsInfo> getListGoods(long playerId) {
		List<GoodsInfo> listInfo = new ArrayList<GoodsInfo>();
		for (GoodsInfo info : infos.values()) {
			if (info.getId() == playerId) {
				listInfo.add(info);
			}
		}
		return listInfo;
	}

	@Override
	protected boolean load() {
		long playerId = player.getPlayerId();
		GoodsUtilsDAO goodsDAO = DaoMgr.getInstance().getDao(GoodsUtilsDAO.class);
		List<GoodsInfo> listInfo = goodsDAO.get(playerId);
		infos = new HashMap<>();
		for (GoodsInfo info : listInfo) {
			infos.put(info.getId(), info);
		}
		return true;
	}

	@Override
	protected void unload() {

	}

	@Override
	protected boolean save() {
		List<GoodsInfo> updates = UpdateData.getUpdataList(infos.values(), true);
		int usize = (updates != null) ? updates.size() : 0;
		if (usize > 0) {
			GoodsDAO goodsDAO = DaoMgr.getInstance().getDao(GoodsDAO.class);
			goodsDAO.insertOrUpdate(updates);
		}
		return true;
	}

}
