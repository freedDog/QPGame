package com.game.base.service.tempmgr;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.utils.DataUtils;
import com.game.entity.bean.Product;
import com.game.entity.configuration.GameConfigTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.struct.time.TimeSlot;

/**
 * 配置管理器<br>
 * 用于加载策划配置<br>
 * GameConfigMgr.java
 * @author JiangBangMing
 * 2019年1月8日上午11:39:48
 */
public class GameConfigMgr {
	@GameConfig(key = "CreatePlayerProducts", desc = "创建角色获取道具", nullable = true)
	public static Product[] createPlayerProducts = new Product[] {};
	//入口被关闭
	@Deprecated
	@GameConfig(key = "NewPlayerAwards", desc = "新手礼包", nullable = true)
	public static Product[] newPlayerAwards = new Product[] {};

	@GameConfig(key = "ShareReward", desc = "分享奖励", nullable = true)
	public static Product[] shareReward = new Product[] {};
	//关闭
	@Deprecated
	@GameConfig(key = "FristPayAwards", desc = "首充基础奖励", nullable = true)
	public static Product[] fristPayAwards = new Product[] {};
	//关闭
	@Deprecated
	@GameConfig(key = "FristPayConditionAwards", desc = "首充限制奖励", nullable = true)
	public static Product[] fristPayConditionAwards = new Product[] {};
	//关闭
	@Deprecated
	@GameConfig(key = "FristPayCondition", desc = "首充限制", nullable = true)
	public static int fristPayCondition = 0;
	//关闭
	@Deprecated
	@GameConfig(key = "FristPayAwards1", desc = "首充奖励1", nullable = true)
	public static String[] fristPayAwards1 = new String[] {};

	@GameConfig(key = "SignExpends", desc = "补签消耗")
	public static Product[] signExpends = new Product[] {};

	@GameConfig(key = "NoticeExpends", desc = "消息消耗")
	public static Product[] noticeExpends = new Product[] {};

	@GameConfig(key = "TaskRandomCount", desc = "每日随机任务数量")
	public static int taskRandomCount = 100;

	@GameConfig(key = "TaskTotalComplete", desc = "任务累计奖励所需完成次数")
	public static int taskTotalComplete = 1;

	@GameConfig(key = "TaskTotalRewards", desc = "任务累计完成奖励")
	public static Product[] taskTotalRewards = new Product[] {};

	@GameConfig(key = "BrokeAward", desc = "破产补助")
	public static Product[] brokeAward = new Product[] {};

	@GameConfig(key = "BrokeAwardCount", desc = "破产补助每天领取次数")
	public static int brokeAwardCount = 0;

	@GameConfig(key = "BrokeLimit", desc = "破产U币限制")
	public static int brokeLimit = 0;
	
	@Deprecated
	@GameConfig(key = "GameChatExpends", desc = "聊天发送消耗")
	public static Product[] gameChatExpends = new Product[] {};
	
	@GameConfig(key = "SendNoticeExpends", desc = "玩家发送世界消息消耗")
	public static Product[] SendNoticeExpends = new Product[] {};

	@GameConfig(key = "BindPhoneAwards", desc = "绑定手机赠送奖励")
	public static Product[] bindPhoneExpends = new Product[] {};
	
	//被关闭
	@Deprecated
	@GameConfig(key = "LoginAwards", desc = "登陆奖励")
	public static Product[] loginAwards = new Product[] {};

	@GameConfig(key = "BullGoldFightRoomCard", desc = "斗牛牛房卡消耗")
	public static int[] bullGoldFightRoomCard = new int[]{};
	
	@GameConfig(key = "DaXuanRoomCard", desc = "打旋房卡消耗")
	public static int[] daXuanRoomCard = new int[]{};
	
	@GameConfig(key = "ZhaJinHuaRoomCard", desc = "扎金花房卡消耗")
	public static int[] zhaJinHuaRoomCard = new int[]{};
	
	@GameConfig(key = "DouDiZhuRoomCard", desc = "斗地主房卡消耗")
	public static int[] douDiZhuRoomCard = new int[]{};
	
	@GameConfig(key = "MaJiangRoomCard", desc = "麻将房卡消耗")
	public static int[] maJiangRoomCard = new int[]{};
	
	@GameConfig(key = "DJNBaseScore", desc = "斗金牛底分")
	public static int[] DJNBaseScore = new int[]{};
	
