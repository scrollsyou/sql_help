package com.gugusong.sqlmapper.common.collection;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 包装转换set
 * 处理set中值重复问题
 * @author yousongshu
 *
 * @param <E>
 */
public class ConverMapToSet<E> extends HashSet<E> {

	private transient HashMap<String, E> mapConvert;

	public ConverMapToSet(){
		super();
		mapConvert = new HashMap<String, E>();
	}

	/**
	 * 增加数据
	 * @param uniqueKey
	 * @param val
	 * @return
	 */
	public boolean add(String uniqueKey, E val) {
		if(mapConvert.put(uniqueKey, val) == null) {
			add(val);
			return true;
		}
		return false;
	}

}
