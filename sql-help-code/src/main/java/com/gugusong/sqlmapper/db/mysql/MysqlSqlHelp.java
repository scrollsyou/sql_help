package com.gugusong.sqlmapper.db.mysql;

import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.common.beans.BeanJoin;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.common.util.TextUtil;
import com.gugusong.sqlmapper.db.ISqlHelp;
import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.NonNull;

/**
 * mysql5.6 sql生成器
 * 1，生成创建表脚本
 * 2，生成查询语句/更新/删除/新增脚本
 * @author yousongshu
 *
 */
public class MysqlSqlHelp implements ISqlHelp{
	
	private static final String SELECT = "select";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String INSERT_INTO = "insert into";
	private static final String VALUES = "values";
	private static final String SET = "set";
	private static final String EQUEST = "=";
	private static final String PARAM_TOKEN = "?";
	private static final String FROM = "from";
	private static final String AS = "as";
	private static final String WHERE = "where";
	private static final String GROUP_BY = "group by";
	private static final String AND = "and";
	private static final String OR = "or";
	private static final String ON = "on";
	private static final String POINT = ".";
	private static final String COMMA = ",";
	private static final String SPLIT = " ";
	private static final String RETRACT = "    ";
	private static final String ENTER = "\n";
	private static final String LEFT_PARENTHESIS = "(";
	private static final String RIGHT_PARENTHESIS = ")";
	
	private static final String SQL_SELECT_METHOD = "getSqlToSelect";
	private static final String SQL_SELECT_ID_METHOD = "getSqlToSelectId";
	private static final String SQL_SELECT_COUNT_METHOD = "getSqlToSelectCount";
	private static final String SQL_SELECT_BY_ID_METHOD = "getSqlToSelectById";
	private static final String SQL_UPDATE_METHOD = "getSqlToUpdate";
	private static final String SQL_INSERT_METHOD = "getSqlToInsert";
	private static final String SQL_DELETE_METHOD = "getSqlToDelete";
	private static final String SQL_DELETE_BY_ID_METHOD = "getSqlToDeleteById";
	private static final String SQL_CREATE_METHOD = "getSqlToCreateTable";

