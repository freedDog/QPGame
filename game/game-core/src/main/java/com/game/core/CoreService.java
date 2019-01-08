package com.game.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.player.PlayerCount;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.base.service.server.App;
import com.game.base.service.struct.GoodsOrderInit;
import com.game.core.http.open.LoginRequest;
import com.game.core.player.CorePlayer;
import com.game.core.player.CorePlayerMgr;
import com.game.core.rank.RankInventory;
import com.game.entity.bean.ProductResult;
import com.game.entity.dao.PlayerExtendDAO;
import com.game.entity.entity.PlayerExtendInfo;
import com.game.entity.http.bean.LoginInfo;
import com.game.entity.http.bean.LoginResult;
import com.game.entity.shared.GameResult;
import com.game.entity.shared.SimplePlayerInfo;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.struct.result.Result;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.rank.RankListMsg;
import com.game.entity.entity.MailInfo;
import com.game.entity.bean.Product;

/**
 * core服务
 * CoreService.java
 * @author JiangBangMing
 * 2019年1月8日下午4:05:07
 */
@Rpc
public class CoreService implements ICoreService {

	@Rpc.RpcFunc
	@Override
	public void loginPlayer(long playerId, final LoginInfo loginInfo, final RpcCallback callback) {
		// 获取玩家
		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			// 找不到玩家
			LoginResult result = LoginResult.error(LanguageSet.get(TextTempId.ID_1004));
			callback.callBack(result);
			return;
		}

