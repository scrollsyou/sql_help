package com.gugusong.sqlmapper.common.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.common.beans.BeanColumn;
import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;
import com.gugusong.sqlmapper.strategy.GenerationType;

/**
 * bean类的反射工具
 * @author chenjing
 *
 */
public class BeanReflectUtil {

	// 映射出数据库字段的类
	public static <T> Map<String, Object> getTableColumn(Class<T> clazz) throws IllegalArgumentException, IllegalAccessException {
		LinkedHashMap<String, Object> resultLinkedHashMap = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> columnLinkedHashMap = new LinkedHashMap<String, Object>();
		Class<? extends Object> objClass = clazz.getClass();
		// 获取参数类的名称
		String objClassName = objClass.getName();
		System.out.println("objClassName=" + objClassName);
		// 判断是否存在Entity注解
		boolean hasEntityAnnotation = objClass.isAnnotationPresent(Entity.class);
		if (!hasEntityAnnotation) {
			// 不能使用没有@Entity的类
			resultLinkedHashMap.put("code", ErrorCodeConstant.E10001.getErrorCode());
			resultLinkedHashMap.put("msg", ErrorCodeConstant.E10001.getErrorMsg());
			return resultLinkedHashMap;
		}
		// 获取entity注解的属性tableName，若为空则使用类名作为表名
		Entity entityAnnotation = objClass.getAnnotation(Entity.class);
		String tableName = entityAnnotation.tableName();
		if (tableName == null || tableName.isEmpty()) {
			String[] objClassNameSplitStrArray = objClassName.split("\\.");
			tableName = TextUtil.humpToJdbcHump(objClassNameSplitStrArray[objClassNameSplitStrArray.length - 1]);
			if (tableName.startsWith("_")) {
				tableName = tableName.substring(1, tableName.length());
			}
		}
		// 获取参数类中定义的属性字段数组
		Field[] fields = objClass.getDeclaredFields();
		System.out.println("objClassFieldSize=" + fields.length);
		if (fields != null && fields.length > 0) {
			for (Field oneField : fields) {
				BeanColumn beanColumn = BeanReflectUtil.getColumnField(oneField);
				columnLinkedHashMap.put(beanColumn.getName(), beanColumn);
			}
		}
		resultLinkedHashMap.put("table", tableName);
		resultLinkedHashMap.put("columns", columnLinkedHashMap);
		resultLinkedHashMap.put("code", 1);
		resultLinkedHashMap.put("msg", "reflect success");
		return resultLinkedHashMap;
	}
	
	/**
	 * 获取列字段
	 * @param oneField
	 * @return
	 */
	private static BeanColumn getColumnField(Field oneField) {
		BeanColumn beanColumn = new BeanColumn();
		String columnName = null;
		// 是否有column注解
		boolean hasColumnAnnotation = oneField.isAnnotationPresent(Column.class);
		if (hasColumnAnnotation) {
			Column oneColumn = oneField.getAnnotation(Column.class);
			columnName = oneColumn.name();
		}
		// 若column注解获取到的列名为空，则使用字段名
		if (columnName == null || columnName.isEmpty()) {
			columnName = TextUtil.humpToJdbcHump(oneField.getName());
		}
		beanColumn.setName(columnName.toLowerCase());
		// 是否有id注解
		boolean hasIdAnnotation = oneField.isAnnotationPresent(Id.class);
		if (hasIdAnnotation) {
			// 获取id的维护策略
			Id idAnnotation = oneField.getAnnotation(Id.class);
			GenerationType idStragegy = idAnnotation.stragegy();
			beanColumn.setIdFlag(hasIdAnnotation);
			beanColumn.setIdStragegy(idStragegy);
		}
		// 列字段的数据类型
		AnnotatedType fieldAnnotatedType = oneField.getAnnotatedType();
		beanColumn.setDateType(fieldAnnotatedType.getType().getTypeName());
		return beanColumn;
	}
	
}
