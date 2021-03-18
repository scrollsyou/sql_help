package com.gugusong.sqlmapper.common.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BeanColumn {

	/**
	 * 创建普通字段定义
	 * @param name 表字段名
	 * @param dateType 表字段类型
	 * @param length 字段长度
	 * @param idFlag 是否为id
	 * @param idStragegy id自增策略
	 * @param fieldName 属性名
	 * @param field 属性字段
	 * @param readMethod 读方法
	 * @param writeMethod 写方法
	 * @param sort 排序字段
	 */
	public BeanColumn(String name, String dateType, Integer length, boolean idFlag, GenerationType idStragegy,
			String fieldName, Field field, Method readMethod, Method writeMethod, Integer sort) {
		super();
		this.name = name;
		this.aliasName = name;
		this.dateType = dateType;
		this.length = length;
		this.idFlag = idFlag;
		this.idStragegy = idStragegy;
		this.fieldName = fieldName;
		this.field = field;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.sort = sort;
	}
	
	/**
	 * 创建vo类中字段定义
	 * @param name 关联表字段名
	 * @param fieldName 当前属性名
	 * @param field 当前属性
	 * @param readMethod
	 * @param writeMethod
	 * @param tableAlias 关联表别名
	 * @param aliasName 字段别名，Object/list/set不需要传
	 * @param fieldBeanWrapper 关联Bean类包装类
	 * @param groupBy 关联一对多时，分组字段
	 */
	public BeanColumn(String name, String fieldName, Field field, Method readMethod, Method writeMethod,
			String tableAlias, String aliasName, BeanWrapper fieldBeanWrapper, String[] groupBy) {
		super();
		this.name = name;
		this.fieldName = fieldName;
		this.field = field;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.tableAlias = tableAlias;
		this.aliasName = aliasName;
		this.fieldBeanWrapper = fieldBeanWrapper;
		this.sort = 0;
		this.groupBy = groupBy;
	}

	/**
	 * 表列字段名称
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
	 * 字段名
	 */
	private String fieldName;
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
	
	// vo类中特有属性
	/**
	 * 表名别名
	 */
	private String tableAlias;
	/**
	 * 表字段别名
	 */
	private String aliasName;
	/**
	 * 关联对象
	 */
	private BeanWrapper fieldBeanWrapper;
	/**
	 * 分组字段，针对 oneToMany注解
	 */
	private String[] groupBy;
	
	
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
