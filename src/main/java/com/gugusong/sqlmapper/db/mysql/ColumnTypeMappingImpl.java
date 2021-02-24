package com.gugusong.sqlmapper.db.mysql;

import java.util.Date;

import com.google.common.base.Strings;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ColumnTypeMappingImpl implements ColumnTypeMapping {

	public static final String STRING_TYPE = "VARCHAR";
	public static final String INT_TYPE = "INT";
	public static final String LONG_TYPE = "BIGINT";
	public static final String DATE_TYPE = "DATETIME";
	public static final String DOUBLE_TYPE = "DOUBLE";
	public static final String FLOAT_TYPE = "FLOAT";
	
	public static final int VAR_DEFAULT_LENGHT = 255;
	/**
	 * 获取数据库字段类型
	 * @return
	 */
	public void convertDbTypeByField(BeanColumn field) {
		if (Strings.isNullOrEmpty(field.getDateType())) {
			field.setDateType(mapping(field.getField().getType()));
		}
		if(field.getLength() == null) {
			field.setLength(VAR_DEFAULT_LENGHT);
		}
	}
	
	/**
	 * 返回数据库中字段类型
	 * @param fieldClazz
	 * @return
	 */
	private String mapping(Class fieldClazz) {
		if(String.class == fieldClazz) {
			return STRING_TYPE;
		}else if(Date.class == fieldClazz) {
			return DATE_TYPE;
		}else if(Integer.class == fieldClazz) {
			return INT_TYPE;
		}else if(int.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!");
			return INT_TYPE;
		}else if(Long.class == fieldClazz) {
			return LONG_TYPE;
		}else if(long.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!");
			return LONG_TYPE;
		}else if(Double.class == fieldClazz) {
			return DOUBLE_TYPE;
		}else if(double.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!");
			return DOUBLE_TYPE;
		}else if(Float.class == fieldClazz) {
			return FLOAT_TYPE;
		}else if(float.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!");
			return FLOAT_TYPE;
		}
		return STRING_TYPE;
	}
}
