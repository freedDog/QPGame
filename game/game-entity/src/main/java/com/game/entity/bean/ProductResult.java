package com.game.entity.bean;

import com.game.framework.component.change.ChangeResult;

/**
 * 产品修改结果<br>
 * 
 */
public class ProductResult extends ChangeResult {
	protected int type;
	protected int id;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ProductResult [type=" + type + ", id=" + id + ", change=" + change + ", code=" + code + ", msg=" + msg + "]";
	}

	/** 成功 **/
	public static ProductResult succeed(int type, int id, long change) {
		return create(SUCCESS, null, type, id, change);
	}

	/** 成功 **/
	public static ProductResult succeed(long change) {
		return succeed(0, 0, change);
	}

	/** 成功 **/
	public static ProductResult succeed() {
		return succeed(0);
	}

	/** 创建错误消息, code=0 **/
	public static ProductResult error(String msg) {
		return create(ERROR, msg, 0, 0, 0);
	}

	/** 创建消息 **/
	public static ProductResult create(int code, String msg, int type, int id, long change) {
		ProductResult result = new ProductResult();
		result.setCode(code);
		result.setMsg(msg);
		result.setType(type);
		result.setId(id);
		result.setChange(change);
		return result;
	}
}
