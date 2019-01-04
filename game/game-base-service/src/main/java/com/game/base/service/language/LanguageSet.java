package com.game.base.service.language;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.game.framework.component.log.Log;

/**
 * 多语言文本<br>
 * 这个只用于组建格式给客户端, 不带任何文本内容. <br>
 * 以客户端格式为准<br>
 * LanguageSet.java
 * @author JiangBangMing
 * 2019年1月4日下午4:31:08
 */
public final class LanguageSet {

	/** 创建带参数的多语言消息体(格式<text=id "arg1" "arg2">) **/
	public static String get(Object key, Object... params) {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("<text=");
		strBdr.append(key);
		for (Object param : params) {
			String pstr = null;
			if (param != null) {
				try {
					pstr = URLEncoder.encode(param.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Log.error("转码失败!" + param, e);
				}
			}
			strBdr.append(" ");
			strBdr.append(pstr);
		}
		strBdr.append(">");
		return strBdr.toString();
	}

	/** 创建带参数的多语言消息体 **/
	public static String get(String key, Object... params) {
		return get((Object) key, params);
	}
}
