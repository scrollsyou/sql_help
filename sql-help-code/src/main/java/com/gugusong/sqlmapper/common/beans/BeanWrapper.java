package com.gugusong.sqlmapper.common.beans;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.annotation.vo.LeftJoin;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
import com.gugusong.sqlmapper.annotation.vo.OneToMany;
import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;
import com.gugusong.sqlmapper.annotation.vo.VOBean;
import com.gugusong.sqlmapper.common.constants.ErrorCodeConstant;
import com.gugusong.sqlmapper.config.GlogalConfig;

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
	private Map<String, BeanJoin> joinBeans = new TreeMap<String, BeanJoin>();
	@Getter
	private BeanWrapper mainWrapper;
	
	private Map<String, String> sqlCache = new TreeMap<String, String>();
	
	private GlogalConfig config;
	
	private BeanWrapper(Class<?> beanClazz, GlogalConfig config) throws Exception {
		this(beanClazz, config, null, null, null);
	}
	private BeanWrapper(Class<?> beanClazz, GlogalConfig config, Map<String, BeanJoin> joinBeans, String tableAliasName, BeanWrapper mainWrapper) throws Exception {
		this.poClazz = beanClazz;
		this.config = config;
		this.tableAliasName = tableAliasName;
		this.mainWrapper = mainWrapper;
		if(joinBeans != null) {
			this.joinBeans = joinBeans;
		}
		Entity antity = beanClazz.getAnnotation(Entity.class);
		VOBean voBean = beanClazz.getAnnotation(VOBean.class);
		if(antity != null) {
			this.beanType = BEAN_TYPE_PO;
			poInstance(beanClazz, config);
		}else if(voBean != null) {
			this.beanType = BEAN_TYPE_VO;
			this.tableAliasName = voBean.entityAlias();
			voInstance(beanClazz, config);
		}else {
			// TODO 普通bean类，用于附值
			this.beanType = BEAN_TYPE_SIMPLE;
			simpleBeanInstanc(beanClazz, config);
		}
		
	}
	/**
	 * 普通bean包装
	 * @param voClazz
	 * @param config
	 */
	private void simpleBeanInstanc(Class<?> voClazz, GlogalConfig config)  throws Exception {

		Field[] physicalFields = voClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = Introspector.getBeanInfo(voClazz, Object.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new Exception(ErrorCodeConstant.NOTBEAN.toString());
			}
			if(physicalField.isAnnotationPresent(ManyToOne.class)) {
				ManyToOne manyToOne = physicalField.getAnnotation(ManyToOne.class);
				@NonNull
				Class<?> oneClazz = manyToOne.tagerClass();
				BeanColumn beanColumn = new BeanColumn(null, 
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, BeanWrapper.instrance(oneClazz, config, joinBeans, tableAliasName, mainWrapper), null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			} else if(physicalField.isAnnotationPresent(OneToMany.class)) {
				OneToMany oneToMany = physicalField.getAnnotation(OneToMany.class);
				@NonNull
				Class<?> manyClazz = oneToMany.tagerClass();
				// 一对多时，多的一方分组去重条件
				BeanWrapper oneToManyWrapper = BeanWrapper.instrance(manyClazz, config, joinBeans, tableAliasName, mainWrapper);
				Set<String> groupBy = new HashSet<String>(oneToManyWrapper.columns.size());
				for (BeanColumn oneToManyColum : oneToManyWrapper.columns) {
					BeanJoin joinBean = joinBeans.get(oneToManyColum.getTableAlias());
					if(joinBean != null && joinBean.getJoinBeanWrapper().getIdColumn() != null) {
						groupBy.add(new StringBuilder(oneToManyColum.getTableAlias()).append("_").append(joinBean.getJoinBeanWrapper().getIdColumn().getName()).toString());
					}
				}
				BeanColumn beanColumn = new BeanColumn(null, 
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, oneToManyWrapper, groupBy.toArray(new String[] {}));
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				// TODO 判断 oneToMany 注解必须在List/Set上
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
				columnList.add(beanColumn);
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
	private void voInstance(Class<?> voClazz, GlogalConfig config) throws Exception {
		VOBean voBean = voClazz.getAnnotation(VOBean.class);
		@NonNull
		Class<?> mainPoClazz = voBean.mainPo();
		if(!isPo(mainPoClazz)) {
			throw new RuntimeException("指定主Po类不存在!");
		}
		mainWrapper = instrance(mainPoClazz, config);
		this.tableName = mainWrapper.getTableName();
		this.tableAliasName = voBean.entityAlias();
		LeftJoin[] leftJoins = voClazz.getAnnotationsByType(LeftJoin.class);
		if(leftJoins != null && leftJoins.length > 0) {
			for (LeftJoin leftJoin : leftJoins) {
				joinBeans.put(leftJoin.entityAlias(), new BeanJoin("left join", leftJoin.joinConditions(), BeanWrapper.instrance(leftJoin.po(), config), leftJoin.entityAlias()));
			}
		}
		Field[] physicalFields = voClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = Introspector.getBeanInfo(voClazz, Object.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new Exception(ErrorCodeConstant.NOTBEAN.toString());
			}
			if(physicalField.isAnnotationPresent(ManyToOne.class)) {
				ManyToOne manyToOne = physicalField.getAnnotation(ManyToOne.class);
				@NonNull
				Class<?> oneClazz = manyToOne.tagerClass();
				BeanColumn beanColumn = new BeanColumn(null, 
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, BeanWrapper.instrance(oneClazz, config, joinBeans, tableAliasName, mainWrapper), null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			} else if(physicalField.isAnnotationPresent(OneToMany.class)) {
				OneToMany oneToMany = physicalField.getAnnotation(OneToMany.class);
				@NonNull
				Class<?> manyClazz = oneToMany.tagerClass();
				BeanWrapper oneToManyWrapper = BeanWrapper.instrance(manyClazz, config, joinBeans, tableAliasName, mainWrapper);
				Set<String> groupBy = new HashSet<String>(oneToManyWrapper.columns.size());
				for (BeanColumn oneToManyColum : oneToManyWrapper.columns) {
					if(oneToManyColum.getTableAlias() == null) {
						continue;
					}
					BeanJoin joinBean = joinBeans.get(oneToManyColum.getTableAlias());
					if(joinBean != null && joinBean.getJoinBeanWrapper().getIdColumn() != null) {
						groupBy.add(new StringBuilder(oneToManyColum.getTableAlias()).append("_").append(joinBean.getJoinBeanWrapper().getIdColumn().getName()).toString());
					}
				}
				BeanColumn beanColumn = new BeanColumn(null, 
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						null, null, oneToManyWrapper, groupBy.toArray(new String[] {}));
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				// TODO 判断 oneToMany 注解必须在List/Set上
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
				columnList.add(beanColumn);
			}else {
				BeanColumn byPropertyName = mainWrapper.getByPropertyName(physicalField.getName());
				BeanColumn beanColumn = new BeanColumn(byPropertyName.getName(), 
						physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(),
						this.tableAliasName, this.tableAliasName + "_" + byPropertyName.getAliasName(), null, null);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
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
	private void poInstance(Class<?> poClazz, GlogalConfig config) throws Exception {
		Field[] physicalFields = poClazz.getDeclaredFields();
		List<BeanColumn> columnList = new ArrayList<BeanColumn>(physicalFields.length);
		BeanInfo beanInfo = Introspector.getBeanInfo(poClazz, Object.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (Field physicalField : physicalFields) {
			if(physicalField.isAnnotationPresent(Transient.class)) {
				continue;
			}
			// TODO 为方便后期扩展，需更改为其它模式
			PropertyDescriptor propertyDesc = getDescriptorByName(propertyDescriptors, physicalField.getName());
			if(propertyDesc == null) {
				throw new Exception(ErrorCodeConstant.NOTBEAN.toString());
			}
			if(physicalField.isAnnotationPresent(Id.class)) {
				Id id = physicalField.getAnnotation(Id.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(id.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):id.name(), 
						null, 11, true, id.stragegy(), physicalField.getName(), physicalField,propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), 0);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
				idColumn = beanColumn;
			}else if(physicalField.isAnnotationPresent(Column.class)) {
				Column column = physicalField.getAnnotation(Column.class);
				BeanColumn beanColumn = new BeanColumn(Strings.isNullOrEmpty(column.name())?config.getImplicitNamingStrategy().getColumntName(physicalField.getName()):column.name(), 
								Strings.isNullOrEmpty(column.dateType())?null:column.dateType(), 
								column.length()==0?null:column.length(),
								false, null, physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), column.sort());
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
				columnList.add(beanColumn);
			}else {
				BeanColumn beanColumn = new BeanColumn(config.getImplicitNamingStrategy().getColumntName(physicalField.getName()), 
						null, null, false, null, physicalField.getName(), physicalField, propertyDesc.getReadMethod(), propertyDesc.getWriteMethod(), Integer.MAX_VALUE);
				config.getColumnTypeMapping().convertDbTypeByField(beanColumn);
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
	
	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlogalConfig config) throws Exception {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config);
			cacheMap.put(poClazz, instrance);
		}
		return instrance;
	}
	
	public static synchronized BeanWrapper instrance(@NonNull Class<?> poClazz, @NonNull GlogalConfig config, Map<String, BeanJoin> parentJoinBeans, String tableAliasName, BeanWrapper mainWrapper) throws Exception {
		BeanWrapper instrance = cacheMap.get(poClazz);
		if(instrance == null) {
			instrance = new BeanWrapper(poClazz, config, parentJoinBeans, tableAliasName, mainWrapper);
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
		List<BeanColumn> bufferColumns = null;
		String simplePropertyName = null;
		if(proSplit.length == 1 || proSplit[0].equals(tableAliasName)) {
			bufferColumns = columns;
			simplePropertyName = propertyName;
		}else {
			@NonNull
			BeanJoin beanJoin = joinBeans.get(proSplit[0]);
			bufferColumns = beanJoin.getJoinBeanWrapper().getColumns();
		}
		for (BeanColumn beanColumn : bufferColumns) {
			if(beanColumn.getFieldName().equals(simplePropertyName)) {
				return beanColumn;
			}
		}
		return null;
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
		String resultName = "";
		if(proSplit.length == 1 || proSplit[0].equals(tableAliasName)) {
			if(mainWrapper == null) {
				bufferColumns = columns;
			}else {
				bufferColumns = mainWrapper.columns;
				resultName = tableAliasName + ".";
			}
			simplePropertyName = proSplit[proSplit.length - 1];
		}else {
			@NonNull
			BeanJoin beanJoin = joinBeans.get(proSplit[0]);
			bufferColumns = beanJoin.getJoinBeanWrapper().getColumns();
			resultName = proSplit[0] + ".";
			simplePropertyName = proSplit[1];
		}
		for (BeanColumn beanColumn : bufferColumns) {
			if(beanColumn.getFieldName().equals(simplePropertyName)) {
				return resultName + beanColumn.getName();
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
		Entity antity = clazz.getAnnotation(Entity.class);
		return antity != null;
	}
}