package com.game.base.service.cmd;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指令类
 * Cmd.java
 * @author JiangBangMing
 * 2019年1月4日下午4:26:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@Documented
public @interface Cmd {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	@Inherited
	@Documented
	public @interface CmdFunc {
		/** 消息码 **/
		short value();
	}
}