package com.gugusong.sqlmapper.db;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Page;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;

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
		currentFragment = currentFragment.createNextExp("=", property, value);
		return this;
	}

	@Override
	public Example in(String property, List<Object> value) {
		currentFragment = currentFragment.createNextExp("in", property, value);
		return this;
	}

	@Override
	public Example like(String property, Object value) {
		currentFragment = currentFragment.createNextExp("like", property, value);
		return this;
	}

	@Override
	public Example gt(String property, Object value) {
		currentFragment = currentFragment.createNextExp(">", property, value);
		return this;
	}

	@Override
	public Example gtEquals(String property, Object value) {
		currentFragment = currentFragment.createNextExp(">=", property, value);
		return this;
	}

	@Override
	public Example lt(String property, Object value) {
		currentFragment = currentFragment.createNextExp("<", property, value);
		return this;
	}

	@Override
	public Example ltEquals(String property, Object value) {
		currentFragment = currentFragment.createNextExp("<=", property, value);
		return this;
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
	 * @return
	 */
	public String toSql(BeanWrapper entityWrapper) {
		StringBuilder sb = new StringBuilder();
		ConditionFragment sqlString = this.sqlFragment;
		sb.append(sqlString.toSql(entityWrapper));
		while (sqlString.getNextFragment() != null) {
			sqlString = sqlString.getNextFragment();
			sb.append(sqlString.toSql(entityWrapper));
		}
		if(orderFragment == null) {
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
				values.add(sqlString.getValue());
			}else if(sqlString.getType() == ConditionFragment.CONDITION_FRAGMENT_CONDITION) {
				values.addAll((List)sqlString.getValue());
			}
		}
		return values;
	}
	@Override
	public void page() {
		this.hasPage = true;
		
	}
	@Override
	public void page(Page page) {
		this.hasPage = true;
		this.page = page;
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
