package com.gugusong.sqlmapper.common.beans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.config.GlogalConfig;

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
	private List<BeanColumn> columns;
	@Getter
	private String tableName;
	
	private GlogalConfig config;
	
	private BeanWrapper(Class<?> poClazz, GlogalConfig config) {
		this.poClazz = poClazz;
		this.config = config;
		Field[] physicalFields = poClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if(physicalField.isAnnotationPresent(Id.class)) {
				Id id = physicalField.getAnnotation(Id.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(id.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):id.name(), 
						null, 11, true, id.stragegy(), physicalField, -1);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}else if(physicalField.isAnnotationPresent(Column.class)) {
				Column column = physicalField.getAnnotation(Column.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(column.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):column.name(), 
								Strings.isNullOrEmpty(column.dateType())?null:column.dateType(), 
								column.length()==0?null:column.length(),
								false, null, physicalField, column.sort());
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}else {
				BeanColumn beanColumn = new BeanColumn(config.getImplicitNamingStrategy().getColumntName(physicalField.getName()), 
						null, null, false, null, physicalField, Integer.MAX_VALUE);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}
		}
		columnList.sort(new Comparator<BeanColumn>() {
			@Override
			public int compare(BeanColumn o1, BeanColumn o2) {
				return o1.getSort() - o2.getSort();
			}
		});
		columns = new ArrayList<BeanColumn>(columnList.size());
		columns.addAll(columnList);
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
