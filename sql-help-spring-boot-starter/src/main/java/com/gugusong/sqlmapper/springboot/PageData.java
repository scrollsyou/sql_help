package com.gugusong.sqlmapper.springboot;

import java.util.List;

import com.gugusong.sqlmapper.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PageData<T> {

	public PageData() {}
	public PageData(Page page) {
		this.pageIndex = page.getPageIndex();
		this.pageSize = page.getPageSize();
	}
	/**
	 * 第几页，从1开始，默认为1
	 */
	private Integer pageIndex;
	/**
	 * 每页返回数据量
	 */
	private Integer pageSize;
	/**
     * 总共多少页
     */
    private int totalPage;
    /**
     * 记录总数
     */
    private int totalRows;
    /**
     * 查询的数据
     */
    private List<T> list;
}
