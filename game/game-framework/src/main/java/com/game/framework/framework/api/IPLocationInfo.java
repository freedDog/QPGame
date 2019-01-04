package com.game.framework.framework.api;

import com.game.framework.utils.StringUtils;

/**
 * IP地址区域数据
 * IPLocationInfo.java
 * @author JiangBangMing
 * 2019年1月3日下午2:14:46
 */
public class IPLocationInfo {
	protected String country; // 国家
	protected String area; // 地域
	protected String region; // 省份
	protected String city; // 城市
	protected String county; // 地区
	protected String isp; // 网络通信

	/** 加密转化 **/
	public String toEncode() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(country).append(",");
		strBdr.append(area).append(",");
		strBdr.append(region).append(",");
		strBdr.append(city).append(",");
		strBdr.append(county).append(",");
		strBdr.append(isp).append(",");
		return strBdr.toString();
	}

	/** 解析 **/
	public boolean toDecode(String str) {
		// 检测地址
		if (StringUtils.isEmpty(str)) {
			// Log.error("填入的区域地址错误" + addr);
			return false;
		}
		// 处理地址
		String[] addrStr = str.split(",");
		if (addrStr.length != 6) {
			// Log.error("裁剪出来的区域地址错误! addr=" + str);
			return false;
		}
		// 返回结果
		country = addrStr[0];
		area = addrStr[1];
		region = addrStr[2];
		city = addrStr[3];
		county = addrStr[4];
		isp = addrStr[5];
		return true;
	}

	/** 解析 **/
	public static IPLocationInfo decode(String str) {
		IPLocationInfo info = new IPLocationInfo();
		if (!info.toDecode(str)) {
			return null;
		}
		return info;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	@Override
	public String toString() {
		return "IPLocationInfo [country=" + country + ", area=" + area + ", region=" + region + ", city=" + city + ", county=" + county + ", isp=" + isp + "]";
	}

}