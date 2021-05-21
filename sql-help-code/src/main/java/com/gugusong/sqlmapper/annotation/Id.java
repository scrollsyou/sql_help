package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.gugusong.sqlmapper.strategy.GenerationType;

/**
 * @Id 注释字段单Bean类中不可存在多个
 * @author yousongshu
 *
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Id {

	/**
	 * 指定id维护策略
	 * 默认用户自己维护不自增
	 * @return
	 */
	GenerationType strategy() default GenerationType.DEFAULT;
	/**
	 * 指定映射名称
	 * @return
	 */
	String name() default "";
}
