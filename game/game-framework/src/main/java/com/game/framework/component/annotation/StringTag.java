package com.game.framework.component.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 字符串标记注解<br>
 * StringTag.java
 * @author JiangBangMing
 * 2019年1月3日下午1:02:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringTag {

	String value();
}
