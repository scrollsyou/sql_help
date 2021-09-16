package com.gugusong.sqlmapper.db.mysql;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ColumnTypeMappingImpl implements ColumnTypeMapping {


	public static final int VAR_DEFAULT_LENGTH = 255;
	/**
	 * 获取数据库字段类型
	 * @return
	 */
	public void convertDbTypeByField(BeanColumn field) {
		if (Strings.isNullOrEmpty(field.getDateType())) {
			field.setDateType(mapping(field.getField().getType(), field.getName()));
		}
		if(field.getLength() == null) {
			field.setLength(VAR_DEFAULT_LENGTH);
		}
	}

	/**
	 * 返回数据库中字段类型
	 * @param fieldClazz
	 * @param fieldName
	 * @return
	 */
	private String mapping(Class fieldClazz, String fieldName) {
		if(String.class == fieldClazz) {
			return STRING_TYPE;
		}else if(Date.class == fieldClazz || java.sql.Date.class == fieldClazz) {
			return DATE_TYPE;
		}else if(Integer.class == fieldClazz) {
			return INT_TYPE;
		}else if(int.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!", fieldName);
			return INT_TYPE;
		}else if(Long.class == fieldClazz) {
			return LONG_TYPE;
		}else if(long.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!", fieldName);
			return LONG_TYPE;
		}else if(Double.class == fieldClazz) {
			return DOUBLE_TYPE;
		}else if(double.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!", fieldName);
			return DOUBLE_TYPE;
		}else if(Float.class == fieldClazz) {
			return FLOAT_TYPE;
		}else if(float.class == fieldClazz) {
			log.warn("字段{}为基础类型，bean类中不建议用基础类型!", fieldName);
			return FLOAT_TYPE;
		}else if(List.class.isAssignableFrom(fieldClazz)) {
			return LIST_TYPE;
		}else if(Set.class.isAssignableFrom(fieldClazz)) {
			return SET_TYPE;
		}else {
			return OBJECT_TYPE;
		}
	}
}