		// 提交线程处理
		player.enqueue(new Runnable() {
			@Override
			public void run() {
				// 执行登陆
				LoginResult result = LoginRequest.loginPlayer(player, loginInfo);
				callback.callBack(result);
			}
		});

	}

	@Rpc.RpcFunc
	@Override
	public boolean releasePlayer(long playerId) {
		// 指定玩家删除
		if (playerId > 0) {
			return CorePlayerMgr.getInstance().remove(playerId);
		}
		// 释放所有玩家
		CorePlayerMgr.getInstance().removeAll();
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public Result updateGlobalMails(List<MailInfo> globalMails) {
		// CorePlayer player = CorePlayerMgr.getInstance().getFromCache(playerId);
//		return MailMgr.updateGlobalMails(globalMails);
		return null;
	}

	@Rpc.RpcFunc
	@Override
	public Result addMailByPlayerIds(List<Long> playerIds, String title, String content, List<Product> products, int activeTime, short sourceType) {
//		Result result = Result.succeed();
//		// 遍历处理
//		int psize = (playerIds != null) ? playerIds.size() : 0;
//		for (int i = 0; i < psize; i++) {
//			// 判断玩家是否在本服
//			long playerId = playerIds.get(i);
//			CorePlayer player = CorePlayerMgr.getInstance().getFromCache(playerId);
//			if (player == null) {
//				result = Result.error("不存在玩家数据" + playerId);
//				continue;
//			}
//			// 添加邮件
//			if (!MailMgr.addMailByPlayer(player, title, content, products, activeTime, sourceType)) {
//				result = Result.error("发送邮件给玩家失败! " + playerId);
//				continue;
//			}
//		}
//		return result;
		return null;
	}

	@Rpc.RpcFunc
	@Override
	public Result addMailByPlayerId(Long playerId, String title, String content, List<Product> products, int activeTime, short sourceType) {
		// 判断是否是本服
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			return Result.error("不存在玩家数据" + playerId);
//		}
//		// 添加邮件
//		if (!MailMgr.addMailByPlayer(player, title, content, products, activeTime, sourceType)) {
//			return Result.error("发送邮件给玩家失败! " + playerId);
//		}
//		return Result.succeed();
		return null;
	}

//	@Rpc.RpcFunc
//	@Override
//	public void contestResult(long playerId, int gameType, ContestResult result) {
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("获取玩家对象失败!" + playerId);
//			return;
//		}
//		// 处理结果
//		ExploitInventory inventory = player.getInventory(ExploitInventory.class);
//		inventory.onContestResult(gameType, result);
//	}

	@Rpc.RpcFunc
	@Override
	public void playGameResult(long playerId, GameResult result) {
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("获取玩家对象失败!" + playerId);
//			return;
//		}
//		long score = result.getScore();
//		if(score > 0){
//			long rankChangeMoney = score;
//			if(result.getRoomLocationType() > 0/**== RoomConst.LOCATION_TEAHOUSE || result.getRoomLocationType() == RoomConst.LOCATION_ROOM*/){
////				score *= 100;
//				switch(result.getGameType()){
//				case GameType.LANDLORDS:{
////					rankChangeMoney = score * 5;
//					rankChangeMoney = score * 25;
//					break;
//				}
//				case GameType.BULLGOLDFIGHT:{
////					rankChangeMoney = (long)(score * 0.5d);
//					rankChangeMoney = (long)(score * 10);
//					break;
//				}
//				case GameType.ZHAJINHUA:{
////					rankChangeMoney = score * 5;
//					break;
//				}
//				case GameType.MJ_CHENGDU:{
////					rankChangeMoney = score * 5;
//					rankChangeMoney = score * 50;
//
//					break;
//				}
//				case GameType.DAXUAN:{
//					rankChangeMoney = score * 2;
//					break;
//				}
//				}
//			}
//			changeRankNew(playerId, rankChangeMoney);
//		}
//		// 处理房间
//		ExploitInventory inventory = player.getInventory(ExploitInventory.class);
//		inventory.onGameResult(result);
	}

	@Rpc.RpcFunc
	@Override
	public void playGameRecords(long playerId, Integer type, Integer count) {
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("获取玩家对象失败!" + playerId);
//			return;
//		}
//		// 处理房间
//		ExploitInventory inventory = player.getInventory(ExploitInventory.class);
//		inventory.onGameRecord(type, count);
	}

	@Rpc.RpcFunc
	@Override
	public void changeProducts(long playerId, final List<Product> products, final double scale, final short source, final RpcCallback callback) {
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			callback.callBack(ProductResult.error("读取不到玩家数据!" + playerId));
//			return;
//		}
//		// 执行处理
//		player.enqueue(new Runnable() {
//			@Override
//			public void run() {
//				ProductResult result = player.changeProducts(products, scale, source, false);
//				callback.callBack(result);
//			}
//		});
//		return;
	}
	@Override
	public void changeRankNew(long playerId,final long score){
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			return ;
//		}
//		// 执行处理
//		player.enqueue(new Runnable() {
//			@Override
//			public void run() {
//
//				// 记录修改
//				PlayerInfo info = player.getInfo();
//				info.setRankScore(info.getRankScore() + score);
//				// 更新消息
//				player.changeCounter.change();
//				try {
//					// 保存数据
//					if (info.isUpdate()) {
//						PlayerDAO dao = DaoMgr.getInstance().getDao(PlayerDAO.class);
//						dao.insertOrUpdate(info);
//						info.commit();
//					}
//				} catch (Exception e) {
//					Log.error("保存玩家消息失败!" + info, e);
//				}
//			
//			}
//		});
//		return;
	}

	@Rpc.RpcFunc
	@Override
	public ProductResult checkProducts(long playerId, List<Product> products, double score) {
		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			return ProductResult.error(LanguageSet.get(TextTempId.ID_7, "玩家不存在!"));
		}
		// 执行处理
		return player.checkProducts(products, score);
	}

	@Rpc.RpcFunc
	@Override
	public long getCurrency(long playerId, int currencyId) {
		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			Log.error("玩家不存在!" + playerId);
			return 0;
		}
		return player.getCurrency(currencyId);
	}

	@Rpc.RpcFunc
	@Override
	public void loadRoomPlayerInfo(long playerId, final RpcCallback callback) {
		// Log.debug("loadRoomPlayerInfo: " + playerId);
		// 获取玩家
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			callback.callBack(-1, "玩家不存在:" + playerId, null);
//			return;
//		}
//
//		// 读取数据
//		player.enqueue(new Runnable() {
//			@Override
//			public void run() {
//				// 获取房间数据
//				LandlordInventory inventory = player.getInventory(LandlordInventory.class);
//				RoomPlayerInfo playerInfo = inventory.createRoomPlayerInfo();
//				if (playerInfo == null) {
//					callback.callBack(-2, "创建数据失败", null);
//					return;
//				}
//				// 成功返回
//				callback.callBack(1, null, playerInfo);
//			}
//		});

	}

	@Rpc.RpcFunc
	@Override
	public void addGoodsOrder(long playerId, GoodsOrderInit order, boolean subCount) {
		// 判断玩家ID
//		if (playerId <= 0) {
//			Log.error("玩家数据错误! playerId=" + playerId);
//			return;
//		}
//		// 获取玩家Id
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("无法获取玩家对象! " + playerId);
//			return;
//		}
//		// 添加实物订单
//		try {
//			player.getInventory(GoodsInventory.class).addGoodsOrder(order, subCount);
//		} catch (Exception e) {
//			Log.error("添加订单错误!", e);
//			return;
//		}
	}

	@Rpc.RpcFunc
	@Override
	public void sendSimplePlayerMsg(long targetId, long playerId) {
//		// 获取玩家
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Player.sendLanguageTextT(targetId, TextTempId.ID_7, "不存在玩家数据");
//			return;
//		}
//		// 发送给指定玩家
//		SimplePlayerMsg playerMsg = player.createSimplePlayerMsg();
//		Player.sendPacket(targetId, Protocol.C_GET_PLAYERINFO, playerMsg);
	}

	@Rpc.RpcFunc
	@Override
	public void onCoreRobetUpdate(final long playerId) {
		// 获取玩家
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			return;
//		}
//		// 提交任务
//		player.enqueue(new Action() {
//			@Override
//			public void execute() throws Exception {
//				player.getInventory(RobetInventory.class).doRobetUpdate();
//			}
//
//			@Override
//			public int getWarningTime() {
//				return 5000;
//			}
//		});
	}

	@Rpc.RpcFunc
	@Override
	public void onRobetJoinRoom(long playerId, int count, int banTime) {
//		// 获取玩家
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			return;
//		}
//		RobetInventory inventory = player.getInventory(RobetInventory.class);
//		inventory.setPlayerCount(count);
//		inventory.setBanTime(banTime);
	}

