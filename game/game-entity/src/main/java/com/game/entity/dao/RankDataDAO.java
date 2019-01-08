package com.game.entity.dao;

import com.game.entity.entity.RankDataInfo;
import com.game.entity.entity.utils.EntityDAO;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface RankDataDAO extends EntityDAO<RankDataInfo> {
	static final String tableName = "t_u_rankdata";
	static final String keys = "`PlayerId`,`Diamond`,`Fquan`,`RoomCard`,`UpdateTime`";
	static final String values = ":1.PlayerId,:1.Diamond,:1.Fquan,:1.RoomCard,:1.UpdateTime";

	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends RankDataInfo> long insertOrUpdate(T info);

	@SQL("select " + keys + " from `" + tableName + "` where PlayerId=:1 limit 1")
	RankDataInfo get(long playerId);
}