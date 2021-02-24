package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;

/**
 * 基础数据库接口
 * @author yousongshu
 *
 */
public interface ISqlHelp {

	/**
	 * 通过带@Entity注解的PO类
	 * 生成建表ddl语句
	 * @param BeanWrapper po类
	 * @param hasFormat 生成sql是否格式化
	 * @return
	 */
	String getSqlToCreateTable(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成单表查询sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception 
	 */
	String getSqlToSelect(BeanWrapper wrapper, boolean hasFormat) throws Exception;
	/**
	 * 生成更新sql
	 * 如 update test set id=?,name=?
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 */
	String getSqlToUpdate(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成插入sql
	 * 如 insert into test(id,name) values(?,?)
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 */
	String getSqlToInsert(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成删除sql
	 * 如 delete from test
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 */
	String getSqlToDelete(BeanWrapper wrapper, boolean hasFormat);

}
