package com.game.framework.framework.api.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.framework.component.log.Log;
import com.game.framework.framework.api.IPLocation;
import com.game.framework.framework.api.IPLocationInfo;
import com.game.framework.framework.server.http.utils.HttpUtils;
import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.collections.ArrayUtils;
import com.game.framework.framework.api.baidu.BaiduIPLocation.BaiduIPLocationInfo;
/**
 * 百度定位<br>
 * 可带定位地址
 * BaiduIPLocation.java
 * @author JiangBangMing
 * 2019年1月3日下午2:16:32
 */
public class BaiduIPLocation extends IPLocation<BaiduIPLocationInfo> {

	@Override
	public BaiduIPLocationInfo location(String ip) {
		// 内网处理
		if (isInternalIp(ip)) {
			BaiduIPLocationInfo nw = new BaiduIPLocationInfo();
			nw.setArea("内网");
			nw.setCountry("中国");
			nw.setRegion("广东");
			nw.setCity("深圳");
			nw.setCounty("龙华区");
			nw.setIsp("电信");
			nw.setX(114.046887);
			nw.setY(22.726275);
			return nw;
		}

		// 访问处理
		String url = "http://api.map.baidu.com/location/ip?ak=32f38c9491f2da9eb61106aaab1e9739&ip=" + ip + "&coor=bd09ll";
		byte[] retDatas = HttpUtils.get(url);
		String retStr = HttpUtils.toString(retDatas);
		if (StringUtils.isEmpty(retStr)) {
			return null;
		}
		// {"content":{"point":{"y":"23.04302382","x":"113.76343399"},"address":"广东省东莞市","address_detail":{"street":"","province":"广东省","street_number":"","city_code":119,"district":"","city":"东莞市"}},"status":0,"address":"CN|广东|东莞|None|CHINANET|0|0"}
		// Log.info(retStr);

		// 解析json
		JSONObject json = null;
		try {
			// 解析json
			json = JSON.parseObject(retStr);
		} catch (Exception e) {
			Log.error("json解析错误" + retStr, e);
			return null;
		}

		try {
			// 判断结果
			if (json.getInteger("status") != 0) {
				return null;
			}

			String address = json.getString("address");
			String[] addstrs = address.split("\\|");
			// 返回结果
			BaiduIPLocationInfo info = new BaiduIPLocationInfo();
			info.setCountry(ArrayUtils.get(addstrs, 0));
			info.setArea(ArrayUtils.get(addstrs, 1));
			info.setCity(ArrayUtils.get(addstrs, 2));

			// 坐标定位
			JSONObject point = ((JSONObject) json.get("content")).getJSONObject("point");
			info.setX(ObjectUtils.toValue(point.get("x"), double.class));
			info.setY(ObjectUtils.toValue(point.get("y"), double.class));
			return info;
		} catch (Exception e) {
			Log.error("参数读取错误" + retStr, e);
		}
		return null;
	}

	/** 百度定位信息 **/
	public static class BaiduIPLocationInfo extends IPLocationInfo {
		protected double x; // 定位坐标X
		protected double y; // 定位坐标Y

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		@Override
		public String toString() {
			return "BaiduIPLocationInfo [x=" + x + ", y=" + y + ", country=" + country + ", area=" + area + ", region=" + region + ", city=" + city + ", county=" + county + ", isp=" + isp + "]";
		}

	}

}
