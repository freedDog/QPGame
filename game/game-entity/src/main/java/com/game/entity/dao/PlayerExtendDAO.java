package com.game.entity.dao;

import com.game.entity.entity.PlayerExtendInfo;
import com.game.entity.entity.utils.EntityDAO;
import com.game.framework.jdbc.annotation.DAO;
import com.game.framework.jdbc.annotation.ReturnGeneratedKeys;
import com.game.framework.jdbc.annotation.SQL;

@DAO
public interface PlayerExtendDAO extends EntityDAO<PlayerExtendInfo> {
	static final String tableName = "t_u_playerextend";
	static final String keys = "`PlayerId`,`UGold`,`Snatch`,`FQuan`,`RoomCard`,`UJun`,`LoginKey`,`GlobalMailUpdateTime`,`ShippingAddress`,`ShippingName`,`ShippingPhone`,`TaskTotalComplete`,`TaskTotalDayAward`,`UpdateIP`,`Address`,`Location`,`AILv`,`BrokeAwardCount`,`AllPay`,`NewPlayerAward`,`FirstPayState`,`FirstPay`,`IsPerspective`,`LastShareTime`,`CardName`,`lhdRecord`,`CardTelephone`,`CardNum`,`CardBankName`,`CardId`,`alipayAccount`,`CardBankBranchName`,`alipayAccountName`";
	static final String values = ":1.PlayerId,:1.UGold,:1.Snatch,:1.FQuan,:1.RoomCard,:1.UJun,:1.LoginKey,:1.GlobalMailUpdateTime,:1.ShippingAddress,:1.ShippingName,:1.ShippingPhone,:1.TaskTotalComplete,:1.TaskTotalDayAward,:1.UpdateIP,:1.Address,:1.Location,:1.AILv,:1.BrokeAwardCount,:1.AllPay,:1.NewPlayerAward,:1.FirstPayState,:1.FirstPay,:1.IsPerspective,:1.LastShareTime,:1.CardName,:1.lhdRecord,:1.CardTelephone,:1.CardNum,:1.CardBankName,:1.CardId,:1.alipayAccount,:1.CardBankBranchName,:1.alipayAccountName";

	@ReturnGeneratedKeys
	@SQL("replace into `" + tableName + "` (" + keys + ") values(" + values + ") ")
	<T extends PlayerExtendInfo> long insertOrUpdate(T info);

	@SQL("select " + keys + " from `" + tableName + "` where PlayerId=:1 limit 1")
	PlayerExtendInfo get(long playerId);
}
