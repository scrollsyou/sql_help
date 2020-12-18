package com.gugusong.sqlmapper.strategy.impl;

import com.gugusong.sqlmapper.common.util.TextUtil;
import com.gugusong.sqlmapper.strategy.ImplicitNamingStrategy;

import lombok.NonNull;

public class DefaultJDBCImplicitNamingStrategyImpl implements ImplicitNamingStrategy {

	public String getTableName(@NonNull String entityName) {
		return TextUtil.humpToJdbcHump(entityName);
	}

	public String getColumntName(@NonNull String attributeName) {
		return TextUtil.humpToJdbcHump(attributeName);
	}

}
