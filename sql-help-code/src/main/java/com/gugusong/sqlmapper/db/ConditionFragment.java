package com.gugusong.sqlmapper.db;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.common.util.TextUtil;

import lombok.Data;
import lombok.NonNull;

/**
 * 条件sql片断
 * @author yousongshu
 *
 */
@Data
public class ConditionFragment {

	/**
	 * 逻辑token，如：or,and,(,)
	 */
	public static final int CONDITION_FRAGMENT_TOKEN = 0;
	/**
	 * 逻辑判断，如:=,>,<,>=
	 */
	public static final int CONDITION_FRAGMENT_EXP = 1;
	/**
	 * 带占位符标识，如 length({property}) > ?
	 */
	public static final int CONDITION_FRAGMENT_CONDITION = 2;
	/**
	 * 排序片断
	 */
	public static final int CONDITION_FRAGMENT_ORDER = 3;

	public ConditionFragment(String token) {
		this(CONDITION_FRAGMENT_TOKEN, token, null, null);
	}
	public ConditionFragment(int type, String expression, String property, Object value) {
		this.type = type;
		this.expression = expression;
		this.property = property;
		this.value = value;
		if("and".equalsIgnoreCase(expression) || "or".equalsIgnoreCase(expression)
				|| "where".equalsIgnoreCase(expression)
				|| "(".equalsIgnoreCase(expression)
				|| ")".equalsIgnoreCase(expression)) {
			this.logicShip = true;
		}
	}
	/**
	 * sql片段类型
	 */
	private int type = CONDITION_FRAGMENT_TOKEN;
	/**
	 * 标识符，如or,and,=,<,<=等
	 */
	private String expression;
	private String property;
	private Object value;
	private boolean logicShip = false;

	private ConditionFragment nextFragment;

	private ConditionFragment beforeFragment;

	/**创建逻辑关系
	 * @param token
	 * @return
	 */
	public  ConditionFragment createNextToken(String token) {
		if(("and".equals(token) || "or".equals(token)) && this.logicShip) {
			return this;
		}
		ConditionFragment currentNext = this.nextFragment;
		this.nextFragment = new ConditionFragment(CONDITION_FRAGMENT_TOKEN, token, null, null);
		this.nextFragment.nextFragment = currentNext;
		this.nextFragment.beforeFragment = this;
		return this.nextFragment;
	}
	/**
	 * 创建判断条件
	 * @param token
	 * @param property
	 * @param value
	 * @return
	 */
	public  ConditionFragment createNextExp(String token, String property, Object value) {
		ConditionFragment currentNext = this.nextFragment;
		this.nextFragment = new ConditionFragment(CONDITION_FRAGMENT_EXP, token, property, value);
		this.nextFragment.nextFragment = currentNext;
		this.nextFragment.beforeFragment = this;
		return this.nextFragment;
	}
	/**
	 * 创建自定义条件
	 * @param expression
	 * @param value
	 * @return
	 */
	public  ConditionFragment createNextCondition(String expression, Object... value) {
		ConditionFragment currentNext = this.nextFragment;
		this.nextFragment = new ConditionFragment(CONDITION_FRAGMENT_CONDITION, expression, null, Arrays.asList(value));
		this.nextFragment.nextFragment = currentNext;
		this.nextFragment.beforeFragment = this;
		return this.nextFragment;
	}
	/**
	 * 删除当前条件及上级逻辑条件
	 * @return
	 */
	public ConditionFragment removeCondition() {
		if("and".equals(this.expression) || "or".equals(this.expression)) {
			ConditionFragment current =  this.beforeFragment;
			current.setNextFragment(this.nextFragment);
			this.beforeFragment = null;
			this.nextFragment = null;
			return current;
		}
		return this;
	}
	/**
	 * 创建排序
	 * @param expression
	 * @param property
	 * @return
	 */
	public  ConditionFragment createNextOrder(String expression, String property) {
		ConditionFragment currentNext = this.nextFragment;
		this.nextFragment = new ConditionFragment(CONDITION_FRAGMENT_ORDER, expression, property, null);
		this.nextFragment.nextFragment = currentNext;
		return this.nextFragment;
	}

	public String toSql(BeanWrapper entityWrapper) {
		StringBuilder sb = new StringBuilder();
		if(CONDITION_FRAGMENT_TOKEN == this.type) {
			sb.append(" ");
			sb.append(this.expression);
			sb.append(" ");
		}else if(CONDITION_FRAGMENT_EXP == this.type) {
			@NonNull
			String columnName = entityWrapper.getColumnNameByPropertyName(this.property);
			sb.append(" ");
			sb.append(columnName);
			sb.append(" ");
			if("in".equalsIgnoreCase(this.expression)) {
				sb.append(this.expression);
				sb.append(" (");
				List inValues = (List)this.value;
				sb.append(Joiner.on(",").join(inValues.stream().map(c -> "?").iterator()));
				sb.append(") ");
			}else {
				sb.append(this.expression);
				sb.append(" ");
				sb.append("?");
			}
		}else if(CONDITION_FRAGMENT_CONDITION == this.type) {
			sb.append(" ");
			sb.append(TextUtil.replaceTemplateParams(this.expression, paramName -> {
				@NonNull
				String columnName = entityWrapper.getColumnNameByPropertyName(paramName);
				return columnName;
			}));
			sb.append(" ");
		}else if(CONDITION_FRAGMENT_ORDER == this.type) {
			@NonNull
			String columnName = entityWrapper.getColumnNameByPropertyName(this.property);
			sb.append(" ");
			sb.append(columnName);
			sb.append(" ");
			sb.append(this.expression);
		}
		return sb.toString();
	}
}
