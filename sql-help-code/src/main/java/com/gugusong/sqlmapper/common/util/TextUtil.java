package com.gugusong.sqlmapper.common.util;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 文本处理工具类
 * @author yousongshu
 *
 */
public class TextUtil {
	
	private static final byte Z_CODE = 90;
	private static final byte A_CODE = 64;
	private static final byte CODE_DIFF = 32;
	private static final char __CODE = '_';
	private static final char TEMP_LEFT_KEY = '{';
	private static final char TEMP_RIGTH_KEY = '}';

	/**
	 * 驼峰命名转数据库驼峰命名
	 * @param name
	 * @return
	 */
	public static String humpToJdbcHump(@NonNull String name) {
		char[] nameChars = name.toCharArray();
		StringBuilder newName = new StringBuilder();
		for (int i = 0; i < nameChars.length; i++) {
			if(nameChars[i] <= Z_CODE && nameChars[i] >= A_CODE) {
				if(i != 0) {
					newName.append(__CODE);
				}
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
				if(nameChars[i] >= 65 && nameChars[i] <= 90) {
					newName.append((char)(nameChars[i] + CODE_DIFF));
				}else {
					newName.append(nameChars[i]);
				}
			}
		}
		return newName.toString();
	}
	/**
	 * 匹配模版中变量，替换
	 * @param template
	 * @param conver
	 * @return
	 */
	public static String replaceTemplateParams(@NonNull String template, Function<String, String> conver) {
		StringBuilder newTemplate = new StringBuilder();
		StringBuilder paramName = new StringBuilder();
		char[] tempChars = template.toCharArray();
		for (int i = 0; i < tempChars.length; i++) {
			if(TEMP_LEFT_KEY == tempChars[i]) {
				i++;
				for (; i < tempChars.length; i++) {
					if(TEMP_RIGTH_KEY == tempChars[i]) {
						newTemplate.append(conver.apply(paramName.toString()));
						paramName = new StringBuilder();
						break;
					}
					paramName.append(tempChars[i]);
				}
			}else {
				newTemplate.append(tempChars[i]);
			}
		}
		return newTemplate.toString();
	}
	
}
