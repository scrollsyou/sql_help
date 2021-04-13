package com.gugusong.sqlmapper.common.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.annotation.Version;
import com.gugusong.sqlmapper.annotation.vo.FunctionMapping;
import com.gugusong.sqlmapper.annotation.vo.GroupBy;
import com.gugusong.sqlmapper.annotation.vo.Join;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
import com.gugusong.sqlmapper.annotation.vo.OneToMany;
import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;
import com.gugusong.sqlmapper.annotation.vo.VOBean;
import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;
import com.gugusong.sqlmapper.common.exception.SqlException;
import com.gugusong.sqlmapper.common.exception.StructureException;
import com.gugusong.sqlmapper.common.util.TextUtil;
import com.gugusong.sqlmapper.config.GlobalConfig;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * bean类包装器，用于解析类属性
 * 用于对BEAN类进行编辑
 * @author yousongshu
 *
 */
public class BeanWrapper {
	// po类
	public static final String BEAN_TYPE_PO = "po";
	// vo查询类
	public static final String BEAN_TYPE_VO = "vo";
	// 普通bean类
	public static final String BEAN_TYPE_SIMPLE = "simple";

	// TODO 后期所有Map都需进行优化，可能会重写个轻量的String为key的Map
	private static final Map<Class<?>, BeanWrapper> cacheMap = new ConcurrentHashMap<Class<?>, BeanWrapper>();
	/**
	 * po类
	 */
	@Getter
	@Setter
	private Class<?> poClazz;
	@Getter
	private BeanColumn idColumn;
	@Getter
	private List<BeanColumn> columns;
	@Getter
	private Map<String, BeanColumn> columnsTree = new TreeMap<String, BeanColumn>();
	@Getter
	private String tableName;
	/**
	 * 表别名
	 */
	@Getter
	private String tableAliasName;
	/**
	 * 是否是Po类
	 */
	@Getter
	private String beanType;
	/**
	 * 关联Bean类映射
	 */
	@Getter
	private Map<String, BeanJoin> joinBeans = new LinkedHashMap<String, BeanJoin>();
	@Getter
	private BeanWrapper mainWrapper;
	/**
	 * 分组字段（只作用于Vo）
	 */
	@Getter
	private String[] groupBys;
	@Getter
	@Setter
	private boolean pageSubSql = false;
	@Getter
	private BeanWrapper voWrapper;
	@Getter
	private List<BeanColumn> funcColumns;

	/**
	 * 乐观锁
	 */
	@Getter
	private boolean version = false;
	@Getter
	private BeanColumn versionColumn = null;

	private Map<String, String> sqlCache = new TreeMap<String, String>();

	private GlobalConfig config;

