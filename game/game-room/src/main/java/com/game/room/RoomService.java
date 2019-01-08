package com.game.room;

import java.util.Map;

import com.game.base.service.constant.CurrencyId;
import com.game.base.service.constant.GameType;
import com.game.base.service.constant.MsgType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mgr.GameLobbyOffMgr;
import com.game.base.service.mgr.RobotOffMgr;
import com.game.base.service.player.Player;
import com.game.base.service.player.PlayerCount;
import com.game.base.service.rpc.handler.IRoomService;
import com.game.base.service.server.App;
import com.game.base.service.tempmgr.GlobalGameConfigMgr;
import com.game.base.service.tempmgr.LobbyTempMgr;
import com.game.entity.configuration.LobbyTempInfo;
import com.game.entity.dao.ForcedissolveroomDAO;
import com.game.entity.entity.ForcedissolveroomInfo;
import com.game.entity.shared.PlayerInfo;
import com.game.entity.shared.RoomInfo;
import com.game.entity.shared.RoomPlayerInfo;
import com.game.framework.component.action.Action;
import com.game.framework.framework.rpc.ProxyService.Rpc;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.proto.rp.room.QuickJoinRoomMsg;
import com.game.room.base.GameRoom;
import com.game.room.base.GameRoomMgr;
import com.game.room.base.GameRoomPlayer;
import com.game.room.base.IGameRoom;
import com.game.room.constant.RoomConst;
import com.game.room.constant.RoomLeaveType;
import com.game.room.player.GamePlayer;
import com.game.room.player.GamePlayerMgr;
import com.game.room.robot.RoomRobotMgr;

/**
 *  房间模块接口
 * RoomService.java
 * @author JiangBangMing
 * 2019年1月8日下午5:49:00
 */
@Rpc
public class RoomService implements IRoomService {

	@Rpc.RpcFunc
	@Override
	public void updatePlayerInfo(final RoomPlayerInfo playerInfo) {
		long playerId = playerInfo.getPlayerId();
		// 获取玩家对象
		final GamePlayer player = GamePlayerMgr.getInstance().getFromCache(
				playerId);
		if (player == null) {
			return;
		}
		// 更新玩家信息
		player.enqueue(new Action() {
			@Override
			public void execute() throws Exception {
				player.updateInfo(playerInfo);
			}
		});
	}

	@Rpc.RpcFunc
	@Override
	public void updatePlayerInfoNotToClient(final RoomPlayerInfo playerInfo) {
		long playerId = playerInfo.getPlayerId();
		// 获取玩家对象
		final GamePlayer player = GamePlayerMgr.getInstance().getFromCache(
				playerId);
		if (player == null) {
			return;
		}
		// 更新玩家信息
		player.enqueue(new Action() {
			@Override
			public void execute() throws Exception {
				player.updateInfoNotToClient(playerInfo);
			}
		});
	}
	@Rpc.RpcFunc
	@Override
	public void quickJoinRoom(final RoomPlayerInfo playerInfo,
			final QuickJoinRoomMsg reqMsg, final long[] filterIds) {
		int lobbyId = reqMsg.getId();
		LobbyTempInfo tempInfo = LobbyTempMgr.getTempInfo(lobbyId);
		if (tempInfo == null) {
			return;
		}
		if(GlobalGameConfigMgr.isGameMaintenance() && playerInfo.getIsSuperAccount() != 1){
			Player.sendText(playerInfo.getPlayerId(), MsgType.WINDOWS, LanguageSet.get(TextTempId.ID_7, "当前服务器正在维护，暂时不能游戏！"));
			return ;
		}
		
		if(GameLobbyOffMgr.getInstance().isGameLobbyOff(tempInfo.getGameType())){
			Player.sendText(playerInfo.getPlayerId(), MsgType.WINDOWS, LanguageSet.get(TextTempId.ID_7, "本游戏维护中，暂时不能进入"));
			return ;
		}
		
		IGameRoom game = null;
		if (tempInfo.getGameType() == GameType.LANDLORDS) {
			game = RoomModule.getLandlord();
		} else if (tempInfo.getGameType() == GameType.BULLGOLDFIGHT) {
			game = RoomModule.getBullGoldFight();
		}
//		else if (tempInfo.getGameType() == GameType.SLOTS) {
//			game = RoomModule.getSlots();
//		} else if (tempInfo.getGameType() == GameType.MAHJOIN) {
//			game = RoomModule.getMahjong();
//		} else if (tempInfo.getGameType() == GameType.GBMAHJONG) {
//			game = RoomModule.getMahjong();
//		} else if (tempInfo.getGameType() == GameType.BJL) {
//			game = RoomModule.getBjl();
//		} else if (tempInfo.getGameType() == GameType.TEXASHOLDEM) {
//			game = RoomModule.getTexasHoldem();
//		}
		else if (tempInfo.getGameType() == GameType.ZHAJINHUA) {
			if (!RoomConst.GAME_ZJH_IS_AVALIABLE) {
				Player.sendText(playerInfo.getPlayerId(), MsgType.WINDOWS, LanguageSet.get(TextTempId.ID_7, "金花正在升级中, 敬请期待！"));
				return ;
			}
			game = RoomModule.getZhaJinHua();
		} else if (tempInfo.getGameType() == GameType.MJ_CHENGDU) {
			game = RoomModule.getMahjong();
		} else if (tempInfo.getGameType() == GameType.DAXUAN) {
			game = RoomModule.getDaXuan();
		}

		if (game == null) {
			return;
		}
		game.quickJoinRoom(playerInfo, reqMsg, filterIds);

	}

