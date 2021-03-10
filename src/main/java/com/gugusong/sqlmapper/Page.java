package com.gugusong.sqlmapper;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页参数
 * @author yousongshu
 *
 */
@AllArgsConstructor
@Data
public class Page {

	/**
	 * 第几页，从1开始，默认为1
	 */
	private Integer pageIndex;
	/**
	 * 每页返回数据量
	 */
	private Integer pageSize;
}
