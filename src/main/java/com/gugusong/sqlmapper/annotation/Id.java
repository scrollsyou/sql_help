package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.gugusong.sqlmapper.strategy.GenerationType;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Id {

	/**
	 * 指定id维护策略
	 * 默认用户自己维护不自增
	 * @return
	 */
	GenerationType stragegy() default GenerationType.DEFAULT;
}
