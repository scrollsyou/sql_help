package com.gugusong.sqlmapper.db;

import java.sql.Connection;

/**
 * 获取connection
 * @author yousongshu
 *
 */
public interface ConnectionHolper {

	/**
	 * 获取连接
	 * @return
	 */
	public Connection getTagerConnection();
	
	/**
	 * 释放连接
	 */
	public void releaseConnection();
}
