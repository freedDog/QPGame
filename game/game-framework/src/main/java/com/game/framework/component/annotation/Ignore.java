package com.game.framework.component.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 忽略注解<br>
 * 常用用于忽略反射处理.
 * Ignore.java
 * @author JiangBangMing
 * 2019年1月3日下午1:01:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {

}
