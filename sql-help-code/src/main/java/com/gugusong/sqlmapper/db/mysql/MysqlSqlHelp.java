package com.gugusong.sqlmapper.db.mysql;

import java.util.Map.Entry;

import com.google.common.base.Joiner;
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



	private static final String SQL_SELECT_METHOD = "getSqlToSelect";
	private static final String SQL_SELECT_ID_METHOD = "getSqlToSelectId";
	private static final String SQL_SELECT_COUNT_METHOD = "getSqlToSelectCount";
	private static final String SQL_SELECT_BY_ID_METHOD = "getSqlToSelectById";
	private static final String SQL_UPDATE_METHOD = "getSqlToUpdate";
	private static final String SQL_INSERT_METHOD = "getSqlToInsert";
	private static final String SQL_DELETE_METHOD = "getSqlToDelete";
	private static final String SQL_CREATE_METHOD = "getSqlToCreateTable";

	/**
	 * 生成单表查询sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getSqlToSelect(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlSb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			sqlSb.append(Joiner.on(COMMA + SPLIT).join(poClazz.getColumns().stream().map(c -> c.getName()).toArray()));
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			// TODO 后期更改为VO中指定字段进行查询
			sqlSb.append(poClazz.getTableAliasName() + POINT);
			sqlSb.append(Joiner.on(COMMA + poClazz.getTableAliasName() + POINT).join(poClazz.getMainWrapper().getColumns().stream().map(c -> {
				StringBuilder selectSb = new StringBuilder();
				selectSb.append(c.getName()).append(SPLIT).append(poClazz.getTableAliasName()).append("_").append(c.getName());
				return  selectSb.toString();
				}).toArray()));
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlSb.append(COMMA + joinTableAlias + POINT);
				sqlSb.append(Joiner.on(COMMA + joinTableAlias + POINT).join(joinBeanWrapper.getColumns().stream().map(c -> {
					StringBuilder selectSb = new StringBuilder();
					selectSb.append(c.getName()).append(SPLIT).append(joinTableAlias).append("_").append(c.getName());
					return  selectSb.toString();
				}).toArray()));
			}
			if(poClazz.getFuncColumns() != null && poClazz.getFuncColumns().size() > 0) {
				sqlSb.append(COMMA);
				sqlSb.append(Joiner.on(COMMA).join(poClazz.getFuncColumns().stream().map(c -> {
					StringBuilder selectSb = new StringBuilder();
					selectSb.append(c.getFunction()).append(SPLIT).append(c.getAliasName());
					return  selectSb.toString();
				}).toArray()));
			}
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlSb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlSb.append(beanJoin.getToken());
				sqlSb.append(SPLIT);
				sqlSb.append(joinBeanWrapper.getTableName());
				sqlSb.append(SPLIT);
				sqlSb.append(joinTableAlias);
				sqlSb.append(SPLIT);
				sqlSb.append(ON + LEFT_PARENTHESIS);
				sqlSb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlSb.append(RIGHT_PARENTHESIS);
				sqlSb.append(SPLIT);

			}
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_METHOD, sqlSb.toString());
		return sqlSb.toString();
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
		StringBuilder sqlSb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getIdColumn().getName());
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			// TODO 后期更改为VO中指定字段进行查询
			sqlSb.append(poClazz.getTableAliasName());
			sqlSb.append(POINT);
			sqlSb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlSb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlSb.append(beanJoin.getToken());
				sqlSb.append(SPLIT);
				sqlSb.append(joinBeanWrapper.getTableName());
				sqlSb.append(SPLIT);
				sqlSb.append(joinTableAlias);
				sqlSb.append(SPLIT);
				sqlSb.append(ON + LEFT_PARENTHESIS);
				sqlSb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlSb.append(RIGHT_PARENTHESIS);
				sqlSb.append(SPLIT);

			}
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_ID_METHOD, sqlSb.toString());
		return sqlSb.toString();
	}

	@Override
	public String getSqlToUpdate(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_UPDATE_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(UPDATE);
		sqlSb.append(SPLIT);
		sqlSb.append(poClazz.getTableName());
		sqlSb.append(SPLIT);
		sqlSb.append(SET);
		sqlSb.append(SPLIT);
		sqlSb.append(Joiner.on(SPLIT + EQUALS + SPLIT + PARAM_TOKEN + COMMA ).join(poClazz.getColumns().stream().filter(c -> !c.isIdFlag()).map(c -> c.getName()).toArray()));
		sqlSb.append(SPLIT + EQUALS + SPLIT + PARAM_TOKEN + SPLIT);
		sqlSb.append(WHERE);
		sqlSb.append(SPLIT);
		sqlSb.append(poClazz.getIdColumn().getName());
		sqlSb.append(SPLIT);
		sqlSb.append(EQUALS);
		sqlSb.append(SPLIT);
		sqlSb.append(PARAM_TOKEN);
		sqlSb.append(SPLIT);
		if(poClazz.isVersion()) {
			sqlSb.append(AND);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getVersionColumn().getName());
			sqlSb.append(SPLIT);
			sqlSb.append(EQUALS);
			sqlSb.append(SPLIT);
			sqlSb.append(PARAM_TOKEN);
		}
		poClazz.putSql(SQL_UPDATE_METHOD, sqlSb.toString());
		return sqlSb.toString();
	}

	@Override
	public String getSqlToInsert(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_INSERT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(INSERT_INTO);
		sqlSb.append(SPLIT);
		sqlSb.append(poClazz.getTableName());
		sqlSb.append(LEFT_PARENTHESIS);
		sqlSb.append(Joiner.on(COMMA).join(poClazz.getColumns().stream().filter(c -> !(c.isIdFlag() && c.getIdstrategy()==GenerationType.IDENTITY )).map(c -> c.getName()).toArray()));
		sqlSb.append(RIGHT_PARENTHESIS);
		sqlSb.append(SPLIT);
		sqlSb.append(VALUES);
		sqlSb.append(LEFT_PARENTHESIS);
		sqlSb.append(Joiner.on(COMMA).join(poClazz.getColumns().stream().filter(c -> !(c.isIdFlag() && c.getIdstrategy()==GenerationType.IDENTITY )).map(c -> PARAM_TOKEN).toArray()));
		sqlSb.append(RIGHT_PARENTHESIS);
		poClazz.putSql(SQL_INSERT_METHOD, sqlSb.toString());
		return sqlSb.toString();
	}


	@Override
	public String getSqlToDelete(BeanWrapper poClazz, boolean hasFormat) {
		String sql = poClazz.getSql(SQL_DELETE_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(DELETE);
		sqlSb.append(SPLIT);
		sqlSb.append(FROM);
		sqlSb.append(SPLIT);
		sqlSb.append(poClazz.getTableName());
		sqlSb.append(SPLIT);
		poClazz.putSql(SQL_DELETE_METHOD, sqlSb.toString());
		return sqlSb.toString();
	}

	@Override
	public String getSqlToSelectCount(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_COUNT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlSb = new StringBuilder();
		if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_PO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			sqlSb.append("count(*)");
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
			sqlSb.append(SPLIT);
			sqlSb.append("{where}");
		}else if(poClazz.getBeanType() == BeanWrapper.BEAN_TYPE_VO) {
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			sqlSb.append("count(*)");
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(LEFT_PARENTHESIS);
			sqlSb.append(SELECT);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableAliasName());
			sqlSb.append(POINT);
			// TODO 后期需兼容无主键表查询
			sqlSb.append(poClazz.getMainWrapper().getIdColumn().getName());
			sqlSb.append(SPLIT);
			sqlSb.append(FROM);
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableName());
			sqlSb.append(SPLIT);
			sqlSb.append(poClazz.getTableAliasName());
			for (Entry<String, BeanJoin> entry : poClazz.getJoinBeans().entrySet()) {
				sqlSb.append(SPLIT);
				String joinTableAlias = entry.getKey();
				BeanJoin beanJoin = entry.getValue();
				BeanWrapper joinBeanWrapper = beanJoin.getJoinBeanWrapper();
				sqlSb.append(beanJoin.getToken());
				sqlSb.append(SPLIT);
				sqlSb.append(joinBeanWrapper.getTableName());
				sqlSb.append(SPLIT);
				sqlSb.append(joinTableAlias);
				sqlSb.append(SPLIT);
				sqlSb.append(ON + LEFT_PARENTHESIS);
				sqlSb.append(TextUtil.replaceTemplateParams(beanJoin.getConditions(), paramName -> {
					@NonNull
					String columnName = poClazz.getColumnNameByPropertyName(paramName);
					return columnName;
				}));
				sqlSb.append(RIGHT_PARENTHESIS);
				sqlSb.append(SPLIT);
			}
			sqlSb.append(SPLIT);
			sqlSb.append("{where}");
			sqlSb.append(SPLIT);
			if(poClazz.isPageSubSql()) {
				sqlSb.append(GROUP_BY);
				sqlSb.append(SPLIT);
				sqlSb.append(poClazz.getTableAliasName());
				sqlSb.append(POINT);
				sqlSb.append(poClazz.getMainWrapper().getIdColumn().getName());
			}else if(poClazz.getGroupBys() != null && poClazz.getGroupBys().length > 0) {
				// TODO 公共部分可以抽出
				sqlSb.append(GROUP_BY);
				sqlSb.append(SPLIT);
				boolean first = true;
				for (String propertyName : poClazz.getGroupBys()) {
					if(!first) {
						sqlSb.append(COMMA);
					}
					sqlSb.append(poClazz.getColumnNameByPropertyName(propertyName));
					first = false;
				}
				sqlSb.append(SPLIT);

			}
			sqlSb.append(RIGHT_PARENTHESIS);
			sqlSb.append(SPLIT);
			sqlSb.append("buffer");
		}else {
			throw new RuntimeException("该Bean类不支持查询，查询操作只支持VO/PO类!");
		}
		poClazz.putSql(SQL_SELECT_COUNT_METHOD, sqlSb.toString());
		return sqlSb.toString();
	}

}
