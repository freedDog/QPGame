package com.game.base.service.rpc.handler;

import java.util.List;

import com.game.entity.shared.RoomCardGamePlayerInfo;

/**
 * core服务<br>
 * 
 */
public interface IControlService {

	/**
	 * 定时发送公告消息
	 * 
	 * @param name
	 *            发送者名称
	 * @param text
	 *            消息内容
	 * @param startTime
	 *            开始时间(s秒)
	 * @param loopCount
	 *            循环次数
	 * @param delayTime
	 *            时间间隔(s秒)
	 * @param type
	 *            消息类型
	 * @param rollCount
	 *            滚动次数
	 */
	void sendNotice(String name, long playerId, String text, int startTime, int loopCount, int delayTime, int rollCount, int type);

	/**
	 * 添加房卡战绩
	 * @param gameType 游戏类型
	 * @param location 位置
	 * @param playerInfo 玩家信息
	 * @param maxGameRound 最大局数
	 * @param curGameRound 当前局数
	 */
	public void addRoomCardResult(int gameType,byte location,int maxGameRound,int curGameRound,List<RoomCardGamePlayerInfo> playerInfo);
	/** 重载机器人 **/
	void reloadRobet();

//	/** 重载活动 **/
//	void reloadActivity();
}
