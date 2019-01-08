package com.game.entity.dao;

import java.util.List;

import com.game.entity.entity.GoodsInfo;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface GoodsUtilsDAO extends GoodsDAO {

	/** 根据订单号选取 **/
	@SQL("select " + GoodsDAO.keys + " from `" + GoodsDAO.tableName + "` where Id=:1 ")
	GoodsInfo getOne(long id);

	/** 选取所有订单 **/
	@SQL("select " + GoodsDAO.keys + " from `" + GoodsDAO.tableName + "`")
	List<GoodsInfo> getAll();

	/** 根据订单状态选取 **/
	@SQL("select " + GoodsDAO.keys + " from `" + GoodsDAO.tableName + "`where State=:1")
	List<GoodsInfo> getState(int state);

	@SQL("delete from `" + GoodsDAO.tableName + "`where Id=:1")
	boolean remove(long id);

	/** 根据玩家id选取 **/
	@SQL("select " + keys + " from `" + GoodsDAO.tableName + "` where PlayerId=:1  and State>=0")
	List<GoodsInfo> get(long playerId);

	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	int insertOrUpdate(List<GoodsInfo> infos);

	@SQL("update t_s_goods set Count=:2 where TemplateId=:1")
	int changeGoodsCount(int templateId, int num);
}
