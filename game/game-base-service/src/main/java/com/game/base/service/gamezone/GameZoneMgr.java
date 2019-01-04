package com.game.base.service.gamezone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.framework.utils.collections.ArrayUtils;

/**
 * 游戏区服管理(本进程的, 不管其他进程)<br>
 * GameZoneMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午5:14:37
 */
public class GameZoneMgr {
	protected static Map<ModuleName, List<GameZone>> moduleGameZones = new HashMap<>();
	protected static Map<ModuleName, Map<Integer, GameZone>> moduleGameZoneMap = new HashMap<>();

	/** 获取本机区服信息 **/
	public static int[] getGameZoneIdList(ModuleName moduleName) {
		return getGameZoneIds(getGameZoneList(moduleName));
	}

	/** 获取本机区服信息(用GameZone区分合区情况) **/
	public static List<GameZone> getGameZoneList(ModuleName moduleName) {
		return moduleGameZones.get(moduleName);
	}

	/** 获取区服所在gameZone **/
	public static GameZone getGameZone(ModuleName moduleName, int gameZoneId) {
		Map<Integer, GameZone> mmap = moduleGameZoneMap.get(moduleName);
		return (mmap != null) ? mmap.get(gameZoneId) : null;
	}

	/** 是否是本机区服 **/
	public static boolean isLocalGameZone(ModuleName moduleName, GameZone gameZone) {
		List<GameZone> mlist = moduleGameZones.get(moduleName);
		return (mlist != null) ? mlist.contains(gameZone) : false;
	}

	/** 是否是本机玩家Id **/
	public static boolean isLocalPlayerId(ModuleName moduleName, long playerId) {
		return getGameZone(moduleName, Player.getGameZoneId(playerId)) != null;
	}

	/** 是否是本机区服 **/
	public static boolean isLocalGameZone(ModuleName moduleName, int gameZoneId) {
		return getGameZone(moduleName, gameZoneId) != null;
	}

	/** 从gameZone中整理出获取所有区服Id **/
	public static int[] getGameZoneIds(List<GameZone> gameZones) {
		int gsize = (gameZones != null) ? gameZones.size() : 0;
		List<Integer> gameZoneIds = new ArrayList<>();
		for (int i = 0; i < gsize; i++) {
			GameZone gameZone = gameZones.get(i);
			int[] gameZoneIds0 = gameZone.getIds();
			int isize = (gameZoneIds0 != null) ? gameZoneIds0.length : 0;
			for (int j = 0; j < isize; j++) {
				gameZoneIds.add(gameZoneIds0[j]);
			}
		}
		return ArrayUtils.toIntArray(gameZoneIds);
	}

	/** 创建区服列表 **/
	public static List<GameZone> createGameZones(int[][] gameZoneIds) {
		int ggsize = (gameZoneIds != null) ? gameZoneIds.length : 0;
		if (ggsize <= 0) {
			return null; // 这个模块没有设置区服, 是通用模块.
		}
		List<GameZone> retlist = new ArrayList<>();
		// 遍历创建
		for (int i = 0; i < ggsize; i++) {
			// 检测gameZoneId
			for (int[] ids : gameZoneIds) {
				// 创建区服
				GameZone gameZone = new GameZone(ids);
				retlist.add(gameZone);
			}
		}
		return retlist;
	}
}