	@GameConfig(key = "DJNMinJoinScore", desc = "斗金牛条件")
	public static String DJNMinJoinScore;
	
	public static List<int[]> DJNMinJoinScoreList = new ArrayList<int[]>();
	
	@GameConfig(key = "CXBaseScore", desc = "打旋儿底分")
	public static int[] CXBaseScore = new int[]{};
	
	@GameConfig(key = "CXMinJoinScore", desc = "打旋儿入场条件")
	public static String CXMinJoinScore;
	
	public static List<int[]> CXMinJoinScoreList = new ArrayList<int[]>();
	
	@GameConfig(key = "CXMINQiBoBo", desc = "打旋儿最低起钵钵")
	public static String CXMINQiBoBo;
	
	public static List<List<int[]>> CXMINQiBoBoList = new ArrayList<List<int[]>>();
	/** 初始化配置 **/
	public static boolean init() {

		// 加载配置
		String jsonStr = ConfigurationMgr.loadConfiguration("GameConfig.json");
		if (StringUtils.isEmpty(jsonStr)) {
			Log.error("找不到配置文件!");
			return false;
		}

		// 获取数据库配置信息
		List<GameConfigTempInfo> infos = JSON.parseArray(jsonStr, GameConfigTempInfo.class);
		Map<String, String> configInfos = new HashMap<>();
		for (GameConfigTempInfo info : infos) {
			String key = info.getKey().trim();
			String old = configInfos.put(key, info.getValue());
			if (old != null) {
				Log.error("GameConfig存在重复的参数! " + info);
				return false;
			}
		}

		// 初始化配置
		if (!initGameConfigByClass(GameConfigMgr.class, configInfos)) {
			return false;
		}
		if(!splistParam(GameConfigMgr.DJNMinJoinScore,GameConfigMgr.DJNMinJoinScoreList)){
			return false;
		}
		if(!splistParam(GameConfigMgr.CXMinJoinScore,GameConfigMgr.CXMinJoinScoreList)){
			return false;
		}
		if(!splistParam3(GameConfigMgr.CXMINQiBoBo,GameConfigMgr.CXMINQiBoBoList)){
			return false;
		}
		
		return true;
	}

