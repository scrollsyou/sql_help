package com.gugusong.sqlmapper.common.exception;

import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;

/**
 * sql异常，执行数据异常等
 * @author yousongshu
 *
 */
public class SqlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4454301356557104452L;

	public SqlException() {
		super();
	}
	
	public SqlException(Exception exc) {
		super(exc);
	}
	
	public SqlException(ErrorCodeConstant msg) {
		super(msg.toString());
	}
	
	public SqlException(String msg) {
		super(msg);
	}
}
