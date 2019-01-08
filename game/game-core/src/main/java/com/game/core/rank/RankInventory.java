package com.game.core.rank;

import java.util.List;

import com.game.base.service.constant.CurrencyId;
import com.game.base.service.constant.RankType;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.player.PlayerInventory;
import com.game.core.player.CorePlayer;
import com.game.entity.dao.RankDataDAO;
import com.game.entity.entity.RankDataInfo;
import com.game.framework.utils.TimeUtils;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.rank.RankListMsg;
import com.game.proto.rp.rank.RankMsg;

/**
 *  货币处理
 * RankInventory.java
 * @author JiangBangMing
 * 2019年1月8日下午2:55:14
 */
public class RankInventory extends PlayerInventory<CorePlayer> {
	protected RankDataInfo info;

	protected RankInventory(CorePlayer player) {
		super(player);

	}

	public void sendRankListMsg(int rankType, RankListMsg lMsg) {
		long playerId = player.getPlayerId();
		RankMsg selfMsg = new RankMsg();
		selfMsg.setPlayerId(playerId);
		selfMsg.setPlayer(player.createSimplePlayerMsg());
		selfMsg.setRankIndex(getRankIndex(lMsg.getRanks()));
		switch (rankType) {
//			case RankType.GOLD: {
//				selfMsg.addRankVals(info.getGold());
//				break;
//			}
//			case RankType.DIAMOND: {
//				selfMsg.addRankVals(player.getCurrency(CurrencyId.POINT));
//				break;
//			}
			case RankType.SCORE2:
			case RankType.SCORE: {
				selfMsg.addRankVals((int)player.getRankScore());
				break;
			}
			case RankType.FQUAN:{
				selfMsg.addRankVals((int)player.getCurrency(CurrencyId.FQUAN));
				break;
			}
		}
		lMsg.setSelf(selfMsg);

		player.sendPacket(Protocol.C_RANK_LIST, lMsg);
	}

	@Override
	protected boolean load() {
		long playerId = this.getPlayerId();
		RankDataDAO dao = DaoMgr.getInstance().getDao(RankDataDAO.class);
		info = dao.get(playerId);
		if (info == null) {
			info = new RankDataInfo();
			info.setPlayerId(playerId);
			info.setUpdateTime(TimeUtils.getCurrentTime());
		}
		// 判断updateTime是否是本周时间
		long nowTime = System.currentTimeMillis();
		int limitTime = TimeUtils.time(TimeUtils.getWeekTime(nowTime - TimeUtils.oneWeekTimeL, 0, 0, 0, 0));
		if (info.getUpdateTime() < limitTime) {
			// 不在本周，清空数据
//			info.setGold(0);
			info.setDiamond(0);
//			info.setGameFirst(0);
//			info.setGameSecond(0);
//			info.setGameThird(0);
//			info.setUGold(0);
//			info.setSnatch(0);
			
			info.setFquan(0);
			info.setRoomCard(0);
			
			info.setUpdateTime(TimeUtils.getCurrentTime());
		}
		return true;
	}

	@Override
	protected void unload() {
	}

	@Override
	protected void onDayReset() {
		// 每天保存
		save();
	}

	@Override
	protected void onTimeUpdate(long prevTime0, long nowTime0, long dt) {
		long nowTime = System.currentTimeMillis();
		// long prevTime = info.getUpdateTime() * 1000;
		// // 获取2个时间的周日时间点.
		// long prevWeekTime = TimeUtils.getWeekTime(prevTime, 0, 0, 10, 0);
		// 本周重置的是时间节点
		long nowWeekTime = TimeUtils.getWeekTime(nowTime, 7, 23, 50, 0);
		// 现在时间不超过重置时间节点
		if (nowTime <= nowWeekTime) {
			// 不重置
			return; // 同一周
		}

		// 周日重置数据
//		info.setGold(0);
		info.setDiamond(0);
		info.setFquan(0);
		info.setRoomCard(0);
//		info.setGameFirst(0);
//		info.setGameSecond(0);
//		info.setGameThird(0);
//		info.setUGold(0);
//		info.setSnatch(0);
		info.setUpdateTime(TimeUtils.getCurrentTime());
	}

	@Override
	protected boolean save() {
		if (info != null && info.isUpdate()) {
			// 保存数据到数据库
			RankDataDAO dao = DaoMgr.getInstance().getDao(RankDataDAO.class);
			dao.insertOrUpdate(info);
			info.commit();
		}
		return true;
	}

	/** 获得排行榜的名次 **/
	private int getRankIndex(List<RankMsg> infos) {
		int isize = (infos != null) ? infos.size() : 0;
		for (int i = 0; i < isize; i++) {
			RankMsg info = infos.get(i);
			if (info.getPlayerId() == getPlayerId()) {
				return info.getRankIndex();
			}
		}
		// 没有上榜
		return RankType.NO_RANK;
	}

//	/** 添加金币记录 **/
//	public void addGoldRecode(int change) {
//		info.setGold(info.getGold() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//	}
//
//	/** 添加U币记录 **/
//	public void addUGoldRecode(int change) {
//		info.setUGold(info.getUGold() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//	}
//
//	/** 添加夺宝第一名记录 **/
//	public void addGameFirstRecode(int change) {
//		info.setGameFirst(info.getGameFirst() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//	}
//
//	/** 添加夺宝第二名记录 **/
//	public void addGameSecondRecode(int change) {
//		info.setGameSecond(info.getGameSecond() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//	}
//
//	/** 添加夺宝第三名记录 **/
//	public void addGameThirdRecode(int change) {
//		info.setGameThird(info.getGameThird() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//	}

	/** 添加钻石记录 **/
	public void addDiamondRecode(int change) {
		info.setDiamond(info.getDiamond() + change);
		info.setUpdateTime(TimeUtils.getCurrentTime());
	}
	
	/** 添加房卡记录 **/
	public void addRoomCardRecode(int change) {
		info.setRoomCard(info.getRoomCard() + change);
		info.setUpdateTime(TimeUtils.getCurrentTime());
	}
	
	/** 添加副券记录 **/
	public void addFuQuanRecode(int change) {
		info.setFquan(info.getFquan() + change);
		info.setUpdateTime(TimeUtils.getCurrentTime());
	}

//	/** 添加抢宝实物记录 **/
//	public void addSnatchRecode(int change) {
//		info.setSnatch(info.getSnatch() + change);
//		info.setUpdateTime(TimeUtils.getCurrentTime());
//
//	}
	
}
