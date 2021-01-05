package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 类属性对应表字段名称映射
 * 属性不存在字段采用命名策略进行映射
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {

	/**
	 * 属性对应表字段名
	 * @return
	 */
	String name() default "";
	/**
	 * 属性数据库中排序
	 * @return
	 */
	public int sort() default 0;
	/**
	 * 数据库类型
	 * @return
	 */
	String dateType() default "";
	/**
	 * 数据库类型长度
	 * @return
	 */
	int length() default 0; 
}