	private BeanWrapper(Class<?> beanClazz, GlobalConfig config) {
		this(beanClazz, config, null, null, null, null);
	}
	private BeanWrapper(Class<?> beanClazz, GlobalConfig config, Map<String, BeanJoin> joinBeans, String tableAliasName, BeanWrapper mainWrapper, BeanWrapper voWrapper) {
		this.poClazz = beanClazz;
		this.config = config;
		this.tableAliasName = tableAliasName;
		this.mainWrapper = mainWrapper;
		this.voWrapper = voWrapper;
		if(joinBeans != null) {
			this.joinBeans = joinBeans;
		}
		Entity entity = beanClazz.getAnnotation(Entity.class);
		VOBean voBean = beanClazz.getAnnotation(VOBean.class);
		if(entity != null) {
			this.beanType = BEAN_TYPE_PO;
			poInstancee(beanClazz, config);
		}else if(voBean != null) {
			this.beanType = BEAN_TYPE_VO;
			this.tableAliasName = voBean.entityAlias();
			voInstancee(beanClazz, config);
		}else {
			// 普通bean类，用于附值
			this.beanType = BEAN_TYPE_SIMPLE;
			simpleBeanInstance(beanClazz, config);
		}

	}
	/**
	 * 普通bean包装
	 * @param voClazz
	 * @param config
	 */
	private void simpleBeanInstance(Class<?> voClazz, GlobalConfig config) {

		Field[] physicalFields = voClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(voClazz, Object.class);
		} catch (IntrospectionException e) {
			throw new StructureException(e);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if(Modifier.isStatic(physicalField.getModifiers()) || Modifier.isFinal(physicalField.getModifiers())) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new StructureException(ErrorCodeConstant.NOT_BEAN);
			}
			if(physicalField.isAnnotationPresent(ManyToOne.class)) {
				ManyToOne manyToOne = physicalField.getAnnotation(ManyToOne.class);
				@NonNull
				Class<?> oneClazz = manyToOne.targetClass();
				BeanColumn beanColumn = new BeanColumn(null,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, BeanWrapper.instrance(oneClazz, config, joinBeans, tableAliasName, mainWrapper, this.voWrapper), null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			} else if(physicalField.isAnnotationPresent(OneToMany.class)) {
				OneToMany oneToMany = physicalField.getAnnotation(OneToMany.class);
				@NonNull
				Class<?> manyClazz = oneToMany.targetClass();
				// 一对多时，多的一方分组去重条件
				BeanWrapper oneToManyWrapper = BeanWrapper.instrance(manyClazz, config, joinBeans, tableAliasName, mainWrapper, this.voWrapper);
				Set<String> groupBy = new HashSet<String>(oneToManyWrapper.columns.size());
				for (BeanColumn oneToManyColum : oneToManyWrapper.columns) {
					BeanJoin joinBean = joinBeans.get(oneToManyColum.getTableAlias());
					if(joinBean != null && joinBean.getJoinBeanWrapper().getIdColumn() != null) {
						groupBy.add(new StringBuilder(oneToManyColum.getTableAlias()).append("_").append(joinBean.getJoinBeanWrapper().getIdColumn().getName()).toString());
						if(this.voWrapper != null) {
							this.voWrapper.setPageSubSql(true);
						}
					}
				}
				BeanColumn beanColumn = new BeanColumn(null,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, oneToManyWrapper, groupBy.toArray(new String[] {}));
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				// TODO 判断 oneToMany 注解必须在List/Set上
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}else if(physicalField.isAnnotationPresent(PropertyMapping.class)) {
				PropertyMapping propertyMapping = physicalField.getAnnotation(PropertyMapping.class);
				@NonNull
				String originalName = propertyMapping.originalName();
				String[] nameSplit = originalName.split("\\.");
				BeanColumn beanColumn = null;
				if(nameSplit.length == 1) {
					throw new RuntimeException("基础bean类中属性必须指定表别名!");
				}
				BeanColumn byPropertyName = null;
				if(nameSplit[0].equals(tableAliasName)) {
					byPropertyName = mainWrapper.getByPropertyName(nameSplit[1]);
				}else {
					BeanJoin beanJoin = joinBeans.get(nameSplit[0]);
					byPropertyName = beanJoin.getJoinBeanWrapper().getByPropertyName(nameSplit[1]);
				}
				beanColumn = new BeanColumn(byPropertyName.getName(),
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						nameSplit[0], nameSplit[0] + "_" + byPropertyName.getAliasName(), null, null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}else if(physicalField.isAnnotationPresent(FunctionMapping.class)) {
				FunctionMapping functionMapping = physicalField.getAnnotation(FunctionMapping.class);
				@NonNull
				String funcValue = functionMapping.function();
				funcValue = TextUtil.replaceTemplateParams(funcValue, paramName -> {
					@NonNull
					String columnName = voWrapper.getColumnNameByPropertyName(paramName);
					return columnName;
				});
				String columnName = config.getImplicitNamingStrategy().getColumnName(physicalField.getName());
				BeanColumn beanColumn = new BeanColumn(columnName,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						voWrapper.tableAliasName, voWrapper.tableAliasName + "_" + columnName, null, null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				beanColumn.setFunc(true);
				beanColumn.setFunction(funcValue);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
				voWrapper.getFuncColumns().add(beanColumn);
			}else {
				throw new RuntimeException("基础bean类中属性必须指定表别名!");
			}

		}
		columnList.sort(new Comparator<BeanColumn>() {
			public int compare(BeanColumn o1, BeanColumn o2) {
				return  o1.getSort() - o2.getSort();
			}
		});
		columns = new ArrayList<BeanColumn>(columnList.size());
		columns.addAll(columnList);

	}
	/**
	 * vo 类进行包装
	 * @param poClazz
	 * @param config
	 * @throws Exception
	 */
	private void voInstancee(Class<?> voClazz, GlobalConfig config) {
		VOBean voBean = voClazz.getAnnotation(VOBean.class);
		this.funcColumns = new ArrayList<BeanColumn>();
		@NonNull
		Class<?> mainPoClazz = voBean.mainPo();
		if(!isPo(mainPoClazz)) {
			throw new RuntimeException("指定主Po类不存在!");
		}
		mainWrapper = instrance(mainPoClazz, config);
		this.tableName = mainWrapper.getTableName();
		this.tableAliasName = voBean.entityAlias();
		Join[] joins = voClazz.getAnnotationsByType(Join.class);
		if(joins != null && joins.length > 0) {
			for (Join join : joins) {
				joinBeans.put(join.entityAlias(), new BeanJoin(join.joinType(), join.joinConditions(), BeanWrapper.instrance(join.po(), config), join.entityAlias()));
			}
		}
		GroupBy groupBy = voClazz.getAnnotation(GroupBy.class);
		if(groupBy != null) {
			this.groupBys = groupBy.properties();
		}

		Field[] physicalFields = voClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(voClazz, Object.class);
		} catch (IntrospectionException e) {
			throw new StructureException(e);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if(Modifier.isStatic(physicalField.getModifiers()) || Modifier.isFinal(physicalField.getModifiers())) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new StructureException(ErrorCodeConstant.NOT_BEAN);
			}
			if(physicalField.isAnnotationPresent(ManyToOne.class)) {
				ManyToOne manyToOne = physicalField.getAnnotation(ManyToOne.class);
				@NonNull
				Class<?> oneClazz = manyToOne.targetClass();
				BeanColumn beanColumn = new BeanColumn(null,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, BeanWrapper.instrance(oneClazz, config, joinBeans, tableAliasName, mainWrapper, this), null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			} else if(physicalField.isAnnotationPresent(OneToMany.class)) {
				OneToMany oneToMany = physicalField.getAnnotation(OneToMany.class);
				@NonNull
				Class<?> manyClazz = oneToMany.targetClass();
				BeanWrapper oneToManyWrapper = BeanWrapper.instrance(manyClazz, config, joinBeans, tableAliasName, mainWrapper, this);
				Set<String> groupByCount = new HashSet<String>(oneToManyWrapper.columns.size());
				for (BeanColumn oneToManyColum : oneToManyWrapper.columns) {
					if(oneToManyColum.getTableAlias() == null) {
						continue;
					}
					BeanJoin joinBean = joinBeans.get(oneToManyColum.getTableAlias());
					if(joinBean != null && joinBean.getJoinBeanWrapper().getIdColumn() != null) {
						groupByCount.add(new StringBuilder(oneToManyColum.getTableAlias()).append("_").append(joinBean.getJoinBeanWrapper().getIdColumn().getName()).toString());
						this.setPageSubSql(true);
					}
				}
				BeanColumn beanColumn = new BeanColumn(null,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, oneToManyWrapper, groupByCount.toArray(new String[] {}));
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				// TODO 判断 oneToMany 注解必须在List/Set上
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}else if(physicalField.isAnnotationPresent(PropertyMapping.class)) {
				PropertyMapping propertyMapping = physicalField.getAnnotation(PropertyMapping.class);
				@NonNull
				String originalName = propertyMapping.originalName();
				String[] nameSplit = originalName.split("\\.");
				BeanColumn beanColumn = null;
				if(nameSplit.length == 1 || nameSplit[0].equals(this.tableAliasName)) {
					BeanColumn byPropertyName = mainWrapper.getByPropertyName(nameSplit[0]);
					beanColumn = new BeanColumn(byPropertyName.getName(),
							physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
							this.tableAliasName, this.tableAliasName + "_" + byPropertyName.getAliasName(), null, null);
				}else {
					BeanJoin beanJoin = joinBeans.get(nameSplit[0]);
					BeanColumn byPropertyName = beanJoin.getJoinBeanWrapper().getByPropertyName(nameSplit[1]);
					beanColumn = new BeanColumn(byPropertyName.getName(),
							physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
							nameSplit[0], nameSplit[0] + "_" + byPropertyName.getAliasName(), null, null);
				}
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}else if(physicalField.isAnnotationPresent(FunctionMapping.class)) {
				FunctionMapping functionMapping = physicalField.getAnnotation(FunctionMapping.class);
				@NonNull
				String funcValue = functionMapping.function();
				funcValue = TextUtil.replaceTemplateParams(funcValue, paramName -> {
					@NonNull
					String columnName = this.getColumnNameByPropertyName(paramName);
					return columnName;
				});
				String columnName = config.getImplicitNamingStrategy().getColumnName(physicalField.getName());
				BeanColumn beanColumn = new BeanColumn(columnName,
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						this.tableAliasName, this.tableAliasName + "_" + columnName, null, null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				beanColumn.setFunc(true);
				beanColumn.setFunction(funcValue);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
				funcColumns.add(beanColumn);
			}else {
				BeanColumn byPropertyName = mainWrapper.getByPropertyName(physicalField.getName());
				BeanColumn beanColumn = new BeanColumn(byPropertyName.getName(),
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						this.tableAliasName, this.tableAliasName + "_" + byPropertyName.getAliasName(), null, null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}

		}
		columnList.sort(new Comparator<BeanColumn>() {
			public int compare(BeanColumn o1, BeanColumn o2) {
				return  o1.getSort() - o2.getSort();
			}
		});
		columns = new ArrayList<BeanColumn>(columnList.size());
		columns.addAll(columnList);

	}
	/**
	 * po 类进行包装
	 * @param poClazz
	 * @param config
	 * @throws Exception
	 */
	private void poInstancee(Class<?> poClazz, GlobalConfig config) {
		Field[] physicalFields = poClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(poClazz, Object.class);
		} catch (IntrospectionException e) {
			throw new StructureException(e);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if(Modifier.isStatic(physicalField.getModifiers()) || Modifier.isFinal(physicalField.getModifiers())) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new StructureException(ErrorCodeConstant.NOT_BEAN);
			}
			if(physicalField.isAnnotationPresent(Id.class)) {
				Id id = physicalField.getAnnotation(Id.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(id.name())?config.getImplicitNamingStrategy().getColumnName(physicalField.getName()):id.name(),
						null, 11, true, id.strategy(), physicalField.getName(), physicalField,propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), 0);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
				idColumn = beanColumn;
			}else if(physicalField.isAnnotationPresent(Column.class)) {
				Column column = physicalField.getAnnotation(Column.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(column.name())?config.getImplicitNamingStrategy().getColumnName(physicalField.getName()):column.name(),
								Strings.isNullOrEmpty(column.dateType())?null:column.dateType(),
								column.length()==0?null:column.length(),
								false, null, physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), column.sort());
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				if(physicalField.isAnnotationPresent(Version.class)) {
					Version versionAnno = physicalField.getAnnotation(Version.class);
					beanColumn.setVersion(true);
					beanColumn.setVersionStrategy(versionAnno.strategy());
					if(version) {
						throw new SqlException("当个PO类中乐观锁字段不支持多个!");
					}
					this.version = true;
					this.versionColumn = beanColumn;
				}
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}else {
				BeanColumn beanColumn = new BeanColumn(config.getImplicitNamingStrategy().getColumnName(physicalField.getName()),
						null, null, false, null, physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), Integer.MAX_VALUE);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				if(physicalField.isAnnotationPresent(Version.class)) {
					Version versionAnno = physicalField.getAnnotation(Version.class);
					beanColumn.setVersion(true);
					beanColumn.setVersionStrategy(versionAnno.strategy());
					if(version) {
						throw new SqlException("当个PO类中乐观锁字段不支持多个!");
					}
					this.version = true;
					this.versionColumn = beanColumn;
				}
				columnsTree.put(beanColumn.getFieldName(), beanColumn);
				columnList.add(beanColumn);
			}
		}
		columnList.sort(new Comparator<BeanColumn>() {
			public int compare(BeanColumn o1, BeanColumn o2) {
				return  o1.getSort() - o2.getSort();
			}
		});
		columns = new ArrayList<BeanColumn>(columnList.size());
		columns.addAll(columnList);
		List<String> splitPackage = Splitter.on(CharMatcher.anyOf(".$")).splitToList(poClazz.getName());
		Entity annotation = poClazz.getAnnotation(Entity.class);
		if(annotation.tableName()!=null && !"".equals(annotation.tableName())) {
			tableName = annotation.tableName();
		}else {
			tableName = config.getImplicitNamingStrategy().getTableName(splitPackage.get(splitPackage.size() - 1));
		}
	}
	/**
	 * 指定属性名查询出Descriptor
	 * @param propertyDescriptors
	 * @param name
	 * @return
	 */
	private PropertyDescriptor getDescriptorByName(@NonNull PropertyDescriptor[] propertyDescriptors, @NonNull String name) {
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if(name.equals(propertyDescriptor.getName())) {
				return propertyDescriptor;
			}
		}
		return null;
	}

	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlobalConfig config) {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}

	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlobalConfig config, Map<String, BeanJoin> parentJoinBeans, String tableAliasName, BeanWrapper mainWrapper, BeanWrapper voWrapper) {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config, parentJoinBeans, tableAliasName, mainWrapper, voWrapper);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}
	/**
	 * 获取缓存sql
	 */
	public String getSql(String key) {
		return sqlCache.get(key);
	}
	/**
	 * 缓存sql
	 */
	public void putSql(String key, String sql) {
		sqlCache.put(key, sql);
	}

	/**
	 * 返回字段数据库相关信息
	 * @param propertyName
	 * @return
	 */
	public BeanColumn getByPropertyName(@NonNull String propertyName) {
		// TODO 该功能需做map映射，方便按属性名获取数据库定义
		String[] proSplit = propertyName.split("\\.");
		Map<String, BeanColumn> bufferColumns = null;
		String simplePropertyName = null;
		if(proSplit.length == 1 || proSplit[0].equals(tableAliasName)) {
			bufferColumns = columnsTree;
			simplePropertyName = propertyName;
		}else {
			@NonNull
			BeanJoin beanJoin = joinBeans.get(proSplit[0]);
			bufferColumns = beanJoin.getJoinBeanWrapper().getColumnsTree();
		}
		return bufferColumns.get(simplePropertyName);
	}

	/**
	 * 返回字段数据库相关信息
	 * @param propertyName
	 * @return
	 */
	public String getColumnNameByPropertyName(@NonNull String propertyName) {
		// TODO 该功能需做map映射，方便按属性名获取数据库定义
		String[] proSplit = propertyName.split("\\.");
		List<BeanColumn> bufferColumns = null;
		String simplePropertyName = null;
		StringBuilder resultName = new StringBuilder();
		if(proSplit.length == 1 || proSplit[0].equals(tableAliasName)) {
			if(mainWrapper == null) {
				bufferColumns = columns;
			}else {
				bufferColumns = mainWrapper.columns;
				resultName.append(tableAliasName).append(".");
			}
			simplePropertyName = proSplit[proSplit.length - 1];
		}else {
			@NonNull
			BeanJoin beanJoin = joinBeans.get(proSplit[0]);
			bufferColumns = beanJoin.getJoinBeanWrapper().getColumns();
			resultName.append(proSplit[0]).append(".");
			simplePropertyName = proSplit[1];
		}
		for (BeanColumn beanColumn : bufferColumns) {
			if(beanColumn.getFieldName().equals(simplePropertyName)) {
				return resultName.append(beanColumn.getName()).toString();
			}
		}
		return null;
	}
	/**
	 * 返回是否为po类
	 * @param clazz
	 * @return
	 */
	public static boolean isPo(@NonNull Class<?> clazz) {
		Entity entity = clazz.getAnnotation(Entity.class);
		return entity != null;
	}
}
