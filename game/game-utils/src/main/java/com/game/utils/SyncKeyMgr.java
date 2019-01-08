package com.game.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.game.framework.utils.EncryptUtils;

/**
 * 同步Key生成
 * @author jh
 *
 */
public class SyncKeyMgr {
	public static final String DATE_FORMAT_STR = "yyyy_MM_dd_HH_mm";
	
	private static volatile long oldTime;//老一个Key
	/**
	 * 获取KEY值
	 * @return
	 */
	public static String getKey(boolean oldKey){
		//获取串
		String sourceStr = oldKey ? SyncKeyMgr.formatDate(new Date(SyncKeyMgr.oldTime)) : SyncKeyMgr.sourceStr();
//		System.out.println("获取源串:"+sourceStr);
		int minute = Integer.parseInt(sourceStr.substring(sourceStr.length() - 2));
		//取分的 前位 和 后位
		int lengthIndex = minute % 10;
		int lengthIndex2 = minute / 10;
		//组成64 位
		String keySourceStr64 = sourceStr.substring(0, lengthIndex) +"-"+ sourceStr.substring(lengthIndex2)+"&"+sourceStr;
//		System.out.println("生成key源串:"+keySourceStr64);
		keySourceStr64 = EncryptUtils.MD5(keySourceStr64);
//		System.out.println("生成key串:"+keySourceStr64);
		return keySourceStr64;
	}
	
	/**
	 * 验证sign
	 * @param str
	 * @param sign
	 * @return
	 */
	public static boolean checkSign(String str,String sign){
		if(sign == null || str == null || str.isEmpty() || sign.isEmpty()){
			return false;
		}
		String validSign = EncryptUtils.MD5(str+getKey(false));
//		System.out.println(validSign);
		if(validSign.equals(sign)){
			return true;
		}
		validSign = EncryptUtils.MD5(str+getKey(true));
//		System.out.println(validSign);
		return validSign.equals(sign);
	}
	
	/**
	 * 创建验证串
	 * @param str
	 * @return
	 */
	public static String createSourceStr(String... str){
		StringBuilder sb = new StringBuilder();
		for(String a : str){
			sb.append(a);
			sb.append("&");
		}
		return sb.toString();
	}
	/**
	 * 获取源串
	 * @return
	 */
	public static String sourceStr(){
		Date date = new Date();
		String newKey = SyncKeyMgr.formatDate(date);
		if(SyncKeyMgr.oldTime == 0 || date.getTime() - SyncKeyMgr.oldTime >= 120000L){
			SyncKeyMgr.oldTime = date.getTime() - 60000L;
		}
		return newKey;
	}
	
	private static String formatDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STR);
		return format.format(date);
	}
}
