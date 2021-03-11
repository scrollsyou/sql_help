package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * vo类中的注解
 * 不能用于po类中
 * 多对一关系配置
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ManyToOne {

	/**
	 * 对应关联entity别名
	 * 必须为Entity类对应别名
	 * @return
	 */
	String entityAlias();
	// TODO 关联数据不定为单独bean类，可能为多个

}
