package com.gugusong.sqlmapper.springboot;

import java.sql.SQLException;
import java.util.List;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Session;

import lombok.NonNull;
/**
 * 基础数据库操作
 * @author yousongshu
 *
 */
public class SqlHelpBaseDao {
	private final Session session;
	
	public SqlHelpBaseDao(Session session) {
		this.session = session;
	}
	
	/**
	 * 保存实体对象
	 * 返回带ID主键的持久化对象
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> T save(@NonNull T entity) {
		return session.save(entity);
	}
	/**
	 * 按id更新实体对象
	 * @param <T>
	 * @param entity
	 * @return 0为更新失败，1为更新成功
	 * @throws Exception 
	 */
	public <T> int update(@NonNull T entity) {
		return session.update(entity);
	}
	
	/**
	 * 按id更新实体对象，null值不更新
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> int updateSelective(@NonNull T entity) {
		return session.updateSelective(entity);
	}
	/**
	 * 按实体对象进行删除
	 * 实体必须存在主键
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> int delete(@NonNull T entity) {
		return session.delete(entity);
	}
	/**
	 * 按条件进行删除
	 * @param <E>
	 * @param example
	 * @param E
	 * @return
	 * @throws Exception 
	 */
	public <T> int delete(@NonNull Example example, @NonNull Class<T> T) {
		return session.delete(example, T);
	}
	/**
	 * 按条件查询数据列表
	 * @param example 条件example
	 * @param E 返回列表类型/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <T> List<T> findAll(@NonNull Example example, @NonNull Class<T> T){
		return session.findAll(example, T);
	}
	/**
	 * 按条件查询数据分页数据
	 * @param example 条件example
	 * @param E 返回列表类型/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <T> PageData<T> findPage(@NonNull Example example, @NonNull Class<T> T){
		if(!example.isPage()) {
			example.page();
		}
		List<T> findAll = session.findAll(example, T);
		int totalRows = session.findCount(example, T);
		PageData<T> pageData = new PageData<T>(example.getPage());
		pageData.setList(findAll);
		pageData.setTotalRows(totalRows);
		pageData.setTotalPage((int)Math.ceil(totalRows/(pageData.getPageSize()+0D)));
		return pageData;
	}
	
	/**
	 * 按条件查询单行数据
	 * @param example 条件example
	 * @param E 返回类型,不允许基础类型，如接收包装类
	 * @return
	 * @throws Exception 
	 */
	public <T> T findOne(Example example, Class<T> T) {
		return session.findOne(example, T);
	}
	/**
	 * 按id查询单行数据
	 * @param <E>
	 * @param E
	 * @param id
	 * @return
	 * @throws SQLException 
	 * @throws Exception 
	 */
	public <T> T findOneById(Class<T> T, Object id) {
		return session.findOneById(T, id);
	}
	/**
	 * 统计总行数
	 * @param example 条件
	 * @param E 查询类/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <T> int findCount(Example example, Class<T> T) {
		return session.findCount(example, T);
	}
	/**
	 * 批量插入
	 * @param <T>
	 * @param entitys
	 * @param clazz
	 * @return
	 */
	public <T> List<T> save(@NonNull List<T> entitys, Class<T> clazz){
		return session.save(entitys, clazz);
	}
	/**
	 * 按条件更新，空值不更新
	 * @param <T>
	 * @param entity
	 * @param example
	 * @return
	 */
	public <T> int updateByExample(T entity, Example example) {
		return session.updateByExample(entity, example);
	}
	/**
	 * 查询for update
	 * @param <E>
	 * @param e
	 * @param id
	 * @return
	 */
	public <T> T findOneByIdForUpdate(Class<T> T, Object id) {
		return session.findOneByIdForUpdate(T, id);
	}
}
