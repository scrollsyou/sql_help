package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * vo类中的注解
 * 一对多关系配置
 * 注解支持List,Set
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface OneToMany {

	/**
	 * 对应关联entity别名
	 * 必须为Entity类对应别名
	 * @return
	 */
	String entityAlias();
	/**
	 * 关联Vo类
	 * @return
	 */
	Class<?> tagerClass();
	// TODO 关联数据不定为单独bean类，可能为多个
}
