package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import lombok.NonNull;

/**
 * 会话 基础数据操作方法
 * 
 * @author yousongshu
 *
 */
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
		if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.UUID) {
			entityWrapper.getIdColumn().getWriteMethod().invoke(entity, UUIDUtil.getUUID());
		}else if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.SNOWFLAKE) {
			entityWrapper.getIdColumn().getWriteMethod().invoke(entity, config.getSnowFlake().nextId());
		}
		PreparedStatement preSta = null;
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
			preSta.setObject(i, beanColumn.getReadMethod().invoke(entity));
			i++;
		}
		preSta.executeUpdate();
		if(entityWrapper.getIdColumn().getIdStragegy() == GenerationType.IDENTITY) {
			ResultSet resultSet = preSta.getGeneratedKeys();
			if(resultSet.next()) {
				if(ColumnTypeMappingImpl.INT_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().getWriteMethod().invoke(entity, resultSet.getInt(1));
				}else if(ColumnTypeMappingImpl.LONG_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().getWriteMethod().invoke(entity, resultSet.getLong(1));
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
//		BeanWrapper beanWrapper = new BeanWrapper();
//		beanWrapper.setPoClazz(E);
//		String sql = iSqlHelp.getSqlToSelect(beanWrapper, false);
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
	
	@Override
	public void commit() throws SQLException {
		conn.commit();
		
	}
	@Override
	public void close() throws SQLException {
		this.conn.close();
	}
}
