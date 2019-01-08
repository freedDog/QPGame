package com.game.room.player;

import java.util.List;

import com.game.base.service.constant.OnlineState;
import com.game.base.service.constant.ProductType;
import com.game.base.service.player.Player;
import com.game.base.service.player.RemotePlayer;
import com.game.base.service.tempmgr.ItemTempMgr;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.shared.RoomPlayerInfo;
import com.game.framework.component.change.IChangeResult;
import com.game.framework.component.log.Log;
import com.game.proto.msg.RpMessage;
import com.game.proto.rp.player.SimplePlayerMsg;
import com.game.room.base.GameRoom;
import com.game.room.base.GameRoomPlayer;

/**
 * 房间模块玩家对象<br>
 * GamePlayer.java
 * @author JiangBangMing
 * 2019年1月8日下午4:55:26
 */
public class GamePlayer extends RemotePlayer {

	protected RoomPlayerInfo info;
	protected GameRoom<?> room; // 所在房间
	protected GameRoomPlayer<?> roomPlayer; // 在房间内的玩家
	protected int count;// 玩的次数
	protected int idleTime;// 解封时间
	protected boolean idle;// 是否禁玩

	public int getFashionId() {
		return info.getFashionId();
	}

	public GamePlayer(RoomPlayerInfo info) {
		this.info = info;

		// 如果是机器人默认登陆, 如果是玩家就验证登陆状态
		long playerId = info.getPlayerId();
		if (this.isRobet() || Player.isOnline(playerId)) {
			setOnline(true);
		}
	}

	/** 更新玩家信息 **/
	public void updateInfo(RoomPlayerInfo info) {
		// 触发更新
		long playerId = this.info.getPlayerId();
		if (playerId != info.getPlayerId()) {
			Log.error("更新的玩家Id跟当前不对应!" + info);
			return;
		}
		// 更新info
		this.info = info;
		if (room != null) {
			room.onPlayerUpdate(this);
		}
	}
	public void updateInfoNotToClient(RoomPlayerInfo info) {
		// 触发更新
		long playerId = this.info.getPlayerId();
		if (playerId != info.getPlayerId()) {
			Log.error("更新的玩家Id跟当前不对应!" + info);
			return;
		}
		// 更新info
		this.info = info;
	}
	@Override
	public ProductResult changeProducts(List<Product> products, double scale, short source) {
		int psize = (products != null) ? products.size() : 0;
		if (psize <= 0) {
			return ItemTempMgr.createProductResult(IChangeResult.NOCHANGE, 0, 0, 0);
		}
		// 执行修改
		ProductResult result = super.changeProducts(getPlayerId(), products, scale, source);
		if (!result.isSucceed()) {
			return result; // 失败了
		}
		// 成功了, 同步一下货币
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			if (product.getType() == ProductType.CURRENCY) {
				int currencyId = product.getId();
				long count = (long) (product.getCount() * scale);
				// 存在这个自
				info.setCurrency(currencyId, info.getCurrency(currencyId) + (int) count);
			}
		}

