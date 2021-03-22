package com.gugusong.sqlmapper.common.exception;

import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;

/**
 * 结构/配置类异常
 * @author yousongshu
 *
 */
public class StructureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -651790419272158862L;

	public StructureException() {
		super();
	}
	
	public StructureException(Exception exc) {
		super(exc);
	}
	
	public StructureException(ErrorCodeConstant msg) {
		super(msg.toString());
	}
	
	public StructureException(String msg) {
		super(msg);
	}
	
	
}
