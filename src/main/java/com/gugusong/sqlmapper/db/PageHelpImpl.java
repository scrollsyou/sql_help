package com.gugusong.sqlmapper.db;

import com.gugusong.sqlmapper.Page;
import com.gugusong.sqlmapper.PageHelp;

import lombok.NonNull;

public class PageHelpImpl implements PageHelp {
	
	private static final ThreadLocal<Page> threadLocal = new ThreadLocal<Page>();

	@Override
	public Page getPage() {
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
	public void setPage(@NonNull Page page) {
		threadLocal.set(page);
	}

}