	@Rpc.RpcFunc
	@Override
	public void enterRoom(long playerId) {

	}

	@Rpc.RpcFunc
	@Override
	public void leaveRoom(long playerId, final int leaveType,
			final RpcCallback callback) {
		// 检测玩家所在房间
		final GamePlayerMgr gamePlayerMgr = GamePlayerMgr.getInstance();
		final GamePlayer player = gamePlayerMgr.get(playerId);
		if (player == null) {
			callback.callBack(-1, "找不到玩家");
			return; // 找不到玩家
		}

		// 获取玩家房间
		final GameRoom<?> room = player.getRoom();
		if (room == null) {
			// 直接移除玩家数据
			gamePlayerMgr.tryRemove(player);
			callback.callBack(1, null); // 移除成功
			return;
		}

		// 执行移除
		room.enqueue(new Action() {
			@Override
			public void execute() throws Exception {
				// 判断离开类型
				int leaveType0 = RoomLeaveType.NORMAL;
				if (leaveType == 1) {
					leaveType0 = RoomLeaveType.CHANGE;
				}

				// 执行移除
				if (!room.playerLeave(player, leaveType0, false)) {
					callback.callBack(-3, "离开失败");
					return; // 离开失败
				}
				// 直接移除玩家数据
				gamePlayerMgr.tryRemove(player);
				callback.callBack(1, null); // 移除成功
			}
		});

	}

	@Rpc.RpcFunc
	@Override
	public void closeAllRoom() {
		GameRoomMgr.getInstance().removeAll();
		// GamePlayerMgr.getInstance().removeAll();
	}

	@Rpc.RpcFunc
	@Override
	public boolean checkPlayer(long playerId) {
		// 检测玩家所在房间
		final GamePlayerMgr gamePlayerMgr = GamePlayerMgr.getInstance();
		final GamePlayer player = gamePlayerMgr.get(playerId);
		if (player == null) {
			return false; // 找不到玩家
		}
		return true;
	}

	@Rpc.RpcFunc
	@Override
	public void onRoomRobetUpdate(final long playerId) {
		RoomRobotMgr.enqueue(new Action() {
			@Override
			public void execute() throws Exception {
				RoomRobotMgr.updateByPlayerId(playerId);
			}

			@Override
			public int getWarningTime() {
				return 5000;
			}
		});
	}

	@Rpc.RpcFunc
	@Override
	public PlayerCount getRoomPlayerCount() {
		PlayerCount pc = new PlayerCount(App.getInstance().getAppName());
		int[] count = GamePlayerMgr.getRoomPlayerCount();
		pc.setRoomPlayerCount(count[0]);
		pc.setRoomPlayerWithoutRobet(count[1]);
		int[] roomSize = GameRoomMgr.getInstance().roomSize();
		pc.setCardRoomSize(roomSize[0]);
		pc.setFreeRoomSize(roomSize[1]);
		return pc;
	}

	@Rpc.RpcFunc
	@Override
	@SuppressWarnings("rawtypes")
	public RoomInfo getRoomInfoBy(long playerId) {
		GamePlayerMgr gamePlayerMgr = GamePlayerMgr.getInstance();
		GamePlayer player = gamePlayerMgr.get(playerId);
		if (player == null) {
			return null;
		}
		GameRoom<GameRoomPlayer<?>> room = player.getRoom();
		if (room == null)
			return null;
		RoomInfo roomInfo = new RoomInfo(room.getId(), room.getCreateTime(), room.getState(), 
				room.getGameState(), room.getRoomLocationType(), room.getInviteCode(), room.getGameType());
		for(GameRoomPlayer p : room.getPlayers()) {
			if (p == null)
				continue;
			PlayerInfo pinfo = new PlayerInfo();
			pinfo.setPlayerId(p.getPlayerId());
			pinfo.setName(p.getName());
			pinfo.setRobot(p.isRobot());
			pinfo.setPoint(p.getPlayer().getCurrency(CurrencyId.POINT));
			roomInfo.addPlayerInfo(pinfo);
		}
		System.out.println("roomInfo:" + roomInfo);
		return roomInfo;
		
	}

	@Rpc.RpcFunc
	@Override
	public int forceDissolveRoom(long roomId) {
		GameRoom<?> room = GameRoomMgr.getInstance().getRoom(roomId);
		if (room == null)
			return -1;
		ForcedissolveroomInfo info = room.forceDissolveRoom();
		ForcedissolveroomDAO dao = DaoMgr.getInstance().getDao(ForcedissolveroomDAO.class);
		dao.insert(info);
		return 0;
	}

	@Rpc.RpcFunc
	@Override
	public void updateRobotOffon(
			Map<Byte, Map<Integer, Byte>> roomRobotOnoff) {
		RobotOffMgr.dataSync01(roomRobotOnoff);
	}

	@Rpc.RpcFunc
	@Override
	public void updateGameLobbyOffRoomMap(Map<Integer, Integer> map) {
		GameLobbyOffMgr.getInstance().syncInform(map);
	}
}