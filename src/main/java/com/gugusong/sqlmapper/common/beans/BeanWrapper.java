package com.gugusong.sqlmapper.common.beans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.config.GlogalConfig;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * bean类包装器，用于解析类属性
 * 用于对BEAN类进行编辑
 * @author yousongshu
 *
 */
public class BeanWrapper {

	private static final Map<Class<?>, BeanWrapper> cacheMap = new ConcurrentHashMap<Class<?>, BeanWrapper>();
	/**
	 * po类
	 */
	@Getter
	@Setter
	private Class<?> poClazz;
	
	@Getter
	@Setter
	private Field[] fields;
	@Getter
	private String[] columnNames;
	@Getter
	private String tableName;
	
	private GlogalConfig config;
	
	private BeanWrapper(Class<?> poClazz, GlogalConfig config) {
		this.poClazz = poClazz;
		this.config = config;
		Field[] physicalFields = poClazz.getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(physicalFields.length);
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			fieldList.add(physicalField);
		}
		fieldList.sort(new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				Column annotation1 = o1.getAnnotation(Column.class);
				Column annotation2 = o2.getAnnotation(Column.class);
				int sort1 = Integer.MAX_VALUE;
				int sort2 = Integer.MAX_VALUE;
				if(annotation1 != null) {
					sort1 = Integer.parseInt(annotation1.sort());
				}else if(o1.isAnnotationPresent(Id.class)) {
					sort1 = -1;
				}
				if(annotation2 != null) {
					sort2 = Integer.parseInt(annotation2.sort());
				}else if(o2.isAnnotationPresent(Id.class)) {
					sort2 = -1;
				}
				return sort1 - sort2;
			}
		});
		fields = fieldList.toArray(new Field[] {});
		columnNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			columnNames[i] = config.getImplicitNamingStrategy().getColumntName(field.getName());
		}
		List<String> splitPackage = Splitter.on(CharMatcher.anyOf(".$")).splitToList(poClazz.getName());
		tableName = config.getImplicitNamingStrategy().getTableName(splitPackage.get(splitPackage.size() - 1));
		
	}
	
	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlogalConfig config) {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}
}
