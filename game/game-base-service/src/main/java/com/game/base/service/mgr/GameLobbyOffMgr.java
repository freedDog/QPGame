package com.game.base.service.mgr;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.base.service.rpc.handler.IRoomService;
import com.game.base.utils.GameChannelUtils;
import com.game.base.utils.GameChannelUtils.IChannelHandler;
import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;

public class GameLobbyOffMgr {
	private static final String GAME_LOBBY_OFFON_KEY = "Game_Lobby_Offon_Key";
	
	private static Map<Integer,Integer> OFFMAP = new HashMap<Integer, Integer>();
	
	private static GameLobbyOffMgr objThis = new GameLobbyOffMgr();
	
	private GameLobbyOffMgr(){}
	
	public static GameLobbyOffMgr getInstance(){
		return objThis;
	}
	
	/**
	 * 初始化
	 */
	public static boolean init(){
		try{
			OFFMAP.putAll(GameLobbyOffMgr.getInstance().getRedisData());
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取数据
	 * @return
	 */
	private  Map<Integer,Integer> getRedisData(){
		String mapJson = RedisMgr.get(GAME_LOBBY_OFFON_KEY, String.class);
		if(mapJson != null && !mapJson.isEmpty()){
			Map<Integer,Integer> jsonMap = JSON.parseObject(mapJson, new TypeReference<Map<Integer,Integer>>(){});
			if(jsonMap != null){
				return jsonMap;
			}
		}
		return new HashMap<>();
	}
	
	/**
	 * 接受通知
	 * 应为该函数是静态的 同服双模块下会被同步2次
	 * @param notMap
	 */
	public  void syncInform(Map<Integer,Integer> notMap){
		Log.info("同步==>"+JSON.toJSONString(notMap));
		OFFMAP = notMap;
	}
	
	/**
	 * 通知模块数据更新
	 */
	public  void inform(){
		inform(getRedisData());
	}
	
	private  void inform(final Map<Integer,Integer> notMap){
		//同步到core
		GameChannelUtils.executeAllByModule(ModuleName.CORE, ICoreService.class, new IChannelHandler<ICoreService>(){
			@Override
			public Result execute(ICoreService service) {
				service.updateGameLobbyOffMap(notMap);
				return null;
			}
		});
		//同步到 room
		GameChannelUtils.executeAllByModule(ModuleName.ROOM, IRoomService.class, new IChannelHandler<IRoomService>(){
			@Override
			public Result execute(IRoomService service) {
				service.updateGameLobbyOffRoomMap(notMap);
				return null;
			}
		});
	}
	/**
	 * 是不是关闭了
	 * @param gameType
	 * @return
	 */
	public  boolean isOff(Integer gameType){
		Integer state = OFFMAP.get(gameType);
		if(state != null && state > 1){
			return true;
		}
		return false;
	}
	
	public  boolean setParamOff(Integer gameType,Integer state){
		Map<Integer,Integer> notMap = getRedisData();
		notMap.put(gameType, state);
		RedisMgr.set(GAME_LOBBY_OFFON_KEY, JSON.toJSONString(notMap));
		inform(notMap);
		return true;
	}
	
	/**
	 * 数据json
	 * @return
	 */
	public String toDataJSON(){
		return JSON.toJSONString(OFFMAP);
	}
	
	/**
	 * 是不是关闭
	 * @param gameLobby 游戏类型
	 * @return
	 */
	public boolean isGameLobbyOff(Integer gameLobby){
		Integer state = GameLobbyOffMgr.OFFMAP.get(gameLobby);
		if(state != null && state >= 1){
			return true;
		}
		return false;
	}
}
