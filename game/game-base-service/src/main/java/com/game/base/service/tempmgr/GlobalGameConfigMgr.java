package com.game.base.service.tempmgr;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.game.base.service.db.RedisMgr;
import com.game.framework.component.log.Log;
import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.StringUtils;

import redis.clients.jedis.ShardedJedis;

/**
 * 全局配置管理器<br>
 * 用于加载策划配置<br>
 * GlobalGameConfigMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午5:20:59
 */
public class GlobalGameConfigMgr {

	/** 全局配置 **/
	public interface IGlobalConfig {
		// *****************************************************周VIP
		@GlobalGameConfig(key = "WNoteCard", value = "false", desc = "周VIP斗地主免费使用记牌器")
		boolean getWNoteCard();

		@GlobalGameConfig(key = "WBiaoQing", value = "true", desc = "周VIP免费使用所有表情")
		boolean getWBiaoQing();

		@GlobalGameConfig(key = "WFlag", value = "true", desc = "周VIP独有的身份标识")
		boolean getWFlag();

		@GlobalGameConfig(key = "WGold", value = "0.03", desc = "周VIP购买金币赠送比例")
		float getWGold();

		@GlobalGameConfig(key = "WExp", value = "1", desc = "周VIP每局结束之后经验翻*倍")
		float getWExp();

		@GlobalGameConfig(key = "WSpeaker", value = "1", desc = "周VIP购买时赠送小喇叭*个")
		int getWSpeaker();

		@GlobalGameConfig(key = "WSignUGold", value = "1000", desc = "周VIP签到额外赠送U币*个")
		int getWSignUGold();

		@GlobalGameConfig(key = "WContestDisCount", value = "0.95", desc = "周VIP抢宝兑换可以参与*折")
		float getWContestDisCount();

		// *************************************月VIP
		@GlobalGameConfig(key = "MNoteCard", value = "true", desc = "月VIP斗地主免费使用记牌器")
		boolean getMNoteCard();

		@GlobalGameConfig(key = "MBiaoQing", value = "true", desc = "月VIP免费使用所有表情")
		boolean getMBiaoQing();

		@GlobalGameConfig(key = "MFlag", value = "true", desc = "月VIP独有的身份标识")
		boolean getMFlag();

		@GlobalGameConfig(key = "MGold", value = "0.06", desc = "月VIP购买金币赠送比例")
		float getMGold();

		@GlobalGameConfig(key = "MExp", value = "2", desc = "月VIP每局结束之后经验翻*倍")
		float getMExp();

		@GlobalGameConfig(key = "MSpeaker", value = "2", desc = "月VIP购买时赠送小喇叭*个")
		int getMSpeaker();

		@GlobalGameConfig(key = "MSignUGold", value = "3000", desc = "月VIP签到额外赠送U币*个")
		int getMSignUGold();

		@GlobalGameConfig(key = "MContestDisCount", value = "0.90", desc = "月VIP抢宝兑换可以参与*折")
		float getMContestDisCount();

		// ******************************************季 VIP
		@GlobalGameConfig(key = "QNoteCard", value = "true", desc = "季VIP斗地主免费使用记牌器")
		boolean getQNoteCard();

		@GlobalGameConfig(key = "QBiaoQing", value = "true", desc = "季VIP免费使用所有表情")
		boolean getQBiaoQing();

		@GlobalGameConfig(key = "QFlag", value = "true", desc = "季VIP独有的身份标识")
		boolean getQFlag();

		@GlobalGameConfig(key = "QGold", value = "0.10", desc = "季VIP购买金币赠送比例")
		float getQGold();

		@GlobalGameConfig(key = "QExp", value = "2.5", desc = "季VIP每局结束之后经验翻*倍")
		float getQExp();

		@GlobalGameConfig(key = "QSpeaker", value = "2", desc = "季VIP购买时赠送小喇叭*个")
		int getQSpeaker();

		@GlobalGameConfig(key = "QSignUGold", value = "6000", desc = "季VIP签到额外赠送U币*个")
		int getQSignUGold();

		@GlobalGameConfig(key = "QContestDisCount", value = "0.90", desc = "季VIP抢宝兑换可以参与*折")
		float getQContestDisCount();

		// *************************************************年VIP
		@GlobalGameConfig(key = "YNoteCard", value = "true", desc = "年VIP斗地主免费使用记牌器")
		boolean getYNoteCard();

		@GlobalGameConfig(key = "YBiaoQing", value = "true", desc = "年VIP免费使用所有表情")
		boolean getYBiaoQing();

