package com.game.framework.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分散独立返回每个结果(只用于更新操作的回馈)<br>
 * 正常更新函数会返回成功条目, 如果加了这个注解, 会返回一个列表表明哪个成功哪个失败.
 * IndependentReturn.java
 * @author JiangBangMing
 * 2019年1月3日下午4:56:50
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IndependentReturn
{
}
