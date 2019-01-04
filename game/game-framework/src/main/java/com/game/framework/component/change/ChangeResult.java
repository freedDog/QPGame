package com.game.framework.component.change;

import com.game.framework.utils.struct.result.Result;

/**
 * 改变结果
 * ChangeResult.java
 * @author JiangBangMing
 * 2019年1月3日上午10:34:59
 */
public class ChangeResult extends Result implements IChangeResult{
	//修改值
	protected long change;
	
	public long getChange() {
		return this.change;
	}
	
	public void setChange(long change) {
		this.change=change;
	}
	
	@Override
	public String toString() {
		return "ChangeResult [change="+this.change+", code="+this.code+", msg="+this.msg+"]";
	}
	
	public static ChangeResult create(int code,String msg,long change) {
		ChangeResult result=new ChangeResult();
		result.setCode(code);
		result.setMsg(msg);
		result.setChange(change);
		return result;
	}
	
	public static ChangeResult error(String msg) {
		ChangeResult result=new ChangeResult();
		result.setCode(ERROR);
		result.setMsg(msg);
		return result;
	}
	
	public static ChangeResult succeed() {
		return succeed(0L);
	}
	public static ChangeResult succeed(long change) {
		ChangeResult result=new ChangeResult();
		result.setCode(SUCCESS);
		result.setChange(change);
		return result;
	}
	
}