		@GlobalGameConfig(key = "YFlag", value = "true", desc = "年VIP独有的身份标识")
		boolean getYFlag();

		@GlobalGameConfig(key = "YGold", value = "0.15", desc = "年VIP购买金币赠送比例")
		float getYGold();

		@GlobalGameConfig(key = "YExp", value = "3", desc = "年VIP每局结束之后经验翻*倍")
		float getYExp();

		@GlobalGameConfig(key = "YSpeaker", value = "5", desc = "年VIP购买时赠送小喇叭*个")
		int getYSpeaker();

		@GlobalGameConfig(key = "YSignUGold", value = "10000", desc = "年VIP签到额外赠送U币*个")
		int getYSignUGold();

		@GlobalGameConfig(key = "YContestDisCount", value = "0.90", desc = "年VIP抢宝兑换可以参与*折")
		float getYContestDisCount();
		
		@GlobalGameConfig(key = "commissions", value = "0.05", desc = "服务器赢家抽成比例")
		float getCommissions();
		
		@GlobalGameConfig(key = "gameMaintenance", value = "0", desc = "游戏维护状态")
		int getGameMaintenance();
		
		@GlobalGameConfig(key = "withdrawcommisions", value = "0.02", desc = "提现抽成比例")
		float getWithdrawCommisions();
		
		@GlobalGameConfig(key = "payPlatform", value = "qraes", desc = "充值平台")
		String getPayPlatform();
	}

	/** 全局配置 **/
	public static final IGlobalConfig globalConfig = createProxy(IGlobalConfig.class);

	protected static final String redisKey = "globalConfig";
	
	/** 服务器是否是维护状态 */
	public static boolean isGameMaintenance() {
		// 0 非维护状态  1 维护状态
		return GlobalGameConfigMgr.globalConfig.getGameMaintenance() == 1;
	}

	/** 创建代理 **/
	@SuppressWarnings("unchecked")
	protected static <T> T createProxy(Class<T> clazz) {
		// 创建动态代理
		ClassLoader classLoader = GlobalGameConfigMgr.class.getClassLoader();
		return (T) Proxy.newProxyInstance(classLoader, new Class<?>[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// 检测注解
				GlobalGameConfig annotation = method.getAnnotation(GlobalGameConfig.class);
				if (annotation == null) {
					throw new Exception("这个函数不能调用, 没有带GlobalGameConfig注解!");
				}
				String key = annotation.key();
				Class<?> retType = method.getReturnType(); // 返回类型
				String defaultValue = annotation.value(); // 默认参数

				// 获取调用
				Object retObj = GlobalGameConfigMgr.getValue(key, retType, defaultValue);
				if (retObj == null) {
					// Log.warn("全局配置值为空!" + method);
					return ObjectUtils.defualtValue(retType);
				}
				return retObj;
			}
		});
	}
	
	/** 常规读取参数 **/
	public static String getValue(String key) {
		return getByRedis(key);
	}

	/** 读取数据 **/
	protected static <T> T getValue(String key, Class<T> clazz, String defaultValue) {
		// 从redis读取
		String value = getByRedis(key);
		if (StringUtils.isEmpty(value)) {
			value = defaultValue;
			if (StringUtils.isEmpty(value)) {
				return null; // 真心没数据, 呵呵呵.
			}
		}
		// 转化值
		T obj = GameConfigMgr.createConfig(key, value, clazz);
		// Log.info("getValue: " + key + " " + value + " " + clazz + " " + obj);
		return obj;
	}

	/** 从redis中读取数据 **/
	protected static String getByRedis(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 转化数据
			return jedis.hget(redisKey, key);
		} catch (Exception e) {
			Log.error("获取全局变量错误:" + key + " " + e.getMessage());
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/** 获取全局参数对应函数 **/
	protected static Method getConfigMethod(Class<?> clazz, String keyname) {
		// 遍历查看参数
		Method[] methods = clazz.getDeclaredMethods();
		for (Method gmethod : methods) {
			GlobalGameConfig gameConfig = gmethod.getAnnotation(GlobalGameConfig.class);
			if (gameConfig == null) {
				continue;
			}
			// 检测是否是这个名字
			if (gameConfig.key().equals(keyname)) {
				return gmethod;
			}
		}
		return null;
	}

	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface GlobalGameConfig {
		/** key **/
		String key();

		/** 默认值 **/
		String value();

		/** 说明 **/
		String desc();

	}

}

