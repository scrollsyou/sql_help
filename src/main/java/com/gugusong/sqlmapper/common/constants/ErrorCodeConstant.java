package com.gugusong.sqlmapper.common.constants;

/**
 * 错误编码常量枚举类
 * @author chenjing
 *
 */
public enum ErrorCodeConstant {

	/**
	 * E10001 - 不能使用没有@Entity的类
	 */
	E10001("E10001", "不能使用没有@Entity的类");

	private String errorCode;

	private String errorMsg;
	
	ErrorCodeConstant(String errorCode, String errorMsg){
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
	
}
