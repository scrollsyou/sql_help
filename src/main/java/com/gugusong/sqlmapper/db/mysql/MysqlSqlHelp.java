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

/**
 * mysql5.6 sql生成器
 * 1，生成创建表脚本
 * 2，生成查询语句/更新/删除/新增脚本
 * @author yousongshu
 *
 */
public class MysqlSqlHelp implements ISqlHelp{
	
	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String AS = "as";
	private static final String WHERE = "where";
	private static final String POINT = ".";
	private static final String COMMA = ",";
	private static final String SPLIT = " ";
	private static final String RETRACT = "    ";
	private static final String ENTER = "\n";
	
	private static final String SQL_SELECT_METHOD = "getSqlToSelect";
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
	public String getSqlToSelect(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		String sql = poClazz.getSql(SQL_SELECT_METHOD);
		if(sql != null) {
			return sql;
		}
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append(SELECT);
		sqlsb.append(ENTER);
		sqlsb.append(RETRACT);
		sqlsb.append(Joiner.on(COMMA + SPLIT).join(poClazz.getColumns().stream().map(c -> c.getName()).toArray()));
		sqlsb.append(ENTER);
		sqlsb.append(FROM);
		sqlsb.append(ENTER);
		sqlsb.append(RETRACT);
		sqlsb.append(poClazz.getTableName());
		return sqlsb.toString();
	}

	public String getSqlToUpdate(BeanWrapper poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToInsert(BeanWrapper poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToDelete(BeanWrapper poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getSqlToCreateTable(BeanWrapper wrapper, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}
}
