package com.game.entity.dao;

import java.util.List;

import com.game.entity.entity.ForcedissolveroomInfo;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface ForcedissolveroomDAO {
	static final String tableName = "t_p_forcedissolveroom";
	static final String keys = "`id`,`roomid`,`gameType`,`createTime`,`roomState`,`gameState`,`roomNum`,`betInfos`,`dissolveTime`,`roomlocation`";
	static final String values = ":1.id,:1.roomid,:1.gameType,:1.createTime,:1.roomState,:1.gameState,:1.roomNum,:1.betInfos,:1.dissolveTime,:1.roomlocation";

	@ReturnGeneratedKeys
	@SQL("insert into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends ForcedissolveroomInfo> long insert(T info);
	
	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends ForcedissolveroomInfo> long insertOrUpdate(T info);
	
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends ForcedissolveroomInfo> int insertOrUpdate(List<T> info);
	
	@SQL("select " + keys + " from `" + tableName + "`")
	List<ForcedissolveroomInfo> getAll();
}
