package com.game.base.service.rpc.handler;

import java.util.Map;

import com.game.base.service.player.PlayerCount;
import com.game.entity.shared.RoomInfo;
import com.game.entity.shared.RoomPlayerInfo;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.proto.rp.room.QuickJoinRoomMsg;

/**
 * 服务接口<br>
 * IRoomService.java
 * @author JiangBangMing
 * 2019年1月8日下午5:37:25
 */
public interface IRoomService {
	/** 快速进入房间 **/
	void quickJoinRoom(RoomPlayerInfo playerInfo, QuickJoinRoomMsg reqMsg, long[] filterIds);

	/** 进入房间 **/
	void enterRoom(long playerId);

	/**
	 * 离开房间<br>
	 * 
	 * @param leaveType
	 *            离开模式 0正常离开, 1换桌离开
	 **/
	void leaveRoom(long playerId, int leaveType, RpcCallback callback);

	/** 关闭所有房间 **/
	void closeAllRoom();

	/** 检测玩家是否存在 **/
	boolean checkPlayer(long playerId);

	/** 更新玩家信息 **/
	void updatePlayerInfo(RoomPlayerInfo playerInfo);
	void updatePlayerInfoNotToClient(final RoomPlayerInfo playerInfo);
	/** 更新机器人 **/
	void onRoomRobetUpdate(long playerId);

//	/** 创建夺宝赛房间 **/
//	void createContestRoom(ContestRoomInfo roomInfo, RpcCallback callback);
	/** 获取房间人数信息*/
	PlayerCount getRoomPlayerCount();
	/** 通过玩家ID获取房间信息 */
	RoomInfo getRoomInfoBy(long playerId);
	
	/** 通过房间ID强制解散房间 */
	int forceDissolveRoom(long roomId);
	
	/**同步开关数据*/
	void updateRobotOffon(Map<Byte, Map<Integer,Byte>> roomRobotOnoff);
	
	/**
	 * 同步 功能开关
	 * @param map
	 */
	void updateGameLobbyOffRoomMap(Map<Integer, Integer> map);
}
