package com.gugusong.sqlmapper.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串转换工具类
 * @author you
 *
 */
public class StringConvertUtil {

	private static Pattern linePattern = Pattern.compile("_(\\w)");
	private static Pattern humpPattern = Pattern.compile("[A-Z]");

	/**
	 * 下划线转驼峰
	 * @param str 需要转换命名的字符串
	 * @return
	 */
	public static String lineToHump(String str) {
		if (str == null || str.isEmpty() || !str.contains("_")) {
			return str;
		}
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 下划线转驼峰
	 * @param str 需要转换命名的字符串
	 * @return
	 */
	public static String lineToHump2(String str) {
		if (str == null || str.isEmpty() || !str.contains("_")) {
			return str;
		}
		str = str.toLowerCase();
		String[] strSplitStrArray = str.split("_");
		StringBuffer sbuBuffer = new StringBuffer(strSplitStrArray[0]);
		if (strSplitStrArray.length > 1) {
			for (int i = 1; i < strSplitStrArray.length; i++) {
				String oneStr = strSplitStrArray[i];
				if (!oneStr.isEmpty()) {
					sbuBuffer.append(oneStr.substring(0,1).toUpperCase()).append(oneStr.substring(1));
				}
			}
		}
		return sbuBuffer.toString();
	}
	
	/** 驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)}) */
	public static String humpToLine(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.replaceAll("[A-Z]", "_$0").toLowerCase();
	}
	
	/** 驼峰转下划线,效率比上面高 */
	public static String humpToLine2(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
