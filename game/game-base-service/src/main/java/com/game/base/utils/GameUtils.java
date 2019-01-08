package com.game.base.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.module.ModuleName;
import com.game.framework.component.log.Log;
import com.game.framework.framework.api.baidu.BaiduIPLocation;
import com.game.framework.utils.EncryptUtils;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.TimeUtils;
import com.game.framework.utils.ZipUtils;

/**
 * 游戏中常用工具
 * GameUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午2:03:40
 */
public final class GameUtils {
	/** ip定位-百度 **/
	public static final BaiduIPLocation iplocation = new BaiduIPLocation();

	/** 特殊字符 **/
	public final static String[] specialChars = new String[] { "$", "!", "?", " ", "#", "@", "%", "*", "\"", "\'" };

	/** 表情字符串 **/
	private final static String regexPattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
	public final static Pattern emoji = Pattern.compile(regexPattern, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

	/** 获取名字长度, 中文长度为2, 英文长度为1 **/
	public static int getNameLen(String name) {
		// return StringUtils.length(name);
		int[] rets = StringUtils.lengthByClassify(name);
		return rets[0] + rets[1];
	}

	/** 表情符过滤 **/
	public static boolean checkEmoji(String str) {
		return emoji.matcher(str).find();
	}

	/** 特殊字符检测 **/
	public static boolean checkSpecial(String str) {
		// 遍历检测
		for (String sc : specialChars) {
			if (str.contains(sc)) {
				return true;
			}
		}
		return false;
	}

	/** 判断是否为手机号码 **/
	public static boolean isMobilePhoneNumaber(String mobiles) {
		// 判断是否为空
		if (StringUtils.isEmpty(mobiles)) {
			return false;
		}
		// 检测
		Pattern p = Pattern.compile("^(13[0-9]|14[56789]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/** 获取客户端显示时间字符串 **/
	public static String toShowTimeString(int second) {
		final String dstr = LanguageSet.get(TextTempId.ID_6007);
		final String hstr = LanguageSet.get(TextTempId.ID_6004);
		final String mstr = LanguageSet.get(TextTempId.ID_6005);
		final String sstr = LanguageSet.get(TextTempId.ID_6006);
		return TimeUtils.toShowString(second, dstr, hstr, mstr, sstr);
	}

//	/** 获取玩家所在的抢宝服务 **/
//	public static ISnatchService getSnatchServiceByPlayerId(long playerId, boolean recheck) {
//		ServerConfig config = MailBox.get(playerId, ModuleName.SNATCH);
//		if (config == null) {
//			return null; // 不存在
//		}
//		// 获取对应连接
//		ProxyChannel proxyChannel = GameChannelMgr.getChannel(config);
//		if (proxyChannel == null) {
//			return null;
//		}
//		// 获取玩家所在房间远程连接
//		ISnatchService snatchService = proxyChannel.createImpl(ISnatchService.class);
//		if (!recheck || snatchService == null) {
//			return snatchService; // 直接返回
//		}
//		// 重新检测, 发送请求检测玩家是否还在进程上
//		if (snatchService.checkSnatchPlayer(playerId)) {
//			return snatchService;
//		}
//		// 玩家已经不在对方进程上, 清除记录.
//		MailBox.remove(playerId, ModuleName.SNATCH);
//		return null;
//	}
	
//	public static IGuildService getGuildServiceByPlayerId(long playerId, boolean recheck) {
//		ServerConfig config = MailBox.get(playerId, ModuleName.GUILD);
//		if (config == null) {
//			// 没有在线, 随机一个进程处理.
//			return GameChannelMgr.getRandomServiceByModule(ModuleName.GUILD, IGuildService.class);
//		}
//		// 获取对应连接
//		ProxyChannel proxyChannel = GameChannelMgr.getChannel(config);
//		if (proxyChannel == null) {
////			System.out.println("proxyChannel is null!");
//			return null;
//		}
//		// 获取玩家所在房间远程连接
//		IGuildService guildService = proxyChannel.createImpl(IGuildService.class);
////		System.out.println("guildService:" + guildService);
//		if (!recheck || guildService == null) {
//			return guildService; // 直接返回
//		}
//		return null;
//	}

	/** 转化成字符串数据 **/
	public static <T> String objectToString(Object obj, Class<T> clazz) {
		// 转化数据, 用json, 方便其他平台使用.
		String jsonStr = JSON.toJSONString(obj);
		try {
			return ZipUtils.gzipAndBase64(jsonStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.error("压缩字符串错误!", e);
		}
		return null;
	}

	/** 转成模板数据 **/
	public static <T> T stringToObject(String str, Class<T> clazz) {
		// 解压
		byte[] data = ZipUtils.ungzipAndBase64(str);
		if (data == null) {
			return null;
		}
		// 生成json字符串
		String jsonStr = null;
		try {
			// 转化
			jsonStr = new String(data, "UTF-8");
			return JSON.parseObject(jsonStr, clazz);
		} catch (Exception e) {
			Log.error("解析json错误! jsonStr=" + jsonStr, e);
		}
		return null;
	}

	/** 系统通用-创建签名 **/
	public static <T> String createSign(Map<String, T> params, String secret) {
		return createSign(params, secret, "UTF-8");
	}

	/** 系统通用-创建签名 **/
	public static <T> String createSign(Map<String, T> params, String secret, String enc) {
		// 参数名排序
		List<String> paramNames = new ArrayList<>(params.keySet());
		int psize = paramNames.size();
		if (psize >= 2) {
			Collections.sort(paramNames);
		}

		// 遍历品阶
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0; i < psize; i++) {
			// 获取参数数据
			String paramName = paramNames.get(i);
			Object value = params.get(paramName);
			String vstr = (value != null) ? value.toString() : null;
			if (StringUtils.isEmpty(vstr) || paramName.equals("sign")) {
				continue; // 空过滤
			}
			// &添加
			if (i > 0) {
				strBdr.append("&");
			}
			// 拼接处理
			strBdr.append(paramName);
			strBdr.append("=");
			strBdr.append(vstr);
		}

		// 拼接签名
		strBdr.append("&key=").append(secret);
		// Log.debug("sign:" + strBdr.toString());
		String signA = EncryptUtils.MD5(strBdr.toString(), enc).toUpperCase(); // 注：MD5签名方式
		return signA;
	}
	
	/**
	 * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
	 * @param lon1
	 * @param last1
	 * @param lon2
	 * @param lat2
	 * @return 赤道半径(单位m)
	 */
	public static double getLocationDistance(double locationX1,double locationY1,double locationX2,double locationY2){
		if(locationX1 == 0 && locationX2 == 0)return 1000;
		double radLat1 = rad(locationY1);
		double radLat2 = rad(locationY2);
		double a = radLat1 - radLat2;
		double b = rad(locationX1) - rad(locationX2);
		double c = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a * 0.5), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b * 0.5),2)));
		return c * 6378137;
	}
	
	/**
	 * 弧度转换角度
	 * @param angel
	 * @return
	 */
	public static double rad(double angel)
	{
	    return Math.PI * angel / 180.0;
	 }
}
