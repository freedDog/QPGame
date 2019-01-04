package com.game.framework.component.change;

import com.game.framework.utils.struct.result.IResult;

/**
 * 修改结果
 * IChangeResult.java
 * @author JiangBangMing
 * 2019年1月3日上午10:28:09
 */
public interface IChangeResult extends IResult {

	/** 修改失败 **/
	public static final int NOCHANGE = -1;
	/** 不足 **/
	public static final int NOTENOUGH = -2;
	/** 太多 **/
	public static final int TOOMUCH = -3;
	/** 修改失败 **/
	public static final int SETERROR = -4;
	/** 未知Id **/
	public static final int IDERROR = -5;
	/** 空间不足 **/
	public static final int NOSPACE = -6;
	/** 找不到对象 **/
	public static final int NOOBJ = -7;

}
