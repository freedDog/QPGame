package com.game.entity.dao;

import com.game.entity.entity.UserInfo;
import com.game.entity.entity.utils.EntityDAO;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface UserDAO extends EntityDAO<UserInfo> {
	static final String tableName = "t_u_user";
	static final String keys = "`UserId`,`GameZoneId`,`Account`,`Platform`,`BanTime`,`CreateTime`,`MobilePhone`";
	static final String values = ":1.UserId,:1.GameZoneId,:1.Account,:1.Platform,:1.BanTime,:1.CreateTime,:1.MobilePhone";

	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends UserInfo> long insertOrUpdate(T info);

	@SQL("select " + keys + " from `" + tableName + "` where UserId=:1 limit 1")
	UserInfo get(long userId);
}
