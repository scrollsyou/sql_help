package com.gugusong.sqlmapper.annotation.vo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 数据关联关系
 * 关联顺序对表别名有关系
 * 在on条件中有相互依赖顺序
 * @author yousongshu
 *
 */
@Repeatable(Joins.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface Join {

	public static final String LEFT_JOIN_TYPE = "left join";
	public static final String INNER_JOIN_TYPE = "inner join";
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
	 * 如：别名为user的用户类中属性schoolId跟别名为school的学校
	 * 类通过id进行关联
	 * {user.schoolId} = {school.id}
	 * @return
	 */
	String joinConditions();
	/**
	 * 指定关联类型
	 * @return
	 */
	String joinType() default LEFT_JOIN_TYPE;
}
