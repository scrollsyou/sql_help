package com.gugusong.sqlmapper;

import java.sql.SQLException;
import java.util.List;

import lombok.NonNull;

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
	 * @throws Exception 
	 */
	public <T> T save(T entity);
	/**
	 * 按id更新实体对象
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> int update(T entity);
	/**
	 * 按id更新实体对象，null值不更新
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> int updateSelective(T entity);
	/**
	 * 按实体对象进行删除
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> int delete(T entity);
	/**
	 * 按条件进行删除
	 * @param <E>
	 * @param example
	 * @param E
	 * @return
	 * @throws Exception 
	 */
	public <E> int delete(Example example, Class<E> E);
	/**
	 * 按条件查询数据列表
	 * @param example 条件example
	 * @param E 返回列表类型/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <E> List<E> findAll(Example example, Class<E> E);
	/**
	 * 按条件查询单行数据
	 * @param example 条件example
	 * @param E 返回类型,不允许基础类型，如接收包装类
	 * @return
	 * @throws Exception 
	 */
	public <E> E findOne(Example example, Class<E> E);
	/**
	 * 按id查询单行数据
	 * @param <E>
	 * @param E
	 * @param id
	 * @return
	 * @throws SQLException 
	 * @throws Exception 
	 */
	public <E> E findOneById(Class<E> E, Object id);
	/**
	 * 统计总行数
	 * @param example 条件
	 * @param E 查询类/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <E> int findCount(Example example, Class<E> E);
	/**
	 * 提交事务
	 * @throws SQLException 
	 */
	public void commit();
	/**
	 * 关闭会话
	 * @throws SQLException 
	 */
	public void close();
	/**
	 * 是否自动提交
	 * @param autoCommit
	 * @throws SQLException 
	 */
	public void setAutoCommit(boolean autoCommit);
	/**
	 * 回滚
	 * @throws SQLException
	 */
	public void rollback();
	
	/**
	 * 批量插入
	 * @param <T>
	 * @param entitys
	 * @param clazz
	 * @return
	 */
	public <T> List<T> save(@NonNull List<T> entitys, Class<T> clazz);
	
	/**
	 * 按条件更新，空值不更新
	 * @param <T>
	 * @param entity
	 * @param example
	 * @return
	 */
	public <T> int updateByExample(T entity, Example example);
	
	/**
	 * 查询for update
	 * @param <E>
	 * @param e
	 * @param id
	 * @return
	 */
	public <E> E findOneByIdForUpdate(Class<E> e, Object id);
}
