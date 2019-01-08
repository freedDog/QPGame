package com.game.entity.dao;

import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface UniqueIdDAO
{
	@SQL("CALL get_global_unique_id(:1,:2,:3,:4);")
	long get(int type, int gameZoneId, long addValue, long defualtValue);
}

