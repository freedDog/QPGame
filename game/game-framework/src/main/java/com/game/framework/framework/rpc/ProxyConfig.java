package com.game.framework.framework.rpc;

import com.game.framework.utils.StringUtils;
import com.game.framework.utils.SystemUtils;

/**
 * 服务器配置<br>
 * ProxyConfig.java
 * @author JiangBangMing
 * 2019年1月3日下午3:50:34
 */
public class ProxyConfig {
	private int id;
	private String name;
	private String host;
	private int port;

	protected ProxyConfig() {
	}

	public ProxyConfig(int id, String name, String host, int port) {
		this.id = id;
		this.name = name;
		this.host = StringUtils.isEmpty(host) ? SystemUtils.getLocalIp() : host; // 如果为空
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyConfig other = (ProxyConfig) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProxyConfig [id=" + id + ", name=" + name + ", host=" + host + ", port=" + port + "]";
	}

}
