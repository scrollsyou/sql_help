package com.gugusong.sqlmapper.strategy.impl;

import com.gugusong.sqlmapper.common.util.TextUtil;
import com.gugusong.sqlmapper.strategy.ImplicitNamingStrategy;

import lombok.NonNull;

/**
 * @author chenjing
 */
public class DefaultJDBCImplicitNamingStrategyImpl implements ImplicitNamingStrategy {

	@Override
	public String getTableName(@NonNull String entityName) {
		return TextUtil.humpToJdbcHump(entityName);
	}

	@Override
	public String getColumnName(@NonNull String attributeName) {
		return TextUtil.humpToJdbcHump(attributeName);
	}

}
