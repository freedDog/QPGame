package com.game.entity.dao;

import java.util.List;

import com.game.entity.entity.RobotIncomeExpenseInfo;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface RobotIncomeExpenseDAO {
	static final String tableName = "t_p_robotincomeexpense";
	static final String keys = "`id`,`gametype`,`income`,`expense`,`updatetime`";
	static final String values = ":1.id,:1.gametype,:1.income,:1.expense,:1.updatetime";

	@ReturnGeneratedKeys
	@SQL("insert into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends RobotIncomeExpenseInfo> long insert(T info);
	
	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends RobotIncomeExpenseInfo> long insertOrUpdate(T info);
	
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends RobotIncomeExpenseInfo> int insertOrUpdate(List<T> info);
	
	@SQL("select " + keys + " from `" + tableName + "`")
	List<RobotIncomeExpenseInfo> getAll();
}