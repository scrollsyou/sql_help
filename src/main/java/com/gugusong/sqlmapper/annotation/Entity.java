package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 实体注解
 * 用于指定po类及指定关联表
 * @author yousongshu
 *
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {

	/**
	 * 默认关联表名以命名策略为准
	 * 当tableName有配置时，以tableName值为准
	 * @return
	 */
	String tableName() default "";
}
