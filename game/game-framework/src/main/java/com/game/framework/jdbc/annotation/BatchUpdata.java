package com.game.framework.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.game.framework.jdbc.JadeBatchBeanFactory;


/**
 *  * 批量提交, 只用于更新操作.
 * <p/>
 * 提交处理会放置到后台定时批量更新.
 * <p/>
 * 添加此标记, 函数调用后无返回值.
 * <p/>
 * 同一条处理(函数)带有先后顺序性, 但是不同函数并无顺序性.
 * BatchUpdata.java
 * @author JiangBangMing
 * 2019年1月3日下午4:56:00
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchUpdata
{
	/** 优先级, 越高在批量时越早执行 **/
	int priority() default 0;

	/** 批量个数 **/
	int maxbatch() default 1000;

	/** 批量处理接口, 必须支持无参构造函数. **/
	Class<? extends JadeBatchBeanFactory.BatchHandler> handler() default JadeBatchBeanFactory.BatchHandler.class;

}
