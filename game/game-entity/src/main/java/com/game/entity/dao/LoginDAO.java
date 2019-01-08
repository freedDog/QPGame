package com.game.entity.dao;

import com.game.entity.entity.PlayerInfo;
import com.game.entity.entity.UserInfo;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface LoginDAO {
	@SQL("select " + UserDAO.keys + " from `" + UserDAO.tableName + "` where `Account`=:1 and Platform=:2 and GameZoneId=:3 limit 1")
	UserInfo getUser(String account, String platform, int gameZoneId);

	@SQL("select " + PlayerDAO.keys + " from `" + PlayerDAO.tableName + "` where UserId=:1 limit 1")
	PlayerInfo getPlayerByUserId(long userId);

	/** 根据玩家名字模糊搜索玩家 **/
	@SQL("select " + PlayerDAO.keys + " from `" + PlayerDAO.tableName + "` where `Name` like :1 limit 1")
	PlayerInfo getPlayerByName(String name);

}
