package com.gugusong.sqlmapper.common.beans;

import lombok.Data;

/**
 * bean类包装器，用于解析类属性
 * 用于对BEAN类进行编辑
 * @author yousongshu
 *
 */
@Data
public class BeanWrapper {

	/**
	 * po类
	 */
	private Class poClazz;
}
