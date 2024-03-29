package com.gugusong.sqlmapper.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
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
import com.gugusong.sqlmapper.config.GlobalConfig;
import com.gugusong.sqlmapper.db.mysql.ColumnTypeMappingImpl;
import com.gugusong.sqlmapper.strategy.GenerationType;
import com.gugusong.sqlmapper.strategy.VersionGenerationType;

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

	private ConnectionHolper connHolper;
	private ISqlHelp sqlHelp;
	private GlobalConfig config;

	public SessionImpl(@NonNull ConnectionHolper connHolper, @NonNull ISqlHelp sqlHelp, @NonNull GlobalConfig config) {
		this.connHolper = connHolper;
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
	@Override
    @SneakyThrows
	public <T> T save(T entity) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		config.getExecuteHandle().insertHandle(entityWrapper, entity);
		String sqlToInsert = sqlHelp.getSqlToInsert(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToInsert);
			log.debug("parameters: {}", entity);
		}
		if(entityWrapper.getIdColumn() != null) {
			if(entityWrapper.getIdColumn().getIdstrategy() == GenerationType.UUID) {
				entityWrapper.getIdColumn().setVal(entity, UUIDUtil.getUUID());
			}else if(entityWrapper.getIdColumn().getIdstrategy() == GenerationType.SNOWFLAKE) {
				if(ColumnTypeMapping.LONG_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().setVal(entity, config.getSnowFlake().nextId());
				}else if (ColumnTypeMapping.STRING_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
					entityWrapper.getIdColumn().setVal(entity, config.getSnowFlake().nextId()+"");
				}else {
					throw new StructureException("实体类id属性不匹配，雪花随机数只能匹配long/string类型字段!");
				}
			}
		}
		@Cleanup PreparedStatement preSta = null;
		try {
			if(entityWrapper.getIdColumn() != null && entityWrapper.getIdColumn().getIdstrategy() == GenerationType.IDENTITY) {
				preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToInsert, Statement.RETURN_GENERATED_KEYS);
			}else {
				preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToInsert);
			}
			List<BeanColumn> columns = entityWrapper.getColumns();
			int i = 1;
			for (BeanColumn beanColumn : columns) {
				if(beanColumn.isIdFlag() && GenerationType.IDENTITY == beanColumn.getIdstrategy()) {
					continue;
				}
				if(beanColumn.isVersion()) {
					if(beanColumn.getVersionStrategy() == VersionGenerationType.DEFAULT) {
						if(ColumnTypeMapping.INT_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, 0);
						}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, 0L);
						}else {
							throw new SqlException("默认乐观锁字段必须为int/long类型!");
						}
					}else if(beanColumn.getVersionStrategy() == VersionGenerationType.SNOWFLAKE) {
						if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, config.getSnowFlake().nextId() + "");
						}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, config.getSnowFlake().nextId());
						}else {
							throw new SqlException("雪花随机数乐观锁字段必须为string/long类型!");
						}
					}else if(beanColumn.getVersionStrategy() == VersionGenerationType.UUID) {
						if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, UUIDUtil.getUUID());
						}else {
							throw new SqlException("UUID乐观锁字段必须为string类型!");
						}
					}else {
						throw new SqlException("乐观锁类型不支持!");
					}
				}else {
					preSta.setObject(i, beanColumn.getVal(entity));
				}
				i++;
			}
			int result = preSta.executeUpdate();
			if(log.isDebugEnabled()) {
				log.debug("result: {}", result);
			}
			if(entityWrapper.getIdColumn() != null && entityWrapper.getIdColumn().getIdstrategy() == GenerationType.IDENTITY) {
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
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
		return entity;
	}

	/**
	 * 批量插入数据
	 * @param <T>
	 * @param entitys
	 * @param clazz
	 * @return
	 */
	@Override
	@SneakyThrows
	public <T> List<T> save(@NonNull List<T> entitys, Class<T> clazz) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(clazz, config);
		String sqlToInsert = sqlHelp.getSqlToInsert(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToInsert);
			log.debug("parameters: list");
		}

		@Cleanup PreparedStatement preSta = null;
		try {
			if(entityWrapper.getIdColumn() != null && entityWrapper.getIdColumn().getIdstrategy() == GenerationType.IDENTITY) {
				preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToInsert, Statement.RETURN_GENERATED_KEYS);
			}else {
				preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToInsert);
			}
			List<BeanColumn> columns = entityWrapper.getColumns();
			for (T entity : entitys) {
				config.getExecuteHandle().insertHandle(entityWrapper, entity);
				if(entityWrapper.getIdColumn() != null) {
					if (entityWrapper.getIdColumn().getIdstrategy() == GenerationType.UUID) {
						entityWrapper.getIdColumn().setVal(entity, UUIDUtil.getUUID());
					} else if (entityWrapper.getIdColumn().getIdstrategy() == GenerationType.SNOWFLAKE) {
						if (ColumnTypeMapping.LONG_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
							entityWrapper.getIdColumn().setVal(entity, config.getSnowFlake().nextId());
						} else if (ColumnTypeMapping.STRING_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
							entityWrapper.getIdColumn().setVal(entity, config.getSnowFlake().nextId() + "");
						} else {
							throw new StructureException("实体类id属性不匹配，雪花随机数只能匹配long/string类型字段!");
						}
					}
				}
				int i = 1;
				for (BeanColumn beanColumn : columns) {
					if(beanColumn.isIdFlag() && GenerationType.IDENTITY == beanColumn.getIdstrategy()) {
						continue;
					}
					if(beanColumn.isVersion()) {
						if(beanColumn.getVersionStrategy() == VersionGenerationType.DEFAULT) {
							if(ColumnTypeMapping.INT_TYPE.equals(beanColumn.getDateType())) {
								preSta.setObject(i, 0);
							}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
								preSta.setObject(i, 0L);
							}else {
								throw new SqlException("默认乐观锁字段必须为int/long类型!");
							}
						}else if(beanColumn.getVersionStrategy() == VersionGenerationType.SNOWFLAKE) {
							if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
								preSta.setObject(i, config.getSnowFlake().nextId() + "");
							}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
								preSta.setObject(i, config.getSnowFlake().nextId());
							}else {
								throw new SqlException("雪花随机数乐观锁字段必须为string/long类型!");
							}
						}else if(beanColumn.getVersionStrategy() == VersionGenerationType.UUID) {
							if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
								preSta.setObject(i, UUIDUtil.getUUID());
							}else {
								throw new SqlException("UUID乐观锁字段必须为string类型!");
							}
						}else {
							throw new SqlException("乐观锁类型不支持!");
						}
					}else {
						preSta.setObject(i, beanColumn.getVal(entity));
					}
					i++;
				}
				preSta.addBatch();
			}
			int[] resultArray = preSta.executeBatch();
			int resultRow = 0;
			if (resultArray != null && resultArray.length > 0) {
				for (int oneResult : resultArray) {
					resultRow += oneResult;
				}
			}
			if(log.isDebugEnabled()) {
				log.debug("result: {}", resultRow);
			}
			if(entityWrapper.getIdColumn() != null && entityWrapper.getIdColumn().getIdstrategy() == GenerationType.IDENTITY) {
				@Cleanup ResultSet resultSet = preSta.getGeneratedKeys();
				Iterator<T> entityIt = entitys.iterator();
				while(resultSet.next()) {
					T entity = entityIt.next();
					if(ColumnTypeMappingImpl.INT_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
						entityWrapper.getIdColumn().setVal(entity, resultSet.getInt(1));
					}else if(ColumnTypeMappingImpl.LONG_TYPE.equals(entityWrapper.getIdColumn().getDateType())) {
						entityWrapper.getIdColumn().setVal(entity, resultSet.getLong(1));
					}else {
						throw new StructureException("数据库自增长id类型不为int/long！");
					}
				}
			}
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
		return entitys;
	}

	/**
	 * 按id更新实体对象
	 *
	 * @param        <T>
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <T> int update(T entity) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		Example example = ExampleImpl.newInstanceAnd();
		String sqlToUpdate = sqlHelp.getSqlToUpdate(entityWrapper, false);
		config.getExecuteHandle().updateHandle(example, entityWrapper, entity);
		sqlToUpdate += example.toSql(entityWrapper, false);
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToUpdate);
			log.debug("parameters: {}", entity);
		}
		try {
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToUpdate);
			List<BeanColumn> columns = entityWrapper.getColumns();
			int i = 1;
			for (BeanColumn beanColumn : columns) {
				if(beanColumn.isIdFlag()) {
					continue;
				}
				if(beanColumn.isVersion()) {
					if(beanColumn.getVal(entity) == null) {
						throw new SqlException("乐观锁PO类更新时version字段必传!");
					}
					if(beanColumn.getVersionStrategy() == VersionGenerationType.DEFAULT) {
						if(ColumnTypeMapping.INT_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, (Integer)beanColumn.getVal(entity) + 1);
						}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, (Long)beanColumn.getVal(entity) + 1L);
						}else {
							throw new SqlException("默认乐观锁字段必须为int/long类型!");
						}
					}else if(beanColumn.getVersionStrategy() == VersionGenerationType.SNOWFLAKE) {
						if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, config.getSnowFlake().nextId() + "");
						}else if(ColumnTypeMapping.LONG_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, config.getSnowFlake().nextId());
						}else {
							throw new SqlException("雪花随机数乐观锁字段必须为string/long类型!");
						}
					}else if(beanColumn.getVersionStrategy() == VersionGenerationType.UUID) {
						if(ColumnTypeMapping.STRING_TYPE.equals(beanColumn.getDateType())) {
							preSta.setObject(i, UUIDUtil.getUUID());
						}else {
							throw new SqlException("UUID乐观锁字段必须为string类型!");
						}
					}else {
						throw new SqlException("乐观锁类型不支持!");
					}
				}else {
					preSta.setObject(i, beanColumn.getVal(entity));
				}
				preSta.setObject(i, beanColumn.getVal(entity));
				i++;
			}
			preSta.setObject(i, entityWrapper.getIdColumn().getVal(entity));
			i++;
			if(entityWrapper.isVersion()) {
				preSta.setObject(i, entityWrapper.getVersionColumn().getVal(entity));
				i++;
			}
			for (Object val : example.getValues()){
				preSta.setObject(i, val);
				i++;
			}
			int result = preSta.executeUpdate();
			if(log.isDebugEnabled()) {
				log.debug("result: {}", result);
			}
			return result;
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
	}
	/**
	 * 按id进行更新，值为null的属性不进行更新
	 * @param <T>
	 * @param entity
	 * @return
	 */
	@Override
	@SneakyThrows
	public <T> int updateSelective(T entity) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		Example example = ExampleImpl.newInstanceAnd();
		config.getExecuteHandle().updateHandle(example, entityWrapper, entity);
		List<Object> values = new ArrayList<Object>();
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(ISqlHelp.UPDATE);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(entityWrapper.getTableName());
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(ISqlHelp.SET);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(Joiner.on(ISqlHelp.SPLIT + ISqlHelp.EQUALS + ISqlHelp.SPLIT + ISqlHelp.PARAM_TOKEN + ISqlHelp.COMMA ).join(entityWrapper.getColumns().stream().filter(c -> {
			try {
				Object value = null;
				if(c.isVersion()) {
					if(c.getVal(entity) == null) {
						throw new SqlException("乐观锁PO类更新时version字段必传!");
					}
					if(c.getVersionStrategy() == VersionGenerationType.DEFAULT) {
						if(ColumnTypeMapping.INT_TYPE.equals(c.getDateType())) {
							value = (Integer)c.getVal(entity) + 1;
						}else if(ColumnTypeMapping.LONG_TYPE.equals(c.getDateType())) {
							value = (Long)c.getVal(entity) + 1L;
						}else {
							throw new SqlException("默认乐观锁字段必须为int/long类型!");
						}
					}else if(c.getVersionStrategy() == VersionGenerationType.SNOWFLAKE) {
						if(ColumnTypeMapping.STRING_TYPE.equals(c.getDateType())) {
							value = config.getSnowFlake().nextId() + "";
						}else if(ColumnTypeMapping.LONG_TYPE.equals(c.getDateType())) {
							value = config.getSnowFlake().nextId();
						}else {
							throw new SqlException("雪花随机数乐观锁字段必须为string/long类型!");
						}
					}else if(c.getVersionStrategy() == VersionGenerationType.UUID) {
						if(ColumnTypeMapping.STRING_TYPE.equals(c.getDateType())) {
							value = UUIDUtil.getUUID();
						}else {
							throw new SqlException("UUID乐观锁字段必须为string类型!");
						}
					}else {
						throw new SqlException("乐观锁类型不支持!");
					}
				}else {
					value = c.getVal(entity);
				}
				if(c.isIdFlag() || value == null) {
					return false;
				}
				values.add(value);
			} catch (Exception e) {
				throw new StructureException(e);
			}

			return true;
		}).map(c -> c.getName()).toArray()));
		sqlSb.append(ISqlHelp.SPLIT + ISqlHelp.EQUALS + ISqlHelp.SPLIT + ISqlHelp.PARAM_TOKEN + ISqlHelp.SPLIT);
		sqlSb.append(ISqlHelp.WHERE);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(entityWrapper.getIdColumn().getName());
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(ISqlHelp.EQUALS);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(ISqlHelp.PARAM_TOKEN);
		values.add(entityWrapper.getIdColumn().getVal(entity));
		if(entityWrapper.isVersion()) {
			sqlSb.append(ISqlHelp.AND);
			sqlSb.append(ISqlHelp.SPLIT);
			sqlSb.append(entityWrapper.getVersionColumn().getName());
			sqlSb.append(ISqlHelp.SPLIT);
			sqlSb.append(ISqlHelp.EQUALS);
			sqlSb.append(ISqlHelp.SPLIT);
			sqlSb.append(ISqlHelp.PARAM_TOKEN);
			values.add(entityWrapper.getVersionColumn().getVal(entity));
		}
		sqlSb.append(example.toSql(entityWrapper, false));
		values.addAll(example.getValues());
		String sqlToUpdate = sqlSb.toString();
		try {

			if(log.isDebugEnabled()) {
				log.debug("Preparing: {}", sqlToUpdate);
				log.debug("parameters: {}", values);
			}
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToUpdate);
			int i = 1;
			for (Object val : values) {
				preSta.setObject(i, val);
				i++;
			}
			int result = preSta.executeUpdate();
			if(log.isDebugEnabled()) {
				log.debug("result: {}", result);
			}
			return result;
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
	}

	/**
	 * 按条件更新
	 */
	@Override
	@SneakyThrows
	public <T> int updateByExample(T entity, Example example) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		config.getExecuteHandle().updateHandle(example, entityWrapper, entity);
		List<Object> values = new ArrayList<Object>();
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(ISqlHelp.UPDATE);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(entityWrapper.getTableName());
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(ISqlHelp.SET);
		sqlSb.append(ISqlHelp.SPLIT);
		sqlSb.append(Joiner.on(ISqlHelp.SPLIT + ISqlHelp.EQUALS + ISqlHelp.SPLIT + ISqlHelp.PARAM_TOKEN + ISqlHelp.COMMA ).join(entityWrapper.getColumns().stream().filter(c -> {
			try {
				Object value = c.getVal(entity);
				if(c.isIdFlag() || value == null) {
					return false;
				}
				values.add(value);
			} catch (Exception e) {
				throw new StructureException(e);
			}

			return true;
		}).map(c -> c.getName()).toArray()));
		sqlSb.append(ISqlHelp.SPLIT + ISqlHelp.EQUALS + ISqlHelp.SPLIT + ISqlHelp.PARAM_TOKEN + ISqlHelp.SPLIT);
		sqlSb.append(example.toSql(entityWrapper, false));
		String sqlToUpdate = sqlSb.toString();
		try {
			values.addAll(example.getValues());
			if(log.isDebugEnabled()) {
				log.debug("Preparing: {}", sqlToUpdate);
				log.debug("parameters: {}", values);
			}
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToUpdate);
			int i = 1;
			for (Object val : values) {
				preSta.setObject(i, val);
				i++;
			}
			int result = preSta.executeUpdate();
			if(log.isDebugEnabled()) {
				log.debug("result: {}", result);
			}
			return result;
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
	}

	/**
	 * 按实体对象进行删除
	 *
	 * @param        <T>
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <T> int delete(T entity) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(entity.getClass(), config);
		return delete(ExampleImpl.newInstance().equals(entityWrapper.getIdColumn().getFieldName(), entityWrapper.getIdColumn().getVal(entity)), entityWrapper);
	}

	@Override
	@SneakyThrows
	public <E> int delete(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		return delete(example, entityWrapper);
	}

	private int delete(Example example, BeanWrapper entityWrapper) throws SQLException {
		config.getExecuteHandle().deleteHandle(example, entityWrapper);
		String sqlToDelete = sqlHelp.getSqlToDelete(entityWrapper, false);
		sqlToDelete += example.toSql(entityWrapper);
		List<Object> values = example.getValues();
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToDelete);
			log.debug("parameters: {}", values);
		}
		try {
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToDelete);
			for (int i = 0; i < values.size(); i++) {
				preSta.setObject(i+1, values.get(i));
			}
			int result = preSta.executeUpdate();
			if(log.isDebugEnabled()) {
				log.debug("result: {}", result);
			}
			return result;
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
	}

	/**
	 * 按条件查询数据列表
	 *
	 * @param example 条件example
	 * @param E       返回列表类型/不限entity类
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <E> List<E> findAll(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		config.getExecuteHandle().selectHandle(example, entityWrapper);
		StringBuilder sqlToSelect = new StringBuilder(sqlHelp.getSqlToSelect(entityWrapper, false));
		List<Object> values = example.getValues();
		if(example.isPage()) {
			Page page = example.getPage();
			if(page == null) {
				page = PageHolder.getPage();
			}
			if(page.getPropertyAsc() != null) {
				for (String propertyAsc : page.getPropertyAsc()) {
					example.orderByAsc(propertyAsc);
				}
			}
			if(page.getPropertyDesc() != null) {
				for (String propertyDesc : page.getPropertyAsc()) {
					example.orderByDesc(propertyDesc);
				}
			}
			if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO || !entityWrapper.isPageSubSql()) {
				sqlToSelect.append(example.toSql(entityWrapper, false));
				// TODO 可抽出
				if(entityWrapper.getGroupBys() != null && entityWrapper.getGroupBys().length > 0) {
					sqlToSelect.append(" ");
					sqlToSelect.append("group by");
					sqlToSelect.append(" ");
					boolean first = true;
					for (String propertyName : entityWrapper.getGroupBys()) {
						if(!first) {
							sqlToSelect.append(",");
						}
						sqlToSelect.append(entityWrapper.getColumnNameByPropertyName(propertyName));
						first = false;
					}
					sqlToSelect.append(" ");

				}
				sqlToSelect.append(example.toOrderSql(entityWrapper));
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
					.append(" ")
					.append(example.toOrderSql(entityWrapper))
					.append(" limit ?,?");
				List<Object> bufferValues = new ArrayList<Object>(values.size() + 2);
				for (Object object : values) {
					bufferValues.add(object);
				}
				bufferValues.add((page.getPageIndex() - 1) * page.getPageSize());
				bufferValues.add(page.getPageSize());
				if(log.isDebugEnabled()) {
					log.debug("Preparing: {}", selectIdSql);
					log.debug("parameters: {}", bufferValues);
				}
				try {
					@Cleanup PreparedStatement bufferPreSta = this.connHolper.getTargetConnection().prepareStatement(selectIdSql.toString());
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
					if(first) {
						// 不存在数据
						return new ArrayList<E>();
					}
				} catch (SQLException e) {
					this.close();
					throw e;
				}finally {
					this.close();
				}
				sqlToSelect.append(")")
				.append(" ");
				if(entityWrapper.getGroupBys() != null && entityWrapper.getGroupBys().length > 0) {
					sqlToSelect.append("group by");
					sqlToSelect.append(" ");
					boolean first = true;
					for (String propertyName : entityWrapper.getGroupBys()) {
						if(!first) {
							sqlToSelect.append(",");
						}
						sqlToSelect.append(entityWrapper.getColumnNameByPropertyName(propertyName));
						first = false;
					}
					sqlToSelect.append(" ");

				}
				sqlToSelect.append(example.toOrderSql(entityWrapper));
			}
		}else {
			sqlToSelect.append(example.toSql(entityWrapper));
			if(entityWrapper.getGroupBys() != null && entityWrapper.getGroupBys().length > 0) {
				sqlToSelect.append(" ");
				sqlToSelect.append("group by");
				sqlToSelect.append(" ");
				boolean first = true;
				for (String propertyName : entityWrapper.getGroupBys()) {
					if(!first) {
						sqlToSelect.append(",");
					}
					sqlToSelect.append(entityWrapper.getColumnNameByPropertyName(propertyName));
					first = false;
				}
				sqlToSelect.append(" ");

			}
			if(example.isForUpdate()) {
				sqlToSelect.append(" for update ");
			}
		}
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToSelect);
			log.debug("parameters: {}", values);
		}
		try {
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToSelect.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < values.size(); i++) {
				preSta.setObject(i+1, values.get(i));
			}

			List<E> entitys = new ConverMapToList<E>();
			@Cleanup ResultSet rs = preSta.executeQuery();
			if(log.isDebugEnabled()) {
				//移到最后一行
				rs.last();
				//通过getRow方法得到当前行号，也就是记录数
				log.debug("result: {}", rs.getRow());
				//如果还需要使用结果集，把指针再移到初始化的位置
				rs.beforeFirst();
			}
			while (rs.next()) {
				if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
					E entity = E.newInstance();
					for (BeanColumn column : entityWrapper.getColumns()) {
						column.setVal(entity, rs.getObject(column.getName()));
					}
					entitys.add(entity);
				}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
					if(entityWrapper.getMainWrapper().getIdColumn() == null || !entityWrapper.isPageSubSql()) {
//						log.warn("vo类中指定{}未存在ID键，无法进行分组!", entityWrapper.getMainWrapper().getTableName());
						E entity = E.newInstance();
						setValues(rs, entity, entityWrapper);
						entitys.add(entity);
						continue;
					}
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
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
	}

	/**
	 * 按条件查询单行数据
	 *
	 * @param example 条件example
	 * @param E       返回类型,不允许基础类型，如接收包装类
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <E> E findOne(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		return findOne(example, E, entityWrapper);
	}

	private <E> E findOne(Example example, Class<E> E, BeanWrapper entityWrapper) throws Exception {
		config.getExecuteHandle().selectHandle(example, entityWrapper);
		StringBuilder sqlToSelect = new StringBuilder(sqlHelp.getSqlToSelect(entityWrapper, false));
		sqlToSelect.append(example.toSql(entityWrapper));
		if(example.isForUpdate()) {
			sqlToSelect.append(" for update ");
		}
		List<Object> values = example.getValues();
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToSelect);
			log.debug("parameters: {}", values);
		}
		try {
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToSelect.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < values.size(); i++) {
				preSta.setObject(i+1, values.get(i));
			}
			@Cleanup ResultSet rs = preSta.executeQuery();
			if(log.isDebugEnabled()) {
				//移到最后一行
				rs.last();
				//通过getRow方法得到当前行号，也就是记录数
				log.debug("result: {}", rs.getRow());
				//如果还需要使用结果集，把指针再移到初始化的位置
				rs.beforeFirst();
			}
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
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
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
				if (column.getField().getType() == java.util.Date.class
						|| column.getField().getType() == Date.class) {
					column.setVal(entity, rs.getDate(column.getAliasName()));
				} else {
					column.setVal(entity, rs.getObject(column.getAliasName()));
				}
			}
		}
	}

	/**
	 * 按id查询单行数据
	 * @param <E>
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <E> E findOneById(Class<E> e, Object id) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(e, config);
		Example example = ExampleImpl.newInstance();
		if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			example.equals(entityWrapper.getIdColumn().getFieldName(), id);
		}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			example.equals(entityWrapper.getTableAliasName() + "." + entityWrapper.getMainWrapper().getIdColumn().getFieldName(), id);
		}else {
			throw new RuntimeException("查询对象非PO/VO对象!");
		}
		return findOne(example, e, entityWrapper);
	}

	@Override
	@SneakyThrows
	public <E> E findOneByIdForUpdate(Class<E> e, Object id) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(e, config);
		Example example = ExampleImpl.newInstance();
		example.forUpdate();
		if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			example.equals(entityWrapper.getIdColumn().getFieldName(), id);
		}else if(entityWrapper.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			example.equals(entityWrapper.getTableAliasName() + "." + entityWrapper.getMainWrapper().getIdColumn().getFieldName(), id);
		}else {
			throw new RuntimeException("查询对象非PO/VO对象!");
		}
		return findOne(example, e, entityWrapper);
	}

	/**
	 * 统计总行数
	 *
	 * @param example 条件
	 * @param E       查询类/不限entity类
	 * @return
	 * @throws Exception
	 */
	@Override
	@SneakyThrows
	public <E> int findCount(Example example, Class<E> E) {
		BeanWrapper entityWrapper = BeanWrapper.instrance(E, config);
		config.getExecuteHandle().selectHandle(example, entityWrapper);
		String sqlToSelect = sqlHelp.getSqlToSelectCount(entityWrapper, false);
		sqlToSelect = TextUtil.replaceTemplateParams(sqlToSelect, param -> example.toSql(entityWrapper, false));
		List<Object> values = example.getValues();
		if(log.isDebugEnabled()) {
			log.debug("Preparing: {}", sqlToSelect);
			log.debug("parameters: {}", values);
		}
		try {
			@Cleanup PreparedStatement preSta = this.connHolper.getTargetConnection().prepareStatement(sqlToSelect, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < values.size(); i++) {
				preSta.setObject(i+1, values.get(i));
			}
			@Cleanup ResultSet rs = preSta.executeQuery();
			if(log.isDebugEnabled()) {
				//移到最后一行
				rs.last();
				//通过getRow方法得到当前行号，也就是记录数
				log.debug("result: {}", rs.getRow());
				//如果还需要使用结果集，把指针再移到初始化的位置
				rs.beforeFirst();
			}
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			this.close();
			throw e;
		}finally {
			this.close();
		}
		return 0;
	}

	@Override
	@SneakyThrows
	public void commit() {
		this.connHolper.getTargetConnection().commit();
	}
	@Override
	@SneakyThrows
	public void close() {
		this.connHolper.releaseConnection();
	}
	@Override
	@SneakyThrows
	public void setAutoCommit(boolean autoCommit) {
		this.connHolper.getTargetConnection().setAutoCommit(autoCommit);
	}
	@Override
	@SneakyThrows
	public void rollback() {
		this.connHolper.getTargetConnection().rollback();
	}

}
