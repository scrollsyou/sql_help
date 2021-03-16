package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 数据左关联关系
 * @author yousongshu
 *
 */
@Repeatable(LeftJoins.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface LeftJoin {

	/**
	 * vo 查询默认对主po关联类
	 * @return
	 */
	Class<?> po();
	
	/**
	 * 对应关联entity别名
	 * vo类上所有关联别名不可重复
	 * @return
	 */
	String entityAlias();
	
	/**
	 * 对应关联条件
	 * 如：别名为user的用户类中属性schoolId跟别名为shcool的学校
	 * 类通过id进行关联
	 * {user.schoolId} = {school.id}
	 * @return
	 */
	String joinConditions();
}
