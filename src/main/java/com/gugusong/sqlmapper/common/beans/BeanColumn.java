package com.gugusong.sqlmapper.common.beans;

import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.Data;

@Data
public class BeanColumn {

	/**
	 * 列字段名称
	 */
	private String name;
	
	/**
	 * 列字段的数据类型
	 */
	private String dateType;
	
	/**
	 * 是否id字段标识
	 */
	private boolean idFlag;
	
	/**
	 * id的策略
	 */
	private GenerationType idStragegy;
	
}
