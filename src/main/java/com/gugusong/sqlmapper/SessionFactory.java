package com.gugusong.sqlmapper;

import java.sql.SQLException;

/**
 * 会话管理工厂
 * @author yousongshu
 *
 */
public interface SessionFactory {

	/**
	 * 开启创建会话
	 * @return
	 * @throws SQLException 
	 */
	Session openSession() throws SQLException;
}
