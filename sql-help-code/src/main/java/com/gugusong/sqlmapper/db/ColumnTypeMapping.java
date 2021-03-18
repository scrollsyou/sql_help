package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.common.beans.BeanColumn;

/**
 * java类型与数据库类型映射
 * @author yousongshu
 *
 */
public interface ColumnTypeMapping {
	
	public static final String STRING_TYPE = "VARCHAR";
	public static final String INT_TYPE = "INT";
	public static final String LONG_TYPE = "BIGINT";
	public static final String DATE_TYPE = "DATETIME";
	public static final String DOUBLE_TYPE = "DOUBLE";
	public static final String FLOAT_TYPE = "FLOAT";
	public static final String OBJECT_TYPE = "OBJECT";
	public static final String LIST_TYPE = "LIST";
	public static final String SET_TYPE = "SET";

	/**
	 * 返回数据库类型
	 * @param field
	 * @return
	 */
	void convertDbTypeByField(BeanColumn field);
}
