package com.game.base.service.mgr;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.IRoomService;
import com.game.base.utils.GameChannelUtils;
import com.game.base.utils.GameChannelUtils.IChannelHandler;
import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;

/**
 * 机器人开关管理
 * RobotOffMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午6:14:20
 */
public class RobotOffMgr {
	protected static String rebootOffOnKey = "rebootOffOnKey_01";
	protected static String rebootOffOnKey2 = "rebootOffOnKey_02";
	/**<房间类型,<房间模板ID,开关>>*/
	private static Map<Byte, Map<Integer,Byte>> roomRobotOnoff = new HashMap<Byte, Map<Integer,Byte>>();
	/**<房间类型,开关>*/
	private static Map<Byte,Byte> roomRobotOnoffup = new HashMap<Byte, Byte>(); //针对那些没有模板的 场次 开关
	
	/**
	 * 数据刷新
	 */
	public static void updateData(){
		Log.info("=======更新机器人开关=======");
		final Map<Byte, Map<Integer,Byte>> roomRebootOnoffup = getRedisData();
		GameChannelUtils.executeAllByModule(ModuleName.ROOM, IRoomService.class, new IChannelHandler<IRoomService>(){
			@Override
			public Result execute(IRoomService service) {
				service.updateRobotOffon(roomRebootOnoffup);
				return null;
			}
		});
		
		final Map<Byte,Byte> roomRebootOnoffup2 = getRedisData2();
//		GameChannelUtils.executeAllByModule(ModuleName.LHD, ILhdService.class, new IChannelHandler<ILhdService>(){
//			@Override
//			public Result execute(ILhdService service) {
//				service.updateRobotOffonLhd(roomRebootOnoffup2);
//				return null;
//			}
//		});
	}
	
	/**
	 * 初始化
	 */
	public static boolean init(){
		dataSync01(getRedisData());
		dataSync02(getRedisData2());
		return true;
	}
	
	/**
	 * 获取数据
	 * @return
	 */
	private static Map<Byte, Map<Integer,Byte>> getRedisData(){
		String mapJson = RedisMgr.get(rebootOffOnKey, String.class);
		Map<Byte, Map<Integer,Byte>> roomRebootOnoffup = null;
		if(mapJson != null && !mapJson.isEmpty()){
			roomRebootOnoffup =  JSON.parseObject(mapJson,new TypeReference<Map<Byte, Map<Integer,Byte>>>(){});
		}
		if(roomRebootOnoffup == null){
			roomRebootOnoffup = new HashMap<>();
		}
		return roomRebootOnoffup;
	}
	
	/**
	 * 获取数据
	 * @return
	 */
	private static Map<Byte,Byte> getRedisData2(){
		String mapJson = RedisMgr.get(rebootOffOnKey2, String.class);
		Map<Byte,Byte> jsonMaplhd = null;
		if(mapJson != null && !mapJson.isEmpty()){
			jsonMaplhd = JSON.parseObject(mapJson,new TypeReference<Map<Byte,Byte>>(){});
		}
		if(jsonMaplhd == null){
			jsonMaplhd = new HashMap<Byte, Byte>();
		}
		Byte lhdKey = 50;
		if(!jsonMaplhd.containsKey(lhdKey)){
			jsonMaplhd.put(lhdKey, (byte)0);
		}
		return jsonMaplhd;
	}
	/**
	 * 数据同步
	 */
	public static void dataSync01(Map<Byte, Map<Integer,Byte>> roomRobotOnoff){
		RobotOffMgr.roomRobotOnoff = roomRobotOnoff;
		Log.info("=======收到-RoomInfo-更新机器人开关=======");
	}
	/**
	 * 数据同步
	 */
	public static void dataSync02(Map<Byte,Byte> roomRobotOnoffup){
		RobotOffMgr.roomRobotOnoffup = roomRobotOnoffup;
		Log.info("=======收到-other-更新机器人开关=======");
	}
	
	/**
	 * 把数据转换成JSON
	 * @return
	 */
	public static Map<String,String> toJSONDataStringMap(){
		Map<String,String> result = new HashMap<String, String>();
		result.put(rebootOffOnKey2, JSON.toJSONString(getRedisData2()));
		result.put(rebootOffOnKey, JSON.toJSONString(getRedisData()));
		return result;
	}
	/**
	 * 清理
	 */
	public static void clearData(){
		RedisMgr.set(rebootOffOnKey, "");
		/*Map<Byte,Byte> _roomRebootOnoffup = new HashMap<Byte, Byte>();
		_roomRebootOnoffup.put((byte)50, (byte)0);
		RedisMgr.set(rebootOffOnKey2, JSON.toJSONString(_roomRebootOnoffup));*/
		RedisMgr.set(rebootOffOnKey2,"");
		updateData();
	}
	/**
	 * Gm更新
	 * @param gameType
	 * @param temp
	 * @param state
	 */
	public static void updateHttp(Byte gameType,Integer temp,Byte state){
		if(temp == null){
			Map<Byte,Byte> _roomRebootOnoffup = getRedisData2();
			_roomRebootOnoffup.put(gameType, state);
			RedisMgr.set(rebootOffOnKey2, JSON.toJSONString(_roomRebootOnoffup));
		}else{
			Map<Byte, Map<Integer,Byte>> _roomRebootOnoffup = getRedisData();
			 Map<Integer,Byte> node = _roomRebootOnoffup.get(gameType);
			 if(node == null){
				 node = new HashMap<Integer, Byte>();
				 _roomRebootOnoffup.put(gameType, node);
			 }
			 node.put(temp, state);
			 RedisMgr.set(rebootOffOnKey, JSON.toJSONString(_roomRebootOnoffup));
		}
		updateData();
	}

	/**
	 * 场次机器人是否关闭
	 * @param gameType
	 * @param tempId
	 * @return true off false on
	 */
	public static boolean isRoomRobootOff(byte gameType,int tempId){
		Map<Integer,Byte> nMap =  RobotOffMgr.roomRobotOnoff.get(gameType);
		if(nMap != null){
			Byte of = nMap.get(tempId);
			if(of != null && of >= 1){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 场次机器人是否关闭
	 * @param gameType
	 * @param tempId
	 * @return true off false on
	 */
	public static boolean isRoomRobootOff(byte gameType){
		Byte of = RobotOffMgr.roomRobotOnoffup.get(gameType);
		if(of != null && of >= 1){
			return false;
		}
		return true;
	}
}

