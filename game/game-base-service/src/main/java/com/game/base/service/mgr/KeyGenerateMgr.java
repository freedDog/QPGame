package com.game.base.service.mgr;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.key.DefaultKeyGenerate;
import com.game.base.service.key.KeyGenerate;
import com.game.base.service.key.KeyGenerateEnum;
import com.game.base.service.key.RedisKeyGenerate;
import com.game.framework.component.log.Log;
import com.game.framework.framework.xml.XmlNode;

public class KeyGenerateMgr {
	
	private static final Map<KeyGenerateEnum,KeyGenerate> pro = new HashMap<KeyGenerateEnum, KeyGenerate>();
	
	public static boolean init(ServerConfig config) throws Exception{
		Log.info("初始化 全局key");
		// 获取key
		XmlNode xmlNode = ConfigMgr.getElem("KeyGenerate");
		if (xmlNode == null) {
			Log.error("找不到KeyGenerate配置!");
			return false;
		}
		String savePath = xmlNode.getAttr("tempFile").toString();
		if(savePath.isEmpty()){
			Log.error("KeyGenerate配置-> tempFile isEmpty!");
			return false;
		}
		register(KeyGenerateEnum.Default,new DefaultKeyGenerate(config.getId()));
		register(KeyGenerateEnum.RedisKey,new RedisKeyGenerate(config.getId(),savePath));
		return true;
	}
	
	private static void register(KeyGenerateEnum em,KeyGenerate key) throws Exception{
		pro.put(em, key);
		em.setKey(key);
		if(em.equals(KeyGenerateEnum.RedisKey)){
			Method initBasic = key.getClass().getDeclaredMethod("initBasic");
			if(initBasic == null){
				throw new RuntimeException("初始化 key 找不到 initBasic");
			}
			Boolean exec = (Boolean)initBasic.invoke(key);
			if(!exec){
				throw new RuntimeException("初始化 key initBasic 失败");
			}
		}
	}
	
	/**
	 * 保存
	 */
	public static void save(){
		for(KeyGenerate kg : pro.values()){
			kg.save(true);
		}
	}
}
