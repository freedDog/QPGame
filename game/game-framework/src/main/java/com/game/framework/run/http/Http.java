package com.game.framework.run.http;

import java.util.Arrays;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.framework.server.http.utils.HttpUtils;
import com.game.framework.utils.SystemUtils;



/**
 * 公用http访问
 * Http.java
 * @author JiangBangMing
 * 2019年1月3日下午5:53:45
 */
public class Http {

	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			args = new String[] { "http://127.0.0.1:9997/stop", "a=b" };
		}

		// 判断参数
		if (args == null || args.length < 1) {
			Log.error("参数错误, 请输入访问的url.");
			return;
		}

		// 解析参数
		Map<String, String> params = SystemUtils.ArgUtils.systemArgs(args);

		// 获取数据
		String url = args[0];
		String url0 = HttpUtils.url(url, params);

		// http访问
		byte[] ret = HttpUtils.get(url0);
		try {
			String retStr = (ret != null) ? new String(ret, "UTF-8") : "";
			System.out.println(retStr);
		} catch (Exception e) {
			Log.error("错误返回: " + Arrays.toString(ret));
		}

	}

}