//	@Rpc.RpcFunc
//	@Override
//	public void addSnatchGoods(long playerId, int change) {
//		// 获取玩家
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("玩家不存在!" + playerId);
//			return;
//		}
//		player.getInventory(RankInventory.class).addSnatchRecode(change);
//
//	}
//
//	@Rpc.RpcFunc
//	@Override
//	public void joinSnatch(long playerId) {
//		// 获取玩家
//		final CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("玩家不存在!" + playerId);
//			return;
//		}
//
//		// 提交任务
//		player.enqueue(new Runnable() {
//			@Override
//			public void run() {
//				player.eventProxy.dispatchEvent(EventType.GAME_RECORD, new Event(1, GameRecordType.TO_SNATCH));
//			}
//		});
//	}

	@Rpc.RpcFunc
	@Override
	public void sendUpdateRankMsg(int rankType, RankListMsg lMsg) {
		// 获取玩家
		List<CorePlayer> players = CorePlayerMgr.getInstance().getOnlinePlayers();
		int isize = (players != null) ? players.size() : 0;
		for (int i = 0; i < isize; i++) {
			CorePlayer player = players.get(i);
			RankInventory rankInventory = player.getInventory(RankInventory.class);
			rankInventory.sendRankListMsg(rankType, lMsg);
		}
	}

	@Rpc.RpcFunc
	@Override
	public void sendRankListMsg(long playerId, int rankType, RankListMsg lMsg) {
		// 获取玩家
		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			Log.error("玩家不存在!" + playerId);
			return;
		}
		RankInventory rankInventory = player.getInventory(RankInventory.class);
		rankInventory.sendRankListMsg(rankType, lMsg);
	}
	
	
//	@Rpc.RpcFunc
//	@Override
//	public void sendRoomCardListMsg(long playerId, RoomCardGameResultListMsg msg) {
//		if(msg == null)return;
//		// 获取玩家
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("玩家不存在!" + playerId);
//			return;
//		}
//		player.sendPacket(Protocol.C_RANK_RESULT, msg);
//	}

