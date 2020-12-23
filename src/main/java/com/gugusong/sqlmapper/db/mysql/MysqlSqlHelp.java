package com.gugusong.sqlmapper.db.mysql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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


	public void aaa() {
		System.out.println("aaa" + this.getClass().getClassLoader().toString());
		System.out.println("aaa" + MysqlSqlHelp.class.getClassLoader().toString());
	}

	public String getSqlToCreateTable(Class poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToSelect(Class poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToUpdate(Class poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToInsert(Class poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlToCreateTable(BeanWrapper poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 生成单表查询sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception 
	 */
	public String getSqlToSelect(BeanWrapper poClazz, boolean hasFormat) throws Exception {
		// TODO Auto-generated method stub
		Class cl = poClazz.getPoClazz();
		Map<String, Object> resultMap = BeanReflectUtil.getTableColumn(cl);
		System.out.println("MysqlSqlHelp.getSqlToSelect.result=" + resultMap);
		String tableName = (String) resultMap.get("table");
		System.out.println("表名：" + tableName);
		LinkedHashMap<String, BeanColumn> columnsMap = (LinkedHashMap<String, BeanColumn>) resultMap.get("columns");
		Set<String> columnNameSet = columnsMap.keySet();
		if (columnNameSet != null && columnNameSet.size() > 0) {
			throw new Exception("没有字段");
		}
		StringBuffer sqlBuffer = new StringBuffer("select ");
		StringBuffer columnSqlBuffer = new StringBuffer();
		Iterator<String> it = columnNameSet.iterator();
		while (it.hasNext()) {
			columnSqlBuffer.append(tableName).append(".").append(it.next()).append(",");
		}
		sqlBuffer.append(columnSqlBuffer.substring(0, columnSqlBuffer.length() - 1)).append(" from ").append(tableName).append(" as ").append(tableName);
		System.out.println("select sql ：" + sqlBuffer);
		return sqlBuffer.toString();
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
}
