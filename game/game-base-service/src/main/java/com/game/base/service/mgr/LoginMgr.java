package com.game.base.service.mgr;

import com.game.base.service.config.ConfigMgr;
import com.game.framework.component.log.Log;
import com.game.framework.framework.http.HttpCall;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.StringUtils;

/**
 * 登陆服配置管理器
 * LoginMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午1:29:24
 */
public class LoginMgr extends HttpCall {
	private static String encryptKey; // 登陆密钥

	protected boolean init() {
		// 获取key
		XmlNode xmlNode = ConfigMgr.getElem("Login");
		if (xmlNode == null) {
			Log.error("找不到Login配置!");
			return false;
		}
		// 获取key
		encryptKey = xmlNode.getAttr("encryptkey", "tgtgame2017");

		// 获取url路径
		callUrl = xmlNode.getAttr("url", "");
		if (StringUtils.isEmpty(callUrl)) {
			Log.error("登录服的Url为空，读取错误");
			return false;
		}

		return true;
	}

	/** 获取登陆解密key **/
	public String getEncryptKey() {
		return encryptKey;
	}

	private static LoginMgr instance = new LoginMgr();

	public static LoginMgr getInstance() {
		return instance;
	}
}
