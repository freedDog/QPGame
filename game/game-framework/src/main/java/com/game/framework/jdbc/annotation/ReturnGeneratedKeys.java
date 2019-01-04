package com.game.framework.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 返回结果的主键<br>
 * 经过研究, 只有插入(insert)和覆盖(replace)才能返回主键, 并且要启用只增Id才能返回. <br>
 * 返回的主键为long
 * ReturnGeneratedKeys.java
 * @author JiangBangMing
 * 2019年1月3日下午4:57:15
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReturnGeneratedKeys {
}
