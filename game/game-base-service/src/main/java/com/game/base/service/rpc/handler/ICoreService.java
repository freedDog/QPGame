package com.game.base.service.rpc.handler;

import java.util.List;
import java.util.Map;

import com.game.base.service.player.PlayerCount;
import com.game.base.service.struct.GoodsOrderInit;
import com.game.entity.bean.Product;
import com.game.entity.bean.ProductResult;
import com.game.entity.entity.MailInfo;
import com.game.entity.http.bean.LoginInfo;
import com.game.entity.shared.GameResult;
import com.game.entity.shared.SimplePlayerInfo;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.utils.struct.result.Result;
import com.game.proto.rp.rank.RankListMsg;

/**
 * core服务<br>
 * 
 */
public interface ICoreService {
	/** 玩家登陆 **/
	void loginPlayer(long playerId, LoginInfo loginInfo, RpcCallback callback);

	/** 释放玩家数据, 0为全部玩家. **/
	boolean releasePlayer(long playerId);

	/** 更新全局邮件 **/
	Result updateGlobalMails(List<MailInfo> globalMails);

	/** 添加邮件给玩家列表 **/
	Result addMailByPlayerIds(List<Long> playerIds, String title, String content, List<Product> products, int activeTime, short sourceType);

	/** 添加邮件给玩家 **/
	Result addMailByPlayerId(Long playerId, String title, String content, List<Product> products, int activeTime, short sourceType);

	/** 斗地主游戏结果 **/
	void playGameResult(long playerId, GameResult gameResult);

	/** 斗地主游戏任务统计 **/
	void playGameRecords(long playerId, Integer type, Integer count);

//	/** 夺宝赛参与结果 **/
//	void contestResult(long playerId, int gameType, ContestResult contestResult);

	/** 修改资源 **/
	void changeProducts(long playerId, List<Product> products, double scale, short source, RpcCallback callback);

	/** 检测资源 **/
	ProductResult checkProducts(long playerId, List<Product> products, double score);

	/** 获取货币数量 **/
	long getCurrency(long playerId, int currencyId);

	/** 读取玩家镜像信息(用于斗地主夺宝赛) **/
	void loadRoomPlayerInfo(long playerId, RpcCallback callback);

	/** 添加一个订单,应该先填好实物ID和数量 **/
	void addGoodsOrder(long playerId, GoodsOrderInit info, boolean subCount);

	/** 发送玩家个人消息 **/
	void sendSimplePlayerMsg(long targetId, long playerId);

	/** 机器人更新 **/
	void onCoreRobetUpdate(long playerId);

	void onRobetJoinRoom(long playerId, int count, int banTime);

//	/** 添加抢宝获得实物数量 **/
//	void addSnatchGoods(long playerId, int change);

//	/** 玩家参与抢宝 **/
//	void joinSnatch(long playerId);

	/** 发送更新排行榜消息 **/
	void sendUpdateRankMsg(int rankType, RankListMsg lMsg);

	/** 发送排行榜消息给玩家 **/
	void sendRankListMsg(long playerId, int rankType, RankListMsg lMsg);
	
	/** 发送战绩消息给玩家 **/
//	void sendRoomCardListMsg(long playerId,RoomCardGameResultListMsg msg);
	
	/** 获得是否开透视功能 **/
	boolean isPrespective(long playerId);

//	/** 获得玩家在抢宝的数据 **/
//	SnatchPlayerInfo getSnatchPlayerInfo(long playerId);
	/**
	 * 获取简单玩家数据
	 * @param playerId
	 * @return
	 */
	SimplePlayerInfo getSimplePlayerInfo(long playerId);
	
	/**
	 * 更新玩家龙虎斗胜率
	 * @param playerId
	 * @param lhdRecord
	 */
	boolean updateLhdRecord(long playerId, int lhdRecord);
	
	public void changeRankNew(long playerId,final long score);
	
	/**
	 * 获取服务器人数信息
	 * @return
	 */
	PlayerCount getPlayerCount();
	
	public void updatePlayerScore();
	
	public void updatePlayerFQuan();

	/**
	 * 同步 功能开关
	 * @param map
	 */
	public void updateGameLobbyOffMap(Map<Integer, Integer> map);
	
	/**
	 * 更改玩家的银行卡信息和支付宝信息
	 * @param playerId
	 * @param bankId
	 * @param ownerName
	 * @param cardNum
	 * @param bankBranchName
	 * @param alipayAccount
	 * @param alipayAccountName
	 * @return
	 */
	public Result changePlayerBank(byte opertionType,long playerId, int bankId, String ownerName, String cardNum, String bankBranchName, String alipayAccount,String alipayAccountName, String ownerId);
}