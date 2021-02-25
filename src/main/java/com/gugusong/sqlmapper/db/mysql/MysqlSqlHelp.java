package com.gugusong.sqlmapper.db.mysql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.common.util.BeanReflectUtil;
import com.gugusong.sqlmapper.db.ISqlHelp;
import com.gugusong.sqlmapper.strategy.GenerationType;

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
	private static final String AND = "and";
	private static final String OR = "or";
	private static final String POINT = ".";
	private static final String COMMA = ",";
	private static final String SPLIT = " ";
	private static final String RETRACT = "    ";
	private static final String ENTER = "\n";
	private static final String LEFT_PARENTHESIS = "(";
	private static final String RIGHT_PARENTHESIS = ")";
	
	private static final String SQL_SELECT_METHOD = "getSqlToSelect";
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
		sqlsb.append(SELECT);
		sqlsb.append(SPLIT);
		sqlsb.append(Joiner.on(COMMA + SPLIT).join(poClazz.getColumns().stream().map(c -> c.getName()).toArray()));
		sqlsb.append(SPLIT);
		sqlsb.append(FROM);
		sqlsb.append(SPLIT);
		sqlsb.append(poClazz.getTableName());
		poClazz.putSql(SQL_SELECT_METHOD, sqlsb.toString());
		return sqlsb.toString();
	}
	
	public String getSqlToSelectById(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_BY_ID_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
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
	public String getSqlToCreateTable(BeanWrapper wrapper, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}
}
