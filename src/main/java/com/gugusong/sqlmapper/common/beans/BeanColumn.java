package com.gugusong.sqlmapper.common.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BeanColumn {

	/**
	 * 列字段名称
	 */
	private String name;
	
	/**
	 * 列字段的数据类型
	 */
	private String dateType;
	
	/**
	 * 字段长度限制
	 */
	private Integer length;
	
	/**
	 * 是否id字段标识
	 */
	private boolean idFlag;
	
	/**
	 * id的策略
	 */
	private GenerationType idStragegy;
	/**
	 * 对应字段
	 */
	private Field field;
	/**
	 * get方法
	 */
	private Method readMethod;
	/**
	 * set方法
	 */
	private Method writeMethod;
	/**
	 * 排序字段
	 */
	private Integer sort;
	
	public Object getVal(Object entity) throws Exception {
		return getReadMethod().invoke(entity);
	}
	
	public void setVal(Object entity, Object value) throws Exception {
		try {
			getWriteMethod().invoke(entity, value);
		} catch (Exception e) {
			log.error("数据库查询数据类型不匹配，{} 类型不可配{}字段类型，数据库字段类型需为：{}", value.getClass().getName(), field.getType().getName(), dateType);
			throw e;
		}
	}
	
}