//	@Rpc.RpcFunc
//	@Override
//	public SnatchPlayerInfo getSnatchPlayerInfo(long playerId) {
//		// 获取玩家
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("玩家不存在!" + playerId);
//			return null;
//		}
//		SnatchInventory snatchInventory = player.getInventory(SnatchInventory.class);
//		return snatchInventory.createSnatchPlayerInfo();
//	}

	@Rpc.RpcFunc
	@Override
	public boolean isPrespective(long playerId) {
		// 获取玩家
		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			Log.error("玩家不存在!" + playerId);
			return false;
		}
		return player.getExtendInfo().getIsPerspective();
	}
	@Rpc.RpcFunc
	@Override
	public SimplePlayerInfo getSimplePlayerInfo(long playerId) {
		// 获取玩家
//		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
//		if (player == null) {
//			Log.error("玩家不存在!" + playerId);
//			return null;
//		}
//		GuildInventory guildInventory = player.getInventory(GuildInventory.class);
//		return guildInventory.createSimplePlayerInfo();
		return null;
	}
	
	@Rpc.RpcFunc
	@Override
	public boolean updateLhdRecord(long playerId, int lhdRecord) {
		// 获取玩家
		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			Log.error("玩家不存在!" + playerId);
			return false;
		}
		player.getExtendInfo().setLhdRecord(lhdRecord);
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public PlayerCount getPlayerCount() {
		PlayerCount pc = new PlayerCount(App.getInstance().getAppName());
		pc.setPlayerCount(CorePlayerMgr.getInstance().getPlayersCount());
		return pc;
	}
	@Rpc.RpcFunc
	@Override
	public void updatePlayerScore(){
		Collection<CorePlayer> allPlayer = CorePlayerMgr.getInstance().getPlayers();
		if(allPlayer != null){
			for(final CorePlayer player : allPlayer){
				if(player == null)
					continue;
				player.enqueue(new Runnable() {
					@Override
					public void run() {
						player.getInfo().setRankScore(0);
						player.save();
					}
				});
			}
		}
	}

	@Rpc.RpcFunc
	@Override
	public void updateGameLobbyOffMap(Map<Integer, Integer> map) {
//		GameLobbyOffMgr.getInstance().syncInform(map);
	}

	@Rpc.RpcFunc
	@Override
	public void updatePlayerFQuan() {
		Collection<CorePlayer> allPlayer = CorePlayerMgr.getInstance().getPlayers();
		if(allPlayer != null){
			for(final CorePlayer player : allPlayer){
				if(player == null)
					continue;
				player.enqueue(new Runnable() {
					@Override
					public void run() {
						player.getExtendInfo().setFQuan(0);
						player.save();
					}
				});
			}
		}
	}

	@Rpc.RpcFunc
	@Override
	public Result changePlayerBank(byte opertionType,long playerId, int bankId, String ownerName,
			String cardNum, String bankBranchName, String alipayAccount,String alipayAccountName, String ownerId) {
		Log.info("changePlayerBank");
		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			return Result.error("玩家不存在, playerId:" + playerId);
		}
		if (player.isVisitor()) {
			return Result.error("游客账号不能绑定银行卡");
		}
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		switch (opertionType) {
			case 1:
//					BankInfo bInfo = PayMgr.getInstance().getBankInfo(bankId);
//					
//					if (bInfo == null) {
//						return Result.error("玩家不存在, bInfo=null");
//					}
//					if (StringUtils.isEmpty(cardNum) || cardNum.length() > 20) {
//						return Result.error("卡号错误, cardNum=" + cardNum);
//					}
//					if (StringUtils.isEmpty(ownerName) || ownerName.length() < 2) {
//						return Result.error("姓名错误, ownerName=" + ownerName);
//					}
//					if (!StringUtils.isEmpty(bankBranchName) &&
//							(bankBranchName.length() < 2 || bankBranchName.length() > 32)) {
//						return Result.error("银行卡支行名称错误, bankBranchName=" + bankBranchName);
//					}
//					
//					extendInfo.setCardBankName(bInfo.getName()); // 银行名称
//					extendInfo.setCardNum(cardNum); // 银行卡号
//					extendInfo.setCardName(ownerName); // 持卡人姓名
//					extendInfo.setCardBankBranchName(bankBranchName); // 开户行名称
//					extendInfo.setCardId(ownerId); // 身份证号
				break;
			case 2:
					if (StringUtils.isEmpty(alipayAccountName) || alipayAccountName.length() < 2) {
						return Result.error("支付宝账户名错误, alipayAccount=" + alipayAccount);
					}
					
					if (StringUtils.isEmpty(alipayAccount) || alipayAccount.length() < 6) {
						return Result.error("支付宝账户错误, alipayAccount=" + alipayAccount);
					}
					
					extendInfo.setAlipayAccount(alipayAccount);
					extendInfo.setAlipayAccountName(alipayAccountName);
				break;
			default:
				return Result.error("操作类型错误！" );
		}
		
		
		PlayerExtendDAO edao = DaoMgr.getInstance().getDao(
				PlayerExtendDAO.class);
		edao.insertOrUpdate(extendInfo);
		extendInfo.commit();
		
//		BindBankInfoMsg bankInfo = new BindBankInfoMsg();
//		bankInfo.setBankName(extendInfo.getCardBankName());
//		bankInfo.setCardNum(extendInfo.getCardNum());
//		bankInfo.setOwnerName(extendInfo.getCardName());
//		bankInfo.setBranchBankName(extendInfo.getCardBankBranchName());
//		bankInfo.setAlipayAccount(extendInfo.getAlipayAccount());
//		bankInfo.setAlipayAccountName(extendInfo.getAlipayAccountName());
//		bankInfo.setOwnerId(extendInfo.getCardId());
//		player.sendPacket(Protocol.C_BIND_BANK_INFO, bankInfo);
//		player.getInventory(ExtendInventory.class).doGetBindBank();
		
		Log.info("保存数据库成功");
		return Result.succeed();
	}
}
