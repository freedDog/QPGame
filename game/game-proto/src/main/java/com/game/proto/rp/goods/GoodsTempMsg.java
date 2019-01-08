package com.game.proto.rp.goods;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class GoodsTempMsg extends RpMessage {
	// 实物Id
	private int templateId;
	// 实物名称
	private String name;
	// 库存
	private int count;
	// 实物类型
	private int type;
	// 说明
	private String desc;
	// 图标
	private String icon;
	// 详情图
	private String detailImage;
	// 展示图01
	private String showImage01;
	// 展示图02
	private String showImage02;

	/** 实物Id */
	public int getTemplateId() {
		return templateId;
	}

	/** 实物Id */
	public void setTemplateId(int value) {
		this.templateId = value;
	}

	/** 实物名称 */
	public String getName() {
		return name;
	}

	/** 实物名称 */
	public void setName(String value) {
		this.name = value;
	}

	/** 库存 */
	public int getCount() {
		return count;
	}

	/** 库存 */
	public void setCount(int value) {
		this.count = value;
	}

	/** 实物类型 */
	public int getType() {
		return type;
	}

	/** 实物类型 */
	public void setType(int value) {
		this.type = value;
	}

	/** 说明 */
	public String getDesc() {
		return desc;
	}

	/** 说明 */
	public void setDesc(String value) {
		this.desc = value;
	}

	/** 图标 */
	public String getIcon() {
		return icon;
	}

	/** 图标 */
	public void setIcon(String value) {
		this.icon = value;
	}

	/** 详情图 */
	public String getDetailImage() {
		return detailImage;
	}

	/** 详情图 */
	public void setDetailImage(String value) {
		this.detailImage = value;
	}

	/** 展示图01 */
	public String getShowImage01() {
		return showImage01;
	}

	/** 展示图01 */
	public void setShowImage01(String value) {
		this.showImage01 = value;
	}

	/** 展示图02 */
	public String getShowImage02() {
		return showImage02;
	}

	/** 展示图02 */
	public void setShowImage02(String value) {
		this.showImage02 = value;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putInt(buffer, templateId);
		ByteBufferHelper.putString(buffer, name);
		ByteBufferHelper.putInt(buffer, count);
		ByteBufferHelper.putInt(buffer, type);
		ByteBufferHelper.putString(buffer, desc);
		ByteBufferHelper.putString(buffer, icon);
		ByteBufferHelper.putString(buffer, detailImage);
		ByteBufferHelper.putString(buffer, showImage01);
		ByteBufferHelper.putString(buffer, showImage02);
	}

	public static GoodsTempMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static GoodsTempMsg deserialize(ByteBuffer buffer) {
		GoodsTempMsg messageInstance = new GoodsTempMsg();
		messageInstance.templateId = ByteBufferHelper.getInt(buffer);
		messageInstance.name = ByteBufferHelper.getString(buffer);
		messageInstance.count = ByteBufferHelper.getInt(buffer);
		messageInstance.type = ByteBufferHelper.getInt(buffer);
		messageInstance.desc = ByteBufferHelper.getString(buffer);
		messageInstance.icon = ByteBufferHelper.getString(buffer);
		messageInstance.detailImage = ByteBufferHelper.getString(buffer);
		messageInstance.showImage01 = ByteBufferHelper.getString(buffer);
		messageInstance.showImage02 = ByteBufferHelper.getString(buffer);
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 12;
		length += ByteBufferHelper.calcStringLength(name);
		length += ByteBufferHelper.calcStringLength(desc);
		length += ByteBufferHelper.calcStringLength(icon);
		length += ByteBufferHelper.calcStringLength(detailImage);
		length += ByteBufferHelper.calcStringLength(showImage01);
		length += ByteBufferHelper.calcStringLength(showImage02);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoodsTempMsg[");
		sb.append("templateId=" + templateId + ", ");
		sb.append("name=" + name + ", ");
		sb.append("count=" + count + ", ");
		sb.append("type=" + type + ", ");
		sb.append("desc=" + desc + ", ");
		sb.append("icon=" + icon + ", ");
		sb.append("detailImage=" + detailImage + ", ");
		sb.append("showImage01=" + showImage01 + ", ");
		sb.append("showImage02=" + showImage02 + ", ");
		sb.append("]");
		return sb.toString();
	}
}