	/**
	 * 生成单表查询sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception 
	 */
	public String getSqlToSelect(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append(Joiner.on(COMMA + SPLIT).join(poClazz.getColumns().stream().map(c -> c.getName()).toArray()));
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			// TODO 后期更改为VO中指定字段进行查询
			sqlsb.append(poClazz.getTableAliasName() + POINT);
			sqlsb.append(Joiner.on(COMMA + poClazz.getTableAliasName() + POINT).join(poClazz.getMainWrapper().getColumns().stream().map(c -> {
				StringBuilder selectSb = new StringBuilder();
				selectSb.append(c.getName()).append(SPLIT).append(poClazz.getTableAliasName()).append("_").append(c.getName());
				return  selectSb.toString();
				}).toArray()));
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(COMMA + joinTableAlias + POINT);
				sqlsb.append(Joiner.on(COMMA + joinTableAlias + POINT).join(joinBeanWrapper.getColumns().stream().map(c -> {
					StringBuilder selectSb = new StringBuilder();
					selectSb.append(c.getName()).append(SPLIT).append(joinTableAlias).append("_").append(c.getName());
					return  selectSb.toString();
				}).toArray()));
			}
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlsb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(beanJoin.getToken());
				sqlsb.append(SPLIT);
				sqlsb.append(joinBeanWrapper.getTableName());
				sqlsb.append(SPLIT);
				sqlsb.append(joinTableAlias);
				sqlsb.append(SPLIT);
				sqlsb.append(ON + LEFT_PARENTHESIS);
				sqlsb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlsb.append(RIGHT_PARENTHESIS);
				sqlsb.append(SPLIT);
				
			}
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}
	/**
	 * 查询id
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getSqlToSelectId(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_ID_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getIdColumn().getName());
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			// TODO 后期更改为VO中指定字段进行查询
			sqlsb.append(poClazz.getTableAliasName());
			sqlsb.append(POINT);
			sqlsb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlsb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(beanJoin.getToken());
				sqlsb.append(SPLIT);
				sqlsb.append(joinBeanWrapper.getTableName());
				sqlsb.append(SPLIT);
				sqlsb.append(joinTableAlias);
				sqlsb.append(SPLIT);
				sqlsb.append(ON + LEFT_PARENTHESIS);
				sqlsb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlsb.append(RIGHT_PARENTHESIS);
				sqlsb.append(SPLIT);
				
			}
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_ID_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}
	
	public String getSqlToSelectById(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_BY_ID_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append(Joiner.on(COMMA + SPLIT).join(poClazz.getColumns().stream().map(c -> c.getName()).toArray()));
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append(WHERE);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getIdColumn().getName());
			sqlsb.append(SPLIT);
			sqlsb.append(EQUEST);
			sqlsb.append(SPLIT);
			sqlsb.append(PARAM_TOKEN);
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			// TODO 后期更改为VO中指定字段进行查询
			sqlsb.append(poClazz.getTableAliasName() + POINT);
			sqlsb.append(Joiner.on(COMMA + poClazz.getTableAliasName() + POINT).join(poClazz.getMainWrapper().getColumns().stream().map(c -> {
				StringBuilder selectSb = new StringBuilder();
				selectSb.append(c.getName()).append(SPLIT).append(poClazz.getTableAliasName()).append("_").append(c.getName());
				return  selectSb.toString();
				}).toArray()));
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(COMMA + joinTableAlias + POINT);
				sqlsb.append(Joiner.on(COMMA + joinTableAlias + POINT).join(joinBeanWrapper.getColumns().stream().map(c -> {
					StringBuilder selectSb = new StringBuilder();
					selectSb.append(c.getName()).append(SPLIT).append(joinTableAlias).append("_").append(c.getName());
					return  selectSb.toString();
				}).toArray()));
			}
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlsb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(beanJoin.getToken());
				sqlsb.append(SPLIT);
				sqlsb.append(joinBeanWrapper.getTableName());
				sqlsb.append(SPLIT);
				sqlsb.append(joinTableAlias);
				sqlsb.append(SPLIT);
				sqlsb.append(ON + LEFT_PARENTHESIS);
				sqlsb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlsb.append(RIGHT_PARENTHESIS);
				sqlsb.append(SPLIT);
				
			}
			sqlsb.append(SPLIT);
			sqlsb.append(WHERE);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			sqlsb.append(POINT);
			sqlsb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlsb.append(SPLIT);
			sqlsb.append(EQUEST);
			sqlsb.append(SPLIT);
			sqlsb.append(PARAM_TOKEN);
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		
		poClazz.putSql(SQL_SELECT_BY_ID_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}

	public String getSqlToUpdate(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_UPDATE_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append(UPDATE);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getTableName());
		sqlsb.append(SPLIT);
		sqlsb.append(SET);
		sqlsb.append(SPLIT);
		sqlsb.append(Joiner.on(SPLIT + EQUEST + SPLIT + PARAM_TOKEN + COMMA ).join(poClazz.getColumns().stream().filter(c -> !c.isIdFlag()).map(c -> c.getName()).toArray()));
		sqlsb.append(SPLIT + EQUEST + SPLIT + PARAM_TOKEN + SPLIT);
		sqlsb.append(WHERE);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getIdColumn().getName());
		sqlsb.append(SPLIT);
		sqlsb.append(EQUEST);
		sqlsb.append(SPLIT);
		sqlsb.append(PARAM_TOKEN);
		poClazz.putSql(SQL_UPDATE_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}

	public String getSqlToInsert(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_INSERT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append(INSERT_INTO);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getTableName());
		sqlsb.append(LEFT_PARENTHESIS);
		sqlsb.append(Joiner.on(COMMA).join(poClazz.getColumns().stream().filter(c -> !(c.isIdFlag() && c.getIdStragegy()==GenerationType.IDENTITY )).map(c -> c.getName()).toArray()));
		sqlsb.append(RIGHT_PARENTHESIS);
		sqlsb.append(SPLIT);
		sqlsb.append(VALUES);
		sqlsb.append(LEFT_PARENTHESIS);
		sqlsb.append(Joiner.on(COMMA).join(poClazz.getColumns().stream().filter(c -> !(c.isIdFlag() && c.getIdStragegy()==GenerationType.IDENTITY )).map(c -> PARAM_TOKEN).toArray()));
		sqlsb.append(RIGHT_PARENTHESIS);
		poClazz.putSql(SQL_INSERT_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}

	public String getSqlToDelete(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_DELETE_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append(DELETE);
		sqlsb.append(SPLIT);
		sqlsb.append(FROM);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getTableName());
		sqlsb.append(SPLIT);
		poClazz.putSql(SQL_DELETE_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}
	
	public String getSqlToDeleteById(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_DELETE_BY_ID_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append(DELETE);
		sqlsb.append(SPLIT);
		sqlsb.append(FROM);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getTableName());
		sqlsb.append(SPLIT);
		sqlsb.append(WHERE);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getIdColumn().getName());
		sqlsb.append(SPLIT);
		sqlsb.append(EQUEST);
		sqlsb.append(SPLIT);
		sqlsb.append(PARAM_TOKEN);
		poClazz.putSql(SQL_DELETE_BY_ID_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}

	@Override
	public String getSqlToSelectCount(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_COUNT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append("count(*)");
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append("{where}");
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append("count(*)");
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(LEFT_PARENTHESIS);
			sqlsb.append(SELECT);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			sqlsb.append(POINT);
			// TODO 后期需兼容无主键表查询
			sqlsb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlsb.append(SPLIT);
			sqlsb.append(FROM);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableName());
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlsb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlsb.append(beanJoin.getToken());
				sqlsb.append(SPLIT);
				sqlsb.append(joinBeanWrapper.getTableName());
				sqlsb.append(SPLIT);
				sqlsb.append(joinTableAlias);
				sqlsb.append(SPLIT);
				sqlsb.append(ON + LEFT_PARENTHESIS);
				sqlsb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlsb.append(RIGHT_PARENTHESIS);
				sqlsb.append(SPLIT);
			}
			sqlsb.append(SPLIT);
			sqlsb.append("{where}");
			sqlsb.append(SPLIT);
			sqlsb.append(GROUP_BY);
			sqlsb.append(SPLIT);
			sqlsb.append(poClazz.getTableAliasName());
			sqlsb.append(POINT);
			sqlsb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlsb.append(RIGHT_PARENTHESIS);
			sqlsb.append(SPLIT);
			sqlsb.append("buffer");
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_COUNT_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}
	
}