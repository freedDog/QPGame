package com.game.entity.dao;

import com.game.entity.entity.PlayerInfo;
import com.game.entity.entity.utils.EntityDAO;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface PlayerDAO extends EntityDAO<PlayerInfo> {
	static final String tableName = "t_u_player";
	static final String keys = "`PlayerId`,`UserId`,`Name`,`Gold`,`Point`,`Sex`,`Exp`,`Level`,`Type`,`OnlineState`,`CreateTime`,`UpdateTime`,`LoginTime`,`LogoutTime`,`FashionId`,`VipLv`,`HeadImgUrl`,`CreateIP`,`ProxyId`,`AgencyNumber`,`rankScore`,`safeBoxPwd`,`isSuperAccount`,`isAgency`";
	static final String values = ":1.PlayerId,:1.UserId,:1.Name,:1.Gold,:1.Point,:1.Sex,:1.Exp,:1.Level,:1.Type,:1.OnlineState,:1.CreateTime,:1.UpdateTime,:1.LoginTime,:1.LogoutTime,:1.FashionId,:1.VipLv,:1.HeadImgUrl,:1.CreateIP,:1.ProxyId,:1.AgencyNumber,:1.rankScore,:1.safeBoxPwd,:1.isSuperAccount,:1.isAgency";

	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends PlayerInfo> long insertOrUpdate(T info);

	@SQL("select " + keys + " from `" + tableName + "` where PlayerId=:1 limit 1")
	PlayerInfo get(long playerId);
}
