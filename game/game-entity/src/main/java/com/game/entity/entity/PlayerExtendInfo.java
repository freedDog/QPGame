package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class PlayerExtendInfo extends EntityObject<PlayerExtendInfo> {
	private long playerId; // 玩家Id
	private int uGold; // uu币
	private int snatch; // 抢宝币
	private int fQuan; // 福券
	private int roomCard; // 房卡
	private int uJun; // uu卷
	private String loginKey; // 登陆key
	private int globalMailUpdateTime; // 全局邮件更新时间
	private String shippingAddress; // 收货地址
	private String shippingName; // 收货人
	private String shippingPhone; // 收货人电话
	private int taskTotalComplete; // 任务累计完成数
	private int taskTotalDayAward; // 每日累计任务奖励领取次数
	private String updateIP; // 玩家登录IP地址
	private String address; // 地址区域信息
	private String location; // 定位坐标
	private int aILv; // AI等级, 只对机器人有用
	private int brokeAwardCount; // 破产补助领取次数
	private int allPay; // 总共充值的金额
	private int newPlayerAward; // 新手礼包
	private int firstPayState; // 首冲状态。
	private int firstPay; // 首冲金额
	private boolean isPerspective; // 是否透视
	private java.util.Date lastShareTime; // 上一次分享时间
	private String cardName; // 银行卡开户人姓名
	private int lhdRecord; // 
	private String cardTelephone; // 银行卡手机号
	private String cardNum; // 银行卡号
	private String cardBankName; // 银行名称
	private String cardId; // 身份证号
	private String alipayAccount; // 阿里支付账户
	private String cardBankBranchName; // 支行名称
	private String alipayAccountName; // 支付宝账户名字

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

	/** 获取uu币 **/
	public int getUGold() {
		return uGold;
	}

	/** 设置uu币 **/
	public void setUGold(int uGold) {
		if (this.uGold == uGold) {
			return;
		}
		this.uGold = uGold;
		this.update();
	}

	/** 获取抢宝币 **/
	public int getSnatch() {
		return snatch;
	}

	/** 设置抢宝币 **/
	public void setSnatch(int snatch) {
		if (this.snatch == snatch) {
			return;
		}
		this.snatch = snatch;
		this.update();
	}

	/** 获取福券 **/
	public int getFQuan() {
		return fQuan;
	}

	/** 设置福券 **/
	public void setFQuan(int fQuan) {
		if (this.fQuan == fQuan) {
			return;
		}
		this.fQuan = fQuan;
		this.update();
	}

	/** 获取房卡 **/
	public int getRoomCard() {
		return roomCard;
	}

	/** 设置房卡 **/
	public void setRoomCard(int roomCard) {
		if (this.roomCard == roomCard) {
			return;
		}
		this.roomCard = roomCard;
		this.update();
	}

	/** 获取uu卷 **/
	public int getUJun() {
		return uJun;
	}

	/** 设置uu卷 **/
	public void setUJun(int uJun) {
		if (this.uJun == uJun) {
			return;
		}
		this.uJun = uJun;
		this.update();
	}

	/** 获取登陆key **/
	public String getLoginKey() {
		return loginKey;
	}

	/** 设置登陆key **/
	public void setLoginKey(String loginKey) {
		if (this.loginKey != null && this.loginKey.equals(loginKey)) {
			return;
		}
		this.loginKey = loginKey;
		this.update();
	}

	/** 获取全局邮件更新时间 **/
	public int getGlobalMailUpdateTime() {
		return globalMailUpdateTime;
	}

	/** 设置全局邮件更新时间 **/
	public void setGlobalMailUpdateTime(int globalMailUpdateTime) {
		if (this.globalMailUpdateTime == globalMailUpdateTime) {
			return;
		}
		this.globalMailUpdateTime = globalMailUpdateTime;
		this.update();
	}

	/** 获取收货地址 **/
	public String getShippingAddress() {
		return shippingAddress;
	}

	/** 设置收货地址 **/
	public void setShippingAddress(String shippingAddress) {
		if (this.shippingAddress != null && this.shippingAddress.equals(shippingAddress)) {
			return;
		}
		this.shippingAddress = shippingAddress;
		this.update();
	}

	/** 获取收货人 **/
	public String getShippingName() {
		return shippingName;
	}

	/** 设置收货人 **/
	public void setShippingName(String shippingName) {
		if (this.shippingName != null && this.shippingName.equals(shippingName)) {
			return;
		}
		this.shippingName = shippingName;
		this.update();
	}

	/** 获取收货人电话 **/
	public String getShippingPhone() {
		return shippingPhone;
	}

	/** 设置收货人电话 **/
	public void setShippingPhone(String shippingPhone) {
		if (this.shippingPhone != null && this.shippingPhone.equals(shippingPhone)) {
			return;
		}
		this.shippingPhone = shippingPhone;
		this.update();
	}

	/** 获取任务累计完成数 **/
	public int getTaskTotalComplete() {
		return taskTotalComplete;
	}

	/** 设置任务累计完成数 **/
	public void setTaskTotalComplete(int taskTotalComplete) {
		if (this.taskTotalComplete == taskTotalComplete) {
			return;
		}
		this.taskTotalComplete = taskTotalComplete;
		this.update();
	}

	/** 获取每日累计任务奖励领取次数 **/
	public int getTaskTotalDayAward() {
		return taskTotalDayAward;
	}

	/** 设置每日累计任务奖励领取次数 **/
	public void setTaskTotalDayAward(int taskTotalDayAward) {
		if (this.taskTotalDayAward == taskTotalDayAward) {
			return;
		}
		this.taskTotalDayAward = taskTotalDayAward;
		this.update();
	}

	/** 获取玩家登录IP地址 **/
	public String getUpdateIP() {
		return updateIP;
	}

	/** 设置玩家登录IP地址 **/
	public void setUpdateIP(String updateIP) {
		if (this.updateIP != null && this.updateIP.equals(updateIP)) {
			return;
		}
		this.updateIP = updateIP;
		this.update();
	}

	/** 获取地址区域信息 **/
	public String getAddress() {
		return address;
	}

	/** 设置地址区域信息 **/
	public void setAddress(String address) {
		if (this.address != null && this.address.equals(address)) {
			return;
		}
		this.address = address;
		this.update();
	}

	/** 获取定位坐标 **/
	public String getLocation() {
		return location;
	}

	/** 设置定位坐标 **/
	public void setLocation(String location) {
		if (this.location != null && this.location.equals(location)) {
			return;
		}
		this.location = location;
		this.update();
	}

	/** 获取AI等级, 只对机器人有用 **/
	public int getAILv() {
		return aILv;
	}

	/** 设置AI等级, 只对机器人有用 **/
	public void setAILv(int aILv) {
		if (this.aILv == aILv) {
			return;
		}
		this.aILv = aILv;
		this.update();
	}

	/** 获取破产补助领取次数 **/
	public int getBrokeAwardCount() {
		return brokeAwardCount;
	}

	/** 设置破产补助领取次数 **/
	public void setBrokeAwardCount(int brokeAwardCount) {
		if (this.brokeAwardCount == brokeAwardCount) {
			return;
		}
		this.brokeAwardCount = brokeAwardCount;
		this.update();
	}

	/** 获取总共充值的金额 **/
	public int getAllPay() {
		return allPay;
	}

	/** 设置总共充值的金额 **/
	public void setAllPay(int allPay) {
		if (this.allPay == allPay) {
			return;
		}
		this.allPay = allPay;
		this.update();
	}

	/** 获取新手礼包 **/
	public int getNewPlayerAward() {
		return newPlayerAward;
	}

	/** 设置新手礼包 **/
	public void setNewPlayerAward(int newPlayerAward) {
		if (this.newPlayerAward == newPlayerAward) {
			return;
		}
		this.newPlayerAward = newPlayerAward;
		this.update();
	}

	/** 获取首冲状态。 **/
	public int getFirstPayState() {
		return firstPayState;
	}

	/** 设置首冲状态。 **/
	public void setFirstPayState(int firstPayState) {
		if (this.firstPayState == firstPayState) {
			return;
		}
		this.firstPayState = firstPayState;
		this.update();
	}

	/** 获取首冲金额 **/
	public int getFirstPay() {
		return firstPay;
	}

	/** 设置首冲金额 **/
	public void setFirstPay(int firstPay) {
		if (this.firstPay == firstPay) {
			return;
		}
		this.firstPay = firstPay;
		this.update();
	}

	/** 获取是否透视 **/
	public boolean getIsPerspective() {
		return isPerspective;
	}

	/** 设置是否透视 **/
	public void setIsPerspective(boolean isPerspective) {
		if (this.isPerspective == isPerspective) {
			return;
		}
		this.isPerspective = isPerspective;
		this.update();
	}

	/** 获取上一次分享时间 **/
	public java.util.Date getLastShareTime() {
		return lastShareTime;
	}

	/** 设置上一次分享时间 **/
	public void setLastShareTime(java.util.Date lastShareTime) {
		if (this.lastShareTime != null && this.lastShareTime.equals(lastShareTime)) {
			return;
		}
		this.lastShareTime = lastShareTime;
		this.update();
	}

	/** 获取银行卡开户人姓名 **/
	public String getCardName() {
		return cardName;
	}

	/** 设置银行卡开户人姓名 **/
	public void setCardName(String cardName) {
		if (this.cardName != null && this.cardName.equals(cardName)) {
			return;
		}
		this.cardName = cardName;
		this.update();
	}

	/** 获取 **/
	public int getLhdRecord() {
		return lhdRecord;
	}

	/** 设置 **/
	public void setLhdRecord(int lhdRecord) {
		if (this.lhdRecord == lhdRecord) {
			return;
		}
		this.lhdRecord = lhdRecord;
		this.update();
	}

	/** 获取银行卡手机号 **/
	public String getCardTelephone() {
		return cardTelephone;
	}

	/** 设置银行卡手机号 **/
	public void setCardTelephone(String cardTelephone) {
		if (this.cardTelephone != null && this.cardTelephone.equals(cardTelephone)) {
			return;
		}
		this.cardTelephone = cardTelephone;
		this.update();
	}

	/** 获取银行卡号 **/
	public String getCardNum() {
		return cardNum;
	}

	/** 设置银行卡号 **/
	public void setCardNum(String cardNum) {
		if (this.cardNum != null && this.cardNum.equals(cardNum)) {
			return;
		}
		this.cardNum = cardNum;
		this.update();
	}

	/** 获取银行名称 **/
	public String getCardBankName() {
		return cardBankName;
	}

	/** 设置银行名称 **/
	public void setCardBankName(String cardBankName) {
		if (this.cardBankName != null && this.cardBankName.equals(cardBankName)) {
			return;
		}
		this.cardBankName = cardBankName;
		this.update();
	}

	/** 获取身份证号 **/
	public String getCardId() {
		return cardId;
	}

	/** 设置身份证号 **/
	public void setCardId(String cardId) {
		if (this.cardId != null && this.cardId.equals(cardId)) {
			return;
		}
		this.cardId = cardId;
		this.update();
	}

	/** 获取阿里支付账户 **/
	public String getAlipayAccount() {
		return alipayAccount;
	}

	/** 设置阿里支付账户 **/
	public void setAlipayAccount(String alipayAccount) {
		if (this.alipayAccount != null && this.alipayAccount.equals(alipayAccount)) {
			return;
		}
		this.alipayAccount = alipayAccount;
		this.update();
	}

	/** 获取支行名称 **/
	public String getCardBankBranchName() {
		return cardBankBranchName;
	}

	/** 设置支行名称 **/
	public void setCardBankBranchName(String cardBankBranchName) {
		if (this.cardBankBranchName != null && this.cardBankBranchName.equals(cardBankBranchName)) {
			return;
		}
		this.cardBankBranchName = cardBankBranchName;
		this.update();
	}

	/** 获取支付宝账户名字 **/
	public String getAlipayAccountName() {
		return alipayAccountName;
	}

	/** 设置支付宝账户名字 **/
	public void setAlipayAccountName(String alipayAccountName) {
		if (this.alipayAccountName != null && this.alipayAccountName.equals(alipayAccountName)) {
			return;
		}
		this.alipayAccountName = alipayAccountName;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("PlayerExtendInfo[");
		strBdr.append("playerId=").append(playerId);
		strBdr.append(",");
		strBdr.append("uGold=").append(uGold);
		strBdr.append(",");
		strBdr.append("snatch=").append(snatch);
		strBdr.append(",");
		strBdr.append("fQuan=").append(fQuan);
		strBdr.append(",");
		strBdr.append("roomCard=").append(roomCard);
		strBdr.append(",");
		strBdr.append("uJun=").append(uJun);
		strBdr.append(",");
		strBdr.append("loginKey=").append(loginKey);
		strBdr.append(",");
		strBdr.append("globalMailUpdateTime=").append(globalMailUpdateTime);
		strBdr.append(",");
		strBdr.append("shippingAddress=").append(shippingAddress);
		strBdr.append(",");
		strBdr.append("shippingName=").append(shippingName);
		strBdr.append(",");
		strBdr.append("shippingPhone=").append(shippingPhone);
		strBdr.append(",");
		strBdr.append("taskTotalComplete=").append(taskTotalComplete);
		strBdr.append(",");
		strBdr.append("taskTotalDayAward=").append(taskTotalDayAward);
		strBdr.append(",");
		strBdr.append("updateIP=").append(updateIP);
		strBdr.append(",");
		strBdr.append("address=").append(address);
		strBdr.append(",");
		strBdr.append("location=").append(location);
		strBdr.append(",");
		strBdr.append("aILv=").append(aILv);
		strBdr.append(",");
		strBdr.append("brokeAwardCount=").append(brokeAwardCount);
		strBdr.append(",");
		strBdr.append("allPay=").append(allPay);
		strBdr.append(",");
		strBdr.append("newPlayerAward=").append(newPlayerAward);
		strBdr.append(",");
		strBdr.append("firstPayState=").append(firstPayState);
		strBdr.append(",");
		strBdr.append("firstPay=").append(firstPay);
		strBdr.append(",");
		strBdr.append("isPerspective=").append(isPerspective);
		strBdr.append(",");
		strBdr.append("lastShareTime=").append(lastShareTime);
		strBdr.append(",");
		strBdr.append("cardName=").append(cardName);
		strBdr.append(",");
		strBdr.append("lhdRecord=").append(lhdRecord);
		strBdr.append(",");
		strBdr.append("cardTelephone=").append(cardTelephone);
		strBdr.append(",");
		strBdr.append("cardNum=").append(cardNum);
		strBdr.append(",");
		strBdr.append("cardBankName=").append(cardBankName);
		strBdr.append(",");
		strBdr.append("cardId=").append(cardId);
		strBdr.append(",");
		strBdr.append("alipayAccount=").append(alipayAccount);
		strBdr.append(",");
		strBdr.append("cardBankBranchName=").append(cardBankBranchName);
		strBdr.append(",");
		strBdr.append("alipayAccountName=").append(alipayAccountName);
		strBdr.append("]");
		return strBdr.toString();
	}
}