package com.game.proto.rp.login;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;


public class LoginBindMsg extends RpMessage {
	// 游戏平台
	private String platform;
	// 游客还是账号 0:账号 1:游客
	private int type;
	// 绑定的手机号码
	private String modilePhone;
	// 密码
	private String password;

	/** 游戏平台 */
	public String getPlatform() {
		return platform;
	}

	/** 游戏平台 */
	public void setPlatform(String value) {
		this.platform = value;
	}

	/** 游客还是账号 0:账号 1:游客 */
	public int getType() {
		return type;
	}

	/** 游客还是账号 0:账号 1:游客 */
	public void setType(int value) {
		this.type = value;
	}

	/** 绑定的手机号码 */
	public String getModilePhone() {
		return modilePhone;
	}

	/** 绑定的手机号码 */
	public void setModilePhone(String value) {
		this.modilePhone = value;
	}

	/** 密码 */
	public String getPassword() {
		return password;
	}

	/** 密码 */
	public void setPassword(String value) {
		this.password = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putString(buffer, platform);
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putString(buffer, modilePhone);
		ByteBufferHelper.putString(buffer, password);
	}

	public static LoginBindMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static LoginBindMsg deserialize(ByteBuffer buffer) {
		LoginBindMsg messageInstance = new LoginBindMsg();
		messageInstance.platform = ByteBufferHelper.getString(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.modilePhone = ByteBufferHelper.getString(buffer);
		messageInstance.password = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 4;
		length += ByteBufferHelper.calcStringLength(platform);
		length += ByteBufferHelper.calcStringLength(modilePhone);
		length += ByteBufferHelper.calcStringLength(password);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LoginBindMsg[");
		sb.append("platform=" + platform + ", ");
		sb.append("type=" + type + ", ");
		sb.append("modilePhone=" + modilePhone + ", ");
		sb.append("password=" + password + ", ");
		sb.append("]");
		return sb.toString();
	}
}