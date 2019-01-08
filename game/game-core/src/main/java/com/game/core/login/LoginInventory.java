package com.game.core.login;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.game.base.service.player.PlayerInventory;
import com.game.core.player.CorePlayer;
import com.game.entity.bean.Device;
import com.game.entity.entity.PlayerExtendInfo;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.utils.BufferUtils;
import com.game.framework.utils.EncryptUtils;

/**
 * 玩家额外处理
 * 
 */
public class LoginInventory extends PlayerInventory<CorePlayer> {
	protected String connectKey; // 连接key(用于过滤登出)
	private Device device; // 玩家登陆设备信息

	protected LoginInventory(CorePlayer player) {
		super(player);
		device = new Device(); // 空对象
	}

	/** 检测登陆key **/
	public boolean checkLoginKey(String checkKey) {
		String loginKey = getLoginKey();
		if (loginKey == null || !loginKey.equals(checkKey)) {
			return false;
		}
		return true;
	}

	/** 获取登陆key **/
	public String getLoginKey() {
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		return extendInfo.getLoginKey();
	}

	/** 设置登陆key **/
	public void setLoginKey(String loginKey) {
		// 修改设置值
		PlayerExtendInfo extendInfo = player.getExtendInfo();
		extendInfo.setLoginKey(loginKey);
	}

	public void setConnect(ProxyChannel channel, long connectId) {
		this.connectKey = createKey(channel.getConfig().getId(), connectId);

	}

	public boolean checkConnect(ProxyChannel channel, long connectId) {
		// 检测连接key
		String checkKey = createKey(channel.getConfig().getId(), connectId);
		if (connectKey == null || !connectKey.equals(checkKey)) {
			return false;
		}
		return true;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Device getDevice() {
		return device;
	}

	@Override
	protected boolean load() {
		return true;
	}

	@Override
	protected void unload() {
	}

	@Override
	protected boolean save() {
		return true;
	}

	/** 生成连接key **/
	private static String createKey(int appId, long connectId) {
		// 生成数据流
		byte[] buff = null;
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			out.write(BufferUtils.intToBytes(appId));
			out.write(BufferUtils.longToBytes(connectId));
			out.flush();
			// 获取数据
			buff = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return String.valueOf(appId) + "-" + String.valueOf(connectId);
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
			}
		}
		// 服务器Id
		String serverId = EncryptUtils.Base64.encode(buff);
		return serverId;
	}
}
