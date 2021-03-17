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

	// TODO 需更改为注解在vo类上，因为会有多个别名
	/**
	 * 关联Vo类
	 * @return
	 */
	Class<?> tagerClass();
	// TODO 关联数据不定为单独bean类，可能为多个

}
