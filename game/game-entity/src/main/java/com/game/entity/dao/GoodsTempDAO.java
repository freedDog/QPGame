package com.game.entity.dao;

import java.util.List;

import com.game.entity.entity.GoodsTempInfo;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface GoodsTempDAO {
	static final String tableName = "t_s_goods";
	static final String keys = "`TemplateId`,`Name`,`Type`,`Cost`,`Market`,`Desc`,`Icon`,`ShowImage01`,`ShowImage02`,`DetailImage`,`IsExist`,`CreateTime`,`UpTime`,`DownTime`,`Count`";
	static final String values = ":1.TemplateId,:1.Name,:1.Type,:1.Cost,:1.Market,:1.Desc,:1.Icon,:1.ShowImage01,:1.ShowImage02,:1.DetailImage,:1.IsExist,:1.CreateTime,:1.UpTime,:1.DownTime,:1.Count";

	@ReturnGeneratedKeys
	@SQL("insert into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends GoodsTempInfo> long insert(T info);
	
	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends GoodsTempInfo> long insertOrUpdate(T info);
	
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends GoodsTempInfo> int insertOrUpdate(List<T> info);
	
	@SQL("select " + keys + " from `" + tableName + "` where `IsExist`>0")
	List<GoodsTempInfo> getAll();
}