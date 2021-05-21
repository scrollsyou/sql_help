package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;

/**
 * 基础数据库接口
 * @author yousongshu
 *
 */
public interface ISqlHelp {

	public static final String SELECT = "select";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	public static final String INSERT_INTO = "insert into";
	public static final String VALUES = "values";
	public static final String SET = "set";
	public static final String EQUALS = "=";
	public static final String PARAM_TOKEN = "?";
	public static final String FROM = "from";
	public static final String AS = "as";
	public static final String WHERE = "where";
	public static final String GROUP_BY = "group by";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String ON = "on";
	public static final String POINT = ".";
	public static final String COMMA = ",";
	public static final String SPLIT = " ";
	public static final String RETRACT = "    ";
	public static final String ENTER = "\n";
	public static final String LEFT_PARENTHESIS = "(";
	public static final String RIGHT_PARENTHESIS = ")";

	/**
	 * 生成单表查询sql
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	String getSqlToSelect(BeanWrapper wrapper, boolean hasFormat) throws Exception;
	/**
	 * 生成单表查询总数
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	String getSqlToSelectCount(BeanWrapper wrapper, boolean hasFormat) throws Exception;
	/**
	 * 生成单表按id查询数据
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	String getSqlToSelectById(BeanWrapper poClazz, boolean hasFormat) throws Exception;
	/**
	 * 生成更新sql
	 * 如 update test set id=?,name=?
	 * @param hasFormat
	 * @return
	 */
	String getSqlToUpdate(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成插入sql
	 * 如 insert into test(id,name) values(?,?)
	 * @param hasFormat
	 * @return
	 */
	String getSqlToInsert(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成删除sql
	 * 如 delete from test
	 * @param hasFormat
	 * @return
	 */
	String getSqlToDelete(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 生成删除sql
	 * 如 delete from test where id=?
	 * @param hasFormat
	 * @return
	 */
	String getSqlToDeleteById(BeanWrapper wrapper, boolean hasFormat);
	/**
	 * 查询id列表sql
	 * @param poClazz
	 * @param hasFormat
	 * @return
	 * @throws Exception
	 */
	String getSqlToSelectId(BeanWrapper poClazz, boolean hasFormat) throws Exception;


}
