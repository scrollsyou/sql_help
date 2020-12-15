package com.gugusong.sqlmapper.util;

import lombok.NonNull;

/**
 * 文本工具类
 * @author yousongshu
 *
 */
public class TextUtil {
	
	private static final byte Z_CODE = 90;
	private static final byte CODE_DIFF = 32;
	private static final char __CODE = '_';

	/**
	 * 驼峰命名转数据库驼峰命名
	 * @param name
	 * @return
	 */
	public static String humpToJdbcHump(@NonNull String name) {
		char[] nameChars = name.toCharArray();
		StringBuilder newName = new StringBuilder();
		for (int i = 0; i < nameChars.length; i++) {
			if(nameChars[i] <= Z_CODE) {
				newName.append(__CODE);
				newName.append((char)(nameChars[i] + CODE_DIFF));
			}else {
				newName.append(nameChars[i]);
			}
		}
		return newName.toString();
	}
	
	/**
	 * 数据库驼峰转java中驼峰
	 * @param name
	 * @return
	 */
	public static String jdbcHumpToHump(@NonNull String name) {
		char[] nameChars = name.toCharArray();
		StringBuilder newName = new StringBuilder();
		boolean hasUpperCase = false;
		for (int i = 0; i < nameChars.length; i++) {
			if(nameChars[i] == __CODE) {
				hasUpperCase = true;
				continue;
			}
			if(hasUpperCase) {
				newName.append((char)(nameChars[i] - CODE_DIFF));
				hasUpperCase = false;
			}else {
				newName.append(nameChars[i]);
			}
		}
		return newName.toString();
	}
	
}
