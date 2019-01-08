package com.game.base.service.mgr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.base.service.constant.GameType;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.db.RedisMgr;
import com.game.entity.dao.RobotIncomeExpenseDAO;
import com.game.entity.entity.RobotIncomeExpenseInfo;
import com.game.framework.component.log.Log;

import redis.clients.jedis.ShardedJedis;

/**
 * 机器人收入支出管理器
 * RobotIncomeExpenseMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午5:01:34
 */
public class RobotIncomeExpenseMgr extends SharedCounterMgr{
	
	private static final String redisKey_income = "robot_income";
	private static final String redisKey_expense = "robot_expense";
	
	private static List<RobotIncomeExpenseInfo> infos = new ArrayList<>(); 
	
	public static boolean init() {
		return reload();
	}
	
	/** 重新加载全局配置 **/
	protected static boolean reload() {
//		Log.info("机器人收入支出管理器reload");
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 加载全局配置
			RobotIncomeExpenseDAO dao = DaoMgr.getInstance().getDao(RobotIncomeExpenseDAO.class);
			List<RobotIncomeExpenseInfo> list = dao.getAll();
			if (list == null || list.isEmpty()) {
				list = new ArrayList<>();
				for(int gameType : GameType.gameTypes) {
					RobotIncomeExpenseInfo info = new RobotIncomeExpenseInfo();
					info.setUpdatetime(new Date());
					info.setGametype(gameType);
					long id = dao.insert(info);
					info.setId(id);
					list.add(info);
				}
			}
			infos = list;
			
			clearAll(redisKey_income);
			clearAll(redisKey_expense);
			
			// 设置到redis
			for (RobotIncomeExpenseInfo tempInfo : list) {
				change(redisKey_income, tempInfo.getGametype(), tempInfo.getIncome());
				change(redisKey_expense, tempInfo.getGametype(), tempInfo.getExpense());
			}
		} catch (Exception e) {
			Log.error("全局配置写入失败", e);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}
	
	public static boolean changeIncomeAndExpense(int gameType, long income, long expense) {
		if (income != 0)
			change(redisKey_income, gameType, income);
		if (expense != 0)
			change(redisKey_expense, gameType, expense);
//		Log.info("type:" + gameType + ", income:" + get(redisKey_income, gameType) + ", expense:" + get(redisKey_expense, gameType));
		return true;
	}
	
	
	public static long getIncomeCount(int gameType) {
		return get(redisKey_income, gameType);
	}
	
	public static long getExpenseCount(int gameType) {
		return get(redisKey_expense, gameType);
	}
	
	
	/** 实物数据保存 **/
	public static boolean save() {
//		Log.info("保存机器人收支信息");
		// 更新实物库存
		ShardedJedis jedis = null;
		List<RobotIncomeExpenseInfo> updates = new ArrayList<>();
		try {
			jedis = RedisMgr.getJedis();
			
			// 从redis读取数据，判断是否修改sql中的数据
			for (RobotIncomeExpenseInfo tempInfo : infos) {
				// 读取数量
				long income = getIncomeCount(tempInfo.getGametype());
				long expense = getExpenseCount(tempInfo.getGametype());
				
				tempInfo.setExpense(expense);
				tempInfo.setIncome(income);
				
				// 判断是否修改数据了
				if (!tempInfo.isUpdate()) {
					continue; // 没变化
				}
				Log.info("tempInfo:" + tempInfo);
				tempInfo.setUpdatetime(new Date());
				updates.add(tempInfo);
				tempInfo.commit();
			}
		} catch (Exception e) {
			Log.error("RobotIncomeExpenseMgr保存错误! " + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		// 执行保存
		int usize = (updates != null) ? updates.size() : 0;
		if (usize > 0) {
			RobotIncomeExpenseDAO dao = DaoMgr.getInstance().getDao(RobotIncomeExpenseDAO.class);
			dao.insertOrUpdate(updates);
		}
		return true;
	}
	
	public void destroy() {
		Log.info("关闭服务器,保存机器人收支信息");
		save();
	}
	
}
