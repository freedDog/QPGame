package com.game.framework.framework.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP定位
 * IPLocation.java
 * @author JiangBangMing
 * 2019年1月3日下午2:15:20
 */
public abstract class IPLocation<T extends IPLocationInfo> {

	/** 定位 **/
	public abstract T location(String ip);

	/** 判断IP地址是否为内网 **/
	public static boolean isInternalIp(String ip) {
		// 本机检测
		if (ip.equals("127.0.0.1")) {
			return true;
		}
		// 通用检测
		String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";// 正则表达式=。
		Pattern p = Pattern.compile(reg);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

}
