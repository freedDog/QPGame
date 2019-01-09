package com.game.control;

import java.util.List;

import com.game.base.service.rpc.handler.IControlService;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.entity.shared.RoomCardGamePlayerInfo;

@Rpc
public class ControlService implements IControlService {
	@Rpc.RpcFunc
	@Override
	public void sendNotice(String name, long playerId, String text, int startTime, int loopCount, int delayTime, int rollCount, int type) {
		// 定时发送
//		CentreNoticeMgr.sendNotice(name, playerId, text, startTime, loopCount, delayTime, rollCount, type);
	}
	
	@Rpc.RpcFunc
	@Override
	public void addRoomCardResult(int gameType,byte location,int maxGameRound,int curGameRound,List<RoomCardGamePlayerInfo> playerInfo){
//		RoomCardGameResult rcg = new RoomCardGameResult();
//		rcg.setGameType(gameType);
//		rcg.setLocation(location);
//		rcg.setTime(System.currentTimeMillis() / 1000);
//		rcg.setPlayerInfo(playerInfo);
//		rcg.setId(KeyGenerateEnum.Default.keyStr());
//		RoomCardResultMgr.getInstance().addRoomCardResult(rcg);
//		for(RoomCardGamePlayerInfo pinfo : playerInfo){
//			RoomCardResultMsgMgr.updateMsg(pinfo.getPlayerId());
//		}
	}
	
	@Rpc.RpcFunc
	@Override
	public void reloadRobet() {
//		RobetMgr.getInstance().reload();
	}

//	@Rpc.RpcFunc
//	@Override
//	public void reloadActivity() {
//		ActivityTempMgr.reloadActivity();
//
//	}
}
