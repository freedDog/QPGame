package com.game.entity.dao;

import java.util.Collection;
import java.util.List;

import com.game.entity.entity.GoodsInfo;
import com.game.entity.entity.utils.EntityDAO;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface GoodsDAO extends EntityDAO<GoodsInfo> {
	static final String tableName = "t_u_goods";
	static final String keys = "`PlayerId`,`Id`,`PlayerName`,`TemplateId`,`Icon`,`TemplateName`,`Count`,`Address`,`Name`,`Phone`,`Expend`,`State`,`StartTime`,`AckTime`,`SendTime`,`DoneTime`,`TrackingNumber`,`ShowTime`,`OrderType`,`TrackingName`,`LuckNumber`,`ChangeTime`,`OrderSourceId`,`Desc`,`HistoryDesc`,`OrderDesc`";
	static final String values = ":1.PlayerId,:1.Id,:1.PlayerName,:1.TemplateId,:1.Icon,:1.TemplateName,:1.Count,:1.Address,:1.Name,:1.Phone,:1.Expend,:1.State,:1.StartTime,:1.AckTime,:1.SendTime,:1.DoneTime,:1.TrackingNumber,:1.ShowTime,:1.OrderType,:1.TrackingName,:1.LuckNumber,:1.ChangeTime,:1.OrderSourceId,:1.Desc,:1.HistoryDesc,:1.OrderDesc";

	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends GoodsInfo> long insertOrUpdate(T info);

	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends GoodsInfo> int insertOrUpdate(Collection<T> infos);

	@SQL("select " + keys + " from `" + tableName + "` where PlayerId=:1 ")
	List<GoodsInfo> get(long playerId);
}
