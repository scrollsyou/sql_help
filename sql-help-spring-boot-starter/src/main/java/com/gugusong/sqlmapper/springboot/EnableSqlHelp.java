package com.gugusong.sqlmapper.springboot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 开启sqlhelp配置
 * @author yousongshu
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(SqlMapperAutoConfiguration.class)
public @interface EnableSqlHelp {

}
