package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class MailInfo extends EntityObject<MailInfo> {
	private long playerId; // 玩家Id
	private long id; // 邮件Id
	private String title; // 标题
	private String content; // 内容
	private String attachment; // 附件
	private short getNum; // 提取或读阅次数
	private short state; // 状态. 1正常, 0删除, 2领取附件.
	private short sourceType; // 邮件来源
	private java.util.Date createTime; // 创建时间
	private java.util.Date endTime; // 过期时间
	private java.util.Date getTime; // 提取或读取时间

	/** 获取玩家Id **/
	public long getPlayerId() {
		return playerId;
	}

	/** 设置玩家Id **/
	public void setPlayerId(long playerId) {
		if (this.playerId == playerId) {
			return;
		}
		this.playerId = playerId;
		this.update();
	}

	/** 获取邮件Id **/
	public long getId() {
		return id;
	}

	/** 设置邮件Id **/
	public void setId(long id) {
		if (this.id == id) {
			return;
		}
		this.id = id;
		this.update();
	}

	/** 获取标题 **/
	public String getTitle() {
		return title;
	}

	/** 设置标题 **/
	public void setTitle(String title) {
		if (this.title != null && this.title.equals(title)) {
			return;
		}
		this.title = title;
		this.update();
	}

	/** 获取内容 **/
	public String getContent() {
		return content;
	}

	/** 设置内容 **/
	public void setContent(String content) {
		if (this.content != null && this.content.equals(content)) {
			return;
		}
		this.content = content;
		this.update();
	}

	/** 获取附件 **/
	public String getAttachment() {
		return attachment;
	}

	/** 设置附件 **/
	public void setAttachment(String attachment) {
		if (this.attachment != null && this.attachment.equals(attachment)) {
			return;
		}
		this.attachment = attachment;
		this.update();
	}

	/** 获取提取或读阅次数 **/
	public short getGetNum() {
		return getNum;
	}

	/** 设置提取或读阅次数 **/
	public void setGetNum(short getNum) {
		if (this.getNum == getNum) {
			return;
		}
		this.getNum = getNum;
		this.update();
	}

	/** 获取状态. 1正常, 0删除, 2领取附件. **/
	public short getState() {
		return state;
	}

	/** 设置状态. 1正常, 0删除, 2领取附件. **/
	public void setState(short state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
		this.update();
	}

	/** 获取邮件来源 **/
	public short getSourceType() {
		return sourceType;
	}

	/** 设置邮件来源 **/
	public void setSourceType(short sourceType) {
		if (this.sourceType == sourceType) {
			return;
		}
		this.sourceType = sourceType;
		this.update();
	}

	/** 获取创建时间 **/
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/** 设置创建时间 **/
	public void setCreateTime(java.util.Date createTime) {
		if (this.createTime != null && this.createTime.equals(createTime)) {
			return;
		}
		this.createTime = createTime;
		this.update();
	}

	/** 获取过期时间 **/
	public java.util.Date getEndTime() {
		return endTime;
	}

	/** 设置过期时间 **/
	public void setEndTime(java.util.Date endTime) {
		if (this.endTime != null && this.endTime.equals(endTime)) {
			return;
		}
		this.endTime = endTime;
		this.update();
	}

	/** 获取提取或读取时间 **/
	public java.util.Date getGetTime() {
		return getTime;
	}

	/** 设置提取或读取时间 **/
	public void setGetTime(java.util.Date getTime) {
		if (this.getTime != null && this.getTime.equals(getTime)) {
			return;
		}
		this.getTime = getTime;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("MailInfo[");
		strBdr.append("playerId=").append(playerId);
		strBdr.append(",");
		strBdr.append("id=").append(id);
		strBdr.append(",");
		strBdr.append("title=").append(title);
		strBdr.append(",");
		strBdr.append("content=").append(content);
		strBdr.append(",");
		strBdr.append("attachment=").append(attachment);
		strBdr.append(",");
		strBdr.append("getNum=").append(getNum);
		strBdr.append(",");
		strBdr.append("state=").append(state);
		strBdr.append(",");
		strBdr.append("sourceType=").append(sourceType);
		strBdr.append(",");
		strBdr.append("createTime=").append(createTime);
		strBdr.append(",");
		strBdr.append("endTime=").append(endTime);
		strBdr.append(",");
		strBdr.append("getTime=").append(getTime);
		strBdr.append("]");
		return strBdr.toString();
	}
}
