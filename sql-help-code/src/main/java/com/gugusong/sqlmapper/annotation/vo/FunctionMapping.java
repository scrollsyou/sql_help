package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义函数执行，如求和函数
 * count({u.id})
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface FunctionMapping {
	/**
	 * 自定义函sql片段
	 * @return
	 */
	String function();
}
