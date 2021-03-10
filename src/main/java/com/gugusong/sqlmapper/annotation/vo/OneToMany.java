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
	 * 属性对应当前字段名
	 * @return
	 */
	String mainProperty();
	/**
	 * 目录对象类型
	 * 必须为Entity类或VO类
	 * @return
	 */
	Class<?> tagerClass();
	/**
	 * 目标对象关联字段
	 * @return
	 */
	String targetProperty();
	/**
	 * 分组属性，默认为主键
	 * @return
	 */
	String groupByProperty() default "";
}
