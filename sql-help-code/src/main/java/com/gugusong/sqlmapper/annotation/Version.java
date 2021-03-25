package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.gugusong.sqlmapper.strategy.GenerationType;
import com.gugusong.sqlmapper.strategy.VersionGenerationType;

/**
 * 乐观锁字段
 * 一个PO类中只能存在一个字段
 * 该注解不可与@Id一起使用
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Version {

	/**
	 * 指定version维护策略
	 * 默认自增长
	 * @return
	 */
	VersionGenerationType stragegy() default VersionGenerationType.DEFAULT;
}
