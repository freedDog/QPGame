package com.game.framework.component.change;

import com.game.framework.utils.struct.result.IResult;

/**
 * 数值修改器
 * Changer.java
 * @author JiangBangMing
 * 2019年1月3日上午10:30:52
 */
public abstract class Changer<D extends ChangeUtils.ChangeData,R extends IResult> implements IChanger<D, R>{

	public long max(D data,int id) {
		return Long.MAX_VALUE;
	}
	
	public long min(D data,int id) {
		return 0L;
	}
	
	public void onChange(D data,int id,long preValue,long setValue,long change) {
		
	}
	
	public void onChangeComplete(D data,int id,long preValue,long setValue,long change) {
		
	}
	
	public R onChangeStart(D data,int id,long change) {
		return result(1, data, id, change, 0L,0L,0L);
	}
	
}
