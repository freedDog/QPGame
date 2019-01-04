package com.game.framework.component.change;

import com.game.framework.utils.struct.result.IResult;

/**
 * 数据修改工具(货币修改)<br>
 * 能控制数据修改访问, 增加或者删除<br>
 * 事件话处理修改过程中各个情况<br>
 * 控制数据修改范围, 防止产生错误, 并且支持锁定同步线程安全<br>
 * ChangeUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:03:55
 */
public final class ChangeUtils {

	/** 数据修改对象 **/
	public static class ChangeData {

	}

	/**
	 * 检测是否能修改<br>
	 * -1 修改为0, -2对象获取不到, -3不足, -4超出
	 * **/
	public static <D extends ChangeData, R extends IResult> R check(D data, int id, long change, Object lock, IChanger<D, R> changer) {
		// 判断是否有改变
		if (change == 0) {
			return changer.result(IChangeResult.NOCHANGE, data, id, change, 0, 0, 0);
		}

		// 修改开始, 如果有其他特殊处理, 跳过了则不执行下去.
		R result = changer.onChangeStart(data, id, change);
		if (!result.isSucceed()) {
			return result;
		}

		long nowValue = 0; // 当前值
		long setValue = 0; // 修改值
		synchronized (lock) {
			nowValue = changer.get(data, id);
			setValue = nowValue + change; // 修改值
			// 判断是删除还是增加
			if (change < 0) {
				// 减少
				if (setValue < changer.min(data, id)) {
					return changer.result(IChangeResult.NOTENOUGH, data, id, change, nowValue, setValue, 0);
				}
			} else {
				// 增加
				if (setValue > changer.max(data, id)) {
					return changer.result(IChangeResult.TOOMUCH, data, id, change, nowValue, setValue, 0);
				}
			}
		}
		return changer.result(IChangeResult.SUCCESS, data, id, change, nowValue, setValue, 0);
	}

	/**
	 * 修改数据<br>
	 * -1 修改为0, -2对象获取不到, -3不足, -4超出
	 * 
	 * @param change
	 *            变化值
	 * @param lock
	 *            锁定对象
	 * @param changer
	 *            修改器
	 * **/
	public static <D extends ChangeData, R extends IResult> R change(D data, int id, long change, Object lock, IChanger<D, R> changer) {
		// 判断是否有改变
		if (change == 0) {
			return changer.result(IChangeResult.NOCHANGE, data, id, change, 0, 0, 0);
		}

		// 修改开始, 如果有其他特殊处理, 跳过了则不执行下去.
		R result = changer.onChangeStart(data, id, change);
		if (!result.isSucceed()) {
			return result;
		}

		long nowValue = 0; // 当前值
		long setValue = 0; // 修改值
		synchronized (lock) {
			nowValue = changer.get(data, id);
			setValue = nowValue + change; // 修改值
			// 判断是删除还是增加
			if (change < 0) {
				// 减少
				if (setValue < changer.min(data, id)) {
					return changer.result(IChangeResult.NOTENOUGH, data, id, change, nowValue, setValue, 0);
				}
			} else {
				// 增加
				if (setValue > changer.max(data, id)) {
					return changer.result(IChangeResult.TOOMUCH, data, id, change, nowValue, setValue, 0);
				}
			}

			// 判断是否有修改
			result = changer.set(data, id, nowValue, setValue, change);
			if (!result.isSucceed()) {
				return result;
			}
			// 修改成功后重新获取一次数据, 可能在设置时产生调整.
			// setValue = changer.get(data, id);
			// long resultChange = setValue - nowValue; // 这个还是保留原本的数量即可.

			// 修改触发
			changer.onChange(data, id, nowValue, setValue, change);
		}
		// 修改完成
		changer.onChangeComplete(data, id, nowValue, setValue, change);
		return result;

	}

}