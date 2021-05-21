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
	E10001("E10001", "不能使用没有@Entity的类"),
	/**
	 * NOT_BEAN - 不能使用不存在get/set方法的bean类
	 */
	NOT_BEAN("NOT_BEAN", "该类不是标准javabean类，可能缺少get/set方法");

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

	@Override
	public String toString() {
		return this.errorCode + ":" + this.errorMsg;
	}


}
