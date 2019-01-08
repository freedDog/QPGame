package com.game.entity.entity;

import com.game.entity.entity.utils.EntityObject;

public class RobotIncomeExpenseInfo extends EntityObject<RobotIncomeExpenseInfo> {
	private long id; // 
	private int gametype; // 游戏类型
	private long income; // 收入总额
	private long expense; // 支出总额
	private java.util.Date updatetime; // 更新时间

	/** 获取 **/
	public long getId() {
		return id;
	}

	/** 设置 **/
	public void setId(long id) {
		if (this.id == id) {
			return;
		}
		this.id = id;
		this.update();
	}

	/** 获取游戏类型 **/
	public int getGametype() {
		return gametype;
	}

	/** 设置游戏类型 **/
	public void setGametype(int gametype) {
		if (this.gametype == gametype) {
			return;
		}
		this.gametype = gametype;
		this.update();
	}

	/** 获取收入总额 **/
	public long getIncome() {
		return income;
	}

	/** 设置收入总额 **/
	public void setIncome(long income) {
		if (this.income == income) {
			return;
		}
		this.income = income;
		this.update();
	}

	/** 获取支出总额 **/
	public long getExpense() {
		return expense;
	}

	/** 设置支出总额 **/
	public void setExpense(long expense) {
		if (this.expense == expense) {
			return;
		}
		this.expense = expense;
		this.update();
	}

	/** 获取更新时间 **/
	public java.util.Date getUpdatetime() {
		return updatetime;
	}

	/** 设置更新时间 **/
	public void setUpdatetime(java.util.Date updatetime) {
		if (this.updatetime != null && this.updatetime.equals(updatetime)) {
			return;
		}
		this.updatetime = updatetime;
		this.update();
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("RobotIncomeExpenseInfo[");
		strBdr.append("id=").append(id);
		strBdr.append(",");
		strBdr.append("gametype=").append(gametype);
		strBdr.append(",");
		strBdr.append("income=").append(income);
		strBdr.append(",");
		strBdr.append("expense=").append(expense);
		strBdr.append(",");
		strBdr.append("updatetime=").append(updatetime);
		strBdr.append("]");
		return strBdr.toString();
	}
}