package com.gugusong.sqlmapper.db;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Page;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;

import lombok.NonNull;

/**
 * 非线程安全类
 * @author yousongshu
 *
 */
public class ExampleImpl implements Example {

	private final ConditionFragment sqlFragment;
	private ConditionFragment orderFragment;
	private ConditionFragment currentOrderFragment;
	private ConditionFragment currentFragment;
	private final Example parent;
	private boolean hasPage = false;
	private Page page;
	
	private ExampleImpl() {
		this(new ConditionFragment("where"), null);
	}
	private ExampleImpl(ConditionFragment sqlFragment, Example parent) {
		this.sqlFragment = sqlFragment;
		this.currentFragment = sqlFragment;
		this.parent = parent;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Example newInstance() {
		return new ExampleImpl();
	}
	
	@Override
	public Example or() {
		currentFragment = currentFragment.createNextToken("or");
		return this;
	}

	@Override
	public Example and() {
		currentFragment = currentFragment.createNextToken("and");
		return this;
	}
	
	@Override
	public Example equals(String property, Object value) {
		return equals(property, value, false);
	}
	
	/**
	 * 判断相等
	 * @param property
	 * @param value
	 * @param nullIsTrue 当值为空时，条件是否永真
	 * @return
	 */
	@Override
	public Example equals(String property, Object value, boolean nullIsTrue) {
		if(nullIsTrue) {
			if(value ==null) {
				currentFragment = currentFragment.createNextCondition("1=1");
				return this;
			}else {
				currentFragment = currentFragment.createNextExp("=", property, value);
				return this;
			}
		}else {
			if(value == null) {
				return isNull(property);
			}else {
				currentFragment = currentFragment.createNextExp("=", property, value);
				return this;
			}
		}
	}
	
	@Override
	public Example isNull(String property) {
		currentFragment = currentFragment.createNextCondition("{" + property + "} is null");
		return this;
	}
	@Override
	public Example startWith(String property, String value) {
		like(property, value==null?"%":value+"%");
		return this;
	}
	@Override
	public Example contains(String property, String value) {
		like(property, value==null?"%":"%" + value + "%");
		return this;
	}
	@Override
	public Example endsWith(String property, String value) {
		like(property, value==null?"%":"%" + value);
		return this;
	}

	@Override
	public Example in(String property, @NonNull List<Object> value) {
		currentFragment = currentFragment.createNextExp("in", property, value);
		return this;
	}

	@Override
	public Example like(String property, @NonNull String value) {
		currentFragment = currentFragment.createNextExp("like", property, value);
		return this;
	}

	@Override
	public Example gt(String property, Object value) {
		currentFragment = currentFragment.createNextExp(">", property, value);
		return this;
	}
	@Override
	public Example gt(String property, Object value, boolean nullIsTrue) {
		if(nullIsTrue && value == null) {
			currentFragment = currentFragment.createNextCondition("1=1");
			return this;
		}
		return gt(property, value);
	}

	@Override
	public Example gtEquals(String property, Object value) {
		currentFragment = currentFragment.createNextExp(">=", property, value);
		return this;
	}
	@Override
	public Example gtEquals(String property, Object value, boolean nullIsTrue) {
		if(nullIsTrue && value == null) {
			currentFragment = currentFragment.createNextCondition("1=1");
			return this;
		}
		return gtEquals(property, value);
	}

	@Override
	public Example lt(String property, Object value) {
		currentFragment = currentFragment.createNextExp("<", property, value);
		return this;
	}
	@Override
	public Example lt(String property, Object value, boolean nullIsTrue) {
		if(nullIsTrue && value == null) {
			currentFragment = currentFragment.createNextCondition("1=1");
			return this;
		}
		return lt(property, value);
	}

	@Override
	public Example ltEquals(String property, Object value) {
		currentFragment = currentFragment.createNextExp("<=", property, value);
		return this;
	}
	@Override
	public Example ltEquals(String property, Object value, boolean nullIsTrue) {
		if(nullIsTrue && value == null) {
			currentFragment = currentFragment.createNextCondition("1=1");
			return this;
		}
		return ltEquals(property, value);
	}

	@Override
	public Example condition(String expression, Object... value) {
		currentFragment = currentFragment.createNextCondition(expression, value);
		return this;
	}

	@Override
	public Example subCondition() {
		this.currentFragment = this.currentFragment.createNextToken("(");
		ConditionFragment beginFragment = this.currentFragment;
		this.currentFragment = this.currentFragment.createNextToken(")");
		return new ExampleImpl(beginFragment, this);
	}
	
	@Override
	public Example upCondition() {
		if(this.parent == null) {
			throw new RuntimeException("顶级Example无法获取上级!");
		}
		return this.parent;
	}
	
	@Override
	public Example orderByAsc(String property) {
		if(orderFragment == null) {
			orderFragment = new ConditionFragment(ConditionFragment.CONDITION_FRAGMENT_ORDER, "asc", property, null);
			currentOrderFragment = orderFragment;
		}else {
			currentOrderFragment = currentOrderFragment.createNextOrder("asc", property);
		}
		return this;
	}
	
	@Override
	public Example orderByDesc(String property) {
		if(orderFragment == null) {
			orderFragment = new ConditionFragment(ConditionFragment.CONDITION_FRAGMENT_ORDER, "desc", property, null);
			currentOrderFragment = orderFragment;
		}else {
			currentOrderFragment = currentOrderFragment.createNextOrder("desc", property);
		}
		return this;
	}

	/**
	 * 转化为条件sql
	 * @param entityWrapper
	 * @param hasOrder
	 * @return
	 */
	public String toSql(BeanWrapper entityWrapper, boolean hasOrder) {
		StringBuilder sb = new StringBuilder();
		ConditionFragment sqlString = this.sqlFragment;
		sb.append(sqlString.toSql(entityWrapper));
		while (sqlString.getNextFragment() != null) {
			sqlString = sqlString.getNextFragment();
			sb.append(sqlString.toSql(entityWrapper));
		}
		if(orderFragment == null || !hasOrder) {
			return sb.toString();
		}
		sb.append(" order by ");
		ConditionFragment orderString = this.orderFragment;
		sb.append(orderString.toSql(entityWrapper));
		while (orderString.getNextFragment() != null) {
			orderString = orderString.getNextFragment();
			sb.append(",");
			sb.append(orderString.toSql(entityWrapper));
		}
		return sb.toString();
	}
	
	public String toSql(BeanWrapper entityWrapper) {
		return toSql(entityWrapper, true);
	}
	
	public String toOrderSql(BeanWrapper entityWrapper) {
		StringBuilder sb = new StringBuilder();
		sb.append(" order by ");
		ConditionFragment orderString = this.orderFragment;
		sb.append(orderString.toSql(entityWrapper));
		while (orderString.getNextFragment() != null) {
			orderString = orderString.getNextFragment();
			sb.append(",");
			sb.append(orderString.toSql(entityWrapper));
		}
		return sb.toString();
	}
	/**
	 * 获取所有值
	 * @return
	 */
	public List<Object> getValues() {
		List<Object> values = new LinkedList<Object>();
		ConditionFragment sqlString = this.sqlFragment;
		while (sqlString.getNextFragment() != null) {
			sqlString = sqlString.getNextFragment();
			if(sqlString.getType() == ConditionFragment.CONDITION_FRAGMENT_EXP) {
				if("in".equalsIgnoreCase(sqlString.getExpression())) {
					values.addAll((List)sqlString.getValue());
				}else {
					values.add(sqlString.getValue());
				}
			}else if(sqlString.getType() == ConditionFragment.CONDITION_FRAGMENT_CONDITION) {
				values.addAll((List)sqlString.getValue());
			}
		}
		return values;
	}
	@Override
	public Example page() {
		this.hasPage = true;
		return this;
		
	}
	@Override
	public Example page(Page page) {
		this.hasPage = true;
		this.page = page;
		return this;
	}
	@Override
	public boolean isPage() {
		return this.hasPage;
	}
	@Override
	public Page getPage() {
		return this.page;
	}


}
