package com.gugusong.sqlmapper.common.collection;

import java.util.ArrayList;
import java.util.HashMap;

public class ConverMapToList<E> extends ArrayList<E> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8337756369863381720L;

	private transient HashMap<String, E> mapConvert;

	public ConverMapToList(){
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

	/**
	 * 返回map中存的数据
	 * @param uniqueKey
	 * @return
	 */
	public E get(String uniqueKey) {
		return mapConvert.get(uniqueKey);
	}

}
