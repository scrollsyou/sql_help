package com.gugusong.sqlmapper;

import java.util.List;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;

/**
 * 查询条件接口
 * 分页参数
 * 可用于mysql等关系型数据库/mongodb等非关系型数据库
 * @author yousongshu
 *
 */
public interface Example {

	
	public Example or();
	public Example and();
	/**
	 * 子条件，相当于条件中的"（）"，返回的
	 * example对象为子对象，可在括号中加子条件
	 * @return
	 */
	public Example subCondition();
	/**
	 * 返回上级example
	 * 相当于退出括号
	 * @return
	 */
	public Example upCondition();
	public Example equals(String property, Object value);
	public Example in(String property, List<Object> value);
	public Example like(String property, Object value);
	public Example gt(String property, Object value);
	public Example gtEquals(String property, Object value);
	public Example lt(String property, Object value);
	public Example ltEquals(String property, Object value);
	/**
	 * 特殊条件
	 * @param expression 条件表达式 legnth({propertyName}) > ?
	 * @param value 条件值
	 * @return
	 */
	public Example condition(String expression, Object... value);
	
	/**
	 * 获取条件sql
	 * @param entityWrapper
	 * @return
	 */
	public String toSql(BeanWrapper entityWrapper);
	/**
	 * 获取变量值
	 * @return
	 */
	public List<Object> getValues();
	/**
	 * 是否需要分页
	 * @return
	 */
	public boolean isPage();
	/**
	 * 分页对象
	 * @return
	 */
	public Page getPage();
	/**
	 * 按字段排序
	 * @param property
	 * @return
	 */
	public Example orderByAsc(String property);
	/**
	 * 按字段倒序
	 * @param property
	 * @return
	 */
	public Example orderByDesc(String property);
	/**
	 * 默认分页
	 * 通过配置获取通用分页逻辑
	 */
	public void page();
	/**
	 * 配置分页
	 * @param page
	 */
	public void page(Page page);
	
}
