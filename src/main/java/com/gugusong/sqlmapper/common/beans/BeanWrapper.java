package com.gugusong.sqlmapper.common.beans;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

/**
 * bean类包装器，用于解析类属性
 * 用于对BEAN类进行编辑
 * @author yousongshu
 *
 */
@Data
public class BeanWrapper {

	private static final Map<Class, BeanWrapper> cacheMap = new ConcurrentHashMap<Class, BeanWrapper>();
	/**
	 * po类
	 */
	private Class<?> poClazz;
	
	private Field[] fields;
	
	private BeanWrapper(Class<?> poClazz) {
		this.poClazz = poClazz;
	}
	
	public static BeanWrapper instrance(Class<?> poClazz) {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}
}
