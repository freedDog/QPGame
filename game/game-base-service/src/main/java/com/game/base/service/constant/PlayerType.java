package com.game.base.service.constant;

/**
 * 玩家类型<br>
 * 小于0的为机器人不能登录.
 * PlayerType.java
 * @author JiangBangMing
 * 2019年1月4日下午5:13:33
 */
public class PlayerType {

	/** 普通玩家 **/
	public final static int NORMAL = 1;

	/** 废弃 **/
	public final static int DISCARD = 0;

	/** 机器人 **/
	public final static int ROBOT = -1;

}