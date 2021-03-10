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
	 * 目录对象关联字段
	 * 默认为Id关联
	 * @return
	 */
	String targetProperty() default "id";
}
