package com.gugusong.sqlmapper.util;

import org.junit.Assert;
import org.junit.Test;

import com.gugusong.sqlmapper.common.util.TextUtil;

/**
 * 文本工具测试类
 * @author yousongshu
 *
 */
public class TextUtilTest {

	@Test
	public void testHumpToJDBCHump() {
		Assert.assertEquals("转化失败", TextUtil.humpToJdbcHump("entityName_aaa"), "entity_name_aaa");
	}
	
	@Test
	public void testJdbcHumpToHump() {
		Assert.assertEquals("转化失败", TextUtil.jdbcHumpToHump("entity_name_aAa"), "entityNameAaa");
	}
	
	@Test
	public void replaceTemplateParams() {
		System.out.println(TextUtil.replaceTemplateParams("select * from user where {userName} = ? and {parentName} = ?", propertyName -> {
			return TextUtil.humpToJdbcHump(propertyName);
		}));
	}
}
