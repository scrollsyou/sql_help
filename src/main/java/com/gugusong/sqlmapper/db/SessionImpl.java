package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.common.util.UUIDUtil;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.mysql.ColumnTypeMappingImpl;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;
import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 会话 基础数据操作方法
 * 
 * @author yousongshu
 *
 */
@Slf4j
public class SessionImpl implements Session {
	
	private Connection conn;
	private ISqlHelp sqlHelp;
	private GlogalConfig config;
	
	public SessionImpl(@NonNull Connection conn, @NonNull ISqlHelp sqlHelp, @NonNull GlogalConfig config) {
		this.conn = conn;
		this.sqlHelp = sqlHelp;
		this.config = config;
	}
	/**
	 * 保存实体对象 返回带ID主键的持久化对象
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> T save(T entity) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		String sqlToInsert = sqlHelp.getSqlToInsert(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToInsert);
		}
		if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.UUID) {
			entityWrapper.getIdColumn().setVal(entity, UUIDUtil.getUUID());
		}else if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.SNOWFLAKE) {
			entityWrapper.getIdColumn().setVal(entity, config.getSnowFlake().nextId());
		}
		@Cleanup PreparedStatement preSta = null;
		if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.IDENTITY) {
			preSta = this.conn.prepareStatement(sqlToInsert, Statement.RETURN_GENERATED_KEYS);
		}else {
			preSta = this.conn.prepareStatement(sqlToInsert);
		}
		List<BeanColumn> columns = entityWrapper.getColumns();
		int i = 1;
		for (BeanColumn beanColumn : columns) {
			if(beanColumn.isIdFlag() && GenerationType.IDENTITY == beanColumn.getIdStragegy()) {
				continue;
			}
			preSta.setObject(i, beanColumn.getVal(entity));
			i++;
		}
		preSta.executeUpdate();
		if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.IDENTITY) {
			@Cleanup ResultSet resultSet = preSta.getGeneratedKeys();
			if(resultSet.next()) {
				if(ColumnTypeMappingImpl.INT_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().setVal(entity, resultSet.getInt(1));
				}else if(ColumnTypeMappingImpl.LONG_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().setVal(entity, resultSet.getLong(1));
				}else {
					throw new Exception("数据库自增长id类型不为int/long！");
				}
			}
		}
		return entity;
	}

	/**
	 * 按id更新实体对象
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> int update(T entity) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		String sqlToUpdate = sqlHelp.getSqlToUpdate(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToUpdate);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToUpdate);
		List<BeanColumn> columns = entityWrapper.getColumns();
		int i = 1;
		for (BeanColumn beanColumn : columns) {
			if(beanColumn.isIdFlag()) {
				continue;
			}
			preSta.setObject(i, beanColumn.getVal(entity));
			i++;
		}
		preSta.setObject(i, entityWrapper.getIdColumn().getVal(entity));
		return preSta.executeUpdate();
	}

	/**
	 * 按实体对象进行删除
	 * 
	 * @param        <T>
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	public <T> int delete(T entity) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		String sqlToDeleteById = sqlHelp.getSqlToDeleteById(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToDeleteById);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToDeleteById);
		preSta.setObject(1, entityWrapper.getIdColumn().getVal(entity));
		return preSta.executeUpdate();
	}
	
	public <E> int delete(Example example, Class<E> E) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		String sqlToDelete = sqlHelp.getSqlToDelete(entityWrapper, false);
		sqlToDelete += example.toSql(entityWrapper);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToDelete);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToDelete);
		List<Object> values = example.getValues();
		for (int i = 0; i < values.size(); i++) {
			preSta.setObject(i+1, values.get(i));
		}
		return preSta.executeUpdate();
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
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		String sqlToSelect = sqlHelp.getSqlToSelect(entityWrapper, false);
		sqlToSelect += example.toSql(entityWrapper);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToSelect);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelect);
		List<Object> values = example.getValues();
		for (int i = 0; i < values.size(); i++) {
			preSta.setObject(i+1, values.get(i));
		}
		List<E> entitys = new ArrayList<E>();
		@Cleanup ResultSet rs = preSta.executeQuery();
		while (rs.next()) {
			E entity = E.newInstance();
			for (BeanColumn column : entityWrapper.getColumns()) {
				column.setVal(entity, rs.getObject(column.getName()));
			}
			entitys.add(entity);
		}
		return entitys;
	}

	/**
	 * 按条件查询单行数据
	 * 
	 * @param example 条件example
	 * @param E       返回类型,不允许基础类型，如接收包装类
	 * @return
	 * @throws Exception 
	 */
	public <E> E findOne(Example example, Class<E> E) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		String sqlToSelect = sqlHelp.getSqlToSelect(entityWrapper, false);
		sqlToSelect += example.toSql(entityWrapper);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToSelect);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelect);
		List<Object> values = example.getValues();
		for (int i = 0; i < values.size(); i++) {
			preSta.setObject(i+1, values.get(i));
		}
		List<E> entitys = new ArrayList<E>();
		@Cleanup ResultSet rs = preSta.executeQuery();
		if (rs.next()) {
			E entity = E.newInstance();
			for (BeanColumn column : entityWrapper.getColumns()) {
				column.setVal(entity, rs.getObject(column.getName()));
			}
			return entity;
		}
		return null;
	}
	
	/**
	 * 按id查询单行数据
	 * @param <E>
	 * @param E
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public <E> E findOneById(Class<E> e, Object id) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(e, config);
		String sqlToSelectById = sqlHelp.getSqlToSelectById(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToSelectById);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelectById);
		preSta.setObject(1, id);
		@Cleanup ResultSet rs = preSta.executeQuery();
		if (rs.next()) {
			E entity = e.newInstance();
			for (BeanColumn column : entityWrapper.getColumns()) {
				column.setVal(entity, rs.getObject(column.getName()));
			}
			return entity;
		}
		return null;
	}

	/**
	 * 统计总行数
	 * 
	 * @param example 条件
	 * @param E       查询类/不限entity类
	 * @return
	 * @throws Exception 
	 */
	public <E> int findCount(Example example, Class<E> E) throws Exception {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		String sqlToSelect = "select count(*) from " + entityWrapper.getTableName();
		sqlToSelect += example.toSql(entityWrapper);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToSelect);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelect);
		List<Object> values = example.getValues();
		for (int i = 0; i < values.size(); i++) {
			preSta.setObject(i+1, values.get(i));
		}
		@Cleanup ResultSet rs = preSta.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}
	
	public void commit() throws SQLException {
		conn.commit();
	}
	public void close() throws SQLException {
		this.conn.close();
	}
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.conn.setAutoCommit(autoCommit);
	}
	public void rollback() throws SQLException {
		this.conn.rollback();
	}
	
}
