package com.game.framework.framework.api.taobao;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.framework.component.log.Log;
import com.game.framework.framework.api.IPLocation;
import com.game.framework.framework.api.IPLocationInfo;
import com.game.framework.utils.StringUtils;

/**
 * 淘宝定位
 * TaobaoIPLocation.java
 * @author JiangBangMing
 * 2019年1月3日下午2:32:39
 */
public class TaobaoIPLocation extends IPLocation<IPLocationInfo> {

	@Override
	public IPLocationInfo location(String ip) {
		// 内网处理
		if (isInternalIp(ip)) {
			IPLocationInfo nw = new IPLocationInfo();
			nw.setArea("内网");
			nw.setCountry("中国");
			nw.setRegion("广东");
			nw.setCity("深圳");
			nw.setCounty("龙华区");
			nw.setIsp("电信");
			return nw;
		}

		// 这里调用pconline的接口
		String urlStr = "http://ip.taobao.com/service/getIpInfo.php";
		// 访问获取
		String retStr = post(urlStr, "ip=" + ip, "utf-8");
		if (StringUtils.isEmpty(retStr)) {
			return null; // 没有结果
		}

		// 解析json
		JSONObject json = null;
		try {
			json = JSON.parseObject(retStr);
		} catch (Exception e) {
			Log.error("json解析错误!" + retStr, e);
		}

		try {
			// 检测是否失败
			if (json == null || json.getIntValue("code") != 0) {
				return null; // 失败
			}

			// 读取地址信息
			JSONObject retData = json.getJSONObject("data");
			String region = retData.getString("region");// 省份
			String country = retData.getString("country"); // 国家
			String area = retData.getString("area"); // 地区
			String city = retData.getString("city"); // 市
			String county = retData.getString("county"); // 县
			String isp = retData.getString("isp"); // 运营商

			// 返回数据
			IPLocationInfo addrIPInfo = new IPLocationInfo();
			addrIPInfo.setCountry(country);
			addrIPInfo.setArea(area);
			addrIPInfo.setRegion(region);
			addrIPInfo.setCity(city);
			addrIPInfo.setCounty(county);
			addrIPInfo.setIsp(isp);
			return addrIPInfo;
		} catch (Exception e) {
			Log.error("提取数据错误!" + retStr, e);
		}
		return null;
	}

	/**
	 * @param urlStr
	 *            请求的地址
	 * @param paramStr
	 *            请求的参数 格式为：name=xxx&pwd=xxx
	 * @param encoding
	 *            服务器端请求编码。如GBK,UTF-8等
	 * @return
	 */
	private static String post(String urlStr, String paramStr, String encoding) {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();// 新建连接实例
			connection.setConnectTimeout(2000);// 设置连接超时时间，单位毫秒
			connection.setReadTimeout(2000);// 设置读取数据超时时间，单位毫秒
			connection.setDoOutput(true);// 是否打开输出流 true|false
			connection.setDoInput(true);// 是否打开输入流true|false
			connection.setRequestMethod("POST");// 提交方法POST|GET
			connection.setUseCaches(false);// 是否缓存true|false
			connection.connect();// 打开连接端口
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());// 打开输出流往对端服务器写数据
			out.writeBytes(paramStr);// 写数据,也就是提交你的表单 name=xxx&pwd=xxx
			out.flush();// 刷新
			out.close();// 关闭输出流
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));// 往对端写完数据对端服务器返回数据
			// ,以BufferedReader流来读取
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			return buffer.toString();
		} catch (IOException e) {
			// 访问超时不输出
			// e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();// 关闭连接
			}
		}
		return null;
	}

	/** 地址数据回馈 **/
	protected static class AddrResult {
		protected int code;
		protected Map<String, String> data;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public Map<String, String> getData() {
			return data;
		}

		public void setData(Map<String, String> data) {
			this.data = data;
		}

		public boolean isSuccess() {
			return code == 0;
		}
	}
}