		return result;
	}

	/** 获取所属代理Id **/
	public long getProxyId() {
		return info.getProxyId();
	}

	@Override
	public long getUserId() {
		return info.getUserId();
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
	public int getLevel() {
		return info.getLevel();
	}

	public String getHeadImgUrl() {
		return info.getHeadImgUrl();
	}

	public int getVipLv() {
		return info.getVipLv();
	}

	public int getPlayerCount() {
		return info.getPlayerCount();
	}

	public void SetPlayerCount(int count) {
		info.setPlayerCount(count);
	}

	public int getBanTime() {
		return info.getBanTime();
	}

	public void SetBanTime(int banTime) {
		info.setBanTime(banTime);
	}

	@Override
	public synchronized boolean load() {
		return super.load();
	}

	@Override
	public synchronized void unload() {
		super.unload();
	}

	@Override
	public synchronized boolean save() {
		if (!super.save()) {
			return false;
		}
		return true;
	}

	/** 提交任务到所在房间,返回false代表没在房间 **/
	public boolean enqueueByRoom(final RoomAction action) {
		final GameRoom<?> room = this.room;
		if (room == null) {
			return false;
		}
		// 绑定参数
		action.setPlayer(this);
		action.setRoom(room);

		// 提交任务到所在房间
		room.enqueue(action);
		return true;
	}

	@Override
	public void enqueue(Runnable runnable) {
		// 如果在房间内, 按照房间执行更新
		if (room != null) {
			room.enqueue(runnable);
			return;
		}
		super.enqueue(runnable);
	}

	/** 获取房间 **/
	@SuppressWarnings("unchecked")
	public <T extends GameRoom<?>> T getRoom() {
		return (T) room;
	}

	/** 获取房间 **/
	@SuppressWarnings("unchecked")
	public <T extends GameRoom<?>> T getRoom(Class<T> clazz) {
		if (!clazz.isInstance(room)) {
			return null;
		}
		return (T) room;
	}

	public void setRoom(GameRoom<?> room, GameRoomPlayer<?> roomPlayer) {
		this.room = room;
		this.roomPlayer = roomPlayer;
	}

	@Override
	public long getCurrency(int currencyId) {
		return info.getCurrency(currencyId);
	}

	@Override
	public int getType() {
		return info.getType();
	}

	/** 发送消息给客户端, 过滤机器人和不在线. */
	@Override
	public void sendPacket(short code, RpMessage rpMsg) {
		// 过滤机器人
		if (this.isRobet()) {
			return;
		}
		// 执行发送
		super.sendPacketByOnline(code, rpMsg);
	}

	/** 修改在线状态 **/
	public void setOnline(boolean enable) {
		if (enable) {
			// 在线处理
			login();
			return;
		}
		// 离线处理
		logout();
	}

	/** 在Room模块,登陆是指是否在房间内.(重连) **/
	@Override
	protected void login() {
		// 修改登录状态
		if (!setOnlineState(OnlineState.ONLINE)) {
			return;
		}

		// 玩家第一次登陆肯定不在房间, 后面登陆在房间肯定是重连.
		enqueueByRoom(new RoomAction() {
			@Override
			public void execute(GameRoom<?> room, GameRoomPlayer<?> roomPlayer) throws Exception {
				// 执行登陆
				room.onPlayerRelogin(GamePlayer.this);
			}
		});
	}

	/** 在Room模块, 离线是指强退游戏(离开游戏, 也自然会触发) **/
	@Override
	protected void logout() {
		// 切换状态
		if (!setOnlineState(OnlineState.OFFLINE)) {
			return;
		}

		// 提交离线任务
		enqueueByRoom(new RoomAction() {
			@Override
			public void execute(GameRoom<?> room, GameRoomPlayer<?> roomPlayer) throws Exception {
				// 执行离线
				room.onPlayerLogout(GamePlayer.this);
			}
		});
	}

	public RoomPlayerInfo getInfo() {
		return info;
	}

	public GameRoomPlayer<?> getRoomPlayer() {
		return roomPlayer;
	}

	@SuppressWarnings("unchecked")
	public <T extends GameRoomPlayer<?>> T getRoomPlayer(Class<T> clazz) {
		if (roomPlayer == null) {
			return null;
		}
		return (clazz.isInstance(roomPlayer)) ? (T) roomPlayer : null;
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
		msg.setFashionId(info.getFashionId());
		msg.setVip(info.getVipLv());
		msg.setHeadImgUrl(info.getHeadImgUrl());
		return msg;
	}

	/** 是否是内置机器人 **/
	public boolean isLocalRobet() {
		return false;
	}

	/** ai等级 **/
	public int getAiLv() {
		return info.getAiLv();
	}

	/** 玩家房间任务 **/
	public static abstract class RoomAction implements Runnable {
		private GamePlayer player;
		private GameRoom<?> room;

		@Override
		public final void run() {
			// 再次获取玩家是否有房间
			final GameRoom<?> nowRoom = player.getRoom();
			if (room != nowRoom) {
				return; // 已经离开房间了
			}
			// 执行处理
			try {
				execute(nowRoom, player.getRoomPlayer());
			} catch (Exception e) {
				Log.error("玩家房间任务执行错误", e);
			}
		}

		/** 执行任务 **/
		public abstract void execute(GameRoom<?> room, GameRoomPlayer<?> roomPlayer) throws Exception;

		public GamePlayer getPlayer() {
			return player;
		}

		protected void setPlayer(GamePlayer player) {
			this.player = player;
		}

		public GameRoom<?> getRoom() {
			return room;
		}

		protected void setRoom(GameRoom<?> room) {
			this.room = room;
		}

	}

	@Override
	public long getRankScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSuperAccount() {
		if (info == null)
			return false;
		return info.getIsSuperAccount() == 1;
	}
}

