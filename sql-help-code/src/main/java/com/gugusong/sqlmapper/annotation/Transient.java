package com.gugusong.sqlmapper.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 指定不需映射字段
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Transient {

}
