package com.gugusong.sqlmapper.db;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Page;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.common.collection.ConverMapToList;
import com.gugusong.sqlmapper.common.collection.ConverMapToSet;
import com.gugusong.sqlmapper.common.exception.SqlException;
import com.gugusong.sqlmapper.common.exception.StructureException;
import com.gugusong.sqlmapper.common.util.TextUtil;
import com.gugusong.sqlmapper.common.util.UUIDUtil;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.mysql.ColumnTypeMappingImpl;
import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
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
	@SneakyThrows
	public <T> T save(T entity) {
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
					throw new StructureException("数据库自增长id类型不为int/long！");
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
	@SneakyThrows
	public <T> int update(T entity) {
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
	@SneakyThrows
	public <T> int delete(T entity) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		String sqlToDeleteById = sqlHelp.getSqlToDeleteById(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToDeleteById);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToDeleteById);
		preSta.setObject(1, entityWrapper.getIdColumn().getVal(entity));
		return preSta.executeUpdate();
	}
	
	@SneakyThrows
	public <E> int delete(Example example, Class<E> E) {
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
	@SneakyThrows
	public <E> List<E> findAll(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		StringBuilder sqlToSelect = new StringBuilder(sqlHelp.getSqlToSelect(entityWrapper, false));
		List<Object> values = example.getValues();
		if(example.isPage()) {
			Page page = example.getPage();
			if(page == null) {
				page = config.getPageHelp().getPage();
			}
			if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
				sqlToSelect.append(example.toSql(entityWrapper));
				sqlToSelect.append(" limit ?,?");
				values.add((page.getPageIndex() - 1) * page.getPageSize());
				values.add(page.getPageSize());
			}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
				sqlToSelect.append(example.toSql(entityWrapper, false));
				sqlToSelect.append(" and ")
					.append(entityWrapper.getTableAliasName())
					.append(".")
					.append(entityWrapper.getMainWrapper().getIdColumn().getName())
					.append(" in (");
				StringBuilder selectIdSql = new StringBuilder();
				selectIdSql.append(sqlHelp.getSqlToSelectId(entityWrapper, false))
					.append(example.toSql(entityWrapper, false))
					.append(" group by ")
					.append(entityWrapper.getTableAliasName())
					.append(".")
					.append(entityWrapper.getMainWrapper().getIdColumn().getName())
					.append(" limit ?,?");
				List<Object> bufferValues = new ArrayList<Object>(values.size() + 2);
				for (Object object : values) {
					bufferValues.add(object);
				}
				bufferValues.add((page.getPageIndex() - 1) * page.getPageSize());
				bufferValues.add(page.getPageSize());
				@Cleanup PreparedStatement bufferPreSta = this.conn.prepareStatement(selectIdSql.toString());
				for (int i = 0; i < bufferValues.size(); i++) {
					bufferPreSta.setObject(i+1, bufferValues.get(i));
				}
				@Cleanup ResultSet idRs = bufferPreSta.executeQuery();
				boolean first = true;
				while (idRs.next()) {
					if(!first) {
						sqlToSelect.append(",");
					}
					first = false;
					values.add(idRs.getObject(1));
					sqlToSelect.append("?");
				}
				sqlToSelect.append(")")
				.append(" ")
				.append(example.toOrderSql(entityWrapper));
			}
		}else {
			sqlToSelect.append(example.toSql(entityWrapper));
		}
		if(log.isDebugEnabled()) {
			log.debug("findAll执行sql: {}", sqlToSelect);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelect.toString());
		for (int i = 0; i < values.size(); i++) {
			preSta.setObject(i+1, values.get(i));
		}
		
		List<E> entitys = new ConverMapToList<E>();
		@Cleanup ResultSet rs = preSta.executeQuery();
		while (rs.next()) {
			if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
				E entity = E.newInstance();
				for (BeanColumn column : entityWrapper.getColumns()) {
					column.setVal(entity, rs.getObject(column.getName()));
				}
				entitys.add(entity);
			}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
				String uniqueKey = rs.getString(entityWrapper.getTableAliasName() + "_" + entityWrapper.getMainWrapper().getIdColumn().getName());
				E entity = (E) ((ConverMapToList)entitys).get(uniqueKey);
				if(entity == null) {
					entity = E.newInstance();
					((ConverMapToList)entitys).add(uniqueKey, entity);
				}
				setValues(rs, entity, entityWrapper);
			}else {
				throw new RuntimeException("查询对象非PO/VO对象!");
			}
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
	@SneakyThrows
	public <E> E findOne(Example example, Class<E> E) {
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
		@Cleanup ResultSet rs = preSta.executeQuery();
		
		if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			if (rs.next()) {
				E entity = E.newInstance();
				for (BeanColumn column : entityWrapper.getColumns()) {
					column.setVal(entity, rs.getObject(column.getName()));
				}
				return entity;
			}
		}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			E entity = E.newInstance();
			E result = null;
			while (rs.next()) {
				result = entity;
				setValues(rs, entity, entityWrapper);
			}
			return result;
		}else {
			throw new RuntimeException("查询对象非PO/VO对象!");
		}
			
		return null;
	}
	
	/**
	 * 递归设置数据库查询值
	 * @param rs
	 * @param entity
	 * @param entityWrapper
	 * @throws Exception
	 */
	private void setValues(ResultSet rs, Object entity, BeanWrapper entityWrapper ) throws Exception {
		for (BeanColumn column : entityWrapper.getColumns()) {
			if(ColumnTypeMapping.OBJECT_TYPE.equals(column.getDateType())) {
				Object valObject = column.getVal(entity);
				if(valObject == null) {
					valObject = column.getField().getType().newInstance();
					column.setVal(entity, valObject);
				}
				setValues(rs, valObject, column.getFieldBeanWrapper());
			}else if(ColumnTypeMapping.SET_TYPE.equals(column.getDateType())) {
				Set<Object> setObject = (Set<Object>) column.getVal(entity);
				if(setObject == null || !(setObject instanceof ConverMapToSet)) {
					setObject = new ConverMapToSet<Object>();
					column.setVal(entity, setObject);
				}
				// TODO 一对多需分组，分组逻辑较难
				String[] groupBy = column.getGroupBy();
				if(groupBy == null || groupBy.length == 0) {
					Object varObject = column.getFieldBeanWrapper().getPoClazz().newInstance();
					setValues(rs, varObject, column.getFieldBeanWrapper());
					setObject.add(varObject);
				}else {
					boolean hasValue = false;
					StringBuilder keyAppend = new StringBuilder();
					for (String key : groupBy) {
						Object keyObj = rs.getObject(key);
						if(keyObj != null) {
							keyAppend.append(keyObj);
							hasValue = true;
						}
					}
					if(!hasValue) {
						return;
					}
					Object varObject = column.getFieldBeanWrapper().getPoClazz().newInstance();
					setValues(rs, varObject, column.getFieldBeanWrapper());
					((ConverMapToSet<Object>)setObject).add(keyAppend.toString(), varObject);
				}
			}else if(ColumnTypeMapping.LIST_TYPE.equals(column.getDateType())) {
				List<Object> listObject = (List<Object>) column.getVal(entity);
				if(listObject == null|| !(listObject instanceof ConverMapToList)) {
					listObject = new ConverMapToList();
					column.setVal(entity, listObject);
				}
				// TODO 一对多需分组，分组逻辑较难
				String[] groupBy = column.getGroupBy();
				if(groupBy == null || groupBy.length == 0) {
					Object varObject = column.getFieldBeanWrapper().getPoClazz().newInstance();
					setValues(rs, varObject, column.getFieldBeanWrapper());
					listObject.add(varObject);
				}else {
					boolean hasValue = false;
					StringBuilder keyAppend = new StringBuilder();
					for (String key : groupBy) {
						Object keyObj = rs.getObject(key);
						if(keyObj != null) {
							keyAppend.append(keyObj);
							hasValue = true;
						}
					}
					if(!hasValue) {
						return;
					}
					Object varObject = column.getFieldBeanWrapper().getPoClazz().newInstance();
					setValues(rs, varObject, column.getFieldBeanWrapper());
					((ConverMapToList<Object>)listObject).add(keyAppend.toString(), varObject);
				}
			}else {
				column.setVal(entity, rs.getObject(column.getAliasName()));
			}
		}
	}
	
	/**
	 * 按id查询单行数据
	 * @param <E>
	 * @param E
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@SneakyThrows
	public <E> E findOneById(Class<E> e, Object id) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(e, config);
		String sqlToSelectById = sqlHelp.getSqlToSelectById(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("执行sql: {}", sqlToSelectById);
		}
		@Cleanup PreparedStatement preSta = this.conn.prepareStatement(sqlToSelectById);
		preSta.setObject(1, id);
		@Cleanup ResultSet rs = preSta.executeQuery();
		if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			if (rs.next()) {
				E entity = e.newInstance();
				for (BeanColumn column : entityWrapper.getColumns()) {
					column.setVal(entity, rs.getObject(column.getName()));
				}
				return entity;
			}
		}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			E entity = e.newInstance();
			E result = null;
			while (rs.next()) {
				result = entity;
				setValues(rs, entity, entityWrapper);
			}
			return result;
		}else {
			throw new RuntimeException("查询对象非PO/VO对象!");
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
	@SneakyThrows
	public <E> int findCount(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		String sqlToSelect = sqlHelp.getSqlToSelectCount(entityWrapper, false);
		sqlToSelect = TextUtil.replaceTemplateParams(sqlToSelect, param -> example.toSql(entityWrapper));
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
	
	@SneakyThrows
	public void commit() {
		conn.commit();
	}
	@SneakyThrows
	public void close() {
		this.conn.close();
	}
	@SneakyThrows
	public void setAutoCommit(boolean autoCommit) {
		this.conn.setAutoCommit(autoCommit);
	}
	@SneakyThrows
	public void rollback() {
		this.conn.rollback();
	}
	
}
