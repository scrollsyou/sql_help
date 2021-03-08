package com.gugusong.sqlmapper;

import java.util.List;

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
	public Example not();
	/**
	 * 子条件，相当于条件中的"（）"，返回的
	 * example对象为子对象，可在括号中加子条件
	 * @return
	 */
	public Example subCondition();
	public Example equals(String property, Object value);
	public Example in(String property, List<Object> value);
	public Example like(String property, Object value);
	public Example gt(String property, Object value);
	public Example gtEquals(String property, Object value);
	public Example lt(String property, Object value);
	public Example ltEquals(String property, Object value);
	
	
}
