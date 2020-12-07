package com.gugusong.sqlmapper.strategy;

/**
 * 主键维护策略
 * @author yousongshu
 *
 */
public enum GenerationType {

	/**
	 * 以数据库自增长
	 * 只能用于long,int类型注解
	 */
	IDENTITY, 
	/**
	 * 采用uuid做为主键
	 * 只能用于string类型注解
	 */
	UUID,
	/**
	 * 采用雪花随机数作为主键
	 * 只能用于string类型注解
	 */
	SNOWFLAKE,
	/**
	 * 默认，以数据指定为准
	 * 未指定数据无法进行入库
	 */
    DEFAULT
}
