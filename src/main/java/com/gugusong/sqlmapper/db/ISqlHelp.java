package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;

/**
 * 基础数据库接口
 * @author yousongshu
 *
 */
public interface ISqlHelp {

	/**
	 * 生成单表查询sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception 
	 */
	String getSqlToSelect(BeanWrapper wrapper, boolean hasFormat) throws Exception;
	/**
	 * 生成单表查询总数
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception 
	 */
	String getSqlToSelectCount(BeanWrapper wrapper, boolean hasFormat) throws Exception;
	/**
	 * 生成单表按id查询数据
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	String getSqlToSelectById(BeanWrapper poClazz, boolean hasFormat) throws Exception;
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
	/**
	 * 生成删除sql
	 * 如 delete from test where id=?
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 */
	String getSqlToDeleteById(BeanWrapper wrapper, boolean hasFormat);
	

}
