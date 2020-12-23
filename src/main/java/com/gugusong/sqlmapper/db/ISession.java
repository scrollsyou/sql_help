package com.gugusong.sqlmapper.db;

import java.util.List;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

/**
 * 会话 基础数据操作方法
 * 
 * @author yousongshu
 *
 */
public class ISession implements Session {
	
	/**
	 * 保存实体对象 返回带ID主键的持久化对象
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 */
	public <T> T save(T entity) {
		return null;
	}

	/**
	 * 按id更新实体对象
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 */
	public <T> int update(T entity) {
		return 0;
	}

	/**
	 * 按实体对象进行删除
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 */
	public <T> int delete(T entity) {
		return 0;
	}

	/**
	 * 按条件查询数据列表
	 * 
	 * @param example 条件example
	 * @param E       返回列表类型/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <E> List<E> findAll(Example example, Class<E> E) throws Exception {
		ISqlHelp iSqlHelp = new MysqlSqlHelp();
		BeanWrapper beanWrapper = new BeanWrapper();
		beanWrapper.setPoClazz(E);
		String sql = iSqlHelp.getSqlToSelect(beanWrapper, false);
		return null;
	}

	/**
	 * 按条件查询单行数据
	 * 
	 * @param example 条件example
	 * @param E       返回类型,不允许基础类型，如接收包装类
	 * @return
	 */
	public <E> E findOne(Example example, Class<E> E) {
		return null;
	}

	/**
	 * 统计总行数
	 * 
	 * @param example 条件
	 * @param E       查询类/不限entity类
	 * @return
	 */
	public <E> int findCount(Example example, Class<E> E) {
		return 0;
	}
}
