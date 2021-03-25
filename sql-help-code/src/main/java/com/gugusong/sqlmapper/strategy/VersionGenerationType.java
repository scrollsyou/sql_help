package com.gugusong.sqlmapper.strategy;

/**
 * 乐观锁维护策略
 * @author yousongshu
 *
 */
public enum VersionGenerationType {

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
	 * 默认自增
	 */
    DEFAULT
}
