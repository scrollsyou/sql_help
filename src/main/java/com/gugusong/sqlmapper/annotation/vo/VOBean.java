package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 明确注解vo类
 * @author yousongshu
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface VOBean {

	/**
	 * vo 查询默认对主po关联类
	 * 属性名默认一一映射
	 * @return
	 */
	Class<?> mainPo();
}
