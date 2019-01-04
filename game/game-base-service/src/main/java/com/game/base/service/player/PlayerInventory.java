package com.game.base.service.player;

import com.game.base.service.inventory.GameEntityInventory;

/**
 * 数据存活<br>
 * 继承以前的用法, 这里只负责处理逻辑.<br>
 * save先不管, 还没定怎么用.
 * PlayerInventory.java
 * @author JiangBangMing
 * 2019年1月4日下午5:07:30
 */
public abstract class PlayerInventory<P extends Player> extends GameEntityInventory {
	protected P player;

	protected PlayerInventory(P player) {
		this.player = player;
	}

	public P getPlayer() {
		return player;
	}

	public long getPlayerId() {
		return (player != null) ? player.getPlayerId() : 0L;
	}

	/** 登陆触发 **/
	protected void onLogin() {
	}

	/** 下线 **/
	protected void onLogout() {

	}
}
