package com.gugusong.sqlmapper;

/**
 * 会话管理工厂
 * @author yousongshu
 *
 */
public interface SessionFactory {

	/**
	 * 开启创建会话
	 * @return
	 */
	Session openSession();
}
