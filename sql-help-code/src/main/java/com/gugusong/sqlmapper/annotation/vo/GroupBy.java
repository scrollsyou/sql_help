package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 分组字段,该注解只可用于vo类注解
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface GroupBy {

	/**
	 * 配置分组字段
	 * @return
	 */
	String[] properties() default {};
}
