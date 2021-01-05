package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.common.beans.BeanColumn;

/**
 * java类型与数据库类型映射
 * @author yousongshu
 *
 */
public interface ColumnTypeMapping {

	/**
	 * 返回数据库类型
	 * @param field
	 * @return
	 */
	void convertDbTypeByField(BeanColumn field);
}
