package com.game.framework.component.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 优先级注解<br>
 * 数值越高越优先, 不写默认为0.
 * Priority.java
 * @author JiangBangMing
 * 2019年1月3日下午1:02:12
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Priority {

	int value();
	
}
