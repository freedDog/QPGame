package com.game.framework.utils.struct.result;


/**
 * 结果返回<br>
 * 带code和msg 2种数据<br>
 * code小于等于0为失败.
 * Result.java
 * @author JiangBangMing
 * 2019年1月4日下午4:36:50
 */
public class Result implements IResult {
	protected int code;
	protected String msg;

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return msg;
	}

	@Override
	public boolean isSucceed() {
		return code > 0;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Result [code=" + code + ", msg=" + msg + "]";
	}

	/** 创建消息 **/
	public static Result create(int code, String msg) {
		Result result = new Result();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	/** 创建错误消息, code=0 **/
	public static Result error(String msg) {
		Result result = new Result();
		result.setCode(FAIL);
		result.setMsg(msg);
		return result;
	}

	/** 创建成功消息 **/
	public static Result succeed() {
		Result result = new Result();
		result.setCode(SUCCESS);
		return result;
	}

	/** 创建消息 **/
	public static <R extends Result> R create(Class<R> clazz, int code, String msg) {
		try {
			R result = clazz.newInstance();
			result.setCode(code);
			result.setMsg(msg);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 创建错误消息, code=0 **/
	public static <R extends Result> R error(Class<R> clazz, String msg) {
		return create(clazz, FAIL, msg);
	}

}
