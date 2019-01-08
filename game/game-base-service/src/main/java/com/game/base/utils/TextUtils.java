package com.game.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.game.base.service.mgr.TextTempMgr;
import com.game.framework.utils.StringUtils;

/**
 * 配置文本中工具
 * TextUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午1:31:59
 */
public class TextUtils {

	/** 获得text配置表中的文本消息 **/
	public static String getText(String text) {
		int tempId = getTemplateId(text);
		if (tempId <= 0) {
			return text;
		}

		return TextTempMgr.getText(tempId);
	}

	/** 用正则表达式获得文本的ID **/
	private static int getTemplateId(String text) {
		if (StringUtils.isEmpty(text)) {
			return -1;
		}
		String regx = "\\d+";// "(?<=\")(.+?)(?=\")";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String content = matcher.group();
			return DataUtils.toInt(content);
		}

		return 0;
	}

	public static void main(String args[]) {

		// 测试
		System.out.print(getTemplateId("<text=14>"));
	}

}
