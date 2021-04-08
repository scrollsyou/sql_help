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
	public Example like(String property, String value);
	public Example gt(String property, Object value);
	public Example gtEquals(String property, Object value);
	public Example lt(String property, Object value);
	public Example ltEquals(String property, Object value);
	/**
	 * 特殊条件
	 * @param expression 条件表达式 length({propertyName}) > ?
	 * @param value 条件值
	 * @return
	 */
	public Example condition(String expression, Object... value);

	/**
	 * 获取条件sql
	 * @param entityWrapper
	 * @return
	 */
	public String toSql(BeanWrapper entityWrapper, boolean hasOrder);
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
	public Example page();
	/**
	 * 配置分页
	 * @param page
	 */
	public Example page(Page page);
	/**
	 * 生成排序
	 * @param entityWrapper
	 * @return
	 */
	public String toOrderSql(BeanWrapper entityWrapper);
	/**
	 * 判断相等
	 * @param property
	 * @param value
	 * @param ignone 条件是忽略
	 * @return
	 */
	Example equals(String property, Object value, boolean ignone);
	/**
	 * 判断字段值为空，等价于equals中值传null
	 * @param property
	 * @return
	 */
	Example isNull(String property);
	/**
	 * 判断字符开头，空时为true
	 * @param property
	 * @param value
	 * @return
	 */
	Example startWith(String property, String value);
	/**
	 * 判断字符包含，空时为true
	 * @param property
	 * @param value
	 * @return
	 */
	Example contains(String property, String value);
	/**
	 * 判断字符结尾,空时为true
	 * @param property
	 * @param value
	 * @return
	 */
	Example endsWith(String property, String value);
	Example gt(String property, Object value, boolean nullIsTrue);
	Example gtEquals(String property, Object value, boolean nullIsTrue);
	Example lt(String property, Object value, boolean nullIsTrue);
	Example ltEquals(String property, Object value, boolean nullIsTrue);
	/**
	 * 不可跟page共同使用
	 * @return
	 */
	Example forUpdate();
	boolean isForUpdate();

}
