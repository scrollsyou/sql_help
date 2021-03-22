package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.Page;

import lombok.NonNull;

/**
 * 默认分页逻辑
 * 
 * @author yousongshu
 *
 */
public class PageHolder{
	
	private static final ThreadLocal<Page> threadLocal = new ThreadLocal<Page>();

	public static Page getPage() {
		Page page = threadLocal.get();
		if(page == null) {
			page = new Page(1, 10);
		}
		return page;
	}
	
	/**
	 * 自定义传递分页参数
	 * @param page
	 */
	public static void setPage(@NonNull Page page) {
		threadLocal.set(page);
	}

}
