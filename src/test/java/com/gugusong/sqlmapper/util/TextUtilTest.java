package com.gugusong.sqlmapper.util;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals("转化失败", TextUtil.jdbcHumpToHump("entity_name_aaa"), "entityNameAaa");
	}
}
