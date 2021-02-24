package com.gugusong.sqlmapper;

import java.sql.SQLException;
import java.util.List;

/**
 * 会话
 * 基础数据操作方法
 * @author yousongshu
 *
 */
public interface Session {

	/**
	 * 保存实体对象
	 * 返回带ID主键的持久化对象
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> T save(T entity);
	/**
	 * 按id更新实体对象
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> int update(T entity);
	/**
	 * 按实体对象进行删除
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> int delete(T entity);
	/**
	 * 按条件查询数据列表
	 * @param example 条件example
	 * @param E 返回列表类型/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <E> List<E> findAll(Example example, Class<E> E) throws Exception;
	/**
	 * 按条件查询单行数据
	 * @param example 条件example
	 * @param E 返回类型,不允许基础类型，如接收包装类
	 * @return
	 */
	public <E> E findOne(Example example, Class<E> E);
	/**
	 * 统计总行数
	 * @param example 条件
	 * @param E 查询类/不限entity类
	 * @return
	 */
	public <E> int findCount(Example example, Class<E> E);
	/**
	 * 提交事务
	 * @throws SQLException 
	 */
	public void commit() throws SQLException;
	/**
	 * 关闭会话
	 * @throws SQLException 
	 */
	public void close() throws SQLException;
}
