package com.gugusong.sqlmapper.common.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;
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

	// TODO 后期所有Map都需进行优化，可能会重写个轻量的String为key的Map
	private static final Map<Class<?>, BeanWrapper> cacheMap = new ConcurrentHashMap<Class<?>, BeanWrapper>();
	/**
	 * po类
	 */
	@Getter
	@Setter
	private Class<?> poClazz;
	@Getter
	private BeanColumn idColumn;
	@Getter
	private List<BeanColumn> columns;
	@Getter
	private String tableName;
	
	private Map<String, String> sqlCache = new TreeMap<String, String>();
	
	private GlogalConfig config;
	
	private BeanWrapper(Class<?> poClazz, GlogalConfig config) throws Exception {
		this.poClazz = poClazz;
		this.config = config;
		Field[] physicalFields = poClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = Introspector.getBeanInfo(poClazz, Object.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new Exception(ErrorCodeConstant.NOTBEAN.toString());
			}
			if(physicalField.isAnnotationPresent(Id.class)) {
				Id id = physicalField.getAnnotation(Id.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(id.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):id.name(), 
						null, 11, true, id.stragegy(), physicalField,propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), -1);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
				idColumn = beanColumn;
			}else if(physicalField.isAnnotationPresent(Column.class)) {
				Column column = physicalField.getAnnotation(Column.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(column.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):column.name(), 
								Strings.isNullOrEmpty(column.dateType())?null:column.dateType(), 
								column.length()==0?null:column.length(),
								false, null, physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), column.sort());
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}else {
				BeanColumn beanColumn = new BeanColumn(config.getImplicitNamingStrategy().getColumntName(physicalField.getName()), 
						null, null, false, null, physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), Integer.MAX_VALUE);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}
		}
		columnList.sort(new Comparator<BeanColumn>() {
			public int compare(BeanColumn o1, BeanColumn o2) {
				return o1.getSort() - o2.getSort();
			}
		});
		columns = new ArrayList<BeanColumn>(columnList.size());
		columns.addAll(columnList);
		List<String> splitPackage = Splitter.on(CharMatcher.anyOf(".$")).splitToList(poClazz.getName());
		Entity annotation = poClazz.getAnnotation(Entity.class);
		if(annotation != null && annotation.tableName()!=null && !"".equals(annotation.tableName())) {
			tableName = annotation.tableName();
		}else {
			tableName = config.getImplicitNamingStrategy().getTableName(splitPackage.get(splitPackage.size() - 1));
		}
	}
	/**
	 * 指定属性名查询出Descriptor
	 * @param propertyDescriptors
	 * @param name
	 * @return
	 */
	private PropertyDescriptor getDescriptorByName(@NonNull PropertyDescriptor[] propertyDescriptors, @NonNull String name) {
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if(name.equals(propertyDescriptor.getName())) {
				return propertyDescriptor;
			}
		}
		return null;
	}
	
	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlogalConfig config) throws Exception {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}
	/**
	 * 获取缓存sql
	 */
	public String getSql(String key) {
		return sqlCache.get(key);
	}
	/**
	 * 缓存sql
	 */
	public void putSql(String key, String sql) {
		sqlCache.put(key, sql);
	}
}
