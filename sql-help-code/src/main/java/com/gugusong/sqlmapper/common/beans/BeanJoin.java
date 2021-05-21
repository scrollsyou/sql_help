package com.gugusong.sqlmapper.common.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * bean类join关系
 * @author yousongshu
 *
 */
@AllArgsConstructor
@Data
public class BeanJoin {

	/**
	 * 存放join编码，如left join,right join等
	 */
	private String token;
	/**
	 * 条件
	 */
	private String conditions;
	/**
	 * 关联bean类包装
	 */
	private BeanWrapper joinBeanWrapper;
	/**
	 * 别名
	 */
	private String alias;
}
