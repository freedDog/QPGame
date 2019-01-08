package com.game.proto.rp.player;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.exploit.ExploitMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class SimplePlayerMsg extends RpMessage {
	// 用户ID
	private long playerId;
	// 玩家名
	private String name;
	// 性别
	private short sex;
	// 等级
	private int level;
	// VIP等级
	private int vip;
	// 称号模板
	private int titleId;
	// 形象模板
	private int fashionId;
	// 战绩模板
	private List<ExploitMsg> exploits = new ArrayList<ExploitMsg>();
	// 玩家头像
	private String headImgUrl;

	/** 用户ID */
	public long getPlayerId() {
		return playerId;
	}

	/** 用户ID */
	public void setPlayerId(long value) {
		this.playerId = value;
	}

	/** 玩家名 */
	public String getName() {
		return name;
	}

	/** 玩家名 */
	public void setName(String value) {
		this.name = value;
	}

	/** 性别 */
	public short getSex() {
		return sex;
	}

	/** 性别 */
	public void setSex(short value) {
		this.sex = value;
	}

	/** 等级 */
	public int getLevel() {
		return level;
	}

	/** 等级 */
	public void setLevel(int value) {
		this.level = value;
	}

	/** VIP等级 */
	public int getVip() {
		return vip;
	}

	/** VIP等级 */
	public void setVip(int value) {
		this.vip = value;
	}

	/** 称号模板 */
	public int getTitleId() {
		return titleId;
	}

	/** 称号模板 */
	public void setTitleId(int value) {
		this.titleId = value;
	}

	/** 形象模板 */
	public int getFashionId() {
		return fashionId;
	}

	/** 形象模板 */
	public void setFashionId(int value) {
		this.fashionId = value;
	}

	public List<ExploitMsg> getExploits() {
		return exploits;
	}
		
	public void addExploits(ExploitMsg value) {
		this.exploits.add(value);
	}
		
	public void addAllExploits(List<ExploitMsg> values) {
		this.exploits.addAll(values);
	}

	/** 玩家头像 */
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	/** 玩家头像 */
	public void setHeadImgUrl(String value) {
		this.headImgUrl = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putLong(buffer, playerId);
		ByteBufferHelper.putString(buffer, name);
		ByteBufferHelper.putShort(buffer, sex);
		ByteBufferHelper.putInt(buffer, level);
		ByteBufferHelper.putInt(buffer, vip);
		ByteBufferHelper.putInt(buffer, titleId);
		ByteBufferHelper.putInt(buffer, fashionId);
		ByteBufferHelper.putObjectArray(buffer, exploits);
		ByteBufferHelper.putString(buffer, headImgUrl);
	}

	public static SimplePlayerMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static SimplePlayerMsg deserialize(ByteBuffer buffer) {
		SimplePlayerMsg messageInstance = new SimplePlayerMsg();
		messageInstance.playerId = ByteBufferHelper.getLong(buffer);
		messageInstance.name = ByteBufferHelper.getString(buffer);
		messageInstance.sex = ByteBufferHelper.getShort(buffer);
		messageInstance.level = ByteBufferHelper.getInt(buffer);
		messageInstance.vip = ByteBufferHelper.getInt(buffer);
		messageInstance.titleId = ByteBufferHelper.getInt(buffer);
		messageInstance.fashionId = ByteBufferHelper.getInt(buffer);
		int exploitsSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < exploitsSize; i++) {
			messageInstance.addExploits(ExploitMsg.deserialize(buffer));
		}
		messageInstance.headImgUrl = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 26;
		length += ByteBufferHelper.calcStringLength(name);
		length += ByteBufferHelper.calcObjectArrayLength(exploits);
		length += ByteBufferHelper.calcStringLength(headImgUrl);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SimplePlayerMsg[");
		sb.append("playerId=" + playerId + ", ");
		sb.append("name=" + name + ", ");
		sb.append("sex=" + sex + ", ");
		sb.append("level=" + level + ", ");
		sb.append("vip=" + vip + ", ");
		sb.append("titleId=" + titleId + ", ");
		sb.append("fashionId=" + fashionId + ", ");
		sb.append("exploits=" + exploits + ", ");
		sb.append("headImgUrl=" + headImgUrl + ", ");
		sb.append("]");
		return sb.toString();
	}
}