package com.game.framework.component.change;

import com.game.framework.utils.struct.result.IResult;

/**
 * 修改器接口
 * IChanger.java
 * @author JiangBangMing
 * 2019年1月3日下午1:04:43
 */
public interface IChanger <D extends ChangeUtils.ChangeData,R extends IResult>{
	/** 获取数据 **/
	long get(D data, int id);

	/** 设置数据(保存) **/
	R set(D data, int id, long preValue, long setValue, long change);

	/** 最大值 **/
	long max(D data, int id);

	/** 最小值 **/
	long min(D data, int id);

	/** 改变触发, set成功后.(在锁定中) **/
	void onChange(D data, int id, long preValue, long setValue, long change);

	/** 修改完成 **/
	void onChangeComplete(D data, int id, long preValue, long setValue, long change);

	/** 修改开始, 用于提前判断, 返回false直接失败. **/
	R onChangeStart(D data, int id, long change);

	/** 生成结果 **/
	R result(int code, D data, int id, long change, long prev, long now, long resultChange);
}
