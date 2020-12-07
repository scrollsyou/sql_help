package com.gugusong.sqlmapper.db.mysql;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;
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

	public String getSqlToSelect(BeanWrapper poClazz, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
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
