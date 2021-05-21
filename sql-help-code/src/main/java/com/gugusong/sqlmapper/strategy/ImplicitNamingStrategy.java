package com.gugusong.sqlmapper.strategy;

/**
 * 隐式命名策略
 * 转化字段名称映射到数据库中名称
 * @author yousongshu
 *
 */
public interface ImplicitNamingStrategy {

	/**
	 * entity名称隐式转化为表名
	 * @param entityName
	 * @return
	 */
	public String getTableName(String entityName);
	/**
	 * entity属性名称转化为数据库表字段名
	 * @param attributeName
	 * @return
	 */
	public String getColumnName(String attributeName);
}