	/**
	 * 2级处理 XXX,XXX|XXX,XXX
	 * @return
	 */
	private static boolean splistParam(String param,List<int[]> list){
		try{
			String[] splitArr = param.split("\\|");
			for(String str: splitArr){
				list.add(DataUtils.splitToInt(str, ","));
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 3级处理XX,XX;XX,XX|XX,XX;XX,XX
	 * @return
	 */
	private static boolean splistParam3(String param,List<List<int[]>> list){
		try{
			String[] splitArr = param.split("\\|");
			for(String str: splitArr){
				String[] fnode = str.split(";");
				List<int[]> node = new ArrayList<int[]>();
				for(String nodeS : fnode){
					node.add(DataUtils.splitToInt(nodeS, ","));
				}
				list.add(node);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/** 初始化类配置 **/
	private static boolean initGameConfigByClass(Class<?> clazz, Map<String, String> configInfos) {
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			GameConfig gameConfig = field.getAnnotation(GameConfig.class); // 获取是否有这个注解
			if (gameConfig == null || field.isAnnotationPresent(Deprecated.class)) {
				continue;
			}
//			Log.info(field.getName()+"=="+field.isAnnotationPresent(Deprecated.class));
			// 判断key
			String key = gameConfig.key();
			if (StringUtils.isEmpty(key)) {
				Log.error("gameConfig变量key为空!" + field);
				return false;
			}

			// 判断是否是静态
			// if (!Modifier.isStatic(field.getModifiers())) {
			// Log.error("gameConfig变量必须是静态!" + field);
			// return false;
			// }
			// 判断是否是final
			// if (!Modifier.isFinal(field.getModifiers())) {
			// Log.error("gameConfig变量必须是final!" + field);
			// return false;
			// }

			// 创建配置
			String date = configInfos.get(key);
			String desc = gameConfig.desc();
			if (StringUtils.isEmpty(date)) {
				// 没有这个配置, 判断是否可空
				if (gameConfig.nullable()) {
					continue;
				}
				if (!ConfigMgr.isDebug()) {
					Log.error("配置参数尚未设置: key=" + key + " desc=" + desc);
					return false; // 非debug模式下, 必须让策划配置上这个参数
				}
				continue;
			}

			// 解析参数
			Object value = null;
			try {
				value = createConfig(key, date, (Class<?>) field.getGenericType());
			} catch (Exception e) {
				Log.error("配置参数解析错误: key=" + key + " value=" + date + " desc=" + desc, e);
				return false;
			}

			// 判断结果
			if (value == null) {
				Log.error("配置参数解析不出来: key=" + key + " [" + clazz + "] value=" + date + " desc=" + desc);
				return false; // 创建不出来
			}

			// 设置参数
			try {
				// // 强制设置数据(没用, 静态有问题)
				// // ReflectUtils.setFinalStatic(field, value);
				//
				// // 获取field的属性
				// Field modifiersField = Field.class.getDeclaredField("modifiers");
				// modifiersField.setAccessible(true);
				// modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL); // 修改掉final属性
				// modifiersField.setAccessible(false);
				// // 强制设置为不可修改
				// field.setAccessible(false);
				field.set(null, value);
			} catch (Exception e) {
				Log.error("设置GameConfig配置错误: key=" + key + " value=" + date + " desc=" + desc, e);
				return false;
			}
		}
		// 设置成功
		return true;
	}

	/** 配置参数创建(第一次读取时) **/
	@SuppressWarnings("unchecked")
	protected static <T> T createConfig(String key, String data, Class<T> clazz) {
		if (clazz == int.class || clazz == Integer.class) {
			try {
				int num = Integer.parseInt(data);
				return (T) ((Integer) num);
			} catch (Exception e) {
				Log.warn("参数无法解析成对应类型! key=" + key + " data=" + data + " -> " + clazz);
				return null;
			}
		} else if (clazz == double.class || clazz == Double.class) {
			try {
				double num = Double.parseDouble(data);
				return (T) (Double) num;
			} catch (Exception e) {
				Log.warn("参数无法解析成对应类型! key=" + key + " data=" + data + " -> " + clazz);
				return null;
			}
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			try {
				boolean num = Boolean.parseBoolean(data);
				return (T) (Boolean) num;
			} catch (Exception e) {
				Log.warn("参数无法解析成对应类型! key=" + key + " data=" + data + " -> " + clazz);
				return null;
			}
		} else if (clazz == float.class || clazz == Float.class) {
			try {
				float num = Float.parseFloat(data);
				return (T) (Float) num;
			} catch (Exception e) {
				Log.warn("参数无法解析成对应类型! key=" + key + " data=" + data + " -> " + clazz);
				return null;
			}
		} else if (clazz == int[].class) {
			int[] obj = DataUtils.splitToInt(data, ",");
			if (obj == null || obj.length <= 0) {
				return null; // 解析错误判断
			}
			return (T) obj;
		} else if (clazz == String[].class) {
			String[] obj = data.split(",");
			if (obj == null || obj.length <= 0) {
				return null; // 解析错误判断
			}
			return (T) obj;
		} else if (clazz == double[].class) {
			double[] obj = DataUtils.splitToDouble(data, ",");
			if (obj == null || obj.length <= 0) {
				return null; // 解析错误判断
			}
			return (T) obj;
		} else if (clazz == int[][].class) {
			int[][] obj = DataUtils.splitToInt2(data, "\\|", ",");
			if (obj == null || obj.length <= 0 || obj[0].length <= 0) {
				return null; // 解析错误判断
			}
			return (T) obj;
		} else if (clazz == TimeSlot.class) {
			TimeSlot[] ts = TimeSlot.toTimeSlots(data);
			return (ts != null && ts.length > 0) ? (T) ts[0] : null;
		} else if (clazz == TimeSlot[].class) {
			return (T) TimeSlot.toTimeSlots(data);
		} else if (clazz == Product.class) {
			Product[] rets = Product.toProductArray(data);
			return (rets != null && rets.length > 0) ? (T) rets[0] : null;
		} else if (clazz == Product[].class) {
			return (T) Product.toProductArray(data);
		} else if (String.class.isAssignableFrom(clazz)) {
			return (T) data;
		} else if (clazz == Object.class) {
			return (T) data;
		} else {
			Log.error("未知配置类型: clazz=" + clazz);
		}
		return null;
	}

	/** 配置对象 **/
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	@Inherited
	@Documented
	public @interface GameConfig {
		/** 配置名 **/
		String key();

		/** 说明 **/
		String desc();

		/** 是否可空! **/
		boolean nullable() default false;
	}
}

