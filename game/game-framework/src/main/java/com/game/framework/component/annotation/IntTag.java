package com.game.framework.component.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * int标记注解<br>
 * IntTag.java
 * @author JiangBangMing
 * 2019年1月3日下午1:02:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntTag {

	int value();
}
